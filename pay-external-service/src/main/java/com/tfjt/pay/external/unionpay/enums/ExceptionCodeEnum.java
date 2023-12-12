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
    ;
    private int code;
    private String msg;

}
