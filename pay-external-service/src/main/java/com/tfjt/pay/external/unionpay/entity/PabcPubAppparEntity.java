package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/9 14:55
 */
@Data
@TableName("tf_pabc_pub_apppar")
public class PabcPubAppparEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 银行代码
     */
    private String aprValue;
    /**
     * 银行名称
     */
    private String aprShowmsg;
    /**
     * 银行类型： BANK_TYPE_EXTEND常用银行，BANK_TYPE_ALL 所有银行
     */
    private String aprCode;
}
