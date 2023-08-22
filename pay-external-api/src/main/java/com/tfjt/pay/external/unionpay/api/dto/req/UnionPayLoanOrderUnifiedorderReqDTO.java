package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 下单
 * @author songx
 * @date 2023-08-15 14:41
 * @email 598482054@qq.com
 */
@Data
public class UnionPayLoanOrderUnifiedorderReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**唯一标识*/
    private String businessOrderNo;

    /**付款账户信息*/
    private String payBalanceAcctId;

    /**付款账户名称*/
    private String payBalanceAcctName;

    /**appid*/
    private String appId;

    /**自定义参数 JSON */
    private String metadata;

    /**收款电子账簿信息*/
    private List<UnionPayLoanOrderDetailsReqDTO> detailsDTOList;

}
