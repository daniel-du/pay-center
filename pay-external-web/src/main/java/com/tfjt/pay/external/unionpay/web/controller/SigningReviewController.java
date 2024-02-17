package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.biz.SignBizService;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayResult;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author tony
 * @version 1.0
 * @title SigningReviewController
 * @description
 * @create 2024/2/5 14:37
 */
@RestController
@RequestMapping("/signing/review")
@Slf4j
public class SigningReviewController {
    @Resource
    SignBizService signBizService;


    /**
     * 入网审核变更
     *
     * @param signData
     * @param jsonData
     * @param accesserId
     * @return
     */
    @PostMapping
    public UnionPayResult signingReview(@RequestParam(value = "sign_data", required = false) String signData, @RequestParam(value = "json_data", required = false) String jsonData, @RequestParam(value = "accesser_id", required = false) String accesserId) {
        log.info("signData:{},jsonData:{},accesserId:{}", signData, jsonData, accesserId);
        return signBizService.signingReview(signData, jsonData, accesserId);
    }

}
