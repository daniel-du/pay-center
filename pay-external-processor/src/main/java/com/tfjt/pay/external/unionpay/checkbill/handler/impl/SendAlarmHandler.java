package com.tfjt.pay.external.unionpay.checkbill.handler.impl;

import com.tfjt.pay.external.unionpay.checkbill.handler.CheckBillHandler;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Auther: songx
 * @Date: 2023/10/28/09:34
 * @Description:
 */
@Order(NumberConstant.TWO)
@Component
public class SendAlarmHandler implements CheckBillHandler {
    @Override
    public boolean handler(Date date) {
        return false;
    }
}
