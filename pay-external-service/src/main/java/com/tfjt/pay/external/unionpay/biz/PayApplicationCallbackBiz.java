package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.entity.LoanOrderEntity;

/**
 * @author songx
 * @date 2023-08-18 18:18
 * @email 598482054@qq.com
 */
public interface PayApplicationCallbackBiz {
    boolean noticeShop(LoanOrderEntity orderEntity);
}
