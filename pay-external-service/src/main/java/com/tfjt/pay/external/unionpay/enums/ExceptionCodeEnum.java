package com.tfjt.pay.external.unionpay.enums;

import com.tfjt.tfcommon.dto.enums.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 异常码枚举
 * <p> 统一7位
 * 10xxxxx-公共异常
 * @author 李晓雷
 * @Date 2021/1/18 16:45
 * @email iballad@163.com
 */
@ToString
@Getter
@AllArgsConstructor
public enum ExceptionCodeEnum implements ExceptionCode {
    SUCCESS(0, "成功"),
    FAIL(500, "系统异常"),
    NOT_PERMISSIONS(401, "无权限"),
    NOT_NULL(1000001, "不能为空"),

    EXPORT_TIME_ERROR(3000001,"五分钟内不能重复导出！"),

    ILLEGAL_ARGUMENT(1000002, "参数异常"),
    IS_NULL(1000010,"数据不存在"),
    DB_RECORD_EXISTS(1000002,""),
    PARAMS_GET_ERROR(1000003,""),
    ACCOUNT_PASSWORD_ERROR(1000004,""),
    ACCOUNT_DISABLE(1000005,""),
    IDENTIFIER_NOT_NULL(1000006,""),
    CAPTCHA_ERROR(1000007,""),
    SUB_MENU_EXIST(1000008,""),
    PASSWORD_ERROR(1000009,""),
    MOBLE_ERROR(1000011,""),
    SUPERIOR_MENU_ERROR(1000012,""),
    DATA_SCOPE_PARAMS_ERROR(1000013,""),
    DEPT_SUB_DELETE_ERROR(1000014,""),
    DEPT_USER_DELETE_ERROR(1000015,""),
    UPLOAD_FILE_EMPTY(1000019,""),
    TOKEN_NOT_EMPTY(1000020,""),
    TOKEN_INVALID(1000021,""),
    ACCOUNT_LOCK(1000022,""),
    OSS_UPLOAD_FILE_ERROR(1000024,""),
    REDIS_ERROR(1000027,""),
    JOB_ERROR(1000028,""),
    INVALID_SYMBOL(1000029,""),
    SUPPLIER_IDENTITY_ISNULL(1000030,"获取经销商用户信息失败！"),


    DATA_LIMIT_31(2000001,"最多只能导出31天的数据!"),
    INCOMING_BUSINESS_ID_IS_NULL(2000002,"营业信息id或营业执照id不能为空!"),
    INCOMING_MERCHANT_ID_IS_NULL(2000003,"商户身份信息id或证件id不能为空!"),
    INCOMING_AGENT_NAME_IS_NULL(2000004,"经办人姓名不能为空!"),
    INCOMING_AGENT_MOBILE_IS_NULL(2000005,"经办人手机号不能为空!"),
    INCOMING_AGENT_ID_NO_IS_NULL(2000006,"经办人证件号码不能为空!"),
    INCOMING_AGENT_EFFECTIVE_IS_NULL(2000007,"经办人证件有效起始日期不能为空!"),
    INCOMING_AGENT_EXPIRE_IS_NULL(2000008,"经办人证件有效截止日期不能为空!"),
    INCOMING_AGENT_IS_LONG_TERM_IS_NULL(2000009,"经办人证件是否长期不能为空!"),
    INCOMING_AGENT_IS_LEGAL_IS_NULL(2000010,"经办人同法人标识不能为空!"),
    INCOMING_AGENT_MOBILE_FORMAT_ERROR(2000011,"经办人手机号格式错误!"),
    INCOMING_AGENT_ID_NO_FORMAT_ERROR(2000012,"经办人证件号码格式错误!"),
    INCOMING_EMAIL_FORMAT_ERROR(2000013,"邮箱格式错误!"),
    INCOMING_BANK_CARD_ACCOUNT_ERROR(2000014,"开户名称必须与法人姓名一致!"),
    INCOMING_BUSINESS_LICENSE_NO_REPEAT(2000015,"统一社会信用代码重复!"),
    INCOMING_LEGAL_ID_NO_FORMAT_ERROR(2000016,"法人证件号码格式错误!"),
    INCOMING_CHANGE_MAIN_TYPE_CODE_ERROR(2000016,"入网主体不允许变更为该类型!"),
    QUERY_INCOMING_MSG_ILLEGAL_ARGUMENT(2000017,"查询进件信息入参异常!"),
    INCOMING_STRATEGY_SERVICE_IS_NULL(2000018,"未找到对应策略服务!"),
    INCOMING_AGENT_FRONT_URL_IS_NULL(2000019,"经办人身份证正面照片不能为空!"),
    INCOMING_AGENT_BACK_URL_IS_NULL(2000020,"经办人身份证背面照片不能为空!"),
    INCOMING_DATA_IS_NULL(2000021, "入网信息为空，请返回重新选择进件主体!"),
    INCOMING_DATA_ALREADY_EXIST(2000022, "当前用户已存在入网信息，请勿重复入网!"),
    INCOMING_LEGAL_EXPIRE_IS_NULL(2000023,"法人证件有效截止日期不能为空!"),
    INCOMING_BANK_CARD_REPEAT(2000024,"结算银行账号重复!"),
    INCOMING_FREQUENT_OPERATION(2000025,"请2分钟之后重新点击!"),
    INCOMING_CORPORATE_BANK_CARD_ACCOUNT_ERROR(2000026,"对公类型开户名称必须与营业名称一致!"),
    INCOMING_INFO_IS_NULL(2000027,"入网信息为空!"),
    SUPPLIER_IS_NULL(2000028,"supplier信息为空!"),
    AREA_CAN_NOT_NULL(2000029,"区域不能为空!"),
    CHANNEL_CODE_CAN_NOT_NULL(2000033,"进件类型不能为空" ),

    PN_API_ERROR(3000010, "调用平安API失败!"),
    PN_API_RESULT_IS_NULL(3000011, "平安api返回结果为空!"),
    //解密失败
    SIGN_DECRYPT_ERROR(4000001, "解密失败!"),
    ;
    private int code;
    private String msg;

}
