package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dao.TfIncomingExtendInfoDao;
import com.tfjt.pay.external.unionpay.entity.TfIncomingExtendInfoEntity;
import com.tfjt.pay.external.unionpay.entity.TfIncomingImportEntity;
import com.tfjt.pay.external.unionpay.service.TfIncomingExtendInfoService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 入网扩展信息表 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2024-03-21
 */
@Service
public class TfIncomingExtendInfoServiceImpl extends BaseServiceImpl<TfIncomingExtendInfoDao, TfIncomingExtendInfoEntity> implements TfIncomingExtendInfoService {

    @Override
    public boolean updateByIncomingId(TfIncomingExtendInfoEntity extendInfo) {
        return this.baseMapper.updateByIncomingId(extendInfo);
    }

    @Override
    public TfIncomingExtendInfoEntity queryNotSignMinIdData() {
        LambdaQueryWrapper<TfIncomingExtendInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TfIncomingExtendInfoEntity::getSignStatus, NumberConstant.ZERO);
        queryWrapper.or().eq(TfIncomingExtendInfoEntity::getBindStatus, NumberConstant.ZERO);
        queryWrapper.orderByAsc(TfIncomingExtendInfoEntity::getIncomingId).last("limit 1");
        return this.baseMapper.selectOne(queryWrapper);
    }
}
