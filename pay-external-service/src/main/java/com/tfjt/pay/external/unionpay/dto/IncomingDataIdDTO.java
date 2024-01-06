package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/1/5 20:21
 * @description 进件相关信息表id字段实体
 */
@Data
public class IncomingDataIdDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户身份信息id
     */
    private Long merchantInfoId;

    /**
     * 法人证件id
     */
    private Long legalId;

    /**
     * 经办人证件id
     */
    private Long agentId;

    /**
     * 营业信息id
     */
    private Long businessInfoId;

    /**
     * 营业执照信息id
     */
    private Long businessLicenseId;

    /**
     * 结算信息id
     */
    private Long setleInfoId;

    /**
     * 结算银行卡信息id
     */
    private Long bankCardId;
}
