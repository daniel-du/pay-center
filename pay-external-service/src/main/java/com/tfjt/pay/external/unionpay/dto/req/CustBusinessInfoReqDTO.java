package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

/**
 * @author tony
 * @version 1.0
 * @title CustBusinessInfoReqDTO
 * @description
 * @create 2023/8/26 13:21
 */
@Data
public class CustBusinessInfoReqDTO {
    private Long id;
    /**
     * 是否有固定营业场所 0 无 1 有
     */
    private Integer isFixedLocation;
    /**
     * 门面照
     */
    private String facadePhotoUrl;
    private Long facadePhotoId;

    /**
     * 店铺室内
     */
    private String shopPhotoUrl;
    private Long shopPhotoId;


    /**
     * 商品照片
     */
    private String goodsPhotoUrl;
    private Long goodsPhotoId;

    /**
     * 辅助正面材料
     */
    private String auxiliaryPhotoUrl;
    private Long auxiliaryPhotoId;

    private Long loanUserId;
}
