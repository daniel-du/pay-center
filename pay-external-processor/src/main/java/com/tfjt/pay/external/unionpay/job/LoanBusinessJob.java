package com.tfjt.pay.external.unionpay.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.tfjt.pay.external.unionpay.biz.LoanUnionPayCheckBillBiz;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansCallbackApiBiz;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

/**
 * 贷款业务定时任务
 * @author songx
 * @date 2023-08-18 21:31
 * @email 598482054@qq.com
 */
@Component
@Slf4j
public class LoanBusinessJob {

    @Resource
    private LoanUnionPayCheckBillBiz loanUnionPayCheckBillBiz;

    @Resource
    private UnionPayLoansCallbackApiBiz unionPayLoansCallbackApiBiz;

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
    /**
     * 定时扫描未确认的订单信息
     */
    @XxlJob("confirmOrder")
    public void confirmOrder(){
        unionPayLoansCallbackApiBiz.confirmOrder();
    }
    /**
     * 定时扫描未确认的订单信息
     */
    @XxlJob("applicationCallback")
    public void applicationCallback(){
        unionPayLoansCallbackApiBiz.applicationCallback();
    }
}
