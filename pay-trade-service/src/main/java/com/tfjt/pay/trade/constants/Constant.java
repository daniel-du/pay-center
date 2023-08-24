package com.tfjt.pay.trade.constants;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

/**
 * 常量类
 *
 * @author effine
 * @Date 2022/9/30 15:35
 * @email iballad#163.com
 */
public class Constant {

    /**
     * 项目名称
     */
    public static final String PROJECT_NAME = "tf-pay:";

    /**
     * 用户ID标识
     */
    public static final String USER_ID_STR = "userId";

    /**
     * 签到团号前缀
     */
    public static final String SIGN_IN_GROUP_NO_PREFIX = "SIGN";


    public static final String APP_TOKEN_KEY = PROJECT_NAME + "token:";


    /**
     * 签到奖励名称
     * TODO effine 优惠券名称待修改
     */
    public static final String SIGN_IN_AWARD_NAMES = "馒头优惠券";

    /**
     * 签到活动规则
     * TODO effine 活动规则待产品补充
     */
    public static final String SIGN_IN_RULES = "签到活动规则：活动周期7天......";

    /**
     * 银联微信测试地址
     */
    public static final String WECHAT_PAY_URL_TEST = "https://test-api-open.chinaums.com/v1/netpay/wx/unified-order";
    /**
     * 银联微信正式地址
     */
    public static final String WECHAT_PAY_URL = "https://api-mop.chinaums.com/v1/netpay/wx/unified-order";
    /**
     * 银联支付宝测试地址
     */
    public static final String ALI_PAY_TEST = "https://test-api-open.chinaums.com/v1/netpay/trade/create";
    /**
     * 银联支付宝正式地址
     */
    public static final String ALI_PAY = "https://api-mop.chinaums.com/v1/netpay/trade/create";
    /**
     * 银联云闪付测试地址
     */
    public static final String CLOUD_FLASH_PAYMENT_TEST = "https://test-api-open.chinaums.com/v1/netpay/uac/mini-order";
    /**
     * 银联云闪付正式地址
     */
    public static final String CLOUD_FLASH_PAYMENT = "https://api-mop.chinaums.com/v1/netpay/uac/mini-order";
    /**
     * 订单交易查询地址
     */
    public static final String ORDER_RECORD_TEST_URL = "https://test-api-open.chinaums.com/v1/netpay/query";
    public static final String ORDER_RECORD_URL = "https://api-mop.chinaums.com/v1/netpay/query";

    /**
     * 退款路径
     */
    public static final String ORDER_REFUND_TEST_URL = "https://test-api-open.chinaums.com/v1/netpay/refund";
    public static final String ORDER_REFUND_URL = "https://api-mop.chinaums.com/v1/netpay/refund";

    /**
     * 退款查询路径
     */
    public static final String ORDER_REFUND_QUERY_TEST_URL = "https://test-api-open.chinaums.com/v1/netpay/refund-query";
    public static final String ORDER_REFUND_QUERY_URL = "https://api-mop.chinaums.com/v1/netpay/refund-query";

    /**
     * 订单关闭路径
     */
    public static final String ORDER_CLOSE_TEST_URL = "https://test-api-open.chinaums.com/v1/netpay/close";
    public static final String ORDER_CLOSE_URL = "https://api-mop.chinaums.com/v1/netpay/close";

    /**
     * 订单状态
     */
    public static final Integer ORDER_STATUS_SUCCESS = 1;
    public static final Integer ORDER_STATUS_ERROR = 2;
    public static final Integer ORDER_STATUS_NO_ANSWER = 3;

    /**
     * 回调类别
     */
    public static final Integer PAY_RESULT_TYPE_PAY = 1;
    public static final Integer PAY_RESULT_TYPE_REFUND = 5;

    /**
     * 验签 支付 退款 回调状态
     */
    public static final String PAY_RESULT_STATUS_SUCCESS = "SUCCESS";
    public static final String PAY_RESULT_STATUS_FAILED = "FAILED";

    public static final String URL_HEADER = "https://api-mop.chinaums.com/v1/netpay/";

    //失败预判码
    public static final Set<String> FAIL_PREDICT = ImmutableSet.of("A","B");

}


























