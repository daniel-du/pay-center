package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zxy
 * @create 2023/12/13 10:31
 */
@Data
public class BusinessChangeRecodRespDTO implements Serializable {
    private static final long serialVersionUID = -6256545845096732228L;
    /**
     * 变更时间
     */
    private Date changeTime;
    /**
     * 操作人Id
     */
    private String operatorId;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 变更项
     */
    private String changeMoudle;
    /**
     * 变更前的值
     */
    private String beforChangeValue;
    /**
     * 变更后的值
     */
    private String afterChangeValue;
}
