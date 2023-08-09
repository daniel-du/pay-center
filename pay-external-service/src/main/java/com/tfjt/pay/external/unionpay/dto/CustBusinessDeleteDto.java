package com.tfjt.pay.external.unionpay.dto;

import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CustBusinessDeleteDto {
    @NotNull(message = "用户id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long loanUserId;
}
