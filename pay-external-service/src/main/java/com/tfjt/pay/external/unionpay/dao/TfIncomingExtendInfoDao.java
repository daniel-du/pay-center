package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.TfIncomingExtendInfoEntity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 入网扩展信息表 Mapper 接口
 * </p>
 *
 * @author Du Penglun
 * @since 2024-03-21
 */
public interface TfIncomingExtendInfoDao extends BaseMapper<TfIncomingExtendInfoEntity> {

    boolean updateByIncomingId(@Param("extendInfo") TfIncomingExtendInfoEntity extendInfo);
}
