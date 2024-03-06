package com.tfjt.pay.external.unionpay.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 银联贷款公共请求参数
 */
@SuperBuilder
@Data
public class UnionPayLoansBaseReq implements Serializable {
    private static final long serialVersionUID = 1L;
    //	交易码
    private String transCode;
    //	版本号
    private String verNo;
    //	请求系统日期
    private String srcReqDate;
    //	请求系统时间
    private String srcReqTime;
    //	请求系统流水号
    private String srcReqId;
    //	渠道号
    private String channelId;
    //	集团号
    private String groupId;
    //交易渠道
    private String lwzBussCode;
    //	资管通业务信息
    private String lwzData;
    //	资管通业务类型
    private String lwzChannelType;
    //	签名
    private String signature;
    //	签名
    private String merNo;
}
