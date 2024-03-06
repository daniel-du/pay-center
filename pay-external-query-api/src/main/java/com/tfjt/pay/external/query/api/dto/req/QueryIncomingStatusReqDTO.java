package com.tfjt.pay.external.query.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/2/29 11:06
 * @description 查询进件状态入参
 */
@Data
public class QueryIncomingStatusReqDTO implements Serializable {

    /**
     * 商户类型，1：供应商、经销商  2：云商
     */
    @NotNull(message = "商户类型不能为空")
    private Integer businessType;

    /**
     * 商户id集合
     */
    @NotNull(message = "商户id不能为空")
    private Long businessId;

    /**
     * 区域-区编号-与“区域-区编号集合”参数二选一
     */
    private String areaCode;

    /**
     * 区域-区编号集合-与“区域-区编号”参数二选一
     */
    private List<String> areaCodes;
}
