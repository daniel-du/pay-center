package com.tfjt.pay.external.unionpay.dto;

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
    private String tradeId;

    /**平台订单号*/
    private String outOrderNo;

    /**处理完成时间*/
    private String finishedAt;

    /**交易创建时间*/
    private String createdAt;

    /**交易状态*/
    private String status;

    /**交易类型*/
    private Integer tradeType;

    private String channelTradeNo;


}
