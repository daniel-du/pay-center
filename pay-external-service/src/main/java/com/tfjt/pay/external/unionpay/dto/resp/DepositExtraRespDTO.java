package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * @author tony
 * @version 1.0
 * @title DepositExtraRespDTO
 * @description
 * @create 2023/10/19 13:52
 */
@Data
public class DepositExtraRespDTO {

    /**
     * 扩展字段详情
     */
    List<ProductInfoRespDTO> productInfos;

}
