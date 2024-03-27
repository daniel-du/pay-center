package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款公共返回参数
 */
@Data
public class UnionPayLoansBaseReturn implements Serializable {
    private static final long serialVersionUID = 1L;
    //	交易码
    private String transCode;
    //	版本号
    private String verNo;
    //	系统日期
    private String srcReqDate;
    //	系统时间
    private String srcReqTime;
    //	请求系统流水号
    private String srcReqId;
    //	请求结果
    private String respCode;
    //	错误信息
    private String respMsg;
    //	资管通业务反馈码
    private String respLwzCode;
    //错误描述
    private String respLwzMsg;
    //	资管通业务信息
    private String lwzRespData;
    //交易渠道
    private String lwzBussCode;
    //资管通业务渠道
    private String lwzChannelType;
    //	签名
    private String signature;
}
