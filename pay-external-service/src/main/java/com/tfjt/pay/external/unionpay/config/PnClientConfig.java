package com.tfjt.pay.external.unionpay.config;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/1/08 10:07
 * @description 平安apiclient配置类
 */
@Configuration
@ConfigurationProperties(prefix = "pnclient",ignoreInvalidFields = false)
public class PnClientConfig {

    private String appPrivateKey;
    private String publicKey;
    private String appId;
    private String baseUrl;
    private String appSecret;
    private String fileUploadUrl;
    private String fileDownLoadUrl;

    public Properties getClientPropertie() {
        Properties properties = new Properties();
        properties.setProperty("appPrivateKey", this.appPrivateKey);
        properties.setProperty("publicKey", this.publicKey);
        properties.setProperty("appId", this.appId);
        properties.setProperty("baseUrl", this.baseUrl);
        properties.setProperty("appSecret", this.appSecret);
        properties.setProperty("fileUploadUrl", this.fileUploadUrl);
        properties.setProperty("fileDownLoadUrl", this.fileDownLoadUrl);
        return properties;
    }


    public String getAppPrivateKey() {
        return appPrivateKey;
    }

    public void setAppPrivateKey(String appPrivateKey) {
        this.appPrivateKey = appPrivateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getFileUploadUrl() {
        return fileUploadUrl;
    }

    public void setFileUploadUrl(String fileUploadUrl) {
        this.fileUploadUrl = fileUploadUrl;
    }

    public String getFileDownLoadUrl() {
        return fileDownLoadUrl;
    }

    public void setFileDownLoadUrl(String fileDownLoadUrl) {
        this.fileDownLoadUrl = fileDownLoadUrl;
    }
}
