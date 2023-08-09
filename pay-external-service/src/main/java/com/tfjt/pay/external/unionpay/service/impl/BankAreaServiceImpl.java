package com.tfjt.pay.external.unionpay.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.pay.external.unionpay.dao.BankAreaDao;
import com.tfjt.pay.external.unionpay.entity.BankAreaEntity;
import com.tfjt.pay.external.unionpay.service.BankAreaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tfBankAreaService")
public class BankAreaServiceImpl extends ServiceImpl<BankAreaDao, BankAreaEntity> implements BankAreaService {


    @Override
    public List<BankAreaEntity> getBankAreaByPro(String province) {
        List<BankAreaEntity> list = this.baseMapper.selectList(new LambdaQueryWrapper<BankAreaEntity>()
                .eq(BankAreaEntity::getPid, province));
        return list;
    }

    @Override
    public List<BankAreaEntity> getAllBankArea() {
        List<BankAreaEntity> list = this.baseMapper.selectList(new LambdaQueryWrapper<BankAreaEntity>()
                .eq(BankAreaEntity::getPid, 0));
        return list;
    }
}