package com.tfjt.pay.external.unionpay.dto.req;

import com.alibaba.fastjson.annotation.JSONField;
import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Lzh
 * @version 1.0
 * @title TransactionCallBack
 * @description 交易类回调实体
 * @Date 2023/8/14 15:20
 */
@Data
public class TransactionCallBackReqDTO implements Serializable {
    /**事件ID*/
    @JSONField(name = "event_id")
    private String eventId;

    /**事件类型*/
    @JSONField(name = "event_type")
    private String eventType;

    /**事件内容*/
    @JSONField(name = "event_data")
    private EventDataDTO eventData;

    /**事件创建时间*/
    @JSONField(name = "created_at")
    private String createdAt;


}
