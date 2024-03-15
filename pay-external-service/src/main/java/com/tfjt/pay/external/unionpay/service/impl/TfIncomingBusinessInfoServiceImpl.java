package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dao.TfIncomingBusinessInfoDao;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingBusinessRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingBusinessInfoEntity;
import com.tfjt.pay.external.unionpay.enums.DeleteStatusEnum;
import com.tfjt.pay.external.unionpay.service.TfIncomingBusinessInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 银行入网-营业信息 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Service
public class TfIncomingBusinessInfoServiceImpl extends BaseServiceImpl<TfIncomingBusinessInfoDao, TfIncomingBusinessInfoEntity> implements TfIncomingBusinessInfoService {

    /**
     * 根据id查询商户营业信息
     * @param id
     * @return
     */
    @Override
    public IncomingBusinessRespDTO queryBusinessById(Long id) {
        return this.baseMapper.queryBusinessById(id);
    }

    /**
     * 根据进件id查询营业信息
     * @param incomingId
     * @return
     */
    @Override
    public TfIncomingBusinessInfoEntity queryByIncomingId(Long incomingId) {
        LambdaQueryWrapper<TfIncomingBusinessInfoEntity> businessInfoEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessInfoEntityLambdaQueryWrapper.eq(TfIncomingBusinessInfoEntity::getIncomingId, incomingId)
                .eq(TfIncomingBusinessInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        return this.baseMapper.selectOne(businessInfoEntityLambdaQueryWrapper);
    }

    /**
     * 根据进件id查询商户营业信息
     * @param incomingId
     * @return
     */
    @Override
    public IncomingBusinessRespDTO queryBusinessByIncomingId(Long incomingId) {
        return this.baseMapper.queryBusinessByIncomingId(incomingId);
    }
}
