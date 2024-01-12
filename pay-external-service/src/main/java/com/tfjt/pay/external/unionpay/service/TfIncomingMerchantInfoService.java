package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.dto.resp.IncomingMerchantRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingMerchantInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 银行入网-商户信息 服务类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
public interface TfIncomingMerchantInfoService extends IService<TfIncomingMerchantInfoEntity> {

    /**
     * 根据id查询商户身份信息
     * @param id
     * @return
     */
    IncomingMerchantRespDTO queryMerchantById(Long id);

    /**
     * 根据进件id查询商户身份信息
     * @param incomingId
     * @return
     */
    TfIncomingMerchantInfoEntity queryByIncomingId(Long incomingId);

}
