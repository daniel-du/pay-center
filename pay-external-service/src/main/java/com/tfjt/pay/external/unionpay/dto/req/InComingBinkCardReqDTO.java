package com.tfjt.pay.external.unionpay.dto.req;

import com.tfjt.pay.external.unionpay.enums.IncomingAccessChannelTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingAccessTypeEnum;
import com.tfjt.pay.external.unionpay.enums.IncomingSettleTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:10
 * @description 进件绑定银行卡请求参数
 */
@Data
public class InComingBinkCardReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 进件id
     */
    private Long incomingId;

    /**
     * 入网渠道
     * @see IncomingAccessChannelTypeEnum
     */
    private Integer accessChannelType;

    /**
     * 入网类型
     * @see IncomingAccessTypeEnum
     */
    private Integer accessType;

    /**
     * 结算账户类型
     * @see IncomingSettleTypeEnum
     */
    private Integer settelAccountType;
}
