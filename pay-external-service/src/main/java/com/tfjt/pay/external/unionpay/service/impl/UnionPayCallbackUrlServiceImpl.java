package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dao.PayApplicationCallbackUrlDao;
import com.tfjt.pay.external.unionpay.entity.PayApplicationCallbackUrlEntity;
import com.tfjt.pay.external.unionpay.service.UnionPayCallbackUrlService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tony
 * @version 1.0
 * @title UnionPayLoansCallbackServiceImpl
 * @description
 * @create 2024/2/7 09:48
 */
@Service
public class UnionPayCallbackUrlServiceImpl extends BaseServiceImpl<PayApplicationCallbackUrlDao, PayApplicationCallbackUrlEntity> implements UnionPayCallbackUrlService {
    @Override
    public List<PayApplicationCallbackUrlEntity> getCallbackUrlByAppIdAndType(Integer type) {
        return this.baseMapper.selectList(
                new LambdaQueryWrapper<PayApplicationCallbackUrlEntity>()
                        .eq(PayApplicationCallbackUrlEntity::getType, type));
    }
}
