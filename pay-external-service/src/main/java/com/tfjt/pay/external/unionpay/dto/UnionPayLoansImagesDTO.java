package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款进件请求参数
 */
@Data
public class UnionPayLoansImagesDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String file;
    private UnionPayLoansMetaDTO meta;

}
