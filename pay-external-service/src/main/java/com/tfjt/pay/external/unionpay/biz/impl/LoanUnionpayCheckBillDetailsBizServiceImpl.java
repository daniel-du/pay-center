package com.tfjt.pay.external.unionpay.biz.impl;


import com.tfjt.pay.external.unionpay.biz.LoanUnionpayCheckBillDetailsServiceBiz;
import com.tfjt.pay.external.unionpay.service.LoanUnionpayCheckBillDetailsService;
import com.tfjt.pay.external.unionpay.service.LoanUnionpayCheckBillService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Auther: songx
 * @Date: 2023/10/28/11:27
 * @Description:
 */
@Component
public class LoanUnionpayCheckBillDetailsBizServiceImpl implements LoanUnionpayCheckBillDetailsServiceBiz {

    @Resource
    private LoanUnionpayCheckBillDetailsService loanUnionpayCheckBillDetailsService;
}
