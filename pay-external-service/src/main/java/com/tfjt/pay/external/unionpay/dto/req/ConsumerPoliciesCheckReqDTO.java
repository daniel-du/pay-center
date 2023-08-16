package com.tfjt.pay.external.unionpay.dto.req;

import com.tfjt.pay.external.unionpay.dto.GuaranteePaymentDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ConsumerPoliciesReqDTO
 * @description: 合并消费担保确认
 * @author Lzh
 * @date 2023年08月09日
 * @version: 1.0
 */
@Data
public class ConsumerPoliciesCheckReqDTO extends ConsumerPoliciesReqDTO implements Serializable {
    /**平台订单号 1、平台的唯一请求单号2、要求64个字符内，只能是数字、大小写字母和_-*/
    private String outOrderNo;

    /**合并消费担保下单子订单系统订单号*/
    private String guaranteePaymentId;

    /**分账参数*/
    private List<GuaranteePaymentDTO> transferParams;

    /**
     * 分账交易授权码
     */
    private String transferPassword;

    /**
     * 确认金额
     */
    private Integer amount;
}
