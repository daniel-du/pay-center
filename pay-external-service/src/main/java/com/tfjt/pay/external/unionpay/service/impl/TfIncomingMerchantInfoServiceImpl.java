package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dao.TfIncomingMerchantInfoDao;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingMerchantRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingMerchantInfoEntity;
import com.tfjt.pay.external.unionpay.enums.DeleteStatusEnum;
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

    /**
     * 根据进件id查询商户身份信息
     * @param incomingId
     * @return
     */
    @Override
    public TfIncomingMerchantInfoEntity queryByIncomingId(Long incomingId) {
        LambdaQueryWrapper<TfIncomingMerchantInfoEntity> merchantInfoEntityQueryWrapper = new LambdaQueryWrapper<>();
        merchantInfoEntityQueryWrapper.eq(TfIncomingMerchantInfoEntity::getIncomingId, incomingId).eq(TfIncomingMerchantInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        return this.baseMapper.selectOne(merchantInfoEntityQueryWrapper);
    }

    @Override
    public IncomingMerchantRespDTO queryMerchantByIncomingId(Long incomingId) {
        return this.baseMapper.queryMerchantByIncomingId(incomingId);
    }
}
