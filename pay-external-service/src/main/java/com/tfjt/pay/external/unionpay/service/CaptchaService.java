package com.tfjt.pay.external.unionpay.service;


import com.tfjt.tfcommon.dto.response.Result;

import java.io.IOException;

public interface CaptchaService {


    /**
     * 验证码
     */
    Result<String> create(String uuid, String phoneNumber) throws IOException;

    /**
     * 验证码效验
     * @param uuid  uuid
     * @param code  验证码
     * @return  true：成功  false：失败
     */
    boolean validate(String uuid, String code);

    String getCache(String key);
}
