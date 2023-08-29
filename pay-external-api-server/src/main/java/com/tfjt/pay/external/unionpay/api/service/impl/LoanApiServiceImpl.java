package com.tfjt.pay.external.unionpay.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayIncomingDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BalanceAcctRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.BankInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.CustBankInfoRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.LoanTransferToTfRespDTO;
import com.tfjt.pay.external.unionpay.api.service.LoanApiService;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansBizService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dao.LoanUserDao;
import com.tfjt.pay.external.unionpay.dto.BankInfoDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanAccountDTO;
import com.tfjt.pay.external.unionpay.dto.resp.LoanBalanceAcctRespDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayLoanUserRespDTO;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import com.tfjt.tfcommon.dto.response.Result;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    UnionPayLoansApiService unionPayLoansApiService;

    @Resource
    UnionPayLoansBizService unionPayLoansBizService;

    @Value("${unionPayLoans.encodedPub}")
    private String encodedPub;


    @Override
    public Result<LoanTransferToTfRespDTO> getBalanceAcctId(String type, String bid) {
        try {
            LoanTransferToTfRespDTO loanTransferToTfDTO = new LoanTransferToTfRespDTO();
            loanTransferToTfDTO.setTfBalanceAcctId(accountConfig.getBalanceAcctId());
            loanTransferToTfDTO.setTfBalanceAcctName(accountConfig.getBalanceAcctName());
            LoanBalanceAcctRespDTO balanceAcc = loanBalanceAcctService.getBalanceAcctIdByBidAndType(bid, type);
            // loanBalanceAcctService.get
            if (Objects.isNull(balanceAcc)) {
                throw new TfException(PayExceptionCodeEnum.BALANCE_ACCOUNT_NOT_FOUND);
            }
            loanTransferToTfDTO.setBalanceAcctId(balanceAcc.getBalanceAcctId());
            loanTransferToTfDTO.setBalanceAcctName(balanceAcc.getBalanceAcctName());
            return Result.ok(loanTransferToTfDTO);
        } catch (TfException e) {
            log.error("");
            return Result.failed(e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> incomingIsFinish(String type, String bid) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal balance = new BigDecimal("0");
        LoanUserEntity loanUser = this.baseMapper.selectOne(new QueryWrapper<LoanUserEntity>()
                .eq("type", type).eq("bus_id", bid).eq("application_status", "succeeded"));
        if (ObjectUtils.isNotEmpty(loanUser)) {
            //进件完成，查询余额信息
            LoanBalanceAcctRespDTO balanceAcc = loanBalanceAcctService.getBalanceAcctIdByBidAndType(bid, type);
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
            result.put("isFrozen",loanAccountDTO.isFrozen());
            return Result.ok(result);
        }
        result.put("isIncoming", false);
        result.put("settledAmount", balance);
        return Result.ok(result);
    }

    @Override
    public Result<Map<String, Object>> listIncomingIsFinish(List<UnionPayIncomingDTO> list) {
        try {
            Map<String, List<UnionPayIncomingDTO>> collect = list.stream().collect(Collectors.groupingBy(UnionPayIncomingDTO::getType));
            List<UnionPayIncomingDTO> shops = collect.get(NumberConstant.ONE.toString());
            List<UnionPayIncomingDTO> dealers = collect.get(NumberConstant.TWO.toString());
            if (CollectionUtil.isEmpty(shops)) {
                return Result.failed(PayExceptionCodeEnum.PAYER_NOT_FOUND);
            }
            if (shops.size() > NumberConstant.ONE) {
                return Result.failed(PayExceptionCodeEnum.PAYER_TOO_MUCH);
            }
            if (CollectionUtil.isEmpty(dealers)) {
                return Result.failed(PayExceptionCodeEnum.PAYEE_NOT_FOUND);
            }
            Map<String, Object> result = new HashMap<>();
            for (UnionPayIncomingDTO unionPayIncomingDTO : dealers) {
                LambdaQueryWrapper<LoanUserEntity> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
                objectLambdaQueryWrapper.eq(LoanUserEntity::getType, unionPayIncomingDTO.getType())
                        .eq(LoanUserEntity::getBusId, unionPayIncomingDTO.getBid()).eq(LoanUserEntity::getApplicationStatus, "succeeded");
                LoanUserEntity one = this.getOne(objectLambdaQueryWrapper);
                if (Objects.isNull(one)) {
                    result.put("isIncoming", true);
                    return Result.ok(result);
                }
            }
            UnionPayIncomingDTO unionPayIncomingDTO = shops.get(NumberConstant.ZERO);
            return incomingIsFinish(unionPayIncomingDTO.getType(), unionPayIncomingDTO.getBid());
        } catch (TfException e) {
            log.error("批量判断进件是否完成tfException:{}", e.getMessage());
            return Result.failed(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.failed(PayExceptionCodeEnum.BALANCE_ACCOUNT_NAME_ERROR);
    }

    /**
     * 获取银行卡
     *
     * @param type
     * @param bid  类型1商家2供应商
     * @return
     */
    @Override
    public Result<List<CustBankInfoRespDTO>> getCustBankInfoList(Integer type, String bid) {
        try {
            log.info("参数：bid={},type={}", bid, type);
            LoanUserEntity loanUser = loanUserService.getLoanUserByBusIdAndType(bid, type);
            if (ObjectUtils.isEmpty(loanUser)) {
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
    public Result<BalanceAcctRespDTO> getAccountInfoByBusId(String type, String busId) {
        Result<List<BalanceAcctRespDTO>> listResult = this.listAccountInfoByBusId(type, Collections.singletonList(busId));
        List<BalanceAcctRespDTO> data = listResult.getData();
        if (CollectionUtil.isEmpty(data)) {
            throw new TfException(ExceptionCodeEnum.ILLEGAL_ARGUMENT);
        }
        return Result.ok(data.get(0));
    }

    @Override
    public Result<List<BalanceAcctRespDTO>> listAccountInfoByBusId(String type, List<String> busIds) {
        List<UnionPayLoanUserRespDTO> unionPayLoanUserRespDTOS = loanUserService.listLoanUserByBusId(type, busIds);
        if (CollectionUtil.isEmpty(unionPayLoanUserRespDTOS)) {
            return Result.ok(new ArrayList<>());
        }
        List<BalanceAcctRespDTO> list = new ArrayList<>(unionPayLoanUserRespDTOS.size());
        for (UnionPayLoanUserRespDTO unionPayLoanUserRespDTO : unionPayLoanUserRespDTOS) {
            BalanceAcctRespDTO balanceAcctRespDTO = new BalanceAcctRespDTO();
            BeanUtil.copyProperties(unionPayLoanUserRespDTO, balanceAcctRespDTO);
            list.add(balanceAcctRespDTO);
        }
        return Result.ok(list);
    }

    @Override
    public Result<String> unbindSettleAcct(BankInfoReqDTO bankInfoReqDTO) {
        unionPayLoansBizService.unbindSettleAcct(bankInfoReqDTO);
        return Result.ok("解绑成功");
    }

    @Override
    public Result<String> bindSettleAcct(BankInfoReqDTO bankInfoReqDTO) {
        boolean boundSettleAcct = unionPayLoansBizService.bindSettleAcct(bankInfoReqDTO);
        if (boundSettleAcct) {
            return Result.ok("绑定成功");
        } else {
            return Result.failed("绑定失败");
        }
    }

}
