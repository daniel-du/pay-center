package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @Date: 2023/11/28/17:47
 * @Description:  数字人民币查询商户信息接收参数
 */
@Data
public class DigitalSelectReqDTO implements Serializable {
    /**
     * 查询类型
     */
    private String queryType;
    /**
     * 账号信息
     */
    private String mchntSideAccount;
    /**
     * 授权码
     */
    private String authCode;
    /**
     * 密钥编号
     */
    private Integer keySn;
    /**
     * 商户信息
     */
    private String mrchntInfo;

}
