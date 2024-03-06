package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/9 15:19
 */
@Data
@TableName("tf_pabc_pub_pay_city")
public class PabcPubPayCityEntity implements Serializable {

    private static final long serialVersionUID = -4494088126151259508L;
    /**
     * 地区代码
     */
    private String cityAreacode;
    /**
     * 城市名称
     */
    private String cityAreaname;
    /**
     * 区间类型（0：无金额，只有单一区间；1：可设置多个金额区间）;默认值：0
     */
    private String cityAreatype;
    /**
     * 所在节点代码
     */
    private String cityNodecode;
    private String cityTopareacode1;
    private String cityTopareacode2;
    private String cityTopareacode3;
    private String cityOraareacode;
}
