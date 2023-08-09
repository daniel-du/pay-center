package com.tfjt.pay.external.unionpay.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dao.BankInterbankNumberDao;
import com.tfjt.pay.external.unionpay.entity.BankInterbankNumberEntity;
import com.tfjt.pay.external.unionpay.service.BankInterbankNumberService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankInterbankNumberServiceImpl extends BaseServiceImpl<BankInterbankNumberDao, BankInterbankNumberEntity> implements BankInterbankNumberService {


    @Override
    public List<BankInterbankNumberEntity> getBankNameListByCity(String city) {
        List<BankInterbankNumberEntity> list = this.baseMapper.selectList(new LambdaQueryWrapper<BankInterbankNumberEntity>()
                .eq(BankInterbankNumberEntity::getCityCode, city));
        return list;
    }


    @Override
    public List<BankInterbankNumberEntity> getBankNameListByBank(String bankName) {
        List<BankInterbankNumberEntity> list = this.baseMapper.selectList(new LambdaQueryWrapper<BankInterbankNumberEntity>()
                .like(BankInterbankNumberEntity::getBankBranchName, bankName).orderByAsc(BankInterbankNumberEntity::getCityCode).last(" limit 100"));
        return list;
    }
}
