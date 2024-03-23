package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/19 20:14
 * @description 天天企赋签约页面唤起请求返参
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TtqfContractRespDTO implements Serializable {

    /**
     * H5签约连接
     */
    private String signUrl;

    /**
     * 签约状态,0：未签约，1：已签约，2：签约中
     */
    private Integer signStatus;




}
