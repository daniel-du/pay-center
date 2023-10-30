package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: songx
 * @Date: 2023/10/30/10:50
 * @Description:
 */
@Accessors(chain = true)
@Data
public class CheckLoanBillDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date date;

    private String warnBatchNo;
}
