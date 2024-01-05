package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.tfjt.constant.MessageStatusEnum;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingMessageReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.biz.IncomingBizService;
import com.tfjt.pay.external.unionpay.constants.RetryMessageConstant;
import com.tfjt.pay.external.unionpay.dto.CheckCodeMessageDTO;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.dto.message.IncomingFinishDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingCheckCodeReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSubmitMessageReqDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.entity.TfIncomingSettleInfoEntity;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessChannelTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingSettleTypeEnum;
import com.tfjt.pay.external.unionpay.service.IncomingBindCardService;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.tfjt.pay.external.unionpay.service.TfIncomingSettleInfoService;
import com.tfjt.producter.ProducerMessageApi;
import com.tfjt.producter.service.AsyncMessageService;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:07
 * @description
 */
@Slf4j
@Service
public class IncomingBizServiceImpl implements IncomingBizService {

    @Autowired
    private Map<String, IncomingBindCardService> incomingBindCardServiceMap;

    @Autowired
    private TfIncomingInfoService tfIncomingInfoService;

    @Autowired
    private TfIncomingSettleInfoService tfIncomingSettleInfoService;

    @Autowired
    private ProducerMessageApi producerMessageApi;

    @Autowired
    private AsyncMessageService asyncMessageService;


    @Value("")
    private String incomingFinishTopic;

    private static final String MQ_FROM_SERVER = "tf-cloud-pay-center";

    private static final String MQ_TO_SERVER = "tf-cloud-shop";


    @Override
    public Result incomingSubmit(IncomingSubmitMessageReqDTO incomingSubmitMessageReqDTO) {
        log.info("IncomingBizServiceImpl--incomingSubmit, incomingSubmitMessageReqDTO:{}", JSONObject.toJSONString(incomingSubmitMessageReqDTO));
        //查询提交进件申请所需信息
        IncomingSubmitMessageDTO incomingSubmitMessageDTO =
                tfIncomingInfoService.queryIncomingMessage(incomingSubmitMessageReqDTO.getIncomingId());
        //根据参数类型获取实现类
        String bindServiceName = getServiceName(incomingSubmitMessageDTO);

        IncomingBindCardService incomingBindCardService = incomingBindCardServiceMap.get(bindServiceName);
        //调用实现类方法
        incomingBindCardService.incomingSubmit(incomingSubmitMessageDTO);
        //更新进件信息

        return Result.ok();
    }

    @Override
    public Result checkCode(IncomingCheckCodeReqDTO inComingCheckCodeReqDTO) {
        log.info("IncomingBizServiceImpl--checkCode, incomingSubmitMessageReqDTO:{}", JSONObject.toJSONString(inComingCheckCodeReqDTO));
        IncomingSubmitMessageDTO incomingSubmitMessageDTO =
                tfIncomingInfoService.queryIncomingMessage(inComingCheckCodeReqDTO.getIncomingId());
        //根据进件信息类型数据获取对应实现
        String bindServiceName = getServiceName(incomingSubmitMessageDTO);
        IncomingBindCardService incomingBindCardService = incomingBindCardServiceMap.get(bindServiceName);

        CheckCodeMessageDTO checkCodeMessageDTO = CheckCodeMessageDTO.builder()
                .id(incomingSubmitMessageDTO.getId())
                .memberId(incomingSubmitMessageDTO.getMemberId())
                .accountNo(incomingSubmitMessageDTO.getAccountNo())
                .bankCardNo(incomingSubmitMessageDTO.getBankCardNo())
                .authAmt(inComingCheckCodeReqDTO.getAuthAmt())
                .messageCheckCode(inComingCheckCodeReqDTO.getMessageCheckCode())
                .ipAddress(inComingCheckCodeReqDTO.getIpAddress())
                .macAddress(inComingCheckCodeReqDTO.getMacAddress()).build();
        //调用实现类方法
        incomingBindCardService.checkCode(checkCodeMessageDTO);
        //异步发送mq-进件完成事件
        MQProcess(incomingSubmitMessageDTO);
        //更新进件信息
        return Result.ok();
    }

    /**
     * 根据商户信息查询进件信息
     * @param incomingMessageReqDTO
     * @return
     */
    @Override
    public Result<IncomingMessageRespDTO> queryIncomingMessage(IncomingMessageReqDTO incomingMessageReqDTO) {
        log.info("IncomingBizServiceImpl--queryIncomingMessage, incomingMessageReqDTO:{}", JSONObject.toJSONString(incomingMessageReqDTO));
        ValidatorUtils.validateEntity(incomingMessageReqDTO);
        IncomingMessageRespDTO incomingMessageRespDTO = tfIncomingInfoService.queryIncomingMessageByMerchant(incomingMessageReqDTO);
        if (ObjectUtils.isEmpty(incomingMessageRespDTO)) {
            return Result.ok();
        }
        //如果结算类型为对公，会员名称返回“营业名称”，否则返回“法人姓名”
        if (IncomingSettleTypeEnum.CORPORATE.getCode().equals(incomingMessageRespDTO.getSettlementAccountType())) {
            incomingMessageRespDTO.setMemberName(incomingMessageRespDTO.getBusinessName());
        } else {
            incomingMessageRespDTO.setMemberName(incomingMessageRespDTO.getLegalName());
        }
        return Result.ok(tfIncomingInfoService.queryIncomingMessageByMerchant(incomingMessageReqDTO));
    }

    /**
     * 根据进行信息获取实现类name
     * @param incomingSubmitMessageDTO
     * @return
     */
    private String getServiceName(IncomingSubmitMessageDTO incomingSubmitMessageDTO) {
        //根据进件信息类型数据获取对应实现
        String bindServiceName = IncomingAccessChannelTypeEnum.getNameFromCode(incomingSubmitMessageDTO.getAccessChannelType()) +
                "_" + IncomingAccessTypeEnum.getNameFromCode(incomingSubmitMessageDTO.getAccessType()) +
                "_" + IncomingSettleTypeEnum.getNameFromCode(incomingSubmitMessageDTO.getSettlementAccountType());
        return bindServiceName;
    }

    private AsyncMessageEntity createMessage(String messageType,String messageBody,String uniqueNo){
        AsyncMessageEntity message = new AsyncMessageEntity();
        // 生产者application name
        message.setFromServerName(MQ_FROM_SERVER);
        // 消费者application name
        message.setToServerName(MQ_TO_SERVER);
        // 消息队列的topic
        message.setTopic(incomingFinishTopic);
        // 消息队列的tag
        message.setMsgTag("");
        // 定义的业务消息类型
        message.setMsgType(messageType);
        // 消息内容
        message.setMsgBody(messageBody);
        // 业务的唯一序列号
        message.setUniqueNo(uniqueNo);
        return message;
    }

    /**
     * 异步发送消息
     * @param incomingMessage
     */
    @Async
    public void MQProcess(IncomingSubmitMessageDTO incomingMessage){
        IncomingFinishDTO incomingFinishDTO = IncomingFinishDTO.builder()
                .id(incomingMessage.getId())
                .accessChannelType(incomingMessage.getAccessChannelType())
                .accessMainType(incomingMessage.getAccessMainType())
                .accountNo(incomingMessage.getAccountNo()).build();
        // 创建消息
        AsyncMessageEntity messageEntity = createMessage(RetryMessageConstant.INCOMING_FINISH, JSONObject.toJSONString(incomingFinishDTO), null);
        // 调用jar包中保存消息到数据库的方法
        asyncMessageService.saveMessage(messageEntity);
        // rocketMQ发送消息自行实现
        boolean result = producerMessageApi.sendMessage(messageEntity.getTopic(), JSONUtil.toJsonStr(messageEntity),messageEntity.getUniqueNo(),
                messageEntity.getMsgTag());
        //更新状态为成功
        messageEntity.setStatus(result ? MessageStatusEnum.SUCCESS.getCode() : MessageStatusEnum.FAILED.getCode());
        asyncMessageService.updateMessageStatus(messageEntity);
    }
}
