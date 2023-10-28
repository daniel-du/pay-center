package com.tfjt.pay.external.unionpay.checkbill.handler.impl;

import com.tfjt.pay.external.unionpay.biz.LoanUnionPayCheckBillBiz;
import com.tfjt.pay.external.unionpay.checkbill.handler.CheckBillHandler;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Auther: songx
 * @Date: 2023/10/28/09:32
 * @Description:
 */
@Order(NumberConstant.ONE)
@Component
public class CheckHandler implements CheckBillHandler {
    @Resource
    private LoanUnionPayCheckBillBiz loanUnionPayCheckBillBiz;


    @Override
    public boolean handler(Date date) {
        return false;
    }
}
