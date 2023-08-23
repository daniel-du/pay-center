package com.tfjt.pay.external.unionpay.web.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansCallbackApiBiz;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseCallBackDTO;
import com.tfjt.pay.external.unionpay.dto.req.TransactionCallBackReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayBaseResp;
import com.tfjt.pay.external.unionpay.service.UnionPayLoansCallbackApiService;
import com.tfjt.pay.external.unionpay.utils.ApiResult;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

/**
 * 贷款回调函数
 */
@Slf4j
@RestController
@RequestMapping("unionPayLoansBack")
public class UnionPayLoansCallbackApiController {

    @Autowired
    private UnionPayLoansCallbackApiBiz unionPayNoticeBiz;


    /**
     * 通用的回调通知
     */
    @PostMapping("/twoIncomingCallBack")
    public void commonCallback(@RequestBody UnionPayLoansBaseCallBackDTO yinLianLoansBaseCallBackDTO) throws IOException, ParseException {
        log.info("交易类回调参数:{}", JSONObject.toJSONString(yinLianLoansBaseCallBackDTO));
        unionPayNoticeBiz.commonCallback(yinLianLoansBaseCallBackDTO);
    }
}
