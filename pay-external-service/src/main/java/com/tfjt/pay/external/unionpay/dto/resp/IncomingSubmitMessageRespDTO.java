package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 11:03
 * @description
 */
@Data
public class IncomingSubmitMessageRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 进件状态
     */
    private Integer accessStatus;

    /**
     * 异常信息
     */
    private String errorMsg;
}
