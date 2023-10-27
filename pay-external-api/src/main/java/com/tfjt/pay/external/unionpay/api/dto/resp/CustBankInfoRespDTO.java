package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tony
 * @version 1.0
 * @title CustBankInfoRespDTO
 * @description
 * @create 2023/8/17 11:26
 */
@Data
public class CustBankInfoRespDTO implements Serializable {
    private Integer id;

    /**
     * 支行
     */
    private String bankName;


    /**
     * 卡号
     */
    private String bankCardNo;

    /**
     * 总行
     */
    private String  bigBankName;

    /**
     * 开户名称
     */
    private String accountName;

    /**
     * 结算类型 1个人 2企业
     */
    private Integer settlementType;

    /**
     * 打款验证
     * @return
     */
    private Integer validateStatus;

    private String settleAcctId;

}
