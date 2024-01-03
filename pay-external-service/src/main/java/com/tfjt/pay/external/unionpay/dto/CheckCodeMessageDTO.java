package com.tfjt.pay.external.unionpay.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/20 19:34
 * @description 回填验证码、打款金额参数实体
 */
@Data
@Builder
public class CheckCodeMessageDTO {

    /**
     * 进件id
     */
    private Long id;

    /**
     * 会员id
     */
    private String memberId;

    /**
     * 子账户号
     */
    private String accountNo;

    /**
     * 结算银行卡
     */
    private String bankCardNo;

    /**
     * 短信验证码
     */
    private String messageCheckCode;

    /**
     * 打款金额
     */
    private BigDecimal authAmt;

    /**
     * 签约渠道
     */
    private Byte signChannel;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * MAC地址
     */
    private String macAddress;
}
