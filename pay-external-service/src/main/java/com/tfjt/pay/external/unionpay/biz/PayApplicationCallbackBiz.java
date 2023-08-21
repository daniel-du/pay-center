package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.entity.LoadBalanceDivideEntity;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceNoticeEntity;
import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;
import com.tfjt.pay.external.unionpay.entity.LoanWithdrawalOrderEntity;

import java.util.List;

/**
 * @author songx
 * @date 2023-08-18 18:18
 * @email 598482054@qq.com
 */
public interface PayApplicationCallbackBiz {
    /**
     * 回调通知shop服务交易结果
     *
     * @param orderEntity     订单吓你
     * @param tradeResultCode 回调地址获取type
     * @param callbackId      银联通知记录表id
     * @return 通知是否成功
     */
    boolean noticeShop(LoanOrderEntity orderEntity, String tradeResultCode, Long callbackId);

    /**
     * 回调通知FMS入账通知
     *
     * @param list            入账信息
     * @param eventId         事件通知唯一值
     * @param tradeResultCode 回调地址获取type
     * @param callbackId      银联通知记录表id
     * @return 通知是否成功
     */
    boolean noticeFmsIncomeNotice(List<LoadBalanceNoticeEntity> list, String eventId, String tradeResultCode, Long callbackId);

    /**
     * 通知fms系统分账信息
     *
     * @param divideEntity 分账信息
     * @param eventType    事件类型
     * @param id           银联事件id
     * @return 通知结果
     */
    boolean noticeFmsDivideNotice(LoadBalanceDivideEntity divideEntity, String eventType, Long id);

    /**
     * 通知shop记录分账记录
     *
     * @param divideEntity
     * @param eventType
     * @param id
     * @return
     */
    boolean noticeShopDivideNotice(LoadBalanceDivideEntity divideEntity, String eventType, Long id);

    boolean noticeWithdrawalNotice(LoanWithdrawalOrderEntity withdrawalOrderEntity, String eventType, Long id);


}
