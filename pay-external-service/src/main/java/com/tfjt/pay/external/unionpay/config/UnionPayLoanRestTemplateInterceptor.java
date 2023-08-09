package com.tfjt.pay.external.unionpay.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * 银联贷款进件接口统一对参数进下加密处理)
 * @Author 李晓雷
 * @Date 2021/8/5 9:17
 * @Version 1.0
 */
@Component
@Slf4j
public class UnionPayLoanRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        byte[] returnBody = tranceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, returnBody);
        return response;
    }

    private byte[] tranceRequest(HttpRequest request, byte[] body) throws UnsupportedEncodingException {
        log.debug("=========================== request begin ===========================");
        log.debug("uri : {}", request.getURI());
        log.debug("method : {}", request.getMethod());
        log.debug("headers : {}", request.getHeaders());
        log.debug("request body byte: {}", body);
        log.debug("request body : {}", new String(body, StandardCharsets.UTF_8));
        log.debug("============================ request end ============================");
        String m = new String(body, StandardCharsets.UTF_8);
        log.info(m);
        return body;
    }
}
