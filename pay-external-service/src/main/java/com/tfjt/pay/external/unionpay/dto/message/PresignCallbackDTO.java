package com.tfjt.pay.external.unionpay.dto.message;

import lombok.Data;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/22 15:33
 * @description 天天企赋-签约状态变更回调实体
 */
@Data
public class PresignCallbackDTO {

    /**
     * 通知类型
     */
    private String type;

    /**
     * 最后一次请求id
     */
    private String orgReqId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 银行卡号
     */
    private String bankCardNo;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 实名认证状态
     */
    private Integer authStatus;

    /**
     * 签约状态
     */
    private Integer signStatus;

    /**
     * 绑卡状态
     */
    private Integer bindStatus;
}
