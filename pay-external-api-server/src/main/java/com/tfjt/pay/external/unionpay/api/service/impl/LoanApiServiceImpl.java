package com.tfjt.pay.external.unionpay.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tfjt.pay.external.unionpay.api.dto.resp.BalanceAcctRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.LoanTransferToTfRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.CustBankInfoRespDTO;
import com.tfjt.pay.external.unionpay.api.service.LoanApiService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.dao.LoanUserDao;
import com.tfjt.pay.external.unionpay.dto.BankInfoDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanAccountDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayLoanUserRespDTO;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceAcctEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.service.CustBankInfoService;
import com.tfjt.pay.external.unionpay.service.LoanBalanceAcctService;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import com.tfjt.tfcommon.dto.response.Result;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Lzh
 * @version 1.0
 * @title 进件是否完成
 * @description
 * @Date 2023/8/11 14:44
 */
@Slf4j
@DubboService
public class LoanApiServiceImpl extends BaseServiceImpl<LoanUserDao, LoanUserEntity> implements LoanApiService {
    @Autowired
    private TfAccountConfig accountConfig;

    @Autowired
    private LoanBalanceAcctService loanBalanceAcctService;

    @Autowired
    private UnionPayService unionPayService;

    @Resource
    CustBankInfoService custBankInfoService;

    @Autowired
    private LoanUserService loanUserService;

    @Override
    public Result<LoanTransferToTfRespDTO> getBalanceAcctId(String type, String bid) {
        LoanTransferToTfRespDTO loanTransferToTfDTO = new LoanTransferToTfRespDTO();
        loanTransferToTfDTO.setTfBalanceAcctId(accountConfig.getBalanceAcctId());
        loanTransferToTfDTO.setTfBalanceAcctName(accountConfig.getBalanceAcctName());
        LoanBalanceAcctEntity balanceAcc = loanBalanceAcctService.getBalanceAcctIdByBidAndType(bid, type);
        if (Objects.isNull(balanceAcc)) {
            return Result.failed("电子账簿信息不存在");
        }
        loanTransferToTfDTO.setBalanceAcctId(balanceAcc.getBalanceAcctId());
        loanTransferToTfDTO.setBalanceAcctName(balanceAcc.getRelAcctNo());
        return Result.ok(loanTransferToTfDTO);
    }

    @Override
    public Result<Map<String, Object>> incomingIsFinish(String type, String bid) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal balance = new BigDecimal("0");
        LoanUserEntity loanUser = this.baseMapper.selectOne(new QueryWrapper<LoanUserEntity>()
                .eq("type", type).eq("bus_id", bid));
        if (ObjectUtils.isNotEmpty(loanUser)) {
            //进件完成，查询余额信息
            LoanBalanceAcctEntity balanceAcc = loanBalanceAcctService.getBalanceAcctIdByBidAndType(bid, type);
            if (Objects.isNull(balanceAcc)) {
                return Result.failed("电子账簿信息不存在");
            }

            LoanAccountDTO loanAccountDTO = unionPayService.getLoanAccount(balanceAcc.getBalanceAcctId());
            if (ObjectUtils.isNotEmpty(loanAccountDTO)) {
                Integer settledAmount = loanAccountDTO.getSettledAmount() == null ? 0 : loanAccountDTO.getSettledAmount();
                BigDecimal bigDecimal = new BigDecimal(100);
                balance = new BigDecimal(settledAmount).divide(bigDecimal);
            }
            result.put("isIncoming", true);
            result.put("settledAmount", balance);
            return Result.ok(result);
        }
        result.put("isIncoming", false);
        result.put("settledAmount", balance);
        return Result.ok(result);
    }

    /**
     * 获取银行卡
     *
     * @param type
     * @param bid 类型1商家2供应商
     * @return
     */
    @Override
    public Result<List<CustBankInfoRespDTO>> getCustBankInfoList(Integer type, String bid) {
        try {
            LoanUserEntity loanUser = loanUserService.getLoanUserByBusIdAndType(bid, type);
            if(ObjectUtils.isEmpty(loanUser)){
                return Result.failed("未查询到相关用户");
            }
            List<BankInfoDTO> bankInfoByBus = custBankInfoService.getBankInfoByBus(loanUser.getId());
            List<CustBankInfoRespDTO> custBankInfoResp = new ArrayList<>();
            bankInfoByBus.forEach(bankInfoDTO -> {
                CustBankInfoRespDTO custBankInfoRespDTO = new CustBankInfoRespDTO();
                BeanUtil.copyProperties(bankInfoDTO, custBankInfoRespDTO);
                custBankInfoResp.add(custBankInfoRespDTO);
            });
            return Result.ok(custBankInfoResp);
        } catch (TfException ex) {
            return Result.failed(ex.getMessage());
        }
    }


    @Override
    public Result<BalanceAcctRespDTO> getAccountInfoByBusId(String type,String busId) {
        Result<List<BalanceAcctRespDTO>> listResult = this.listAccountInfoByBusId(type,Arrays.asList(busId));
        List<BalanceAcctRespDTO> data = listResult.getData();
        if(CollectionUtil.isEmpty(data)){
            throw new TfException(ExceptionCodeEnum.ILLEGAL_ARGUMENT);
        }
        return Result.ok(data.get(0));
    }

    @Override
    public Result<List<BalanceAcctRespDTO>> listAccountInfoByBusId(String type,List<String> busIds) {
        List<UnionPayLoanUserRespDTO> unionPayLoanUserRespDTOS = loanUserService.listLoanUserByBusId(type,busIds);
        if(CollectionUtil.isEmpty(loanUserService.listLoanUserByBusId(type, busIds))){
            return Result.ok(new ArrayList<>());
        }
        List<BalanceAcctRespDTO> list = new ArrayList<>(unionPayLoanUserRespDTOS.size());
        for (UnionPayLoanUserRespDTO unionPayLoanUserRespDTO : unionPayLoanUserRespDTOS) {
            BalanceAcctRespDTO balanceAcctRespDTO = new BalanceAcctRespDTO();
            BeanUtil.copyProperties(unionPayLoanUserRespDTO,balanceAcctRespDTO);
            list.add(balanceAcctRespDTO);
        }
        return Result.ok(list);
    }
}
