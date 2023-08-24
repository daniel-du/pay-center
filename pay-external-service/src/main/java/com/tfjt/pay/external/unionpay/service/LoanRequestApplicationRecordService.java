package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoanRequestApplicationRecordEntity;

import java.util.List;


/**
 * 贷款-调用回调业务日志表
 *
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-18 20:59:10
 */
public interface LoanRequestApplicationRecordService extends IService<LoanRequestApplicationRecordEntity> {
    /**
     * 回调日志记录
     * @param record
     */
    void asyncSave(LoanRequestApplicationRecordEntity record);

    /**
     * 获取当前日志失败的
     * @return list
     */
    List<LoanRequestApplicationRecordEntity> listError();
}

