package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ElectronicBookReqDTO
 * @description: 电子账簿查询
 * @author Lzh
 * @date 2023年08月09日
 * @version: 1.0
 */
@Data
public class ElectronicBookReqDTO implements Serializable {
    /**电子账簿ID*/
    private String balanceAcctId;

    /**开始时间 格式:RFC3339*/
    private String sentAt;

    /**结束时间 格式:RFC3339*/
    private String endAt;

    /**游标*/
    private String cursor;

    /**每页条数*/
    private Integer size;

    /**系统订单号*/
    private String tradeId;

}
