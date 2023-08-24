package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-24 20:17
 * @email 598482054@qq.com
 */
@Data
public class MergeConsumerRepDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**交易状态*/
    private String status;

    /**原因描述*/
    private String reason;

    private String businessOrderNo;
}
