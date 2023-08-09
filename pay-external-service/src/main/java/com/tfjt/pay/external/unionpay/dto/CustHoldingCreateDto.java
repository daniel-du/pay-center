package com.tfjt.pay.external.unionpay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

import static com.tfjt.pay.external.unionpay.constants.RegularConstants.*;


@Data
public class CustHoldingCreateDto {
    @NotNull(message = "用户id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long loanUserId;
    /**
     * holding_type =1 实际控制企业名称   holding_type=2股东名称
     */
    @NotBlank(message = "名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(min=1,max = 50)
    private String holdingName;
    /**
     * 1 企业 2 个人
     */
    @NotNull(message = "类型不能为空")
    private Integer holdingType;
    /**
     * holding_type = 1实际控制企业营业执照号   holding_type=2 身份证号
     */
    @NotBlank(message = "营业执照号或身份证号不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Pattern(regexp = LICENSE_PATTERN,message = "营业执照号码不正确")
    private String holdingNum;
    /**
     * 生效日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT-8")
    private String effectiveDate;
    /**
     * 营业执照失效期
     */
    private String expiryDate;

    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改人
     */
    private String updater;

    /**
     * 身份证是否长期 （0否，1是）
     */
    private Integer isLongTerm;
}
