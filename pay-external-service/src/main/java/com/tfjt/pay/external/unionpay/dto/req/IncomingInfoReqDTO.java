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
 * @description 进件-进件主表信息入参
 */
@Data
public class IncomingInfoReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;




    /**
     * 经销商/供应商id/店铺id
     */
    @NotNull(message = "商户id不能为空", groups = {AddGroup.class})
    private Long businessId;

    /**
     * 系统来源
     */
    @NotNull(message = "系统来源不能为空", groups = {AddGroup.class})
    private Byte businessType;

    /**
     * 入网渠道类型（1：平安，2：银联）
     */
    @NotNull(message = "入网渠道类型不能为空", groups = {AddGroup.class})
    private Byte accessChannelType;

    /**
     * 入网类型（1：贷款，2：商户入网）
     */
    @NotNull(message = "入网类型不能为空", groups = {AddGroup.class})
    private Byte accessType;

    /**
     * 入网主体类型（1：个人，2：企业）
     */
    @NotNull(message = "入网主体类型不能为空", groups = {AddGroup.class})
    private Byte accessMainType;


    /**
     * 签约渠道，1：APP，2：平台h5网页，3：公众号，4：小程序
     */
    @NotNull(message = "签约渠道类型不能为空", groups = {AddGroup.class})
    private Byte signChannel;


}
