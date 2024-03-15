package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/13 10:11
 */
@Data
public class BusinessBasicInfoRespDTO implements Serializable {
    private static final long serialVersionUID = 3918368908452330102L;
    /**
     * 商户身份：商家、经销商、供应商
     */
    private String businessIdentity;
    /**
     * 商户ID
     */
    private String businessId;
    /**
     * 商户名称
     */
    private String businessName;
    /**
     * 进件类型
     */
    private String incomingType;
    /**
     * 商户号
     */
    private String businessNo;
    /**
     * 绑定结算账户
     */
    private String bindSettleAccount;
    /**
     * 绑定账户开户行
     */
    private String bindAccountBank;
    /**
     * 销售区域
     */
    private String salesArea;
    /**
     * 统一信用证代码
     */
    private String uniformSocialCreditCode;
    /**
     * 营业地址
     */
    private String tradeAddress;
    /**
     * 营业执照
     */
    private String businessLicenseUrl;


}
