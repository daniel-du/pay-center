package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2024/1/9 15:47
 */
@Data
@TableName("fa_standard_location_dict")
public class FaStandardLocationDictEntity implements Serializable {
    private static final long serialVersionUID = -1087782250682684018L;


    /**
     * 主键
     */
    @TableId
    private String id;
    /**
     * 省名
     */
    private String provinceName;
    /**
     * 省编码
     */
    private String provinceCode;
    /**
     * 市名
     */
    private String cityName;
    /**
     * 市编码
     */
    private String cityCode;
    /**
     * 区名
     */
    private String districtName;
    /**
     * 区编码
     */
    private String districtCode;
    /**
     * 所属门店
     */
    private String areaShoppingId;
    /**
     * 创建时间
     */
    private String createDate;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 删除标识: 0未删除1已删除
     */
    private String delFlag;
}
