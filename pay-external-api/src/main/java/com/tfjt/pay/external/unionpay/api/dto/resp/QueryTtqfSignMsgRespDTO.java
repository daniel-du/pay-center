package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/20 17:40
 * @description
 */
@Data
public class QueryTtqfSignMsgRespDTO implements Serializable {

    /**
     * 姓名
     */
    private String userName;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 银行卡号
     */
    private String bankCardNo;

    /**
     * 实名认证状态
     */
    private Integer authStatus;

    /**
     * 签约状态
     */
    private Integer signStatus;

    /**
     * 绑卡状态
     */
    private Integer bindStatus;


}
