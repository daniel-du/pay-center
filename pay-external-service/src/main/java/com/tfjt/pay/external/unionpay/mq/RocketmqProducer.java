package com.tfjt.pay.external.unionpay.mq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import com.tfjt.pay.external.unionpay.client.ProducerClient;
import com.tfjt.producter.ProducerMessageApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/1/5 9:47
 * @description
 */
@Slf4j
@Component
public class RocketmqProducer implements ProducerMessageApi {


    @Autowired
    private ProducerClient producerClient;


    @Override
    public Boolean sendMessage(String topic, String msg, String messageKey, String tag) {
//        Message message = new Message(topic,
//                tag, messageKey,
//                msg.getBytes(Charset.defaultCharset()));
        try {
            SendResult sendResult = producerClient.sendMessage(topic, msg, messageKey, tag);
            // 同步发送消息，只要不抛异常就是成功。
            if (sendResult != null) {
                log.info(new Date() + " Send mq message success. Topic is:" + topic + " msgId is: " + sendResult.getMessageId());
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理。
            log.error(new Date() + " Send mq message failed. Topic is:" + topic);
        }
        return Boolean.FALSE;
    }
}
