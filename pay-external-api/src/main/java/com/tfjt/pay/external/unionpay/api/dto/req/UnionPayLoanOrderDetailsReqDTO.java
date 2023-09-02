package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @NotBlank(message = "收款电子账户不能为空")
    private String recvBalanceAcctId;

    /**收款方名称*/
   // @NotBlank(message = "收款电子账户名称不能为空")
    private String recvBalanceAcctName;
    /***
     * 业务子交易单号
     */
    @NotBlank(message = "子交易单号不能为空")
    private String subBusinessOrderNo;
    /***
     * 自定义参数 JSON
     */
    private String metadata;

    /**
     * 收款金额
     */
    @NotBlank(message = "收款金额不能为空")
    private Integer amount;

    /**
     * 商品信息
     */
    @NotNull(message = "商品信息不能为空")
    @Size(min = 1,message = "商品信息不能为空")
    private List<UnionPayLoanOrderGoodsReqDTO> goodsDTOList;
}
