package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-18 10:09
 * @email 598482054@qq.com
 */
@Data
public class UnionPayLoanUserRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统生成的唯一的电子账簿ID
     */
    private String balanceAcctId;
    /**
     * 账户名称
     */
    private String balanceAcctName;

    private String busId;

}
