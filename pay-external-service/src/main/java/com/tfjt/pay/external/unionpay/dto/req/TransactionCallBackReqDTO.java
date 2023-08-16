package com.tfjt.pay.external.unionpay.dto.req;

import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Lzh
 * @version 1.0
 * @title TransactionCallBack
 * @description 交易类回调实体
 * @Date 2023/8/14 15:20
 */
@Data
public class TransactionCallBackReqDTO implements Serializable {
    /**事件ID*/
    private String event_id;

    /**事件类型*/
    private String event_type;

    /**事件内容*/
    private EventDataDTO event_data;

    /**事件创建时间*/
    private String created_at;


}
