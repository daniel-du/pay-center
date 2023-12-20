package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 9:43
 * @description 进件-查询商户结算信息出参
 */
@Data
public class IncomingSettleRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结算信息id
     */
    private Long id;

    /**
     * 进件id
     */
    private Long incomingId;

    /**
     * 结算账户类型，1：对公，2：对私
     */
    private Byte settlementAccountType;

    /**
     * 银行卡id
     */
    private Long bankCardId;

    /**
     * 开户名称
     */
    private String bankAccountName;
    /**
     * 联行号
     */
    private String bankBranchCode;
    /**
     * 银行预留手机号
     */
    private String bankCardMobile;
    /**
     * 银行卡号
     */
    private String bankCardNo;
    /**
     * 银行卡照片
     */
    private String bankCardUrl;
    /**
     * 开户银行编码
     */
    private String bankCode;
    /**
     * 开户总行名称
     */
    private String bankName;
    /**
     * 开户支行名称
     */
    private String bankSubBranchName;
    /**
     * 超级网银号
     */
    private String eiconBankBranch;

    /**
     * 开户行所在地-省code
     */
    private String openAccountProvince;
    /**
     * 开户行所在地-省名称
     */
    private String openAccountProvinceName;
    /**
     * 开户行所在地-市code
     */
    private String openAccountCity;
    /**
     * 开户行所在地-市名称
     */
    private String openAccountCityName;
    /**
     * 开户行所在地-区code
     */
    private String openAccountDistrict;
    /**
     * 开户行所在地-区名称
     */
    private String openAccountDistrictName;

    /**
     * 职业
     */
    private String occupation;


}
