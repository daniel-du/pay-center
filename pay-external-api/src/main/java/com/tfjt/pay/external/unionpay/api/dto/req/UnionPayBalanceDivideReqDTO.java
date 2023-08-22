package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author songx
 * @date 2023-08-14 10:52
 * @email 598482054@qq.com
 */
@Data
public class UnionPayBalanceDivideReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分账系统标识  fms
     */
    @NotBlank(message = "分账业务系统标识不能为空")
    private String appId;
    /**
     * 业务系统唯一标识
     */
    @NotBlank(message = "分账订单号不能为空")
    private String businessOrderNo;
    /**
     * 批量交易信息
     */
    @NotNull(message = "子交易订单不能为空")
    @Size(min = 1,message = "子交易订单不能为空")
    private List<UnionPaySubBalanceDivideReqDTO> list;
    /**
     * 业务系统用户id
     */
    @NotNull(message = "业务系统操作人不能为空")
    private Long userId;

}
