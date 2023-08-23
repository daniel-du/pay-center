package com.tfjt.pay.external.unionpay.api.dto.req;

import com.tfjt.pay.external.unionpay.api.dto.UserTypeDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 支付密码
 * </p>
 *
 * @author young
 * @since 2023-08-11
 */
@Data
public class PaymentPasswordReqDTO extends UserTypeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;


    @NotBlank(message = "盐不能为空")
    /**
     * 盐
     */
    private String salt;

}
