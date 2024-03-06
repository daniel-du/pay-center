package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/12 10:34
 */
@Data
@TableName("tf_sales_area_incoming_channel")
public class SalesAreaIncomingChannelEntity implements Serializable {
    private static final long serialVersionUID = -1297462749462038356L;


    /**
     * 主键
     */
    @TableId
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
