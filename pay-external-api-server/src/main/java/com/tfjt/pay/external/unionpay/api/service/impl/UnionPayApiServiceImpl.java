package com.tfjt.pay.external.unionpay.api.service.impl;

import com.tfjt.pay.external.unionpay.api.dto.resp.UnionPayTransferDTO;
import com.tfjt.pay.external.unionpay.api.service.UnionPayApiService;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.dto.resp.LoanAccountDTO;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

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
        LoanAccountDTO loanAccountDTO = unionPayService.getLoanAccount(accountConfig.getBalanceAcctId());
        if (Objects.isNull(loanAccountDTO)){
            return Result.failed("未查询到账户信息");
        }
        return Result.ok(loanAccountDTO.getSettledAmount());
    }


}
