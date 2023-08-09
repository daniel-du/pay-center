package com.tfjt.pay.external.unionpay.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description: IdcardDTO <br>
 * @date: 2023/5/20 11:16 <br>
 * @author: young <br>
 * @version: 1.0
 */
@Data
public class BankCardDTO {
    @JsonProperty("bank_name")
    private String bankName;
    @JsonProperty("card_num")
    private String cardNum;
    @JsonProperty("card_type")
    private String cardType;
    @JsonProperty("is_fake")
    private boolean fake;
    @JsonProperty("request_id")
    private String requestId;
    private boolean success;
    @JsonProperty("valid_date")
    private String validDate;
    private String url;
}
