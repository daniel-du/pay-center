package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款打款验证基类回调参数
 */
@Data
public class UnionPayLoansBaseCallBackDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    //	事件ID;
    private String eventId;
    //	事件类型;
    private String eventType;
    //事件创建时间
    private String createdAt;
    //时间内容
    private Object eventData;
}
