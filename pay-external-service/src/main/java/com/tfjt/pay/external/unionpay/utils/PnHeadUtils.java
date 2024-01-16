package com.tfjt.pay.external.unionpay.utils;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pingan.openbank.api.sdk.client.ApiClient;
import com.pingan.openbank.api.sdk.common.http.HttpResult;
import com.pingan.openbank.api.sdk.entity.SdkRequest;
import com.pingan.openbank.api.sdk.exception.OpenBankSdkException;
import com.tfjt.pay.external.unionpay.config.ALiYunRocketMQConfig;
import com.tfjt.pay.external.unionpay.config.PnClientConfig;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
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

    @Autowired
    private PnClientConfig pnClientConfig;

    /**
     * http�ɹ���ʶ
     */
    private static final Integer HTTP_SUCCESS_CODE = 200;

    @Value("${pnclient.mrchCode}")
    private String mrchCodeValue;

    @Value("${pnclient.txnClientNo}")
    private String txnClientNoValue;

    @Value("${pnclient.fundSummaryAcctNo}")
    private String fundSummaryAcctNoValue;


    protected static String txnClientNo;

    protected static String fundSummaryAcctNo;

    protected static ApiClient apiClient;

    protected static String mrchCode;

    @PostConstruct
    public void init() {
        apiClient = ApiClient.getInstance(pnClientConfig.getClientPropertie());
        mrchCode = mrchCodeValue;
        txnClientNo = txnClientNoValue;
        fundSummaryAcctNo = fundSummaryAcctNoValue;
    }


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
        jsonObject.put("MrchCode", mrchCode);
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
        incomingApiLogService.logProcessAsync(jsonObject, serviceId, httpResult, reqTime, respTime, sw.getLastTaskTimeMillis());
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
        if (CollectionUtils.isEmpty(errorArray)) {
            return new JSONObject();
        }
        log.info("PnHeadUtils---getError, errorArray:{}", JSONArray.toJSONString(errorArray));
        JSONObject errorJson = errorArray.getJSONObject(0);
//        return errorJson.getString("ErrorMessage");
//        System.out.println("ErroeCode=" + errorJson.getString("ErrorCode") + ",ErrorMessage=" + errorJson.getString("ErrorMessage"));
//        JSONObject extendJson = resultJson.getJSONObject("ExtendData");
//        System.out.println("extendJson=" + JSONArray.toJSONString(extendJson));
        return errorJson;
    }

}
