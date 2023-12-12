package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/11 15:42
 */
@Data
public class QueryAccessBankStatueReqDTO implements Serializable {
    private static final long serialVersionUID = 1439701799776535789L;

    //经销商/供应商id/店铺id
    private String businessId;
    //系统来源：1、经销商；2、供应商；3、商家
    private Integer businessType;
    //1、商户进件；2、贷款进件
    private Integer networkType;
}
