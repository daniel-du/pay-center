package com.tfjt.pay.external.unionpay.web.controller;

import com.baomidou.lock.annotation.Lock4j;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 探活Controller
 *
 * @author effine
 * @Date 2022/10/10 17:08
 * @email iballad#163.com
 */
@Slf4j
@RestController
public class HealthController {

    /**
     * 监控检测
     */
    @RequestMapping("/healthCheck")
    public String healthCheck() {
        return "探活请求，服务健康...";
    }

    //完全配置，支持spel
    @GetMapping("/lock")
    public Result<?> lock(String key) {
        ;
        return Result.ok(lockTest(key));
    }
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#key"}, expire = 10000, acquireTimeout = 3000)
    public String lockTest(String key) {
        log.info("请求｛｝-------------");
        return key;
    }

}
