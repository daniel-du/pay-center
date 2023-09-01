package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.*;

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

    /**
     * 通知fms系统分账信息
     *
     * @param divideEntity 分账信息
     * @param eventType    事件类型
     * @param id           银联事件id
     * @return 通知结果
     */
     void noticeFmsDivideNotice(LoadBalanceDivideEntity divideEntity, String eventType, Long id);

    /**
     * 通知shop记录分账记录
     *
     * @param divideEntity
     * @param eventType
     * @param id
     * @return
     */
    void noticeShopDivideNotice(LoadBalanceDivideEntity divideEntity, String eventType, Long id);

    /**
     * 补偿发送
     * @param o 失败记录信息
     */
    void retryNotice(LoanRequestApplicationRecordEntity o);

    /**
     * 回调通知FMS入账通知
     *
     * @param list            入账信息
     * @param eventId         事件通知唯一值
     * @param tradeResultCode 回调地址获取type
     * @param callbackId      银联通知记录表id
     */
    void noticeFmsIncomeNotice(List<LoadBalanceNoticeEntity> list, String eventId, String tradeResultCode, Long callbackId);
}

