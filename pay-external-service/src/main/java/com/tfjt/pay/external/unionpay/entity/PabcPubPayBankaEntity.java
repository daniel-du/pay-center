package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/9 15:13
 */
@Data
@TableName("tf_pabc_pub_pay_banka")
public class PabcPubPayBankaEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 行号与小站号联合
     */
    private String bankBnkcode;
    /**
     * 状态1：有效0：生效前2：注销 9：辅表
     */
    private String bankStatus;
    /**
     * 行别代码
     */
    private String bankClscode;
    /**
     * 所属直接参与者行号
     */
    private String bankDreccode;
    /**
     * 所在节点代码
     */
    private String bankNodecode;
    /**
     * 所在城市
     */
    private String bankCitycode;
    /**
     * 全称
     */
    private String bankLname;

}
