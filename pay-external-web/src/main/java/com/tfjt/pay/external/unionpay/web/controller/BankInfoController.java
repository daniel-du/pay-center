package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.biz.PabcBizService;
import com.tfjt.pay.external.unionpay.dto.resp.BankNameAndCodeRespDTO;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 16:03
 */
@RestController
@RequestMapping("/bankinfo")
@Slf4j
public class BankInfoController {

    @Autowired
    private PabcBizService pabcBizService;

    @GetMapping("/getBankInfoByName")
    public Result<List<BankNameAndCodeRespDTO>> getBankInfoByName(String name){
        return pabcBizService.getBankInfoByName(name);
    }

}
