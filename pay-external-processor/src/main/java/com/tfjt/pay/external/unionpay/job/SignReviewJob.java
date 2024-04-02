package com.tfjt.pay.external.unionpay.job;

import com.tfjt.pay.external.unionpay.biz.SignBizService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author tony
 * @version 1.0
 * @title SignReviewJob
 * @description
 * @create 2024/3/8 14:11
 */
@Component
public class SignReviewJob {

    @Resource
    SignBizService signBizService;

    /**
     * 入网成功没有商户绑定关系的状态进行定时推送
     * 通知业务段入网成功状态
     */
    @XxlJob("notionBusinessSignStatus")
    public void notionBusinessSignStatus() {
        String params = XxlJobHelper.getJobParam();
        XxlJobHelper.log("开始执行更新业务入网状态的任务.......");
        signBizService.queryMerchantBySignSuccess(params);
        XxlJobHelper.log("结束执行更新业务入网状态的任务.......");
    }

    /**
     * 入网状态一致性
     * 确保redis数据和数据库数据一致
     */
    @XxlJob("updateSelfSignStatus")
    public void updateSelfSignStatus() {
        String params = XxlJobHelper.getJobParam();
        XxlJobHelper.log("开始执行更新业务入网状态的任务.......");
        signBizService.updateSelfSignStatus(params);
        XxlJobHelper.log("结束执行更新业务入网状态的任务.......");
    }
}
