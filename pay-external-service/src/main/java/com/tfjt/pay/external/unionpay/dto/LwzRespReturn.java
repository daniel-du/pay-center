package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LwzRespReturn implements Serializable {
    private static final long serialVersionUID = 1L;
    private String reason;
    private String param;
}
