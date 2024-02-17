package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.dao.SelfSignDao;
import com.tfjt.pay.external.unionpay.entity.SelfSignEntity;
import com.tfjt.pay.external.unionpay.service.SelfSignService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelfSignServiceImpl extends BaseServiceImpl<SelfSignDao, SelfSignEntity> implements SelfSignService {

    @Override
    public List<SelfSignEntity> selectByMid(String mid) {
        return this.baseMapper.selectList(Wrappers.<SelfSignEntity>lambdaQuery().eq(SelfSignEntity::getMid, mid));
    }
}
