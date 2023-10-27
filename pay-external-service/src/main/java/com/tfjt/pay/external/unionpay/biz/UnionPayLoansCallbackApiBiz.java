package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseCallBackDTO;

import java.text.ParseException;

/**
 * 银联入金通知接口
 * @author songx
 * @date 2023-08-14 22:08
 * @email 598482054@qq.com
 */
public interface UnionPayLoansCallbackApiBiz {

    /**
     * 银联回调通知
     *
     * @param transactionCallBackReqDTO
     */
    String commonCallback(UnionPayLoansBaseCallBackDTO transactionCallBackReqDTO) throws ParseException;


    /**
     * 订单确认
     */
    void confirmOrder();

    /**
     * 失败通知定时补偿
     */
    void applicationCallback();



}
