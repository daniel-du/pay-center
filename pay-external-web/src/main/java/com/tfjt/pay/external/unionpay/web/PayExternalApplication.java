package com.tfjt.pay.external.unionpay.web;

import cn.xuyanwu.spring.file.storage.EnableFileStorage;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author tony
 */
@SpringBootApplication(scanBasePackages = {"com.tfjt.pay.external"})
@MapperScan({"com.tfjt.pay.external.**.dao"})
@ComponentScan(basePackages={"com.tfjt.pay.external","com.tfjt.tfcommon.core.util","com.tfjt.tfcommon.auth.interceptor"})
@ServletComponentScan
@EnableAsync
@EnableFeignClients
@EnableDiscoveryClient
@EnableFileStorage
@EnableDubbo(scanBasePackages = "com.tfjt.pay.external")
public class PayExternalApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayExternalApplication.class, args);
    }

}
