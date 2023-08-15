package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 银联入金通知
 *
 * @author songx
 * @date 2023-08-14 22:13
 * @email 598482054@qq.com
 */
@Data
public class UnionPayIncomeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件类型
     */
    private String eventType;
    /**
     * 事件id
     */
    private String eventId;
    /***事件内容*/
    private List<UnionPayIncomeDetailsDTO> eventData;
    /**
     * 创建时间
     */
    private String createdAt;

}
