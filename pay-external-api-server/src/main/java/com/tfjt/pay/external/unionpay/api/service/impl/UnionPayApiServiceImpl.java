package com.tfjt.pay.external.unionpay.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.api.dto.resp.BalanceAcctDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.UnionPayTransferDTO;
import com.tfjt.pay.external.unionpay.api.service.UnionPayApiService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.dto.resp.LoanAccountDTO;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 银联接口服务实现类
 * @author songx
 * @date 2023-08-10 17:02
 * @email 598482054@qq.com
 */
@Slf4j
@DubboService
public class UnionPayApiServiceImpl implements UnionPayApiService {
    @Autowired
    private TfAccountConfig accountConfig;

    @Autowired
    private UnionPayService unionPayService;
    @Override
    public Result<String> transfer(UnionPayTransferDTO payTransferDTO) {
        return null;
    }

    @Override
    public Result<Integer> currentBalance() {
        BalanceAcctDTO balanceAcctDTOByAccountId = getBalanceAcctDTOByAccountId(accountConfig.getBalanceAcctId());
        log.debug("查询母账户交易余额返回:{}",balanceAcctDTOByAccountId.getSettledAmount());
        return Result.ok(balanceAcctDTOByAccountId.getSettledAmount());
    }

    @Override
    public Result<BalanceAcctDTO> getBalanceByAccountId(String balanceAcctId) {
        log.debug("查询电子账簿id:{}",balanceAcctId);
        if(StringUtil.isBlank(balanceAcctId)){
            return Result.failed("电子账簿id不能为空");
        }
        BalanceAcctDTO balanceAcctDTO = getBalanceAcctDTOByAccountId(balanceAcctId);
        if (Objects.isNull(balanceAcctDTO)){
            String message = String.format("[%s]电子账簿信息不存在", balanceAcctId);
            log.error(String.format(message));
            return Result.failed(message);
        }
        return Result.ok(balanceAcctDTO);
    }

    @Override
    public Result<Map<String,BalanceAcctDTO>> listBalanceByAccountIds(List<String> balanceAcctIds) {
        log.debug("批量查询电子账户参数信息:{}",JSONObject.toJSONString(balanceAcctIds));
        if (CollectionUtil.isEmpty(balanceAcctIds)){
            return Result.failed("电子账簿id不能为空");
        }
        Map<String,BalanceAcctDTO> result = new HashMap<>(balanceAcctIds.size());
        for (String balanceAcctId : balanceAcctIds) {
            BalanceAcctDTO balanceAcctDTOByAccountId = getBalanceAcctDTOByAccountId(balanceAcctId);
            if (!Objects.isNull(balanceAcctDTOByAccountId)){
                result.put(balanceAcctId,balanceAcctDTOByAccountId);
            }
        }
        log.debug("批量查询电子账户返回信息:{}",JSONObject.toJSONString(result));
        return Result.ok(result);
    }

    /**
     * 获取指定电子账簿的账户信息
     * @param balanceAcctId 账户账户id
     * @return 电子账户信息
     */
    private BalanceAcctDTO getBalanceAcctDTOByAccountId(String balanceAcctId) {
        LoanAccountDTO loanAccountDTO = unionPayService.getLoanAccount(balanceAcctId);
        if (Objects.isNull(loanAccountDTO)){
            return null;
        }
        BalanceAcctDTO balanceAcctDTO = new BalanceAcctDTO();
        BeanUtil.copyProperties(loanAccountDTO,balanceAcctDTO);
        return balanceAcctDTO;
    }


}
