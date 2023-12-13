package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.dto.resp.IncomingSettleRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingSettleInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 银行入网-结算信息 服务类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
public interface TfIncomingSettleInfoService extends IService<TfIncomingSettleInfoEntity> {

    /**
     * 根据进件id查询结算信息
     * @param incomingId
     * @return
     */
    TfIncomingSettleInfoEntity querySettleInfoByIncomingId(Long incomingId);

    /**
     * 根据结算信息id查询结算信息
     * @param id
     * @return
     */
    IncomingSettleRespDTO querySettleById(Long id);

}
