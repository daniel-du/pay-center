package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-25 15:07
 * @email 598482054@qq.com
 */
@Data
public class UnionPayIncomingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bid;

    private String type;

}
