package com.tfjt.pay.external.unionpay.dto.req;

import com.tfjt.pay.external.unionpay.constants.RegularConstants;
import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 9:43
 * @description 进件-保存商户结算信息入参
 */
@Data
public class IncomingSettleReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结算信息id
     */
    @NotNull(message = "结算id不能为空", groups = {UpdateGroup.class})
    private Long id;

    /**
     * 进件id
     */
    @NotNull(message = "进件id不能为空", groups = {AddGroup.class})
    private Long incomingId;

    /**
     * 结算账户类型，1：对公，2：对私
     */
    @NotNull(message = "结算账户类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Byte settlementAccountType;

    /**
     * 银行卡id
     */
    @NotNull(message = "银行卡id不能为空", groups = {UpdateGroup.class})
    private Long bankCardId;

    /**
     * 开户名称
     */
    @NotBlank(message = "开户名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(min=1, max = 20, message ="开户名称最大长度为20", groups = { AddGroup.class, UpdateGroup.class })
    private String bankAccountName;
    /**
     * 联行号
     */
    @NotBlank(message = "联行号不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String bankBranchCode;
    /**
     * 银行预留手机号
     */
    @NotBlank(message = "银行预留手机号不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Pattern(regexp = RegularConstants.MOBILE, message = "银行预留手机号格式错误", groups = { AddGroup.class, UpdateGroup.class })
    @Length(min=11, max = 11, message ="预留手机号长度不正确", groups = { AddGroup.class, UpdateGroup.class })
    private String bankCardMobile;
    /**
     * 银行卡号
     */
    @NotBlank(message = "银行卡号不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Pattern(regexp = "^[0-9]+$", message = "银行卡号格式错误", groups = { AddGroup.class, UpdateGroup.class })
    @Length(min = 6,max = 34, message ="银行卡号长度不正确", groups = { AddGroup.class, UpdateGroup.class })
    private String bankCardNo;
    /**
     * 银行卡照片
     */
    @NotBlank(message = "银行卡照片不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String bankCardUrl;
    /**
     * 开户银行编码
     */
    private String bankCode;
    /**
     * 开户总行名称
     */
    @NotBlank(message = "开户总行名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String bankName;
    /**
     * 开户支行名称
     */
    @NotBlank(message = "开户支行名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String bankSubBranchName;
    /**
     * 超级网银号
     */
    @NotBlank(message = "超级网银号不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String eiconBankBranch;

    /**
     * 开户行所在地-省code
     */
    @NotBlank(message = "开户行所在地-省code不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String openAccountProvince;
    /**
     * 开户行所在地-省名称
     */
    @NotBlank(message = "开户行所在地-省名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String openAccountProvinceName;
    /**
     * 开户行所在地-市code
     */
    @NotBlank(message = "开户行所在地-市code不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String openAccountCity;
    /**
     * 开户行所在地-市名称
     */
    @NotBlank(message = "开户行所在地-市名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String openAccountCityName;
    /**
     * 开户行所在地-区code
     */
    @NotBlank(message = "开户行所在地-区code不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String openAccountDistrict;
    /**
     * 开户行所在地-区名称
     */
    @NotBlank(message = "开户行所在地-区名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String openAccountDistrictName;

    /**
     * 职业
     */
//    @NotBlank(message = "职业不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String occupation;
}
