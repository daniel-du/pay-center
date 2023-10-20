package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author tony
 * @version 1.0
 * @title DepositExtraRespDTO
 * @description
 * @create 2023/10/19 13:52
 */
@Data
public class DepositExtraRespDTO implements Serializable {

    /**
     * 扩展字段详情
     */
    List<ProductInfoRespDTO> productInfos;

}
