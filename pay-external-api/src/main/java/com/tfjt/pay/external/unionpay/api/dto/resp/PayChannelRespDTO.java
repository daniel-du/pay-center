package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 21568
 * @create 2024/1/25 9:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PayChannelRespDTO implements Serializable {
    private String id;
    private String name;
    private List<PayChannelRespDTO> childrenList;
}
