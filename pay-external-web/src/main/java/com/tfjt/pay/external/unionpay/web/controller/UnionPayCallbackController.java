package com.tfjt.pay.external.unionpay.web.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.biz.UnionPayCallbackBiz;
import com.tfjt.pay.external.unionpay.dto.req.TransactionCallBackReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.ConsumerPoliciesRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayBaseResp;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;

/**
 * 银联回调通知接口
 * @author songx
 * @date 2023-08-14 21:59
 * @email 598482054@qq.com
 */
@Slf4j
@RestController
@RequestMapping("/unionPay/notice")
public class UnionPayCallbackController {
    @Autowired
    private UnionPayCallbackBiz unionPayNoticeBiz;
    /**
     * 银联入金通知
     */
    @PostMapping("/balanceIncomeCallback")
    public void balanceIncomeNotice(@RequestBody UnionPayBaseResp unionPayBaseResp, HttpServletResponse response){
        unionPayNoticeBiz.balanceIncomeNotice(unionPayBaseResp,response);
    }

    /**
     * 通用的回调通知
     */
    @PostMapping("/commonCallback")
    public void commonCallback(HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException {
        String param = HttpUtil.getString(request.getInputStream(), null, false);
        log.info("交易类回调参数:{}", param);
        TransactionCallBackReqDTO transactionCallBackReqDTO = JSONObject.parseObject(param, TransactionCallBackReqDTO.class);
        unionPayNoticeBiz.commonCallback(transactionCallBackReqDTO,response);
    }
}
