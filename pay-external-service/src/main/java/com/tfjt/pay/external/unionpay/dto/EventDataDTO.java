package com.tfjt.pay.external.unionpay.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Lzh
 * @version 1.0
 * @title EventDataDTO
 * @description 交易类回调事件内容
 * @Date 2023/8/14 15:24
 */
@Data
public class EventDataDTO implements Serializable {
    /**系统订单号*/
    @JSONField(name = "trade_id")
    private String tradeId;

    /**平台订单号*/
    @JSONField(name = "out_order_no")
    private String outOrderNo;

    /**处理完成时间*/
    @JSONField(name = "finished_at")
    private String finishedAt;

    /**交易创建时间*/
    @JSONField(name = "created_at")
    private String createdAt;

    /**交易状态*/
    private String status;

    /**交易类型*/
    @JSONField(name = "trade_type")
    private Integer tradeType;

    @JSONField(name = "channel_trade_no")
    private String channelTradeNo;


}
