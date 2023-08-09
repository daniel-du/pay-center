package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 事件内容
 */
@Data
public class TwoIncomingEventDataDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String applicationStatus; //进件状态
    private Date acctVerifiedAt;//账户验证通过时间
    private Date auditedAt;//审核通过时间
    private Date succeededAt;//进件申请通过时间
    private Date failedAt;//进件申请失败时间
    private List<LwzRespReturn> failureMsgs;//驳回消息数组
    private String outRequestNo; //平台订单号
    private String mchApplicationId; //系统订单号
    private String balanceAcctId; //电子账簿ID
    private String relAcctNo; //电子账簿账簿号
    private String settleAcctId; //电子账簿账簿号
    private String metadata; //电子账簿账簿号
    private String  mchId; //二级商户ID

}
