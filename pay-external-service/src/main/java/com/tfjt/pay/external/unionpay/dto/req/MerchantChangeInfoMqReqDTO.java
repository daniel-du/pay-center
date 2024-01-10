package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.util.List;

/**
 * @Author zxy
 * @create 2024/1/8 9:49
 */
@Data
public class MerchantChangeInfoMqReqDTO {

    /**
     * 经销商ID
     */
    private Long supplierId;
    /**
     * 变更前销售区域
     */
    private List<String> oldSaleAreas;
    /**
     * 变更后销售区域
     */
    private List<String> newSaleAreas;
    /**
     * 变更前身份
     */
    private List<Integer> oldIdentifyList;
    /**
     * 变更后身份
     */
    private List<Integer> newIdentifyList;

    private Long creator;

    private String userName;
}
