package com.tfjt.pay.external.unionpay.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class DevConfig {

    @Value("${spring.profiles.active}")
    private String active = "";

    /**
     * 获取是否为开发环境，可用于调试
     *
     * @return
     */
    public boolean isLocal() {
        return "local".equals(active);
    }

    /**
     * 判断是否生产环境
     * @return
     */
    public boolean isProd() {
        return "prod".equals(active);
    }

    /**
     * 判断是否预发环境
     * @return
     */
    public boolean isPre() {
        return "pre".equals(active);
    }

    /**
     * 判断是否预发或生产环境
     * @return
     */
    public boolean isPreOrProd() {
        return "pre".equals(active) ||"prod".equals(active);
    }

    public String getActive() {
        return active;
    }

    /**
     * 判断调试环境，并判断是否有相关调试标识
     *
     * @return
     */
    public boolean CheckLocalFlag(String flag) {
        if (!isLocal()) {
            return false;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == servletRequestAttributes) {
            return false;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return null != request.getHeader(flag);
    }

}
