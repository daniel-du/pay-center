package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingMerchantRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingMerchantInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 银行入网-商户信息 Mapper 接口
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
@Mapper
public interface TfIncomingMerchantInfoDao extends BaseMapper<TfIncomingMerchantInfoEntity> {

    IncomingMerchantRespDTO queryMerchantById(@Param("id") Long id);
}
