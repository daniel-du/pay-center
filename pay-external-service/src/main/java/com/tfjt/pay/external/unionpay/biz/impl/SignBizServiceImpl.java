package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.openservices.shade.org.apache.commons.lang3.StringEscapeUtils;
import com.tfjt.dto.response.Result;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.pay.external.unionpay.biz.SignBizService;
import com.tfjt.pay.external.unionpay.dto.req.SelfSignAppDTO;
import com.tfjt.pay.external.unionpay.dto.req.SelfSignParamDTO;
import com.tfjt.pay.external.unionpay.dto.req.SigningReviewReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.SigningReviewRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayResult;
import com.tfjt.pay.external.unionpay.entity.PayApplicationCallbackUrlEntity;
import com.tfjt.pay.external.unionpay.entity.SelfSignEntity;
import com.tfjt.pay.external.unionpay.entity.SigningReviewLogEntity;
import com.tfjt.pay.external.unionpay.enums.ExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.PayCallbackLogService;
import com.tfjt.pay.external.unionpay.service.SelfSignService;
import com.tfjt.pay.external.unionpay.service.SigningReviewLogService;
import com.tfjt.pay.external.unionpay.service.UnionPayCallbackUrlService;
import com.tfjt.pay.external.unionpay.constants.RetryMessageConstant;
import com.tfjt.pay.external.unionpay.utils.DESUtil;
import com.tfjt.producter.ProducerMessageApi;
import com.tfjt.producter.service.AsyncMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author tony
 * @version 1.0
 * @title SignBizServiceImpl
 * @description
 * @create 2024/2/5 15:36
 */
@Service
@Slf4j
public class SignBizServiceImpl implements SignBizService {

    @Value("${unionPay.sign.appId}")
    private String appId;

    @Value("${unionPay.sign.appKey}")
    private String appKey;


    @Value("${unionPay.signgys.appId}")
    private String ysAppId;


    @Value("${unionPay.signgys.appKey}")
    private String ysAppKey;


    @Value("${unionPay.signgys.payAppId}")
    private String ysPayAppId;

    @Value("${spring.profiles.active}")
    private String env;


    @Resource
    private SigningReviewLogService signingReviewLogService;

    @Resource
    private UnionPayCallbackUrlService unionPayCallbackUrlService;

    @Resource
    private SelfSignService selfSignService;

    @Resource
    private PayCallbackLogService payCallbackLogService;


    @Resource
    private ProducerMessageApi producerMessageApi;
    @Resource
    private AsyncMessageService asyncMessageService;

    @Value("${rocketmq.topic.signingReviewTopic}")
    private String signingReviewTopic;


    /**
     * @param signData
     * @param jsonData
     * @param accesserId
     * @return
     */
    @Override
    public UnionPayResult signingReviewCreateMessage(String signData, String jsonData, String accesserId) {
        if (Objects.isNull(signData)) {
            log.error("sign_data不能为空");
            return new UnionPayResult().setResCode("9999").setResMsg("sign_data不能为空");
        }
        if (Objects.isNull(jsonData)) {
            log.error("json_data不能为空");
            return new UnionPayResult().setResCode("9999").setResMsg("sign_data不能为空");
        }
        if (Objects.isNull(accesserId)) {
            log.error("accesser_id不能为空");
            return new UnionPayResult().setResCode("9999").setResMsg("accesser_id不能为空");
        }
        try {
            SigningReviewReqDTO signingReviewReqDTO = new SigningReviewReqDTO();
            signingReviewReqDTO.setSignData(signData);
            signingReviewReqDTO.setJsonData(jsonData);
            signingReviewReqDTO.setAccesserId(accesserId);
            AsyncMessageEntity messageEntity = createMessage(JSON.toJSONString(signingReviewReqDTO), UUID.randomUUID().toString());
            // 调用jar包中保存消息到数据库的方法
            asyncMessageService.saveMessage(messageEntity);
            // rocketMQ发送消息自行实现
            producerMessageApi.sendMessage(messageEntity.getTopic(), JSON.toJSONString(messageEntity), messageEntity.getUniqueNo(),
                    messageEntity.getMsgTag());
        } catch (Exception ex) {
            log.error("保存入网消息异常", ex);
            return new UnionPayResult().setResCode("9999").setResMsg("服务异常");
        }

        return new UnionPayResult().setResCode("0000").setResMsg("成功");
    }


    /**
     * 消费入网mq消息
     *
     * @param asyncMessageEntity
     * @return
     */
    @Override
    public Result<String> signingReview(AsyncMessageEntity asyncMessageEntity) {
        SigningReviewReqDTO signingReviewReqDTO = JSON.parseObject(asyncMessageEntity.getMsgBody(), SigningReviewReqDTO.class);
        SigningReviewLogEntity signingReviewLogEntity = new SigningReviewLogEntity();
        signingReviewLogEntity.setSignData(signingReviewReqDTO.getSignData())
                .setJsonData(signingReviewReqDTO.getJsonData())
                .setAccesserId(signingReviewReqDTO.getAccesserId())
                .setEnv(env);
        signingReviewLogService.save(signingReviewLogEntity);
        //解密jsonData
        String data = null;
        if (Objects.equals(signingReviewReqDTO.getAccesserId(), ysAppId)) {
            try {
                data = DESUtil.decrypt(signingReviewReqDTO.getJsonData(), ysAppKey);
            } catch (Exception ex) {
                log.error("解密失败", ex);
                return Result.failed(ExceptionCodeEnum.SIGN_DECRYPT_ERROR.getMsg());
            }
        }

        if (Objects.equals(signingReviewReqDTO.getAccesserId(), appId)) {
            try {
                data = DESUtil.decrypt(signingReviewReqDTO.getJsonData(), appKey);
            } catch (Exception ex) {
                log.error("解密失败", ex);
                return Result.failed(ExceptionCodeEnum.SIGN_DECRYPT_ERROR.getMsg());
            }
        }
        log.info("解密后的数据：{}", data);
        signingReviewLogEntity.setData(data);
        signingReviewLogService.updateById(signingReviewLogEntity);


        SelfSignParamDTO selfSignParamDTO = null;
        if (Objects.nonNull(data)) {

            SigningReviewRespDTO signingReviewRespDTO = JSON.parseObject(data, SigningReviewRespDTO.class);
            //获取入网信息
            SelfSignEntity selfSignEntity = selfSignService.querySelfSignByAccessAcct(signingReviewRespDTO.getAccesserAcct());
            selfSignParamDTO = new SelfSignParamDTO();
            //将推送的入网状态写入参数
            selfSignParamDTO.setSigningStatus(signingReviewRespDTO.getApplyStatus());
            selfSignParamDTO.setMid(selfSignEntity.getMid());
            selfSignParamDTO.setBusinessNo(selfSignEntity.getBusinessNo());
            //失败原因
            selfSignParamDTO.setMsg(signingReviewRespDTO.getFailReason());
            if (StringUtils.isNotBlank(signingReviewRespDTO.getMerMsRelation()) && Objects.equals(appId, ysPayAppId)) {
                selfSignParamDTO.setMerMsRelation(getMerMsRelation(signingReviewRespDTO.getMerMsRelation()));
                //当供应商没有关联成功时，入网状态修改审核中
                if (Objects.equals(selfSignParamDTO.getSigningStatus(), "03") && !Objects.equals(getMerMsRelation(signingReviewRespDTO.getMerMsRelation()), "1")) {
                    selfSignParamDTO.setSigningStatus("02");
                }
            }
        }
        //处理业务数据
        if (Objects.nonNull(selfSignParamDTO)) {
            log.info("供应商入网状态修改审核中，入参：{}", JSON.toJSONString(selfSignParamDTO));
            updateSignStatus(selfSignParamDTO);
        }
        return Result.ok();
    }


    /**
     * 更新业务入网状态
     *
     * @param selfSignParamDTO
     */
    private void updateSignStatus(SelfSignParamDTO selfSignParamDTO) {

        SelfSignEntity selfSignEntity = selfSignService.selectByMid(selfSignParamDTO.getMid());
        //设置失败原因
        selfSignEntity.setMsg(selfSignParamDTO.getMsg());
        //设置
        selfSignEntity.setSigningStatus(selfSignParamDTO.getSigningStatus());
        //rpc调用业务
        RestTemplate restTemplate = new RestTemplate();
        //回调地址查询
        List<PayApplicationCallbackUrlEntity> callbackUrlList = unionPayCallbackUrlService.getCallbackUrlByAppIdAndType(6);

        if (CollUtil.isNotEmpty(callbackUrlList)) {
            for (PayApplicationCallbackUrlEntity callbackUrlEntity : callbackUrlList) {
                List<SelfSignParamDTO> selfSignParamDTOList = new ArrayList<>();
                selfSignParamDTO.setAppId(callbackUrlEntity.getAppId());
                selfSignParamDTO.setAccesserAcct(selfSignEntity.getAccesserAcct());
                selfSignParamDTOList.add(selfSignParamDTO);
                SelfSignAppDTO selfSignAppDTO = new SelfSignAppDTO();
                selfSignAppDTO.setAppId(callbackUrlEntity.getAppId());
                selfSignAppDTO.setSelfSignParamDTOList(selfSignParamDTOList);
                Date r = new Date();
                //发起请求
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set("Content-Type", "application/json");
                HttpEntity<String> stringHttpEntity = new HttpEntity<>(JSON.toJSONString(selfSignAppDTO), httpHeaders);
                String rest = "";
                try {
                    log.info("请求入网URL" + callbackUrlEntity.getUrl());
                    log.info("请求入网回调参数" + JSON.toJSONString(selfSignAppDTO));
                    rest = restTemplate.postForObject(callbackUrlEntity.getUrl(), stringHttpEntity, String.class);
                    if (StringUtils.isNotBlank(rest)) {
                        JSONObject json = JSONObject.parseObject(rest);
                        if (Objects.equals("0", json.get("code").toString())) {
                            //更新入网签约
                            log.info("入网表参数：{}",JSON.toJSONString(selfSignEntity));
                            selfSignService.updateById(selfSignEntity);
                        }
                    } else {
                        log.error("返回接口为空---");
                    }
                } catch (Exception e) {
                    log.error("远程调用业务接口失败", e);
                }
                payCallbackLogService.saveCallBackLog(callbackUrlEntity.getUrl(), appId, JSON.toJSONString(selfSignAppDTO), rest, 4, r, "");
            }
        }

    }


    /**
     * 获取89813015499AQ1G绑定结果
     *
     * @param merMsRelation
     * @return
     */
    private static String getMerMsRelation(String merMsRelation) {
        String status = "0";
        if (StringUtils.isNotBlank(merMsRelation)) {
            merMsRelation = StringEscapeUtils.unescapeJava(merMsRelation);
            Map map = JSON.parseObject(merMsRelation, new TypeReference<Map>() {
            });
            String aQ1G = (String) map.get("7756962ec46748c09ed69a4cbc93db53");
            String aQ14 = (String) map.get("2d9081bd83cb5ab80183f85c81404690");
            if (Objects.equals(aQ1G, "1") && Objects.equals(aQ14, "1")) {
                status = "1";
            }
        }
        return status;
    }

    /**
     * 生产topic
     *
     * @param messageBody
     * @param uniqueNo
     * @return
     */
    private AsyncMessageEntity createMessage(String messageBody, String uniqueNo) {
        AsyncMessageEntity message = new AsyncMessageEntity();
        // 生产者application name
        message.setFromServerName(RetryMessageConstant.MQ_FROM_SERVER);
        // 消费者application name
        message.setToServerName(RetryMessageConstant.MQ_TO_SERVER);
        // 消息队列的topic
        message.setTopic(signingReviewTopic);
        // 消息队列的tag
        message.setMsgTag(RetryMessageConstant.SIGN_TAG);
        // 定义的业务消息类型
        message.setMsgType(RetryMessageConstant.SIGN_REVIEW);
        // 消息内容
        message.setMsgBody(messageBody);
        // 业务的唯一序列号
        message.setUniqueNo(uniqueNo);
        return message;
    }


    public static void main(String[] args) throws IllegalBlockSizeException, UnsupportedEncodingException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        //供应商
        String encrypt = DESUtil.encrypt("{\"accesser_acct\":\"6e5b6f6f-b8fe-41f0-ac00-cb0aa8ba0917\",\"apply_status\":\"04\",\"apply_status_msg\":\"入网失败\",\"fail_reason\":\"自主测试\",\"ums_reg_id\":\"02403040827007933399\"}", "978vna9lg3adfpkdjkcd2m0r");
        System.out.println("供应商加密结果:" + encrypt);
        //商户
        String shopEncrypt = DESUtil.encrypt("111", "7ghjyjl6xwwapg3ug43otlry");
        System.out.println("商户加密结果:" + encrypt);
    }
}
