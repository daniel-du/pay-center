package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.pay.external.unionpay.dao.SelfSignDao;
import com.tfjt.pay.external.unionpay.entity.SelfSignEntity;
import com.tfjt.pay.external.unionpay.service.SelfSignService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class SelfSignServiceImpl extends BaseServiceImpl<SelfSignDao, SelfSignEntity> implements SelfSignService {

    @Override
    public SelfSignEntity selectByMid(String mid) {
        return this.baseMapper.selectOne(Wrappers.<SelfSignEntity>lambdaQuery().eq(SelfSignEntity::getMid, mid));
    }

    @Override
    public List<SelfSignEntity> querySelfSignsByAccessAccts(List<String> accessAccts) {
        LambdaQueryWrapper<SelfSignEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SelfSignEntity::getAccesserAcct, accessAccts);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public SelfSignEntity querySelfSignByAccessAcct(String accessAcct) {
        LambdaQueryWrapper<SelfSignEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SelfSignEntity::getAccesserAcct, accessAcct);
        return this.baseMapper.selectOne(queryWrapper);
    }
}
