package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

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
    @NotNull(message = "会员id不能为空")
    private Long businessId;

    /**
     * 业务类型-3：会员体系
     * 业务类型-4: 会员体系-经销商
     * {@link com.tfjt.pay.external.unionpay.enums.BusinessTypeEnum}
     */
    @NotNull(message = "业务类型不能为空")
    private Integer businessType;

    /**
     * 会员注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String userName;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号码不能为空")
    private String idCardNo;

    /**
     * 身份证有效期-起始时间
     */
    @NotBlank(message = "身份证有效期-起始时间不能为空")
    private String expiryStart;

    /**
     * 身份证有效期-截止时间
     */
    @NotBlank(message = "身份证有效期-截止时间不能为空")
    private String expiryEnd;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    /**
     * 身份证正面照片url
     */
    @NotBlank(message = "身份证正面照片url不能为空")
    private String idCardPicAFileId;

    /**
     * 身份证背面照片url
     */
    @NotBlank(message = "身份证背面照片url不能为空")
    private String idCardPicBFileId;

    /**
     * 银行卡号
     */
    @NotBlank(message = "银行卡号不能为空")
    private String bankCardNo;
}
