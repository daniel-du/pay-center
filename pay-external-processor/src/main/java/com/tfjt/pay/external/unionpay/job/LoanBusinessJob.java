package com.tfjt.pay.external.unionpay.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.tfjt.pay.external.unionpay.biz.IncomingTtqfBizService;
import com.tfjt.pay.external.unionpay.biz.LoanUserBizService;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansCallbackApiBiz;
import com.tfjt.pay.external.unionpay.job.checkbill.processor.CheckProcessor;
import com.tfjt.pay.external.unionpay.dto.CheckLoanBillDTO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UnionPayLoansCallbackApiBiz unionPayLoansCallbackApiBiz;

    @Resource
    private LoanUserBizService loanUserBizService;

    @Resource
    private CheckProcessor checkProcessor;


    /**
     * 下载昨日对账单
     */
    @XxlJob("downloadCheckBill")
    public void downloadCheckBill(){
        String jobParam = XxlJobHelper.getJobParam();
        DateTime yesterday = DateUtil.yesterday();
        XxlJobHelper.log("--------------------------开始执行:{}下载账单下载任务----------------------",yesterday);
        XxlJobHelper.log("--------------------------jobParam:{}----------------------",jobParam);
        checkProcessor.checkBill(new CheckLoanBillDTO().setDate(yesterday));
        XxlJobHelper.log("结束执行:{}下载账单下载任务........",yesterday);
    }
    /**
     * 定时扫描未确认的订单信息
     */
    @XxlJob("confirmOrder")
    public void confirmOrder(){
        XxlJobHelper.log("开始执行订单确认任务.......");
        unionPayLoansCallbackApiBiz.confirmOrder();
        XxlJobHelper.log("结束执行订单确认任务.......");
    }
    /**
     * 通知失败的任务
     */
    @XxlJob("applicationCallback")
    public void applicationCallback(){
        XxlJobHelper.log("开始执行通知失败的任务.......");
        unionPayLoansCallbackApiBiz.applicationCallback();
        XxlJobHelper.log("结束执行通知失败的任务.......");
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
