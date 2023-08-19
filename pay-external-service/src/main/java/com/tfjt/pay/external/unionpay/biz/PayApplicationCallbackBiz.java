package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;

/**
 * @author songx
 * @date 2023-08-18 18:18
 * @email 598482054@qq.com
 */
public interface PayApplicationCallbackBiz {
    /**
     * 回调通知shop服务交易结果
     * @param orderEntity 订单吓你
     * @param tradeResultCode60 回调地址获取type
     * @param noticeUrl 回调地址
     * @return 通知是否成功
     */
    boolean noticeShop(LoanOrderEntity orderEntity, String tradeResultCode60, String noticeUrl);
}
