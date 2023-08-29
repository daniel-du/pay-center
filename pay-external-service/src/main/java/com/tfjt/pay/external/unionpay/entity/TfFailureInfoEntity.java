package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 线程锁失败记录表
 * @TableName tf_failure_info
 */
@TableName(value ="tf_failure_info")
@Data
public class TfFailureInfoEntity implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 幂等性校验参数
     */
    private String keyName;

    /**
     * 方法名
     */
    private String method;

    /**
     * 参数
     */
    private String arguments;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
