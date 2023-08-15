package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.biz.UnionPayNoticeBiz;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayBaseResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 银联回调通知接口
 * @author songx
 * @date 2023-08-14 21:59
 * @email 598482054@qq.com
 */
@Slf4j
@RestController
@RequestMapping("/unionPay/notice")
public class UnionPayNoticeController {
    @Autowired
    private UnionPayNoticeBiz unionPayNoticeBiz;
    /**
     * 银联入金通知
     */
    @RequestMapping("/balanceIncomeNotice")
    public void balanceIncomeNotice(@RequestBody UnionPayBaseResp unionPayBaseResp, HttpServletResponse response){

        unionPayNoticeBiz.balanceIncomeNotice(unionPayBaseResp,response);
    }
}
