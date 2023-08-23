package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-23 15:56
 * @email 598482054@qq.com
 */
@Data
public class UnionPayCheckBillReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     *  日期
     */
    private String date;
    /**
     *  日期
     */
    private Long recordId;


}
