package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/19 19:29
 * @description 天天企赋签约页面唤起请求入参
 */
@Data
public class TtqfContractReqDTO implements Serializable {

    /**
     * 会员id
     */
    private Long businessId;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 回调页面url
     */
    private String mchReturnUrl;
}
