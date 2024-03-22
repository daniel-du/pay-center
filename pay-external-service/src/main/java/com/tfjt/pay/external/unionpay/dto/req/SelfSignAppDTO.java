package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SelfSignAppDTO implements Serializable {

    /**
     * 第三方用户唯一凭证
     */
    private String appId;

    private List<SelfSignParamDTO> selfSignParamDTOList;

}
