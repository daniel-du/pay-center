package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanOrderDetailsDao;
import com.tfjt.pay.external.unionpay.entity.LoanOrderDetailsEntity;
import com.tfjt.pay.external.unionpay.service.LoanOrderDetailsService;

import java.util.Date;
import java.util.List;


@Service("payLoanOrderDetailsService")
public class LoanOrderDetailsServiceImpl extends ServiceImpl<LoanOrderDetailsDao, LoanOrderDetailsEntity> implements LoanOrderDetailsService {

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