package com.tfjt.pay.external.unionpay.dto.req;

import com.tfjt.pay.external.unionpay.dto.UnionPayProduct;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 银联分账明细
 * @author songx
 * @date 2023-08-14 14:35
 * @email 598482054@qq.com
 */
@Data
public class UnionPayDivideSubReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**收款电子账簿ID*/
    private String recvBalanceAcctId;

    /**分账金额*/
    private Integer amount;

    /**备注*/
    private String remark;
    /**
     * 扩展字段商品信息
     */
    private Map<String,Object> extra;


}
