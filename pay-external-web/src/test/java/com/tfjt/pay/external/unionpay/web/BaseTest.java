package com.tfjt.pay.external.unionpay.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 业务描述：
 * 应用场景：
 *
 * @author ： seeyoe@126.com
 * @date ： 2022-10-17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PayExternalApplication.class)
@MapperScan({"com.tfjt.**.dao"})
@ComponentScan({"com.tfjt"})
@ActiveProfiles("local")
public class BaseTest {


    @Test
    public void test1(){
        System.out.println();
    }
}
