package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

/**
 * @Author 21568
 * @create 2024/1/25 13:46
 */
@Data
public class AllSalesAreaRespDTO {


    /**
     * 主键
     */
    private String id;
    /**
     * 省名称
     */
    private String province;
    /**
     * 省编码
     */
    private String provinceCode;
    /**
     * 市名称
     */
    private String city;
    /**
     * 市编码
     */
    private String cityCode;
    /**
     * 区名称
     */
    private String districts;
    /**
     * 区编码
     */
    private String districtsCode;
    /**
     * 省/市/区
     */
    private String area;
    /**
     * 进件通道
     */
    private String channel;
    /**
     * 进件通道编码
     */
    private String channelCode;
}
