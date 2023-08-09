package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SettleAcctsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String requestId;

    private List<SettleAcctsMxDTO> settleAccts ;

}
