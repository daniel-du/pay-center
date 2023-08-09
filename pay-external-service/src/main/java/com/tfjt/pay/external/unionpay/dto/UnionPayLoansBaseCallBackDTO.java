package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款打款验证基类回调参数
 */
@Data
public class UnionPayLoansBaseCallBackDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String eventId;//	事件ID;
    private String eventType;//	事件类型;
    private String createdAt;//事件创建时间
    private Object eventData; //时间内容
}
