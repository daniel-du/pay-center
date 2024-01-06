package com.tfjt.pay.external.unionpay.mq;

import cn.hutool.json.JSONUtil;
import com.aliyun.openservices.ons.api.*;
import com.tfjt.consumer.SingleConsumerRetryJob;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.pay.external.unionpay.config.ALiYunRocketMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * @Author zxy
 * @create 2024/1/6 8:54
 * 商户信息变更mq接收
 */
@Component
@Slf4j
public class MqConfig implements ApplicationContextAware {

//    @Value("${rocketmq.accessKey}")
//    private String accessKey;
//    @Value("${rocketmq.accessSecret}")
//    private String accessSecret;
//    @Value("${rocketmq.endpoints}")
//    private String endpoints;
    @Value("${rocketmq.topic.dealerChange}")
    private String dealerChangeTopic;
    @Value("${rocketmq.group.consumer.dealerChange}")
    private String dealerChangeGroup;
    @Value("${async-retry-job.product.updateMsgUrl}")
    private String updateMsgUrl;


    private ApplicationContext applicationContext;

    @Autowired
    private ALiYunRocketMQConfig aLiYunRocketMQConfig;


    @PostConstruct
    void consumer() {
        log.info("---------------mq消费啦！！！！！！！！！");
        Properties properties = new Properties();
//        properties.put(PropertyKeyConst.AccessKey, accessKey);
//        properties.put(PropertyKeyConst.SecretKey, accessSecret);
//        properties.put(PropertyKeyConst.NAMESRV_ADDR, endpoints);
        // 消费下单数据
        consumeOrder(aLiYunRocketMQConfig.getMqPropertie());
    }


    private void consumeOrder(Properties properties){
        properties.put(PropertyKeyConst.GROUP_ID, dealerChangeGroup);
        log.info("---------------------:{}",dealerChangeTopic);
        Consumer consumer = ONSFactory.createConsumer(properties);
        // 订阅另外一个Topic，如需取消订阅该Topic，请删除该部分的订阅代码，重新启动消费端即可。
        // 订阅全部Tag。
        consumer.subscribe(dealerChangeTopic, "*", (message, context) -> {
            log.info("OrderChangeConsumer_Receive: " + message);
            return processExport(message);
        });
        consumer.start();
    }



    private Action processExport(Message msg){
        try {
            final String message = new String(msg.getBody());
            AsyncMessageEntity asyncMessage = JSONUtil.toBean(message, AsyncMessageEntity.class);
            log.info("agency export consume message :{}", message);
            // SingleConsumerRetryJob，这里一定要通过applicationContext.getBean获取，不要直接通过
            SingleConsumerRetryJob retryJob = applicationContext.getBean("singleConsumerRetryJob",SingleConsumerRetryJob.class);
            // 配置“更新生产者消息状态”的接口url
           /* retryJob.setUpdateMsgUrl(updateMsgUrl)
                    // 开启幂等处理
                    .repeatEnable()
                    // 设置消费业务逻辑的处理方法
                    .bizFunc(orderSettleService::generateExportData)
                    // 设置幂等检查的方法
                    .checkFunc(null)
                    // 执行补偿任务
                    .executeMsg(asyncMessage);*/
        } catch (Exception e) {
            log.error("消费错误",e);
            return Action.CommitMessage;
        }
        return Action.CommitMessage;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}
