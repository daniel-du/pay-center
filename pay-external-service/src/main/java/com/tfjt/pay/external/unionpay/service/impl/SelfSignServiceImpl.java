package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
    public SelfSignEntity selectByAccessAcct(String accessAcct) {
        return this.baseMapper.selectOne(Wrappers.<SelfSignEntity>lambdaQuery().eq(SelfSignEntity::getAccesserAcct, accessAcct));
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
     * 如果 accesserAcct为null
     * 近7天签约成功的商户
     * 反之返回指定商户
     *
     * @return
     */
    @Override
    public List<SelfSignEntity> querySelfSignBySuccess(String accesserAcct, String gysPayAppId, boolean isGys) {
        LambdaQueryWrapper<SelfSignEntity> queryWrapper = Wrappers.lambdaQuery(SelfSignEntity.class)
                .eq(isGys, SelfSignEntity::getMerMsRelation, "0")
                .eq(isGys, SelfSignEntity::getAppId, gysPayAppId)
                .isNull(!isGys,SelfSignEntity::getBusinessNo)
                .apply(Objects.isNull(accesserAcct) || accesserAcct.isEmpty(), "TO_DAYS(NOW())-TO_DAYS(sign_success_date)<=7")
                .eq(Objects.nonNull(accesserAcct) && !accesserAcct.isEmpty(), SelfSignEntity::getAccesserAcct, accesserAcct);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<SelfSignEntity> querySelfSignByUpdateTime(String accesserAcct) {
        LambdaQueryWrapper<SelfSignEntity> queryWrapper = Wrappers.lambdaQuery(SelfSignEntity.class)
                .apply(Objects.isNull(accesserAcct) || accesserAcct.isEmpty(), "TO_DAYS(NOW())-TO_DAYS(update_time)<=1")
                .eq(Objects.nonNull(accesserAcct) && !accesserAcct.isEmpty(), SelfSignEntity::getAccesserAcct, accesserAcct);
        return this.baseMapper.selectList(queryWrapper);
    }

}
