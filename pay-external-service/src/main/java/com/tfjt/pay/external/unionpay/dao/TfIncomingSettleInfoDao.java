package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.req.QueryIncomingSettleByMerchantReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingSettleRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingSettleInfoEntity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 银行入网-结算信息 Mapper 接口
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
public interface TfIncomingSettleInfoDao extends BaseMapper<TfIncomingSettleInfoEntity> {

    IncomingSettleRespDTO querySettleById(@Param("id") Long id);

    IncomingSettleRespDTO querySettleByMerchant(@Param("req") QueryIncomingSettleByMerchantReqDTO reqDTO);
}
