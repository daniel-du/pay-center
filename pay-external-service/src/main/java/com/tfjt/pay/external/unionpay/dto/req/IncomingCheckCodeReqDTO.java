package com.tfjt.pay.external.unionpay.dto.req;

import com.tfjt.pay.external.unionpay.enums.IncomingAccessChannelTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingSettleTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:10
 * @description 进件绑定银行卡请求参数
 */
@Data
public class IncomingCheckCodeReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 进件id
     */
    private Long incomingId;

    /**
     * 短信验证码
     */
    private String messageCheckCode;

    /**
     * 打款金额
     */
    private BigDecimal authAmt;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * MAC地址
     */
    private String macAddress;

}
