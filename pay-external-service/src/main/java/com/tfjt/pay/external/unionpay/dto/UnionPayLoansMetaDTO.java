package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款进件请求参数
 */
@Data
public class UnionPayLoansMetaDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    //名称;
    private String filename;
    //加密后参数;
    private String sha256;

}
