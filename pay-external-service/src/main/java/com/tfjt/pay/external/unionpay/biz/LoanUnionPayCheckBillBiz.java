package com.tfjt.pay.external.unionpay.biz;

import cn.hutool.core.date.DateTime;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayCheckBillReqDTO;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillEntity;
import com.tfjt.tfcommon.dto.response.Result;

import java.io.FileNotFoundException;

/**
 * @author songx
 * @date 2023-08-18 21:33
 * @email 598482054@qq.com
 */
public interface LoanUnionPayCheckBillBiz {
    /**
     * 下载指定日期的银联对账单
     *
     * @param yesterday 指定日期
     */
    LoanUnionpayCheckBillEntity downloadCheckBill(DateTime yesterday);

    /**
     * dobbo接口 主要是fms调用下载指定日期的电子对账单
     * @param unionPayCheckBillReqDTO
     * @return 电子对账对下载地址
     */
    Result<String> downloadCheckBill(UnionPayCheckBillReqDTO unionPayCheckBillReqDTO);
    /**
     * 下载指定日期的电子对账单,定时任务调用
     * @param unionPayCheckBillReqDTO
     * @return 下载账单是否成功
     */
   boolean download(UnionPayCheckBillReqDTO unionPayCheckBillReqDTO);
}
