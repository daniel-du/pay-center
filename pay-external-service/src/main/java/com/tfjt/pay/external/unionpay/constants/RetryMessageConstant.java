package com.tfjt.pay.external.unionpay.constants;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/1/5 8:30
 * @description 消息重试常量
 */
public class RetryMessageConstant {

    /**
     * 进件完成
     */
    public static final String INCOMING_FINISH = "1";

    public static final String SIGN_REVIEW = "SIGN_REVIEW";


    public static final String MQ_FROM_SERVER = "tf-cloud-pay-external";

    public static final String MQ_TO_SERVER = "tf-cloud-pay-external";
    public static final String SIGN_TAG = "sign";


    public static final String SETTLE_CENTER_APPLICATION_NAME = "tf-cloud-settle-center";
}
