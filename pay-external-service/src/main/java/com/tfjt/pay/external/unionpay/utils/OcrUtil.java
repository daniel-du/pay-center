package com.tfjt.pay.external.unionpay.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: OcrUtil <br>
 * @date: 2023/5/20 10:30 <br>
 * @author: young <br>
 * @version: 1.0
 */
@Slf4j
public class OcrUtil {
    public static String ocr(String host, String path, String appcode, String bodys) {
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Map<String, String> querys = new HashMap<>();
        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            } else {
                throw new RuntimeException(response.toString());
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }
}
