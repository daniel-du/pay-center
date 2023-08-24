package com.tfjt.pay.trade.web.controller;

import com.tfjt.pay.trade.dto.req.PayApplicationEntityDTO;
import com.tfjt.pay.trade.service.PayApplicationService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.DefaultGroup;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 应用表
 *
 * @author ???
 * @email 598085205@qq.com
 * @date 2022-11-05 10:11:23
 */
@Slf4j
@RestController
@RequestMapping("payapplication")
public class PayApplicationController {
    @Autowired
    private PayApplicationService payApplicationService;

    /**
     * 保存
     */
    @PostMapping("/save")
    public Result save(@RequestBody PayApplicationEntityDTO payApplicationEntityDTO) throws Exception {
        try{
            ValidatorUtils.validateEntity(payApplicationEntityDTO, AddGroup.class, DefaultGroup.class);
            return payApplicationService.savePayApplication(payApplicationEntityDTO);
        }catch (TfException e){
            log.error("PayApplicationController.save.error: " + e.getMessage());
            return  Result.failed(e.getCode(),e.getMessage(),"");
        }
    }

    /**
     * 获取token
     */
    @GetMapping("/getToken")
    public Result getToken(String appId, String appSecret){
        Map map;
        try{
            map =  payApplicationService.getToken(appId, appSecret);
        }catch (TfException e){
            log.error("PayApplicationController.save.error: " + e.getMessage());
            return  Result.failed(e.getCode(),e.getMessage(),"");
        }
        return Result.ok(map);
    }

    @GetMapping("/loadAppSecret")
    public Result<String> loadAppSecret(){
        int count = payApplicationService.loadAppSecret();
        return Result.ok("加载成功"+count+"条");
    }
}
