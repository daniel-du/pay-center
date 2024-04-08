package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 21568
 * @create 2024/4/7 11:47
 */
@Data
public class AreaInfoReqDTO implements Serializable {
    private static final long serialVersionUID = 1469481662286431161L;
    private String provinceCode;
    private String cityCode;
    private String districtsCode;

    private String province;
    private String city;
    private String districts;
}
