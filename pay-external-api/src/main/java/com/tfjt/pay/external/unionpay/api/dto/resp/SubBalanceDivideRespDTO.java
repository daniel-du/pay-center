package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-14 11:08
 * @email 598482054@qq.com
 */
@Data
public class SubBalanceDivideRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**银行附言*/
    private String remark;
    /**交易状态*/
    private String status;
    /**失败原因*/
    private String reason;
    /**完成时间*/
    private Long finishedAt;
    /**支付系统 id*/
    private Long paySystemId;

}
