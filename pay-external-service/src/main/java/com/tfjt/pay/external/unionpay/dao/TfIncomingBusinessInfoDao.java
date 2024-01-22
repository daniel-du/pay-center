package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingBusinessRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingBusinessInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 银行入网-营业信息 Mapper 接口
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Mapper
public interface TfIncomingBusinessInfoDao extends BaseMapper<TfIncomingBusinessInfoEntity> {

    IncomingBusinessRespDTO queryBusinessById(@Param("id") Long id);

    IncomingBusinessRespDTO queryBusinessByIncomingId(@Param("incomingId") Long incomingId);
}
