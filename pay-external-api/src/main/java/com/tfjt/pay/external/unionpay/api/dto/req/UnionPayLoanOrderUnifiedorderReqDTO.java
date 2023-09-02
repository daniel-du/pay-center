package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @NotBlank(message = "交易单号不能为空")
    private String businessOrderNo;

    /**付款账户信息*/
    @NotBlank(message = "付款账户信息不能为空")
    private String payBalanceAcctId;

    /**付款账户名称*/
    //@NotBlank(message = "付款账户名称不能为空")
    private String payBalanceAcctName;

    /**appid*/
    @NotBlank(message = "appId不能为空")
    private String appId;

    /**自定义参数 JSON */
    private String metadata;

    /**收款电子账簿信息*/
    @NotNull(message = "收款信息不能为空")
    @Size(min = 1,message = "收款信息不能为空")
    private List<UnionPayLoanOrderDetailsReqDTO> detailsDTOList;

}
