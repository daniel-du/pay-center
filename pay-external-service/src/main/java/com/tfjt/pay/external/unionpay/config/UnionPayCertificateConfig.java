package com.tfjt.pay.external.unionpay.config;

import com.tfjt.pay.external.unionpay.utils.UnionPaySignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.cert.X509Certificate;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @Author: suxiaochang
 * @date: 2022/11/22
 */
@Configuration
@Slf4j
public class UnionPayCertificateConfig {
    @Value("${sftp.privateKeyPath}")
    private String privateKeyPath;
    @Value("${sftp.privateKeyPwd}")
    private String privateKeyPwd;
    @Value("${sftp.publicKeyPath}")
    private String publicKeyPath;
    public static PrivateKey batchPrivateKey;
    public static PublicKey countPubKey;

    @Bean
    public void getBatchPrivateKey() {
        try {
            FileInputStream inputStream = new FileInputStream(privateKeyPath);
            batchPrivateKey = UnionPaySignUtil.getPriKeyPkcs12(inputStream, privateKeyPwd);
            log.info("银联私钥加载完毕:"+batchPrivateKey);
        } catch (FileNotFoundException e) {
            log.info("银联获取私钥错误:"+e);
        }
    }
    @Bean
    public void getBatchPublicKey() {
        try {
            //回盘文件验签公钥--商户用
            InputStream countPubKeyStoreFileStream = new FileInputStream(publicKeyPath);
            X509Certificate countCert = X509Certificate.getInstance(countPubKeyStoreFileStream);
            countPubKey = countCert.getPublicKey();
            log.info("回盘验签公钥加载完毕：" + countPubKey);
        } catch (Exception e) {
            log.info("回盘验签公钥错误:"+e);
        }
    }
}
