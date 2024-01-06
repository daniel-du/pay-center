package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 进件日志记录表
 * </p>
 *
 * @author Du Penglun
 * @since 2024-01-03
 */
@Getter
@Setter
@TableName("tf_incoming_api_log")
public class TfIncomingApiLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 请求url地址
     */
    private String url;

    /**
     * 接口编号
     */
    private String apiCode;

    /**
     * 请求报文
     */
    private String requestParam;

    /**
     * 请求方式
     */
    private String requestType;

    /**
     * 请求时间
     */
    private LocalDateTime requestTime;

    /**
     * 响应时间
     */
    private LocalDateTime responseTime;

    /**
     * 响应体
     */
    private String responseBody;

    /**
     * 耗时(毫秒)
     */
    private Integer consumeTime;

    /**
     * 状态;0: 初始化 1: 成功 2:失败
     */
    private Integer status;

    /**
     * 入网渠道类型（1：平安，2：银联）
     */
    private Byte accessChannelType;

    /**
     * 入网类型（1：贷款，2：商户入网）
     */
    private Byte accessType;

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
