package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.service.UnionPayLoansApiService;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @description: SmsController <br>
 * @date: 2023/5/22 09:14 <br>
 * @author: young <br>
 * @version: 1.0
 */

@RestController
@RequestMapping("sms")
@Slf4j
public class SmsController {

    @Resource
    UnionPayLoansApiService unionPayLoansApiService;

    @PostMapping("send")
    public Result<?> send(@RequestBody Map<String,String> params){
        String phone = params.get("phone");
        try {
            String str  = unionPayLoansApiService.validationMobileNumber(phone);
            return Result.ok(str);
        } catch (Exception e) {
           log.error("发送短信异常",e);
           return Result.failed("发送短信异常");
        }
    }
}
