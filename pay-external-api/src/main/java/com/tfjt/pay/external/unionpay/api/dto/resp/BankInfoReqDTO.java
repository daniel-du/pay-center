package com.tfjt.pay.external.unionpay.api.dto.resp;

import com.tfjt.pay.external.unionpay.api.dto.UserTypeDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @title 添加银行卡
 * @description
 * @author tony
 * @version 1.0
 * @create 2023/8/12 14:21
 */
@Data
public class BankInfoReqDTO extends UserTypeDTO implements Serializable {
    /**
     * 银行卡号
     */
    private String bankCardNo;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 验证码
     */
    private String smsCode;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 开户银行编码
     */
    private String bankCode;
    /**
     * 开户银行联行号
     */
    private String bankBranchCode;

    /**
     *
     */
    private String settlementType;
    /**
     * 开户行
     */
    private String accountName;

 }
