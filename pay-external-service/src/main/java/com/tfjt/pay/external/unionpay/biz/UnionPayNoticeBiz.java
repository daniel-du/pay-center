package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.dto.resp.UnionPayBaseResp;

import javax.servlet.http.HttpServletResponse;

/**
 * 银联入金通知接口
 * @author songx
 * @date 2023-08-14 22:08
 * @email 598482054@qq.com
 */
public interface UnionPayNoticeBiz {
    /**
     * 处理银联入金通知
     * @param unionPayBaseResp
     * @param response
     */
    void balanceIncomeNotice(UnionPayBaseResp unionPayBaseResp, HttpServletResponse response);
}
