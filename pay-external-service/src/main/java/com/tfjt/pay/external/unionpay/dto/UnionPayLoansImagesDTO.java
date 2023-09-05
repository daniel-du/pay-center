package com.tfjt.pay.external.unionpay.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款进件请求参数
 */
@Data
public class UnionPayLoansImagesDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    private String file;//	图片;
    private UnionPayLoansMetaDTO meta;//	图片;

}
