package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 下单收款商户信息
 * @author songx
 * @date 2023-08-15 14:47
 * @email 598482054@qq.com
 */
@Data
public class UnionPayLoanOrderDetailsReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**收款电子账簿id*/
    private String recvBalanceAcctId;

    /**收款方名称*/
    private String recvBalanceAcctName;
    /***
     * 业务子交易单号
     */
    private String subBusinessOrderNo;
    /***
     * 自定义参数 JSON
     */
    private String metadata;

    /**
     * 收款金额
     */
    private Integer amount;

    /**
     * 商品信息
     */
    private List<UnionPayLoanOrderGoodsReqDTO> goodsDTOList;
}
