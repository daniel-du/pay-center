package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

/**
 * @author tony
 * @version 1.0
 * @title SigningReviewReqDTO
 * @description
 * @create 2024/3/6 17:53
 */
@Data
public class SigningReviewReqDTO {

    /**
     * 对json_data明文内容做签名摘要
     */
    private String signData;
    /**
     * 对json_data明文内容做签名摘要
     */
    private String jsonData;
    /**
     * 外部系统平台标识(明文)，由ums分配
     */
    private String accesserId;
}
