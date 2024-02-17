package com.tfjt.pay.external.unionpay.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author tony
 * @version 1.0
 * @title UnionPayResult
 * @description
 * @create 2024/2/7 09:36
 */
@Data
@Accessors(chain = true)
public class UnionPayResult implements Serializable {
    @JsonProperty("res_code")
    private String resCode;

    @JsonProperty("res_msg")
    private String resMsg;
}
