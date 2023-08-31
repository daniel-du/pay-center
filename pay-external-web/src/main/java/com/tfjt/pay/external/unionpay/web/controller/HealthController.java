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
    @Lock4j(keys = {"#key"}, expire = 10000, acquireTimeout = 3000)
    public Result<?> lock(String key) throws InterruptedException {
        Thread.sleep(4000L);
        return Result.ok(key);
    }

}
