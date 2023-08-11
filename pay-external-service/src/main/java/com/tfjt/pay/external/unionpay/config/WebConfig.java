/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.tfjt.pay.external.unionpay.config;

import com.tfjt.pay.external.unionpay.interceptor.AuthInterceptor;
import com.tfjt.tfcommon.core.cache.RedisCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * WebMvc配置
 *
 * @author Mark sunlightcs@gmail.com
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 认证拦截器，并指定拦截的路径
        registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**");
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


    @LoadBalanced
    @Bean
    public RestTemplate loadBalanced1() {
        return new RestTemplate();
    }

    @Primary
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RedisCache redisCache(){
        return new RedisCache();
    }

    @LoadBalanced
    @Bean("unionPayLoan")
    public RestTemplate initUnionPayLoanRestTemplate(){
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(max);
        connectionManager.setDefaultMaxPerRoute(defaultMax);
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(timeOut) // timeout to get connection from pool
                .setSocketTimeout(timeOut) // standard connection timeout
                .setConnectTimeout(timeOut) // standard connection timeout
                .build();

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig).build();

        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate= new RestTemplate(requestFactory);
        restTemplate.getInterceptors().add(unionPayLoanRestTemplateInterceptor);
        return restTemplate;
    }

    /**
     * 跨域设置
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(false)
                .maxAge(3600)
                .allowedHeaders("*");
    }
}
