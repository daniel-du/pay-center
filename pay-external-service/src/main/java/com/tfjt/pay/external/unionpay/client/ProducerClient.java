package com.tfjt.pay.external.unionpay.client;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.tfjt.pay.external.unionpay.config.ALiYunRocketMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProducerClient {


    @Autowired
    private ALiYunRocketMQConfig aLiYunRocketMQConfig;


    /**
     * 普通消息生产者
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean buildProducer() {
        ProducerBean producer = new ProducerBean();
        producer.setProperties(aLiYunRocketMQConfig.getMqPropertie());
        return producer;
    }

    /**
     * 顺序消息生产者
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public OrderProducerBean buildOrderProducer() {
        OrderProducerBean producer = new OrderProducerBean();
        producer.setProperties(aLiYunRocketMQConfig.getMqPropertie());
        return producer;
    }

    /**
     * 发送普通消息
     * @param topic
     * @param msg
     * @param msgKey
     * @param tag
     * @return
     */
    public SendResult sendMessage(String topic, String msg, String msgKey, String tag){
        Message message = new Message(topic, tag, msgKey, msg.getBytes());
        return buildProducer().send(message);
    }



}
