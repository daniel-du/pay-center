package com.tfjt.pay.external.unionpay.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description: IdCardDTO <br>
 * @date: 2023/5/20 11:23 <br>
 * @author: young <br>
 * @version: 1.0
 */

@Data
public class IdCardDTO {

    /**
     * 地址信息
     */
    private String address;
    /**
     * 出生日期
     */
    private String birth;
    /**
     * 是否是复印件
     */
    @JsonProperty("is_fake")
    private boolean fake;
    /**
     * 姓名
     */
    private String name;
    /**
     * 民族
     */
    private String nationality;
    /**
     * 身份证号
     */
    private String num;
    @JsonProperty("request_id")
    private String requestId;
    /**
     * 性别
     */
    private String sex;
    private boolean success;
    private String url;
    /**
     * 生效日期
     */
    @JsonProperty("start_date")
    private String startDate;
    /**
     * 失效日期
     */
    @JsonProperty("end_date")
    private String endDate;
    /**
     * 签发机关
     */
    private String issue;
}
