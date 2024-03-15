package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 11:03
 * @description
 */
@Data
public class IncomingSubmitMessageReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 进件id
     */
    private Long incomingId;
}
