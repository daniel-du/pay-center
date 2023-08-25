package com.tfjt.pay.trade.enums;

import com.tfjt.tfcommon.dto.response.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 异常码枚举
 * <p>
 * 50xx-公共异常
 * 51xx-签到业务异常
 *
 * @author effine
 * @Date 2021/1/18 16:45
 * @email iballad@163.com
 */
@ToString
@Getter
@AllArgsConstructor
public enum ExceptionCodeEnum {


    /**
     * 服务器走丢了
     */
    SERVER_EXCEPTION(5001, "服务器走丢了"),
    /**
     * 参数异常
     */
    PARAM_EXCEPTION(5002, "参数校验不通过"),

    /**
     * 当天已签到
     */
    ALREADY_SIGN_IN(5101, "当天已签到"),

    SIGN_IN_FAIL(5102, "签到失败"),

    DATA_NOT_EXISTS(5004, "数据不存在"),

    DATA_EXISTS(5005, "数据已存在"),

    APP_ISNULL(5007, "应用不存在"),

    APP_SECRET_ERROR(5008, "应用密钥错误"),

    /**
     * accesser_acct与ums_reg_id必传一个
     */
    YINLIAN_SIGN_ACCESSER_ACCT(6001, "accesser_acct与ums_reg_id必传一个"),

    ;

    private final int code;
    private final String msg;

    public <T> Result<T> fail() {
        return Result.failed(this.getCode(), this.getMsg(), null);
    }
}
