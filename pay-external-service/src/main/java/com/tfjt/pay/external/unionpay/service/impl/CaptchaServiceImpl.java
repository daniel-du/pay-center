package com.tfjt.pay.external.unionpay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.tfjt.pay.external.unionpay.service.CaptchaService;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {
    @Value("${dysmsapi.aliyuncs.com.accessKeyId}")
    private String accessKeyId;
    @Value("${dysmsapi.aliyuncs.com.accessKeySecret}")
    private String accessKeySecret;
    @Value("${dysmsapi.aliyuncs.com.signName}")
    private String signName;
    @Value("${dysmsapi.aliyuncs.com.templateCode}")
    private String templateCode;
    /**
     * 短信验证码
     */
    private static final String SMS_CODE = "sms_code:";


    @Autowired
    private RedisCache redisCache;

    @Override
    public Result<String> create(String uuid, String phoneNumber) throws IOException {
        //60s内不能重复发送
        if (StringUtil.isNotBlank(getCache(uuid))) {
            return Result.ok("已经发送,不能重复发送");
        }

        int smsCode = (int) ((Math.random() + 1) * 1000);

        //发送短信
        SendSmsResponse smsResponse = sendSms(phoneNumber, smsCode);
        if (!"OK".equals(smsResponse.getBody().getCode())) {
            return Result.failed(smsResponse.getBody().getMessage());
        }
        //保存到缓存
        setCache(uuid, String.valueOf(smsCode));
        return Result.ok("获取验证码成功");

    }

    @Override
    public boolean validate(String key, String code) {
        //获取验证码
        String captcha = getCache(key);
        //效验成功
        return code.equalsIgnoreCase(captcha);
    }

    public SendSmsResponse sendSms(String phone, int code) {
        Client client;
        try {
            client = createClient(accessKeyId, accessKeySecret);
        } catch (Exception e) {
            log.error("发送短信,使用初始化账号Client异常, {}", e.getMessage());
            return null;
        }
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                // 手机号
                .setPhoneNumbers(phone)
                // 签名,比如 "xxx公司",问运营要
                .setSignName(signName)
                // 模板编号,比如 SMS_123456,问运营要
                .setTemplateCode(templateCode)
                // json格式String类型模板,比如 "{"k1", v1, "k2", v2}",问运营要
                .setTemplateParam("{\"code\":" + code + "}");
        SendSmsResponse resp;
        try {
            resp = client.sendSms(sendSmsRequest);
        } catch (Exception e) {
            log.error("发送短信失败, {}", e.getMessage());
            return null;
        }
        log.info("发送短信【{}】, 验证码【{}】, 结果【{}】", phone, code, JSONObject.toJSONString(resp));
        if (resp == null || !Objects.equals(resp.getBody().getCode(), "OK")) {
            log.error("发送短信失败【{}】, 验证码【{}】, 结果【{}】", phone, code, JSONObject.toJSONString(resp));
            return null;
        }
        return resp;
    }

    /**
     * 初始化账号Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    private static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }


    private void setCache(String key, String value) {
        String smsKey = SMS_CODE + key;
        redisCache.setCacheObject(smsKey, value, 600, TimeUnit.SECONDS);
    }

    @Override
    public String getCache(String key) {
        String smsKey = SMS_CODE + key;
        return redisCache.getCacheObject(smsKey);
    }

    public static void main(String[] args) {
        System.out.println(Math.round((Math.random() + 1) * 1000));
    }
}
