package com.tfjt.pay.external.unionpay.job.checkbill.processor;

import com.tfjt.pay.external.unionpay.job.checkbill.handler.CheckBillHandler;
import com.tfjt.pay.external.unionpay.dto.CheckLoanBillDTO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 对账单下载
 * @Auther: songx
 * @Date: 2023/10/28/09:40
 * @Description:
 */
@Component
public class CheckProcessor {

    @Resource
    private List<CheckBillHandler> checkBillHandlers;

    /**
     * 银联账单核对
     * @param date 账单日期
     */
    public void checkBill(CheckLoanBillDTO date) {
        for (CheckBillHandler checkBillHandler : checkBillHandlers) {
            boolean handler = checkBillHandler.handler(date);
            if (!handler){
                break;
            }
        }
    }
}
