package com.tfjt.pay.external.unionpay.utils;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pingan.openbank.api.sdk.client.ApiClient;
import com.pingan.openbank.api.sdk.common.http.HttpResult;
import com.pingan.openbank.api.sdk.entity.SdkRequest;
import com.pingan.openbank.api.sdk.exception.OpenBankSdkException;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.PnSdkConstant;
import com.tfjt.pay.external.unionpay.entity.TfIncomingApiLogEntity;
import com.tfjt.pay.external.unionpay.enums.ExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessChannelTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessTypeEnum;
import com.tfjt.pay.external.unionpay.service.TfIncomingApiLogService;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/20 11:07
 * @description ƽ��api����ͷ
 */
@Slf4j
@Component
public class PnHeadUtils {

    @Autowired
    private TfIncomingApiLogService incomingApiLogService;

    protected static String MrchCode="5655";

    /**
     * http�ɹ���ʶ
     */
    private static final Integer HTTP_SUCCESS_CODE = 200;

    /**
     * ƽ��api�ɹ���ʶ
     */
    public static final String API_SUCCESS_CODE = "000000";

    private static final String REQUEST_TYPE = "POST";

//    protected static ApiClient apiClient = ApiClient.getInstance("conf/config-fat007.properties");
//
//    protected static String TxnClientNo="680001343736";


    protected static String confPath;

    protected static String txnClientNo;

    protected static String fundSummaryAcctNo;

//    @Value("${pnIncoming.confPath}")
//    private void setConfPath(String confPath) {
//        this.confPath = confPath;
//    }
//
//    @Value("${pnIncoming.txnClientNo}")
//    private void setTxnClientNo(String txnClientNo) {
//        this.txnClientNo = txnClientNo;
//    }
//
//    @Value("${pnIncoming.fundSummaryAcctNo}")
//    private void setFundSummaryAcctNo(String fundSummaryAcctNo) {
//        this.fundSummaryAcctNo = fundSummaryAcctNo;
//    }

//    protected static ApiClient apiClient = ApiClient.getInstance(confPath);
    protected static ApiClient apiClient = ApiClient.getInstance("pnconf/config-fat007.properties");



    public JSONObject send(JSONObject jsonObject, String txnCode, String serviceId) throws OpenBankSdkException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
        StopWatch sw = new StopWatch();
        Random random = new Random();
        String seq = simpleDateFormat.format(new Date()) + "" + random.nextInt(99999999);
        //������ˮ��:ϵͳ��ˮ�ţ�����淶���û��̺ţ�6λ��+���ڣ�6λ��+�����ţ�10λ������C256341801183669951236ƽ̨Ҳ�����ж��壬���㳤�ȼ���
        jsonObject.put("CnsmrSeqNo", seq);
        //������
        jsonObject.put("TxnCode", txnCode);
        //����ʱ��:��ʽΪYYYYMMDDHHmmSSNNN����λ�̶�000
        jsonObject.put("TxnTime", simpleDateFormat1.format(new Date()));
        //�̻���:ǩԼ�ͻ��ţ���֤����Ʒ���ֶ�Ϊ����
//        jsonObject.put("MrchCode", MrchCode);
        //�̻���:���׿ͻ��ţ�Ecif�ͻ��ţ�����680000376596��
        jsonObject.put("TxnClientNo", txnClientNo);
        //����˻�
//        jsonObject.put("FundSummaryAcctNo", "15000101232520");
        jsonObject.put("FundSummaryAcctNo", fundSummaryAcctNo);
        SdkRequest sdkRequest = new SdkRequest();
        sdkRequest.setInterfaceName(serviceId);
        sdkRequest.setBody(jsonObject);
        log.info("PnHeadUtils---send, txnCode:{}, serviceId:{}, requestBody:{}", txnCode, serviceId, jsonObject.toJSONString());
        LocalDateTime reqTime = LocalDateTime.now();
        sw.start();
        HttpResult httpResult = apiClient.invoke(sdkRequest);
        sw.stop();
        LocalDateTime respTime = LocalDateTime.now();
        log.info("PnHeadUtils---send, txnCode:{}, serviceId:{}, sw:{}, responseBody:{}", txnCode, serviceId, sw.getLastTaskTimeMillis(), httpResult.toString());
        //�첽���������־
        logProcessAsync(jsonObject, serviceId, httpResult, reqTime, respTime, sw.getLastTaskTimeMillis());
        //http��ӦΪ��
        if (ObjectUtils.isEmpty(httpResult)) {
            throw new TfException(ExceptionCodeEnum.PN_API_RESULT_IS_NULL);
        }
        //http��Ӧʧ��
        if (!HTTP_SUCCESS_CODE.equals(httpResult.getCode())) {
            throw new TfException(ExceptionCodeEnum.PN_API_ERROR);
        }
        String resultMessage = httpResult.getData();
        //�ӿڷ��ؽ��Ϊ�գ��׳��쳣
        if (StringUtils.isBlank(resultMessage)) {
            throw new TfException(ExceptionCodeEnum.PN_API_RESULT_IS_NULL);
        }
        JSONObject resultJson = JSONObject.parseObject(resultMessage);
        log.info("PnHeadUtils---send, resultJson:{}", resultJson.toJSONString());
//        //ƽ��api���ر�ʶ�ǳɹ�
//        if (!API_SUCCESS_CODE.equals(resultJson.getString("Code"))) {
////            throw new TfException(errorProcess(resultJson));
//        }
//
//        JSONObject dataJson = resultJson.getJSONObject("Data");
//        log.info("PnHeadUtils---send, dataJson:{}", dataJson.toJSONString());
        return resultJson;
    }

    public static JSONObject getError(JSONObject resultJson) {
        JSONArray errorArray = resultJson.getJSONArray(PnSdkConstant.RESULT_ERRORS_FIELD);
        System.out.println("errorArray=" + JSONArray.toJSONString(errorArray));
        JSONObject errorJson = errorArray.getJSONObject(0);
//        return errorJson.getString("ErrorMessage");
        System.out.println("ErroeCode=" + errorJson.getString("ErrorCode") + ",ErrorMessage=" + errorJson.getString("ErrorMessage"));
        JSONObject extendJson = resultJson.getJSONObject("ExtendData");
        System.out.println("extendJson=" + JSONArray.toJSONString(extendJson));
        return errorJson;
    }

    /**
     * �첽���������־
     * @param jsonObject
     * @param serviceId
     * @param httpResult
     * @param reqTime
     * @param respTime
     * @param comsumingTime
     */
    @Async
    public void logProcessAsync(JSONObject jsonObject, String serviceId, HttpResult httpResult, LocalDateTime reqTime,
                           LocalDateTime respTime, Long comsumingTime) {
        TfIncomingApiLogEntity incomingApiLogEntity = new TfIncomingApiLogEntity();
        incomingApiLogEntity.setUrl(serviceId);
        incomingApiLogEntity.setApiCode(jsonObject.getString("TxnCode"));
        incomingApiLogEntity.setRequestType(REQUEST_TYPE);
        incomingApiLogEntity.setRequestTime(reqTime);
        incomingApiLogEntity.setResponseTime(respTime);
        incomingApiLogEntity.setRequestParam(jsonObject.toJSONString());
        incomingApiLogEntity.setResponseBody(JSONObject.toJSONString(httpResult));
        incomingApiLogEntity.setConsumeTime(comsumingTime.intValue());
        incomingApiLogEntity.setStatus(NumberConstant.ONE);
        incomingApiLogEntity.setAccessChannelType(IncomingAccessChannelTypeEnum.PINGAN.getCode().byteValue());
        incomingApiLogEntity.setAccessType(IncomingAccessTypeEnum.COMMON.getCode().byteValue());
        if (!HTTP_SUCCESS_CODE.equals(httpResult.getCode())) {
            incomingApiLogEntity.setStatus(NumberConstant.TWO);
        }
        JSONObject resultJson = JSONObject.parseObject(httpResult.getData());
        if (!API_SUCCESS_CODE.equals(resultJson.getString("Code"))) {
            incomingApiLogEntity.setStatus(NumberConstant.TWO);
        }
        incomingApiLogService.save(incomingApiLogEntity);
    }
}
