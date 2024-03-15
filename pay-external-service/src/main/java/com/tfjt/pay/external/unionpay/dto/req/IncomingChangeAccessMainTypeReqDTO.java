package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/1/6 9:05
 * @description 变更进件主体类型请求入参
 */
@Data
public class IncomingChangeAccessMainTypeReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 进件id
     */
    @NotNull(message = "进件id不能为空！")
    private Long id;

    /**
     * 入网主体类型
     */
    @NotNull(message = "入网主体类型不能为空！")
    private Integer accessMainType;
}
