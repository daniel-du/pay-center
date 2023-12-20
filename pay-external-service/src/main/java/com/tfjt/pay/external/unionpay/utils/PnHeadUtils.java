package com.tfjt.pay.external.unionpay.utils;


import com.alibaba.fastjson.JSONObject;
import com.pingan.openbank.api.sdk.client.ApiClient;
import com.pingan.openbank.api.sdk.common.http.HttpResult;
import com.pingan.openbank.api.sdk.entity.SdkRequest;
import com.pingan.openbank.api.sdk.exception.OpenBankSdkException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/20 11:07
 * @description 平安api报文头
 */
public class PnHeadUtils {
    protected static String MrchCode="5655";
    protected static ApiClient apiClient = ApiClient.getInstance("conf/config-fat007.properties");

    protected static String TxnClientNo="680001343736";

    public static void send(JSONObject jsonObject, String txnCode, String serviceId) throws OpenBankSdkException {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
        Random random = new Random();
        String seq = simpleDateFormat.format(new Date()) + "" + random.nextInt(99999999);
        //交易流水号:系统流水号，建议规范：用户短号（6位）+日期（6位）+随机编号（10位）例：C256341801183669951236平台也可自行定义，满足长度即可
        jsonObject.put("CnsmrSeqNo", seq);
        //交易码
        jsonObject.put("TxnCode", txnCode);
        //发送时间:格式为YYYYMMDDHHmmSSNNN后三位固定000
        jsonObject.put("TxnTime", simpleDateFormat1.format(new Date()));
        //商户号:签约客户号，见证宝产品此字段为必输
        jsonObject.put("MrchCode", MrchCode);
        //商户号:交易客户号，Ecif客户号（例：680000376596）
        jsonObject.put("TxnClientNo", TxnClientNo);
        //监管账户
        jsonObject.put("FundSummaryAcctNo", "15000101232520");
        SdkRequest sdkRequest = new SdkRequest();
        sdkRequest.setInterfaceName(serviceId);
        sdkRequest.setBody(jsonObject);

        HttpResult httpResult = apiClient.invoke(sdkRequest);
        String resultMessage = httpResult.getData();
        System.out.println("requestBody=" + jsonObject);
        System.out.println("responseBody" + httpResult.getData());
    }
}
