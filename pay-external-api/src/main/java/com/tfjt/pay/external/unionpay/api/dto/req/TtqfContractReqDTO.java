package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @NotNull(message = "会员id不能为空")
    private Long businessId;

    private Integer businessType;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 回调页面url
     */
    @NotBlank(message = "回调url不能为空")
    private String mchReturnUrl;
}
