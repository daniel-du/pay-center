package com.tfjt.pay.external.unionpay.dto.req;

import com.tfjt.pay.external.unionpay.dto.GuaranteePaymentDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ConsumerPoliciesReqDTO
 * @description: 合并消费担保下单
 * @author Lzh
 * @date 2023年08月09日
 * @version: 1.0
 */
@Data
public class ConsumerPoliciesReqDTO implements Serializable {
    /**平台订单号 1、平台的唯一请求单号2、要求64个字符内，只能是数字、大小写字母和_-*/
    private String combinedOutOrderNo;

    /**发送时间 格式:RFC3339*/
    private String sentAt;

    /**付款电子账簿ID 付款用户的电子账簿ID*/
    private String payBalanceAcctId;

    /**交易授权码  即密码 需要加密处理，加密方法详  见：敏感信息加密说明*/
    private String password;

    /**担保消费明细参数*/
    private List<GuaranteePaymentDTO> guaranteePaymentParams;

    /**
     * 备注
     */
    private String remark;
    /**
     * 扩展字段
     */
    private Map<String,Object> extra;
    /**
     * 自定义参数
     */
    private Map<String,Object> metadata;
}
