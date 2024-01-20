package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSettleReqDTO;
import com.tfjt.pay.external.unionpay.entity.TfBankCardInfoEntity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 银行卡信息 Mapper 接口
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
public interface TfBankCardInfoDao extends BaseMapper<TfBankCardInfoEntity> {

    int queryCountByBankNo(@Param("incomingSettle")IncomingSettleReqDTO incomingSettleReqDTO);
}
