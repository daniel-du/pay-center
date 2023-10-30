package com.tfjt.pay.external.unionpay.checkbill.handler;

import com.tfjt.pay.external.unionpay.dto.CheckLoanBillDTO;


/**
 * @Auther: songx
 * @Date: 2023/10/28/09:22
 * @Description:
 */
public interface CheckBillHandler {

    boolean handler(CheckLoanBillDTO checkLoanBillDTO);
}
