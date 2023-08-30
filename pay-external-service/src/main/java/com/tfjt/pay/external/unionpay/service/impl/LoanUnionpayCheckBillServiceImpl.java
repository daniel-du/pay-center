package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tfjt.pay.external.unionpay.dao.LoanUnionpayCheckBillDao;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillEntity;
import com.tfjt.pay.external.unionpay.service.LoanUnionpayCheckBillService;

import java.util.List;


@Service("loanUnionpayCheckBillService")
public class LoanUnionpayCheckBillServiceImpl extends ServiceImpl<LoanUnionpayCheckBillDao, LoanUnionpayCheckBillEntity> implements LoanUnionpayCheckBillService {


    @Override
    public LoanUnionpayCheckBillEntity getByDateAndAccountId(String date, String balanceAcctId) {
        LambdaQueryWrapper<LoanUnionpayCheckBillEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoanUnionpayCheckBillEntity::getDate, DateUtil.parseDate(date))
                .eq(LoanUnionpayCheckBillEntity::getBalanceAcctId,balanceAcctId)
                .orderByDesc(LoanUnionpayCheckBillEntity::getId);
        List<LoanUnionpayCheckBillEntity> list = this.list(wrapper);
        if(CollectionUtil.isNotEmpty(list)){
            return null;
        }

        return list.get(NumberConstant.ZERO);
    }

    @Override
    public void loadFile(String absolutePath) {
        this.getBaseMapper().loadFile(absolutePath);
    }
}