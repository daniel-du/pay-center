package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 银联分账支付参数
 * @author songx
 * @date 2023-08-14 14:30
 * @email 598482054@qq.com
 */
@Data
public class UnionPayDivideReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**平台订单号*/
    private String outOrderNo;
    /**发送时间*/
    private String sentAt;
    /**付款电子账簿ID*/
    private String payBalanceAcctId;
    /**分账明细*/
    private List<UnionPayDivideSubReq>  transferParams;
    /**交易授权码*/
    private String password;
    /**交易附言*/
    private String remark;
    /**自定义参数*/
    private String metadata;
}
