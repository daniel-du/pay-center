package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: UnionPayLoanBussCodeEnum <br>
 * @date: 2023/5/23 13:58 <br>
 * @author: young <br>
 * @version: 1.0
 */

@AllArgsConstructor
@Getter
public enum UnionPayLoanBussCodeEnum {

    /**
     * 个人进件
     */
    LWZ51_PERSON_APP("LWZ51_PERSON_APP","个人进件"),

    /**
     * 二级进件
     */
    LWZ56_MCH_APP("LWZ56_MCH_APP","二级进件"),

    LWZ54_CUS_APPLICATIONS_RENEW("LWZ54_CUS_APPLICATIONS_RENEW","个人用户信息修改"),

    LWZ59_MCH_APPLICATIONS_RENEW("LWZ59_MCH_APPLICATIONS_RENEW","二级商户信息修改"),
    /**
     * 个人用户进件状态查询
     */
    LWZ53_PERSON_APP_REQ("LWZ53_PERSON_APP_REQ","个人用户进件状态查询"),

    /**
     * 二级用户进件状态查询
     */
    LWZ58_MCH_APP_REQ("LWZ58_MCH_APP_REQ","二级用户进件状态查询"),


    LWZ55_PERSONAL_VALIDATION_SMS_CODES("LWZ55_PERSONAL_VALIDATION_SMS_CODES","个人手机号验证"),
    /**
     * 图片上传
     */
    LWZ526_IMAGES("LWZ526_IMAGES","图片上传"),
    /**
     * 新增绑定账户
     */
    LWZ517_SETTLE_ACCT_ADD("LWZ517_SETTLE_ACCT_ADD","新增绑定账户"),
    /**
     *绑定账户查询(用户ID)
     */
    LWZ520_SETTLE_ACCTS_QUERY("LWZ520_SETTLE_ACCTS_QUERY","查询绑定账户"),

    LWZ519_SETTLE_ACCTS_QUERY("LWZ519_SETTLE_ACCTS_QUERY","查询绑定账户"),
    LWZ527_SETTLE_ACCTS_VALIDATE("LWZ527_SETTLE_ACCTS_VALIDATE","打款金额验证"),

    /**
     * 删除绑定账户(用户银行账号)
     */
    LWZ522_SETTLE_ACCTS_DELETE("LWZ522_SETTLE_ACCTS_DELETE","删除绑定账户");

    /**
     * code
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;
}
