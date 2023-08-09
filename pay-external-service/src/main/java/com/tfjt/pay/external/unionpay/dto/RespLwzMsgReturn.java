package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RespLwzMsgReturn implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;//
    private String message;//	版本号
    private String field;//	版本号
    private String issue;//	版本号

}
