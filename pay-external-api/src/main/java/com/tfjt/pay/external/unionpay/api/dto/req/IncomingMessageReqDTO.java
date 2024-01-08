package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/25 15:25
 * @description 查询进件信息入参
 */
@Data
public class IncomingMessageReqDTO  implements Serializable {

    /**
     * 商户类型，1：供应商、经销商  2：云商
     */
    @NotNull(message = "商户类型不能为空")
    private Integer businessType;

    /**
     * 商户id
     */
    @NotNull(message = "商户id不能为空")
    private Long businessId;

    /**
     * 入网渠道类型，1：平安  2：银联
     */
    private Integer accessChannelType;

    /**
     * 商户所属区域
     */
    private String areaCode;
}
