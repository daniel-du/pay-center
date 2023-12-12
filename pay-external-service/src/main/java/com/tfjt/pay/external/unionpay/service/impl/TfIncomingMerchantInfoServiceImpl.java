package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.TfIncomingMerchantInfoDao;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingMerchantRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingMerchantInfoEntity;
import com.tfjt.pay.external.unionpay.service.TfIncomingMerchantInfoService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 银行入网-商户信息 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
@Service
public class TfIncomingMerchantInfoServiceImpl extends BaseServiceImpl<TfIncomingMerchantInfoDao, TfIncomingMerchantInfoEntity> implements TfIncomingMerchantInfoService {


    @Override
    public IncomingMerchantRespDTO queryMerchantById(Long id) {
        return this.baseMapper.queryMerchantById(id);
    }
}
