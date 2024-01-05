package com.tfjt.pay.external.unionpay.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pingan.openbank.api.sdk.client.ApiClient;
import com.pingan.openbank.api.sdk.common.http.HttpResult;
import com.pingan.openbank.api.sdk.entity.SdkRequest;
import com.pingan.openbank.api.sdk.exception.OpenBankSdkException;
import com.tfjt.pay.external.unionpay.constants.PnSdkConstant;
import com.tfjt.pay.external.unionpay.entity.TfIncomingApiLogEntity;
import com.tfjt.pay.external.unionpay.enums.ExceptionCodeEnum;
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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/20 11:07
 * @description 平安api报文头
 */
@Slf4j
@Component
public class PnHeadUtils {

    @Autowired
    private TfIncomingApiLogService incomingApiLogService;

    protected static String MrchCode="5655";

    /**
     * http成功标识
     */
    private static final Integer HTTP_SUCCESS_CODE = 200;

    /**
     * 平安api成功标识
     */
    public static final String API_SUCCESS_CODE = "000000";

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
        //交易流水号:系统流水号，建议规范：用户短号（6位）+日期（6位）+随机编号（10位）例：C256341801183669951236平台也可自行定义，满足长度即可
        jsonObject.put("CnsmrSeqNo", seq);
        //交易码
        jsonObject.put("TxnCode", txnCode);
        //发送时间:格式为YYYYMMDDHHmmSSNNN后三位固定000
        jsonObject.put("TxnTime", simpleDateFormat1.format(new Date()));
        //商户号:签约客户号，见证宝产品此字段为必输
//        jsonObject.put("MrchCode", MrchCode);
        //商户号:交易客户号，Ecif客户号（例：680000376596）
        jsonObject.put("TxnClientNo", txnClientNo);
        //监管账户
//        jsonObject.put("FundSummaryAcctNo", "15000101232520");
        jsonObject.put("FundSummaryAcctNo", fundSummaryAcctNo);
        SdkRequest sdkRequest = new SdkRequest();
        sdkRequest.setInterfaceName(serviceId);
        sdkRequest.setBody(jsonObject);
        log.info("PnHeadUtils---send, txnCode:{}, serviceId:{}, jsonObject:{}", txnCode, serviceId, jsonObject.toJSONString());
        sw.start();
        HttpResult httpResult = apiClient.invoke(sdkRequest);
        sw.stop();
        log.info("PnHeadUtils---send, txnCode:{}, serviceId:{}, sw:{}, httpResult:{}", txnCode, serviceId, sw.getLastTaskTimeMillis(), httpResult.toString());
        //http响应为空
        if (ObjectUtils.isEmpty(httpResult)) {

        }
        //http响应失败
        if (!HTTP_SUCCESS_CODE.equals(httpResult.getCode())) {

        }
        String resultMessage = httpResult.getData();
        System.out.println("requestBody=" + jsonObject);
        System.out.println("responseBody" + httpResult.getData());
        //接口返回结果为空，抛出异常
        if (StringUtils.isBlank(resultMessage)) {
            throw new TfException(ExceptionCodeEnum.ACCOUNT_DISABLE);
        }
        JSONObject resultJson = JSONObject.parseObject(resultMessage);
        log.info("PnHeadUtils---send, resultJson:{}", resultJson.toJSONString());
        //平安api返回标识非成功
        if (!API_SUCCESS_CODE.equals(resultJson.getString("Code"))) {
//            throw new TfException(errorProcess(resultJson));
        }

        JSONObject dataJson = resultJson.getJSONObject("Data");
        log.info("PnHeadUtils---send, dataJson:{}", dataJson.toJSONString());
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

    @Async
    public void logProcess(JSONObject jsonObject, String txnCode, HttpResult httpResult, LocalDateTime reqTime,
                           LocalDateTime respTime, Long comsumingTime) {
        TfIncomingApiLogEntity incomingApiLogEntity = new TfIncomingApiLogEntity();
//        incomingApiLogEntity.setUrl();
        incomingApiLogEntity.setApiCode(txnCode);
        incomingApiLogEntity.setRequestTime(reqTime);
        incomingApiLogEntity.setResponseTime(respTime);
        incomingApiLogEntity.setRequestParam(jsonObject.toJSONString());
        incomingApiLogEntity.setResponseBody(JSONObject.toJSONString(httpResult));
    }
}
