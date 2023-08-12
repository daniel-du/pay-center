package com.tfjt.pay.external.unionpay.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dao.LoanBalanceAcctDao;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceAcctEntity;
import com.tfjt.pay.external.unionpay.service.CustBankInfoService;
import com.tfjt.pay.external.unionpay.service.LoanBalanceAcctService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LoanBalanceAcctServiceImpl extends BaseServiceImpl<LoanBalanceAcctDao, LoanBalanceAcctEntity> implements LoanBalanceAcctService {

    @Resource
    private CustBankInfoService bankInfoService;

    @Override
    public List<LoanBalanceAcctEntity> getAccountBooksListByBus(Integer loanUserId) {
        LoanUserEntity loanUserEntity = this.bankInfoService.getTfLoanUserEntity(loanUserId);

        List<LoanBalanceAcctEntity> list = this.list(new LambdaQueryWrapper<LoanBalanceAcctEntity>()
                .eq(LoanBalanceAcctEntity::getLoanUserId, loanUserEntity.getId()));
        return list;
    }

    @Override
    public LoanBalanceAcctEntity getTfLoanBalanceAcctEntity(String relAcctNo, String balanceAcctId, Long loanUserId) {
        return this.getOne(new LambdaQueryWrapper<LoanBalanceAcctEntity>().eq(LoanBalanceAcctEntity::getRelAcctNo, relAcctNo).eq(LoanBalanceAcctEntity::getBalanceAcctId, balanceAcctId).eq(LoanBalanceAcctEntity::getLoanUserId, loanUserId));
    }

    @Override
    public LoanBalanceAcctEntity getBalanceAcctIdByBidAndType(String busId, String type) {

        return this.getBaseMapper().getBalanceAcctIdByBidAndType(busId,type);
    }
}
