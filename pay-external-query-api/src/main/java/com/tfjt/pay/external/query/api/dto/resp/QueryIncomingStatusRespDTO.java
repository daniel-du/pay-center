package com.tfjt.pay.external.query.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/2/29 11:14
 * @description 查询进件状态出参
 */
@Data
public class QueryIncomingStatusRespDTO implements Serializable {

    /**
     * 商户类型，1：供应商、经销商  2：云商
     */
    private Integer businessType;

    /**
     * 商户id
     */
    private Long businessId;

    /**
     * 入网状态 00:未入网，03：已入网
     */
    private String incomingStatus;

    /**
     * 入网渠道类型，1：平安  2：银联，获取多渠道（区域）合并入网状态时，该字段不返回
     */
    private Integer accessChannelType;

    /**
     * 未入网原因
     */
    private String noIncomingReason;

    /**
     * 未入网标识，0：所有渠道均未入网，1：平安未入网，2：银联未入网
     */
    private Integer noIncomingFlag;
}
