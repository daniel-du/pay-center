package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/1/23 11:14
 * @description 查询进件状态出参
 */
@Data
public class IncomingStatusRespDTO implements Serializable {

    /**
     * 商户类型，1：供应商、经销商  2：云商
     */
    private Integer businessType;

    /**
     * 商户id
     */
    private Long businessId;

    /**
     * 入网状态
     */
    private String incomingStatus;
}
