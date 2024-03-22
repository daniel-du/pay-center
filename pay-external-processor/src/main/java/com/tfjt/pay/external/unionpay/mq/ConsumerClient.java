package com.tfjt.pay.external.unionpay.mq;

import cn.hutool.json.JSONUtil;
import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.tfjt.consumer.SingleConsumerRetryJob;
import com.tfjt.dto.response.Result;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.pay.external.unionpay.biz.IncomingTtqfBizService;
import com.tfjt.pay.external.unionpay.biz.PabcBizService;
import com.tfjt.pay.external.unionpay.biz.SignBizService;
import com.tfjt.pay.external.unionpay.config.ALiYunRocketMQConfig;
import com.tfjt.pay.external.unionpay.constants.RetryMessageConstant;
import com.tfjt.pay.external.unionpay.dto.req.ShopExamineMqReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.ShopUpdateMqReqDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Properties;
import java.util.function.Function;

/**
 * @Author zxy
 * @create 2024/1/6 8:54
 * 商户信息变更mq接收
 */
@Component
@Slf4j
public class ConsumerClient implements ApplicationContextAware {


    @Value("${rocketmq.topic.dealerChange}")
    private String dealerChangeTopic;
    @Value("${rocketmq.group.consumer.dealerChange}")
    private String dealerChangeGroup;
    @Value("${async-retry-job.product.supply-merchant.updateMsgUrl}")
    private String merchantUpdateMsgUrl;
    @Value("${async-retry-job.product.sign.updateMsgUrl}")
    private String signUpdateMsgUrl;

    /**
     * 入网审核
     */
    @Value("${rocketmq.group.consumer.signingReview}")
    private String signingReviewGroup;
    @Value("${rocketmq.topic.signingReviewTopic}")
    private String signingReviewTopic;
    @Value("${rocketmq.topic.shopChange}")
    private String shopChangeTopic;
    @Value("${rocketmq.group.consumer.shopChange}")
    private String shopChangeGroup;
//    @Value("${async-retry-job.product.updateMsgUrl}")
//    private String updateMsgUrl;

    @Value("${rocketmq.group.consumer.ttqfCallback}")
    private String ttqfCallbackGroup;
    @Value("${rocketmq.topic.ttqfCallback}")
    private String ttqfCallbackTopic;



    private String shopExaminTag = "examine";
    private String shopUpdateTag = "update";
    private String shopchangeDistrictTag = "changeDistrict";


    private ApplicationContext applicationContext;

    @Autowired
    private ALiYunRocketMQConfig mqConfig;

    @Autowired
    private PabcBizService pabcBizService;

    @Resource
    private SignBizService signBizService;

    @Autowired
    private IncomingTtqfBizService incomingTtqfBizService;


    @PostConstruct
    void consumer() {
        // 消费下单数据
        consumeOrder(mqConfig.getMqPropertie());
        consumeShopChange(mqConfig.getMqPropertie());
        // 入网审核
        consumeSigningReview(mqConfig.getMqPropertie());
        // 天天企赋回调
        consumeTtqfCallback(mqConfig.getMqPropertie());
    }

    /**
     * 入网回调消费
     *
     * @param properties
     */
    private void consumeSigningReview(Properties properties) {
        properties.put(PropertyKeyConst.GROUP_ID, signingReviewGroup);
        Consumer consumer = ONSFactory.createConsumer(properties);
        signReviewByTag(consumer, RetryMessageConstant.SIGN_TAG);
        consumer.start();
    }

    private void signReviewByTag(Consumer consumer, String tag) {

        consumer.subscribe(signingReviewTopic, tag, (message, context) -> {
            log.info("MerchantChangeConsumer_Receive: " + message);
            return processSignReview(message);
        });
    }


    private Action processSignReview(Message msg) {
        return commitMsg(msg, signBizService::signingReview, signUpdateMsgUrl);
    }

    /**
     * 入网回调消费
     *
     * @param properties
     */
    private void consumeTtqfCallback(Properties properties) {
        properties.put(PropertyKeyConst.GROUP_ID, ttqfCallbackGroup);
        Consumer consumer = ONSFactory.createConsumer(properties);
        ttqfCallbackByTag(consumer, RetryMessageConstant.TTQF_PRESIGN_TAG);
        consumer.start();
    }

    private void ttqfCallbackByTag(Consumer consumer, String tag) {

        consumer.subscribe(signingReviewTopic, tag, (message, context) -> {
            log.info("MerchantChangeConsumer_Receive: " + message);
            return processTtqfCallback(message);
        });
    }


    private Action processTtqfCallback(Message msg) {
        return commitMsg(msg, incomingTtqfBizService::consumerCallbackMsg, signUpdateMsgUrl);
    }


    private Action commitMsg(Message msg, Function<AsyncMessageEntity, Result<String>> bizFunc, String updateMsgUrl) {
        try {
            String msgId = msg.getMsgID();
            log.info("msgID:{} ", msgId);
            String message = new String(msg.getBody());
            log.info("message: " + message);
            AsyncMessageEntity asyncMessage = JSONUtil.toBean(message, AsyncMessageEntity.class);
            log.info("pay-center supplier change info consume message :{}", message);
            // SingleConsumerRetryJob，这里一定要通过applicationContext.getBean获取，不要直接  通过
            SingleConsumerRetryJob retryJob = applicationContext.getBean("singleConsumerRetryJob", SingleConsumerRetryJob.class);
            // 配置“更新生产者消息状态”的接口url
            retryJob.setUpdateMsgUrl(updateMsgUrl)
                    // 开启幂等处理
                    .repeatEnableNoTrans()
                    // 设置消费业务逻辑的处理方法
                    .bizFunc(bizFunc)
                    // 设置幂等检查的方法
                    .checkFunc(null)
                    // 执行补偿任务
                    .executeMsg(asyncMessage);
        } catch (Exception e) {
            log.error("消费错误", e);
            return Action.CommitMessage;
        }
        return Action.CommitMessage;
    }


    private void consumeShopChange(Properties properties) {
        properties.put(PropertyKeyConst.GROUP_ID, shopChangeGroup);
        Consumer consumer = ONSFactory.createConsumer(properties);
        // 订阅另外一个Topic，如需取消订阅该Topic，请删除该部分的订阅代码，重新启动消费端即可。
        // 订阅Tag。
        shopChangeBYTag(consumer, shopExaminTag);
        shopChangeBYTag(consumer, shopUpdateTag);
        shopChangeBYTag(consumer, shopchangeDistrictTag);
        consumer.start();
    }

    private void shopChangeBYTag(Consumer consumer, String shopExaminTag) {
        consumer.subscribe(shopChangeTopic, shopExaminTag, (message, context) -> {
            log.info("MerchantChangeConsumer_Receive: " + message);
            return processShopChange(message, shopExaminTag);
        });
    }


    private void consumeOrder(Properties properties) {
        properties.put(PropertyKeyConst.GROUP_ID, dealerChangeGroup);
        Consumer consumer = ONSFactory.createConsumer(properties);
        // 订阅另外一个Topic，如需取消订阅该Topic，请删除该部分的订阅代码，重新启动消费端即可。
        // 订阅全部Tag。
        consumer.subscribe(dealerChangeTopic, "*", (message, context) -> {
            log.info("MerchantChangeConsumer_Receive: " + message);
            return processExport(message);
        });
        consumer.start();
    }

    private Action processShopChange(Message msg, String tag) {
        String msgID = msg.getMsgID();
        log.info("msgID: " + msgID);
        String message = new String(msg.getBody());
        log.info("message: " + message);
        if (tag.equals(shopExaminTag)) {
            ShopExamineMqReqDTO dto = JSONObject.parseObject(message, ShopExamineMqReqDTO.class);
            pabcBizService.saveShopExamineInfo(dto);
        }
        if (tag.equals(shopUpdateTag) || tag.equals(shopchangeDistrictTag)) {
            ShopUpdateMqReqDTO dto = JSONObject.parseObject(message, ShopUpdateMqReqDTO.class);
            pabcBizService.saveShopUpdateInfo(dto);
        }

        return Action.CommitMessage;
    }


    private Action processExport(Message msg) {
        return commitMsg(msg, pabcBizService::saveChangeInfo, merchantUpdateMsgUrl);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
