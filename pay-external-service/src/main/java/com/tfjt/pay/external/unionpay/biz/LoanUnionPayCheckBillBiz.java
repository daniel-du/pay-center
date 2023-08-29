package com.tfjt.pay.external.unionpay.biz;

import cn.hutool.core.date.DateTime;

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
    void downloadCheckBill(DateTime yesterday);
}
