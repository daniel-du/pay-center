package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillEntity;


/**
 * 贷款银联对账表
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-18 20:59:10
 */
public interface LoanUnionpayCheckBillService extends IService<LoanUnionpayCheckBillEntity> {
    /**
     * 获取指定日期的对账单信息
     * @param date 指定日
     * @param balanceAcctId 指定账户
     * @return 对账单保存信息
     */
    LoanUnionpayCheckBillEntity getByDateAndAccountId(String date, String balanceAcctId);

    /**
     * 保存文件到数据库中
     * @param absolutePath 文件的绝对路径
     */
    void loadFile(String absolutePath);
}

