package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款公共返回参数
 */
@Data
public class UnionPayLoansBaseReturn implements Serializable {
    private static final long serialVersionUID = 1L;
    private String transCode;//	交易码
    private String verNo;//	版本号
    private String srcReqDate;//	系统日期
    private String srcReqTime;//	系统时间
    private String srcReqId;//	请求系统流水号
    private String respCode;//	请求结果
    private String respMsg;//	错误信息
    private String respLwzCode;//	资管通业务反馈码
    private String respLwzMsg;//错误描述
    private String lwzRespData;//	资管通业务信息
    private String lwzBussCode;//交易渠道
    private String lwzChannelType;//资管通业务渠道
    private String signature;//	签名
}
