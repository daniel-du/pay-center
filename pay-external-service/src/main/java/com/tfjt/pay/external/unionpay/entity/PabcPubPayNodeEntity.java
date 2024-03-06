package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2023/12/9 15:26
 */
@Data
@TableName("tf_pabc_pub_pay_node")
public class PabcPubPayNodeEntity implements Serializable {
    private static final long serialVersionUID = 7845615137779502456L;

    /**
     * 所在节点代码
     */
    private String nodeNodecode;
    /**
     * 省名称
     */
    private String nodeNodename;
}
