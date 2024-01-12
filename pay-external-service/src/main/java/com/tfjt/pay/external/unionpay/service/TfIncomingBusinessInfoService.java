package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.dto.resp.IncomingBusinessRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingBusinessInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 银行入网-营业信息 服务类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
public interface TfIncomingBusinessInfoService extends IService<TfIncomingBusinessInfoEntity> {

    /**
     * 根据id查询商户营业信息
     * @param id
     * @return
     */
    IncomingBusinessRespDTO queryBusinessById(Long id);

    /**
     * 根据进件id查询营业信息
     * @param incomingId
     * @return
     */
    TfIncomingBusinessInfoEntity queryByIncomingId(Long incomingId);
}
