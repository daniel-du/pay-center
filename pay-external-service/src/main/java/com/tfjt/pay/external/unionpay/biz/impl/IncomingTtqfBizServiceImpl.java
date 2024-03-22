package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.ipaynow.jiaxin.domain.QueryPresignResultModel;
import com.tfjt.constant.MessageStatusEnum;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.pay.external.unionpay.api.dto.req.QueryTtqfSignMsgReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.TtqfContractReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryTtqfSignMsgRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.TtqfCallbackRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.TtqfContractRespDTO;
import com.tfjt.pay.external.unionpay.biz.IncomingTtqfBizService;
import com.tfjt.pay.external.unionpay.constants.RetryMessageConstant;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.dto.TtqfSignMsgDTO;
import com.tfjt.pay.external.unionpay.dto.message.IncomingFinishDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingExtendInfoEntity;
import com.tfjt.pay.external.unionpay.enums.ExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.TfIncomingExtendInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.pay.external.unionpay.utils.TtqfApiUtil;
import com.tfjt.producter.ProducerMessageApi;
import com.tfjt.producter.service.AsyncMessageService;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/21 17:14
 * @description
 */
@Slf4j
@Service
public class IncomingTtqfBizServiceImpl implements IncomingTtqfBizService {

    @Autowired
    private TfIncomingInfoService incomingInfoService;

    @Autowired
    private TfIncomingExtendInfoService incomingExtendInfoService;

    @Autowired
    private AsyncMessageService asyncMessageService;

    @Autowired
    private ProducerMessageApi producerMessageApi;

    @Autowired
    private IdentifierGenerator identifierGenerator;

    @Value("${rocketmq.topic.ttqfCallback}")
    private String ttqfCallbackTopic;

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String BIZ_CODE_SUCCESS = "0000";

    private static final String BIZ_CODE_FAIL = "5000";

    @Override
    public Result<TtqfContractRespDTO> ttqfContract(TtqfContractReqDTO ttqfContractReqDTO) {
        String signUrl = TtqfApiUtil.contractH5(ttqfContractReqDTO.getIdCardNo(), ttqfContractReqDTO.getMchReturnUrl());
        TtqfContractRespDTO ttqfContractRespDTO = TtqfContractRespDTO.builder().signUrl(signUrl).build();
        return Result.ok(ttqfContractRespDTO);
    }

    @Override
    public Result<QueryTtqfSignMsgRespDTO> queryTtqfSignMsg(QueryTtqfSignMsgReqDTO queryTtqfSignMsgReqDTO) {
        log.info("IncomingTtqfBizServiceImpl--queryTtqfSignMsg, req:{}", JSONObject.toJSONString(queryTtqfSignMsgReqDTO));
        return Result.ok(incomingInfoService.queryTtqfSignMsg(queryTtqfSignMsgReqDTO.getBusinessId()));
    }

    /**
     * 批量更新天天企赋签约状态
     */
    @Override
    public void updateTtqfSignStatus() {
        //获取初始id
        TfIncomingExtendInfoEntity extendInfo = incomingExtendInfoService.queryNotSignMinIdData();
        log.info("IncomingTtqfBizServiceImpl--updateTtqfSignStatus, extendInfo:{}", JSONObject.toJSONString(extendInfo));
        if (ObjectUtils.isEmpty(extendInfo)) {
            return;
        }
        boolean updateFlag = true;
        long startId = extendInfo.getIncomingId();
        while(updateFlag) {
            //根据初始id批量查询
            List<TtqfSignMsgDTO> signMsgList = incomingInfoService.querySignMsgStartByIncomingId(startId);
            if (CollectionUtils.isEmpty(signMsgList)) {
                break;
            }
            queryAndUpdatePresignStatus(signMsgList);
            startId = signMsgList.get(signMsgList.size() - 1).getId();
        }
    }

    /**
     * 接收天天企赋回调通知
     * @param reqJSON
     * @return
     */
    @Override
    public TtqfCallbackRespDTO receviceCallbackMsg(JSONObject reqJSON) {
        log.error("IncomingTtqfBizServiceImpl--receviceCallbackMsg, req:{}", JSONObject.toJSONString(reqJSON));
        if (ObjectUtils.isEmpty(reqJSON) || StringUtils.isBlank(reqJSON.getString("type"))) {
            log.error("IncomingTtqfBizServiceImpl--receviceCallbackMsgError");
            return new TtqfCallbackRespDTO(BIZ_CODE_FAIL);
        }
        //发送mq
        MQProcess(reqJSON);
        return new TtqfCallbackRespDTO(BIZ_CODE_SUCCESS);
    }

    /**
     * 根据未完全完成状态数据查询天天企赋api，并更新数据
     * @param signMsgList
     */
    private void queryAndUpdatePresignStatus(List<TtqfSignMsgDTO> signMsgList) {
        signMsgList.forEach(signMsg -> {
            try {
                QueryPresignResultModel presignResult = TtqfApiUtil.queryPresign(signMsg.getIdCardNo());
                if (ObjectUtils.isEmpty(presignResult)) {
                    return;
                }
                TfIncomingExtendInfoEntity extendInfoEntity = new TfIncomingExtendInfoEntity();
                extendInfoEntity.setIncomingId(signMsg.getId());
                extendInfoEntity.setSignStatus(presignResult.getSignStatus().byteValue());
                extendInfoEntity.setAuthStatus(presignResult.getAuthStatus().byteValue());
                if (CollectionUtils.isEmpty(presignResult.getCards())) {
                    incomingExtendInfoService.updateByIncomingId(extendInfoEntity);
                    return;
                }
                presignResult.getCards().forEach(card -> {
                    if (card.getBankCardNo().equals(signMsg.getBankCardNo())) {
                        extendInfoEntity.setBindStatus(card.getBindStatus().byteValue());
                    }
                });
                incomingExtendInfoService.updateByIncomingId(extendInfoEntity);
            } catch (Exception e) {
                log.error("IncomingTtqfBizServiceImpl--queryAndUpdatePresignStatus, error", e);
            }
        });
    }

    /**
     * 异步发送消息
     * @param jsonObject
     */
    public void MQProcess(JSONObject jsonObject){
        log.info("IncomingTtqfBizServiceImpl--MQProcess, start jsonObject:{}", JSONObject.toJSONString(jsonObject));

        // 创建消息
        AsyncMessageEntity messageEntity = createMessage(RetryMessageConstant.INCOMING_FINISH,
                jsonObject, identifierGenerator.nextId(AsyncMessageEntity.class).toString());
        // 调用jar包中保存消息到数据库的方法
        asyncMessageService.saveMessage(messageEntity);
        // rocketMQ发送消息自行实现
        boolean result = producerMessageApi.sendMessage(messageEntity.getTopic(), JSONUtil.toJsonStr(messageEntity),messageEntity.getUniqueNo(),
                messageEntity.getMsgTag());
        log.info("IncomingBizServiceImpl--MQProcess, end");
    }

    private AsyncMessageEntity createMessage(String messageType,JSONObject messageBody,String uniqueNo){
        AsyncMessageEntity message = new AsyncMessageEntity();
        // 生产者application name
        message.setFromServerName(applicationName);
        // 消费者application name
        message.setToServerName("order".equals(messageBody.getString("type")) ? RetryMessageConstant.SETTLE_CENTER_APPLICATION_NAME : applicationName);
        // 消息队列的topic
        message.setTopic(ttqfCallbackTopic);
        // 消息队列的tag
        message.setMsgTag(messageBody.getString("type"));
        // 定义的业务消息类型
        message.setMsgType(messageType);
        // 消息内容
        message.setMsgBody(JSONObject.toJSONString(messageBody));
        // 业务的唯一序列号
        message.setUniqueNo(uniqueNo);
        return message;
    }


}
