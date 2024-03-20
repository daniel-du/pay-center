package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/20 17:34
 * @description 天天企赋-查询签约信息
 */
@Data
public class QueryTtqfSignMsgReqDTO implements Serializable {

    /**
     * 会员id
     */
    private Long businessId;


}
