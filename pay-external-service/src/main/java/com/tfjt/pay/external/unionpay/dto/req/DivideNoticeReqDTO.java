package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * 分账回调FMS参数
 * @author songx
 * @date 2023-08-21 10:19
 * @email 598482054@qq.com
 */
@Data
public class DivideNoticeReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 支付系统id
     */
    private Long paySystemId;
    /**
     * 完成时间
     */
    private Long finishAt;
    /**
     * 状态
     */
    private String status;
}
