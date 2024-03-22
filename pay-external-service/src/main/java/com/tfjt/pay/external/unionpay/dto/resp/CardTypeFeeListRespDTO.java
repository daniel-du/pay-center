package com.tfjt.pay.external.unionpay.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <pre>
 *  Card_type_fee_list
 * </pre>
 *
 * @author 李扬
 * @verison $Id: Card_type_fee_list v 0.1 2024-02-05 14:59:11
 */
@Data
public class CardTypeFeeListRespDTO {

    /**
     * <pre>
     *
     * </pre>
     */
    @JsonProperty("card_fee")
    private String cardFee;

    /**
     * <pre>
     * 借记卡费率
     * </pre>
     */
    @JsonProperty("card_type")
    private String cardType;


}
