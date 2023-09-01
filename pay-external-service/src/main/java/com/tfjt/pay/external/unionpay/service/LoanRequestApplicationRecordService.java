package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.tfjt.pay.external.unionpay.entity.LoanRequestApplicationRecordEntity;
import com.tfjt.pay.external.unionpay.entity.LoanWithdrawalOrderEntity;

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

    /**
     * 回调通知shop服务交易结果
     *
     * @param orderEntity     订单信息
     * @param treadType       回调地址获取type
     * @param callbackId      银联通知记录表id
     */
    void noticeShop(LoanOrderEntity orderEntity, String treadType, Long callbackId);

    /**
     * 提现业务回调
     * @param withdrawalOrder
     * @param tradeType
     * @param id
     */
    void noticeWithdrawalNotice(LoanWithdrawalOrderEntity withdrawalOrder, String tradeType, Long id);
}

