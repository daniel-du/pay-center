package com.tfjt.pay.external.unionpay.checkbill.handler;

import com.tfjt.pay.external.unionpay.dto.CheckLoanBillDTO;


/**
 * 银联贷款对账流程
 * @Auther: songx
 * @Date: 2023/10/28/09:22
 * @Description:
 */
public interface CheckBillHandler {
    /**
     * 对账流程具体实现
     * @param checkLoanBillDTO
     * @return
     */
    boolean handler(CheckLoanBillDTO checkLoanBillDTO);
}
