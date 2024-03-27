package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RespLwzMsgReturn implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;
    private String message;
    private String field;
    private String issue;

}
