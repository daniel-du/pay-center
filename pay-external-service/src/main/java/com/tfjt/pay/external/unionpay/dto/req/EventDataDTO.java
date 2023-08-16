package com.tfjt.pay.external.unionpay.dto.req;

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
    private String trade_id;

    /**平台订单号*/
    private String out_order_no;

    /**处理完成时间*/
    private String finished_at;

    /**交易创建时间*/
    private String created_at;

    /**交易状态*/
    private String status;

    /**交易类型*/
    private Integer trade_type;

    private String channel_trade_no;


}
