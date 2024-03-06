package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/11 10:40
 */
@Data
public class PabcBranchBankInfoRespDTO implements Serializable {
    private static final long serialVersionUID = -8386987035762488350L;
    /**
     * 大小额联行号
     */
    private String bankBnkcode;
    /**
     * 清算行号
     */
    private String bankDreccode;
    /**
     * 支行名称
     */
    private String bankLname;
    /**
     * 超级网银号
     */
    private String bankNo;
}
