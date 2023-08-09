package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款进件公共返回参数
 */
@Data
public class IncomingReturn implements Serializable {
    private static final long serialVersionUID = 1L;
    private String applicationStatus;//	进件状态
    private String cusId;//	个人用户ID
    private String succeededAt;//	进件申请通过时间
    private String failedAt;//	进件申请失败时间
    private String failureMsgs;//	审核失败原因
    private String relAcctNo;//	电子账簿账簿号
    private String bindAcctName;//	银行生成的户名
    private String balanceAcctId;//	电子账簿ID
    private String settleAcctId;//	绑定账户ID
    private String outRequestNo;//	平台订单号
    private String cusApplicationId;//	系统订单号
    private String metadata;//	自定义参数
    private String extra;//	额外信息
    private String mediaId;//	图片ID
    private String mobileNumber;//	手机号
    private String mchApplicationId;//	系统单号
    private String requestId;//
    private Boolean isDeleted;



}
