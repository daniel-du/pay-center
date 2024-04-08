package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 21568
 * @create 2024/4/7 11:43
 */
@Data
public class SaleAreaInsertReqDTO implements Serializable {
    private static final long serialVersionUID = -7578332748952216843L;

    private List<AreaInfoReqDTO> areaList;

    private String channelName;
    private String channelCode;
    private Long userId;
    private String userName;
    
}
