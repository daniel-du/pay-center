package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: ImgTypeEnum <br>
 * @date: 2023/5/22 10:34 <br>
 * @author: young <br>
 * @version: 1.0
 */
@AllArgsConstructor
@Getter
public enum ImgTypeEnum {
    /**
     * 门面照
     */
    FACADE_PHOTO(1, "门面照"),
    /**
     * 店铺室内
     */
    SHOP_PHOTO(2, "店铺室内"),
    /**
     * 商品
     */
    GOOD_PHOTO(3, "商品"),
    /**
     * 证明材料
     */
    EVIDENC_PHOTO(4, "证明材料");


    /**
     * code
     */
    private final Integer code;
    /**
     * 描述
     */
    private final String desc;
}
