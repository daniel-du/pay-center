package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/9 15:28
 */
@Data
@TableName("tf_pabc_superbankcode")
public class PabcSuperbankcodeEntity implements Serializable {

    private static final long serialVersionUID = -3288037435706871705L;
    /**
     * 行号
     */
    private String bankno;
    /**
     * 全称
     */
    private String bankname;
    /**
     * 行别代码
     */
    private String bankclscode;
    /**
     * 状态1：有效0：生效前2：注销 9：辅表
     */
    private String status;
    private String agentbankcode;
}
