/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.tfjt.pay.external.unionpay.config;

import com.tfjt.tfcommon.auth.interceptor.AuthInterceptor;
import com.tfjt.tfcommon.core.cache.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * WebMvc配置
 *
 * @author Mark sunlightcs@gmail.com
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Resource
    InterceptorProperties interceptorProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("要过滤的路径{}",interceptorProperties.getExcludePath());
        // 认证拦截器，并指定拦截的路径
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**").excludePathPatterns(interceptorProperties.getExcludePath());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/statics/**").addResourceLocations("classpath:/statics/");
    }

    static final int timeOut = 10 * 1000;
    static final int max = 200;
    static final int defaultMax = 50;

    @Resource
    private UnionPayLoanRestTemplateInterceptor unionPayLoanRestTemplateInterceptor;


    @Primary
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
//        // 将xml解析的优先级调低
//        int xml = 0, json = 0;
//        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
//        for (int i = 0; i < messageConverters.size(); i++) {
//            HttpMessageConverter<?> h = messageConverters.get(i);
//            if (h instanceof MappingJackson2XmlHttpMessageConverter) {
//                xml = i;
//            } else if (h instanceof MappingJackson2HttpMessageConverter) {
//                json = i;
//            }
//        }
//        Collections.swap(restTemplate.getMessageConverters(), xml, json);
        return restTemplate;
    }

    @Bean
    public RedisCache redisCache() {
        return new RedisCache();
    }
/*
    *//**
     * 跨域设置
     * @param registry
     *//*
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("环境参数变量{}", env);
        if(Objects.equals(env, "4")){
            registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowCredentials(true)
                    .maxAge(3600)
                    .allowedHeaders("*");
        }
    }*/
}
