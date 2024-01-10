package com.tfjt.pay.external.unionpay.constants;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/22 15:41
 * @description
 */
public class PnSdkConstant {

    /**
     * 默认币种，人民币
     */
    public static final String DEFAULT_CCY = "RMB";

    /**
     * 平安银行编号
     */
    public static final String PN_BANK_CODE = "313";

    /**
     * http成功标识
     */
    public static final Integer HTTP_SUCCESS_CODE = 200;

    /**
     * 平安api成功标识
     */
    public static final String API_SUCCESS_CODE = "000000";

    /**
     * 平安api返回code字段
     */
    public static final String RESULT_CODE_FIELD = "Code";

    /**
     * 平安api返回数据字段
     */
    public static final String RESULT_DATA_FIELD = "Data";

    /**
     * 平安api返回错误字段
     */
    public static final String RESULT_ERRORS_FIELD = "Errors";

    /**
     * 平安api返回错误描述字段
     */
    public static final String RESULT_ERROR_MSG_FIELD = "ErrorMessage";

    /**
     * 开户返回子账户号字段
     */
    public static final String RESULT_SUB_ACCT_NO_FIELD = "SubAcctNo";
}
