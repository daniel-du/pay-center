package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.req.TransactionCallBackReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayBaseResp;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

/**
 * 银联入金通知接口
 * @author songx
 * @date 2023-08-14 22:08
 * @email 598482054@qq.com
 */
public interface UnionPayCallbackBiz {
    /**
     * 处理银联入金通知
     * @param unionPayBaseResp
     * @param response
     */
    void balanceIncomeNotice(UnionPayBaseResp unionPayBaseResp, HttpServletResponse response);

    /**
     * 银联回调通知
     *
     * @param transactionCallBackReqDTO
     * @param response
     */
    void commonCallback(TransactionCallBackReqDTO transactionCallBackReqDTO, HttpServletResponse response) throws ParseException;
}
