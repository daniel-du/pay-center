package com.tfjt.pay.external.unionpay.api.dto.req;

import com.tfjt.tfcommon.dto.request.ReqDto;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/13 10:09
 */
@Data
public class BusinessInfoReqDTO extends ReqDto implements Serializable {
    private static final long serialVersionUID = -7804473768579882162L;
    /**
     * 商户id
     */
    private String buisnessId;
    /**
     * 商铺类型（1：经销商 ，2：云商）
     */
    private String businessType;
}
