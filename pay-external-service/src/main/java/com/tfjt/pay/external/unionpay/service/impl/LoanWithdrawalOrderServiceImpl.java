package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.dao.LoanWithdrawalOrderDao;
import com.tfjt.pay.external.unionpay.entity.LoanWithdrawalOrderEntity;
import com.tfjt.pay.external.unionpay.service.LoanWithdrawalOrderService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 提现 服务实现类
 * </p>
 *
 * @author young
 * @since 2023-08-17
 */
@Service
public class LoanWithdrawalOrderServiceImpl extends BaseServiceImpl<LoanWithdrawalOrderDao, LoanWithdrawalOrderEntity> implements LoanWithdrawalOrderService {

    /**
     * 通过提现号获取提现业务数据
     * @param outOrderNo
     * @return
     */
    @Override
    public LoanWithdrawalOrderEntity getWithdrawalOrderByNo(String outOrderNo) {
        return this.getOne(Wrappers.lambdaQuery(LoanWithdrawalOrderEntity.class).eq(LoanWithdrawalOrderEntity::getWithdrawalOrderNo, outOrderNo));
    }
}
