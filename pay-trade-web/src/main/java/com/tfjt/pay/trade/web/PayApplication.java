package com.tfjt.pay.trade.web;

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
@SpringBootApplication(scanBasePackages = {"com.tfjt.pay.trade"})
@MapperScan({"com.tfjt.pay.trade.**.dao"})
@ComponentScan(basePackages={"com.tfjt.pay.trade"})
@ServletComponentScan
@EnableAsync
@EnableFeignClients
@EnableDiscoveryClient
@EnableFileStorage
@EnableDubbo(scanBasePackages = "com.tfjt.pay.trade")
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }

}
