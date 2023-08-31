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
    /**
     * 通用异常
     */
    TREAD_PARAMETER_ILLEGAL(9000001,"交易参数异常"),
    DATABASE_SAVE_FAIL(9000002,"数据保存异常"),
    DATABASE_UPDATE_FAIL(9000003,"数据更新异常"),
    SMSCODE_ERROR(9000004, "验证码失效，请重新填写。"),
    REPEAT_OPERATION(9000005,"请勿重复操作"),
    NO_DATA(9000006,"数据不存在"),
    UPLOAD_FILE_ERROR(9000007,"上传文件失败"),
    SIGN_ERROR(9000008,"验签失败"),
    SAVE_DATA_ERROR(9001000,"保存数据异常"),
    UPDATE_DATA_ERROR(9001001,"更新数据异常"),
    SERVICE_ERROR(9002000,"服务异常"),

    /**
     * 贷款用户
     */
    NO_LOAN_USER(9001000,"贷款用户不存在"),
    BALANCE_ACCOUNT_NOT_FOUND(9001001,"电子账簿不存在"),
    BALANCE_ACCOUNT_FREEZE(9001002,"电子账簿已冻结"),
    BALANCE_NOT_ENOUTH(9001003,"账号余额不足"),
    BALANCE_ACCOUNT_NAME_ERROR(9001004,"电子名称与电子账簿不符合"),
    PAYER_NOT_FOUND(9001005,"付款方不存在"),
    PAYER_TOO_MUCH(9001006,"暂不支付多个付款方"),
    PAYEE_NOT_FOUND(9001007,"收款方不存在"),
    /**
     * 查询银行编码失败
     */
    QUERY_BANK_CODE_FAILED(9001008,"查询银行编码失败"),

    /**
     * 银行卡
     */
    EXISTED_BANK_CARD(9002000,"银行卡已经存在"),
    ABSENT_BANK_CARD(9002001,"银行卡不存在"),
    LAST_ONE_BANK_CARD(9002002,"解绑银行卡失败，至少保留一张银行卡"),
    BIND_BANK_CARD_SUCCESS(9002003,"绑定成功"),
    BIND_BANK_CARD_FAILED(9002004,"绑定失败"),
    UNBIND_BANK_CARD_FAILED(9002005,"解绑失败"),
    NOT_NULL_MERCHANT(9002006,"商户简称不能为空"),

    /**
     * 银联交易
     */
    UNIONPAY_RESPONSE_ERROR(9003001, "银联系统交易异常"),
    UNIONPAY_CHECK_BILL_NOT_FOUND(9003002,"当日电子对账单不存在"),
    CALLBACK_URL_NOT_FOUND(9003003,"应用回到地址未找到"),
    TREAD_ORDER_NO_REPEAT(9003004, "业务单号已进行过交易"),
    NO_SETTLE_ACCT(9003006, "没有绑定账号不能进行打款"),


    ;
    private int code;
    private String msg;

    public <T> Result<T> fail() {
        return Result.failed(this.getCode(), this.getMsg(), null);
    }
}
