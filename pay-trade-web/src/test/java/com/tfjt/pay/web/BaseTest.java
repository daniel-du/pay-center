package com.tfjt.pay.trade.web;

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

}
