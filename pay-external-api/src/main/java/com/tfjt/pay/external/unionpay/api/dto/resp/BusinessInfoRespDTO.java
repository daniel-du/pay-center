package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/13 10:29
 */
@Data
public class BusinessInfoRespDTO implements Serializable {
    private static final long serialVersionUID = 4094343954177959782L;
    /**
     * 商户基本信息
     */
    private BusinessBasicInfoRespDTO businessBasicInfoRespDTO;
    /**
     * 联系人信息
     */
    private BusinessContactsRespDTO businessContactsRespDTO;
}
