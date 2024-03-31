package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/22 10:20
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TtqfCallbackRespDTO implements Serializable {

    /**
     * 响应编码，成功：0000
     */
    private String bizCode;
}
