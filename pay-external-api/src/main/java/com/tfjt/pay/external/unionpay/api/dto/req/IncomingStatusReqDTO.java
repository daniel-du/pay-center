package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/1/23 11:06
 * @description 查询进件状态入参
 */
@Data
public class IncomingStatusReqDTO implements Serializable {

    /**
     * 商户类型，1：供应商、经销商  2：云商
     */
    @NotNull(message = "商户类型不能为空")
    private Integer businessType;

    /**
     * 商户id集合
     */
    @NotEmpty(message = "商户id集合不能为空")
    private List<Long> businessIds;
}
