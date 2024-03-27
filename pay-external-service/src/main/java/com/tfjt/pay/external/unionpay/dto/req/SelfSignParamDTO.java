package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class SelfSignParamDTO  implements Serializable {

    /**
     * 来源平台账户
     */
    private String accesserAcct;
    /**
     * 第三方用户唯一凭证
     */
    private String appId;

    /**
     * 商户号
     */
    private String mid;

    /**
     * 企业用户号
     */
    private String businessNo;

    private String signingStatus;

    /**
     * 返回信息
     */
    private String msg;
    /**
     * 主从关系绑定结果 “0”:绑定失败” “1”:”绑定成功
     */
    private String  merMsRelation;
}
