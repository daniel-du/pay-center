package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/19 19:29
 * @description 天天企赋签约请求入参
 */
@Data
public class TtqfSignReqDTO implements Serializable {

    /**
     * 会员id
     */
    private String businessId;

    /**
     * 业务类型
     */
    private Integer businessType;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 身份证有效期-起始时间
     */
    private String expiryStart;

    /**
     * 身份证有效期-截止时间
     */
    private String expiryEnd;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 身份证正面照片url
     */
    private String idCardPicAFileId;

    /**
     * 身份证背面照片url
     */
    private String idCardPicBFileId;
}
