package com.tfjt.pay.external.unionpay.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.tfjt.pay.external.unionpay.biz.LoanUnionPayCheckBillBiz;
import com.tfjt.pay.external.unionpay.biz.LoanUserBizService;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansCallbackApiBiz;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    @Resource
    private LoanUserBizService loanUserBizService;

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
     * 通知失败的任务
     */
    @XxlJob("applicationCallback")
    public void applicationCallback(){
        unionPayLoansCallbackApiBiz.applicationCallback();
    }



    /**
     * 贷款签约状态修改
     * @param param
     * @return
     */
    @XxlJob("applicationStatusUpdateJob")
    public ReturnT<String> applicationStatusUpdateJob(String param) {
        long start = System.currentTimeMillis();
        String jobParam = XxlJobHelper.getJobParam();

        XxlJobHelper.log("--------------------------开始贷款签约状态修改任务----------------------");
        loanUserBizService.applicationStatusUpdateJob(jobParam);
        long end1 = System.currentTimeMillis();

        XxlJobHelper.log("--------------------------结束贷款签约状态修改任务任务----------------------");
        XxlJobHelper.log("---------------------"+Thread.currentThread().getName()+"总计用时"+(end1 - start)/1000+"S---------------------------");
        return ReturnT.SUCCESS;
    }
}
