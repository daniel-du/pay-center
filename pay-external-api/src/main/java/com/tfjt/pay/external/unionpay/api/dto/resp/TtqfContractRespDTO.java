package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/19 20:14
 * @description 天天企赋签约页面唤起请求返参
 */
@Data
public class TtqfContractRespDTO implements Serializable {

    /**
     * H5签约连接
     */
    private Integer signUrl;

    /**
     * 业务状态码
     */
    private String bizCode;

    /**
     * 成功信息或失败原因
     */
    private String bizMsg;


}
