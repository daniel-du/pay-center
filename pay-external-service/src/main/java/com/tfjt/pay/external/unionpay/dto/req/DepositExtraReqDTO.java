package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author tony
 * @version 1.0
 * @title DepositExtraRespDTO
 * @description
 * @create 2023/10/19 13:52
 */
@Data
public class DepositExtraReqDTO {

    /**
     * 扩展字段详情
     */
    List<ProductInfoReqDTO> productInfos;
    /**
     * 交易结果通知地址
     */
    private String notifyUrl;

}
