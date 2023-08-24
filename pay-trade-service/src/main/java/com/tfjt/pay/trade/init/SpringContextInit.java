package com.tfjt.pay.trade.init;

import com.tfjt.pay.trade.service.PayApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
@Slf4j
public class SpringContextInit {
    @Resource
    PayApplicationService payApplicationService;

    @PostConstruct
    public void pingStart() {
        int count = payApplicationService.loadAppSecret();
        log.info("load appSecret count:{}", count);
    }
}