package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款进件公共返回参数
 */
@Data
public class IncomingReturn implements Serializable {
    private static final long serialVersionUID = 1L;
    //	进件状态
    private String applicationStatus;
    //	个人用户ID
    private String cusId;
    //	进件申请通过时间
    private String succeededAt;
    //	进件申请失败时间
    private String failedAt;
    //	审核失败原因
    private String failureMsgs;
    //	电子账簿账簿号
    private String relAcctNo;
    //	银行生成的户名
    private String bindAcctName;
    //	电子账簿ID
    private String balanceAcctId;
    //	绑定账户ID
    private String settleAcctId;
    //	平台订单号
    private String outRequestNo;
    //	系统订单号
    private String cusApplicationId;
    //	自定义参数
    private String metadata;
    //	额外信息
    private String extra;
    //	图片ID
    private String mediaId;
    //	手机号
    private String mobileNumber;
    //	系统单号
    private String mchApplicationId;
    //
    private String requestId;
    private Boolean isDeleted;



}
