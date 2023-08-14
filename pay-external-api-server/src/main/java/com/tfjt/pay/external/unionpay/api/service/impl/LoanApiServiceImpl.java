package com.tfjt.pay.external.unionpay.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tfjt.pay.external.unionpay.api.dto.req.LoanTransferToTfDTO;
import com.tfjt.pay.external.unionpay.api.service.LoanApiService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.dao.LoanUserDao;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceAcctEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.service.LoanBalanceAcctService;
import com.tfjt.pay.external.unionpay.service.LoanBalanceService;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.tfcommon.dto.response.Result;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public Result<LoanTransferToTfDTO> getBalanceAcctId(String type, String bid) {
        LoanTransferToTfDTO loanTransferToTfDTO = new LoanTransferToTfDTO();
        loanTransferToTfDTO.setTfBalanceAcctId(accountConfig.getBalanceAcctId());
        loanTransferToTfDTO.setTfBalanceAcctName(accountConfig.getBalanceAcctName());
        LoanBalanceAcctEntity balanceAcc = loanBalanceAcctService.getBalanceAcctIdByBidAndType(bid, type);
        if (Objects.isNull(balanceAcc)){
            return Result.failed("电子账簿信息不存在");
        }
        loanTransferToTfDTO.setBalanceAcctId(balanceAcc.getBalanceAcctId());
        loanTransferToTfDTO.setBalanceAcctName(balanceAcc.getRelAcctNo());
        return Result.ok(loanTransferToTfDTO);
    }

    @Override
    public Result<Map<String, Object>> incomingIsFinish(String type, String bid) {
        Map<String, Object> result = new HashMap<>();
        LoanUserEntity loanUser = this.baseMapper.selectOne(new QueryWrapper<LoanUserEntity>()
                .eq("loan_user_type", type).eq("bus_id", bid));
        if (ObjectUtils.isNotEmpty(loanUser)) {
            result.put("isIncoming", true);
            result.put("settledAmount", 10);
            return Result.ok(result);
        }
        result.put("isIncoming", false);
        result.put("settledAmount", 0);
        return Result.ok(result);
    }
}
