package com.tfjt.pay.external.unionpay.enums;


import com.tfjt.tfcommon.dto.enums.ExceptionCode;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 异常码枚举
 * <p> 统一7位
 * 10xxxxx-公共异常
 * 20xxxxx-pms业务异常
 * 30xxxxx-fms业务异常
 * 40xxxxx-供应商业务异常
 * 50xxxxx-已存在
 * 60xxxxx- 供应商小程序
 * 70xxxxx- 商家
 * 80xxxxx- 商品
 * 90xxxxx- pay 支付 异常
 */
@ToString
@Getter
@AllArgsConstructor
public enum PayExceptionCodeEnum  implements ExceptionCode {
    TREAD_ORDER_NO_REPEAT(9000001, "业务单号已进行过交易"),


    BALANCE_ACCOUNT_NOT_FOUND(9000002,"电子账簿不存在"),

    BALANCE_ACCOUNT_FREEZE(9000003,"电子账簿已冻结"),
    TREAD_PARAMETER_ILLEGAL(9000004,"交易参数异常"),
    DATABASE_SAVE_FAIL(9000005,"数据保存异常"),
    DATABASE_UPDATE_FAIL(9000006,"数据更新异常"),
    CALLBACK_URL_NOT_FOUND(9000007,"应用回到地址未找到"),
    BALANCE_NOT_ENOUTH(9000008,"账号余额不足"),
    BALANCE_ACCOUNT_NAME_ERROR(9000009,"电子名称与电子账簿不符合"),
    PAYER_NOT_FOUND(9000010,"付款方不存在"),
    PAYER_TOO_MUCH(9000011,"暂不支付多个付款方"),
    PAYEE_NOT_FOUND(9000012,"收款方不存在"),
    UNIONPAY_CHECK_BILL_NOT_FOUND(9000015,"当日电子对账单不存在"),



    UNIONPAY_RESPONSE_ERROR(9000013, "银联系统交易异常"),
    SMSCODE_ERROR(9000014, "验证码失效，请重新填写。"),
    REPEAT_OPERATION(9000015,"请勿重复操作"),
    NO_DATA(9000016,"数据不存在"),

    /**
     * 贷款用户
     */
    NO_LOAN_USER(9001000,"贷款用户不存在"),

    EXISTED_BANK_CARD(9002000,"银行卡已经存在"),
    ABSENT_BANK_CARD(9002001,"银行卡不存在"),
    LAST_ONE_BANK_CARD(9002002,"解绑银行卡失败，至少保留一张银行卡"),
    BIND_BANK_CARD_SUCCESS(9002003,"绑定成功"),
    BIND_BANK_CARD_FAILED(9002004,"绑定失败"),
    UNBIND_BANK_CARD_FAILED(9002005,"解绑失败"),
    ;
    private int code;
    private String msg;

    public <T> Result<T> fail() {
        return Result.failed(this.getCode(), this.getMsg(), null);
    }
}
