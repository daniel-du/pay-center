package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dao.TfIncomingImportDao;
import com.tfjt.pay.external.unionpay.entity.TfIncomingImportEntity;
import com.tfjt.pay.external.unionpay.service.ITfIncomingImportService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2024-01-07
 */
@Service
public class TfIncomingImportServiceImpl extends BaseServiceImpl<TfIncomingImportDao, TfIncomingImportEntity> implements ITfIncomingImportService {

    /**
     * 根据起始id查询未提交数据，每次一百条
     * @param id
     * @return
     */
    @Override
    public List<TfIncomingImportEntity> queryListByStartId(Long id) {
        LambdaQueryWrapper<TfIncomingImportEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TfIncomingImportEntity::getSubmitStatus, NumberConstant.ZERO);
        queryWrapper.ge(TfIncomingImportEntity::getId, id);
        queryWrapper.orderByAsc(TfIncomingImportEntity::getId).last("limit 100");
        return this.baseMapper.selectList(queryWrapper);
    }

    /**
     * 查询未提交状态最小id数据
     * @return
     */
    @Override
    public TfIncomingImportEntity queryNotSubmitMinIdData() {
        LambdaQueryWrapper<TfIncomingImportEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TfIncomingImportEntity::getSubmitStatus, NumberConstant.ZERO);
        queryWrapper.orderByAsc(TfIncomingImportEntity::getId).last("limit 1");
        return this.baseMapper.selectOne(queryWrapper);
    }


}
