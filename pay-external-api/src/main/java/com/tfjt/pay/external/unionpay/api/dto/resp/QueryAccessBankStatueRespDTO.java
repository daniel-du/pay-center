package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/11 15:43
 */
@Data
public class QueryAccessBankStatueRespDTO implements Serializable {
    private static final long serialVersionUID = 5532506225179688983L;
    // 0、未入网；1、入网
    private String status;
    //银联、平安、银联贷款
    private String  networkChannel;

    private String msg;
}
