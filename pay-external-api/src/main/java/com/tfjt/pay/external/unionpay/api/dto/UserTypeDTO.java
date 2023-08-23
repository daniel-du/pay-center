package com.tfjt.pay.external.unionpay.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author tony
 * @version 1.0
 * @title UserTypeDTO
 * @description
 * @create 2023/8/23 10:15
 */

@Data
public class UserTypeDTO implements Serializable {
    /**
     * 类型1商家2供应商
     */
    @NotNull(message = "类型不能为空")
    private Integer type;
    /**
     * 业务ID
     */
    @NotBlank(message = "业务ID不能为空")
    private String busId;
}
