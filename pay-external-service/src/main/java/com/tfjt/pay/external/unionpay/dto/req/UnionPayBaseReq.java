package com.tfjt.pay.external.unionpay.dto.req;

import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseReq;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 银联贷款公共请求参数
 */
@SuperBuilder
@Data
public class UnionPayBaseReq  extends UnionPayLoansBaseReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private String transCode;//	交易码
    private String verNo;//	版本号
    private String srcReqDate;//	请求系统日期
    private String srcReqTime;//	请求系统时间
    private String srcReqId;//	请求系统流水号
    private String channelId;//	渠道号
    private String groupId;//	集团号
    private String lwzBussCode;//交易渠道
    private String lwzData;//	资管通业务信息
    private String lwzChannelType;//	资管通业务类型
    private String signature;//	签名
    private String merNo;//	签名
}
