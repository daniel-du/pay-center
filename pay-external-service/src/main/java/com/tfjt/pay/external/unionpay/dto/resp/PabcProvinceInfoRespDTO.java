package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/11 9:30
 */
@Data
public class PabcProvinceInfoRespDTO implements Serializable {
    private static final long serialVersionUID = 2750194309585726792L;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 省份code
     */
    private String provinceCode;
}
