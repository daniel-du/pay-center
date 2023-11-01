package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tfjt.pay.external.unionpay.dao.LoanWithdrawalOrderDao;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.LoanWithdrawalOrderEntity;
import com.tfjt.pay.external.unionpay.service.LoanWithdrawalOrderService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    @Override
    public List<LoanUnionpayCheckBillDetailsEntity> listUnCheckBill(Date date, Integer pageNo, Integer pageSize) {
        Page<LoanUnionpayCheckBillDetailsEntity> page = Page.of(pageNo, pageSize);
        Page<LoanUnionpayCheckBillDetailsEntity> result = this.baseMapper.listUnCheckBill(date,page);
        return result.getRecords();
    }

    @Override
    public Integer countUnCheckBill(Date date) {
        return this.baseMapper.countUnCheckBill(date);
    }
}
