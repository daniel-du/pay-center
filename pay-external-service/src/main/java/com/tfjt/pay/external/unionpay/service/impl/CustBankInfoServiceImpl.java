package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dao.CustBankInfoDao;
import com.tfjt.pay.external.unionpay.dto.BankInfoDTO;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansSettleAcctDTO;
import com.tfjt.pay.external.unionpay.entity.BankEntity;
import com.tfjt.pay.external.unionpay.entity.BankInterbankNumberEntity;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustBankInfoServiceImpl extends BaseServiceImpl<CustBankInfoDao, CustBankInfoEntity> implements CustBankInfoService {
    @Resource
    @Lazy
    private LoanUserService loanUserService;
    @Resource
    private UnionPayLoansApiService unionPayLoansApiService;
    @Override
    public List<BankInfoDTO> getBankInfoByBus(Long loanUserId) {
        LoanUserEntity one = getTfLoanUserEntity(loanUserId);
        List<CustBankInfoEntity> list = this.list(new LambdaQueryWrapper<CustBankInfoEntity>().eq(CustBankInfoEntity::isDeleted, false)
                .eq(CustBankInfoEntity::getLoanUserId, one.getId()));
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<BankInfoDTO> collect = list.stream().map(custBankInfoEntity -> {
            //查询总行信息
            BankInfoDTO bankInfoDTO = new BankInfoDTO();
//            String concat = StringUtils.left(custBankInfoEntity.getBankCardNo(), 4).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(custBankInfoEntity.getBankCardNo(), 4), StringUtils.length(custBankInfoEntity.getBankCardNo()), "*"), "******"));
            bankInfoDTO.setBankName(custBankInfoEntity.getBankName());
            bankInfoDTO.setBigBankName(custBankInfoEntity.getBigBankName());
            bankInfoDTO.setAccountName(custBankInfoEntity.getAccountName());
            bankInfoDTO.setId(custBankInfoEntity.getId());
            bankInfoDTO.setBankCardNo(custBankInfoEntity.getBankCardNo());
            return bankInfoDTO;
        }).collect(Collectors.toList());
        return collect;

    }

    @Override
    public LoanUserEntity getTfLoanUserEntity(Long loanUserId) {
        //查询 loanID
        LoanUserEntity one = this.loanUserService.getById(loanUserId);
        if (ObjectUtil.isEmpty(one)) {
            throw new TfException(PayExceptionCodeEnum.NO_DATA);
        }
        return one;
    }

    /**
     * 获取默认卡 取创建时间最早的一条记录
     *
     * @param loanUserId
     * @return
     */
    @Override
    public CustBankInfoEntity getByLoanUserId(Long loanUserId) {
        log.info("获取默认卡:{}",loanUserId);
        return this.getOne(new LambdaQueryWrapper<CustBankInfoEntity>().eq(CustBankInfoEntity::getLoanUserId, loanUserId).eq(CustBankInfoEntity::isDeleted, false).last(" limit 1"));
    }

    /**
     * 结算信息修改
     *
     * @param custBankInfo
     */
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#custBankInfo.id"}, expire = 3000, acquireTimeout = 4000)
    @Override
    public UnionPayLoansSettleAcctDTO updateCustBankInfo(CustBankInfoEntity custBankInfo) {
        UnionPayLoansSettleAcctDTO unionPayLoansSettleAcctDTO = new UnionPayLoansSettleAcctDTO();

        //当银行卡修改则调用新增绑定账号逻辑
        CustBankInfoEntity old = this.getById(custBankInfo.getId());
        if (!Objects.equals(custBankInfo.getBankCardNo(), old.getBankCardNo())
                || !Objects.equals(custBankInfo.getAccountName(), old.getAccountName())
                || !Objects.equals(custBankInfo.getCity(), old.getCity())
                || !Objects.equals(custBankInfo.getBankCode(), old.getBankCode())
                || !Objects.equals(custBankInfo.getCareer(), old.getCareer())
        ) {
            //删除只能删除已绑定的
            LoanUserEntity byId = loanUserService.getById(custBankInfo.getLoanUserId());
            String mchId = byId.getMchId();
            String cusId = byId.getCusId();
            Integer loanUserType = byId.getLoanUserType();
            if ((StringUtils.isNotBlank(mchId) || StringUtils.isNotBlank(cusId)) && (loanUserType!=0)) {
                unionPayLoansSettleAcctDTO = unionPayLoansApiService.delAndBindAddSettleAcct(custBankInfo, old.getBankCardNo());
            }
        }

        this.updateById(custBankInfo);
        return unionPayLoansSettleAcctDTO;
    }

    @Override
    public void updateCustBankVerifyStatus(Long loanUserId, String bankAcctno, String verifystatus) {
        CustBankInfoEntity custBankInfoEntity = this.getOne(new LambdaQueryWrapper<CustBankInfoEntity>().eq(CustBankInfoEntity::getBankCardNo, bankAcctno).eq(CustBankInfoEntity::getLoanUserId, loanUserId));
        custBankInfoEntity.setVerifyStatus(verifystatus);
        this.updateById(custBankInfoEntity);
    }

    /**
     * 通过银行卡获取信息
     *
     * @param bankCardNo
     * @param loanUserId
     * @return
     */
    @Override
    public CustBankInfoEntity getBankInfoByBankCardNoAndLoanUserId(String bankCardNo, Long loanUserId) {
        return this.getOne(new LambdaQueryWrapper<CustBankInfoEntity>().eq(CustBankInfoEntity::isDeleted, false).eq(CustBankInfoEntity::getBankCardNo, bankCardNo).eq(CustBankInfoEntity::getLoanUserId, loanUserId));
    }

    @Override
    public List<CustBankInfoEntity> getBankInfoByLoanUserId(Long loanUserId) {
        return this.list(new LambdaQueryWrapper<CustBankInfoEntity>().eq(CustBankInfoEntity::isDeleted, false).eq(CustBankInfoEntity::getLoanUserId, loanUserId));
    }
}
