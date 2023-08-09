package com.tfjt.pay.external.unionpay.utils;

import com.aliyun.ocr_api20210707.Client;
import com.aliyun.teaopenapi.models.Config;

/**
 * @version V1.0
 * @description:
 * @author: Cdx
 * @date: 2023/3/16
 */
public class OCRApiUtil {

    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";
        return new Client(config);
    }

}
