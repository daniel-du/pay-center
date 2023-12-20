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
 * @description ƽ��api����ͷ
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
        //������ˮ��:ϵͳ��ˮ�ţ�����淶���û��̺ţ�6λ��+���ڣ�6λ��+�����ţ�10λ������C256341801183669951236ƽ̨Ҳ�����ж��壬���㳤�ȼ���
        jsonObject.put("CnsmrSeqNo", seq);
        //������
        jsonObject.put("TxnCode", txnCode);
        //����ʱ��:��ʽΪYYYYMMDDHHmmSSNNN����λ�̶�000
        jsonObject.put("TxnTime", simpleDateFormat1.format(new Date()));
        //�̻���:ǩԼ�ͻ��ţ���֤����Ʒ���ֶ�Ϊ����
        jsonObject.put("MrchCode", MrchCode);
        //�̻���:���׿ͻ��ţ�Ecif�ͻ��ţ�����680000376596��
        jsonObject.put("TxnClientNo", TxnClientNo);
        //����˻�
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
