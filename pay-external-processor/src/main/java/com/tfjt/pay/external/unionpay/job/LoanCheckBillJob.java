package com.tfjt.pay.external.unionpay.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.tfjt.pay.external.unionpay.biz.LoanUnionPayCheckBillBiz;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.service.LoanUnionpayCheckBillService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author songx
 * @date 2023-08-18 21:31
 * @email 598482054@qq.com
 */
@Component
@Slf4j
public class LoanCheckBillJob {

    @Resource
    private LoanUnionPayCheckBillBiz loanUnionPayCheckBillBiz;

    /**
     * 下载昨日对账单
     */
    @XxlJob("downloadCheckBill")
    public void downloadCheckBill(){
        DateTime yesterday = DateUtil.yesterday();
        log.info("开始执行:{}下载账单下载任务........",yesterday);
        loanUnionPayCheckBillBiz.downloadCheckBill(yesterday, NumberConstant.ZERO);
        log.info("结束执行:{}下载账单下载任务........",yesterday);

    }
}
