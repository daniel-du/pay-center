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
import java.util.Objects;


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

    /**
     *
     * 如果 accesserAcct为null
     * 近7天签约成功的商户
     * 反之返回指定商户
     *
     * @return
     */
    @Override
    public List<SelfSignEntity> querySelfSignsBySuccess(String accesserAcct) {
        LambdaQueryWrapper<SelfSignEntity> queryWrapper = Wrappers.lambdaQuery(SelfSignEntity.class)
                .eq(SelfSignEntity::getMerMsRelation, "0")
                .apply(Objects.isNull(accesserAcct), "TO_DAYS(NOW()-TO_DAYS(sign_success_date)<=7)")
                .eq(Objects.nonNull(accesserAcct), SelfSignEntity::getAccesserAcct, accesserAcct);
        return this.baseMapper.selectList(queryWrapper);
    }

}
