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
import javax.xml.transform.Result;
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
    private UnionPayLoansCallbackApiBiz yinLianLoansCallbackApiService;



    /**
     * 二级商户进件回调结果通知OR打款验证通知
     * @param yinLianLoansBaseCallBackDTO
     * @return
     */
    @PostMapping("/twoIncomingCallBack")
    public ApiResult<?> twoIncomingCallBack(@RequestBody UnionPayLoansBaseCallBackDTO yinLianLoansBaseCallBackDTO){
        try {
            log.info("二级商户回调结果通知入参{}", JSONObject.toJSONString(yinLianLoansBaseCallBackDTO));
            return ApiResult.ok(yinLianLoansCallbackApiService.commonCallback(yinLianLoansBaseCallBackDTO));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.twoIncomingCallBack.err:{}" , e);
            return ApiResult.failed(e.getCode(),e.getMessage());
        } catch (Exception e) {
            log.error("二级商户回调结果通知入参：param={}", JSON.toJSONString(yinLianLoansBaseCallBackDTO), e);
            return  ApiResult.failed(e.getMessage());
        }
    }
}
