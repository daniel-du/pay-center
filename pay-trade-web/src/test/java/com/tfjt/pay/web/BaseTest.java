package com.tfjt.pay.web;

import com.tfjt.pay.external.unionpay.service.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PayApplication.class)
@MapperScan({"com.tfjt.pay.trade.**.dao"})
@ComponentScan({"com.tfjt.pay.trade"})
@ActiveProfiles("local")
public class BaseTest {

    @DubboReference
    DemoService demoService;

    @Test
    public void test1(){
        String joy = demoService.demo("joy");
        System.out.println(joy);
    }
}
