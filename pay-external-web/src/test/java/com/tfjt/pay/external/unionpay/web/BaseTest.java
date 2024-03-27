package com.tfjt.pay.external.unionpay.web;

import com.tfjt.pay.external.unionpay.utils.DESUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
@ComponentScan(basePackages = {"com.tfjt.pay.external", "com.tfjt.tfcommon.core.util"})
@ActiveProfiles("local")
@Slf4j
public class BaseTest {

}
