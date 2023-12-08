package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Getter
@Setter
@TableName("tf_incoming_business_attach_info")
public class TfIncomingBusinessAttachInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 附件地址
     */
    private String imgUrl;

    /**
     * 1- 门面照 2-店铺室内 3-经营商品照片 4-辅助正面材料
     */
    private Boolean imgType;

    /**
     * 进件主表id
     */
    private Long incomingId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
