package com.tfjt.pay.external.unionpay.web.controller;

import com.tfjt.pay.external.unionpay.dto.BankInfoDTO;
import com.tfjt.pay.external.unionpay.dto.LoanUserInfoDTO;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceAcctEntity;
import com.tfjt.pay.external.unionpay.service.CustBankInfoService;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.pay.external.unionpay.service.LoanBalanceAcctService;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("userInfo")
public class UserLoanInfoController {

    @Autowired
    private CustBankInfoService custBankInfoService;

    @Resource
    private LoanBalanceAcctService loanBalanceAcctService;

    @Resource
    private LoanUserService loanUserService;

    /**
     * 获取用户银行卡信息
     * @param loanUserId  贷款用户id
     * @return
     */
    @GetMapping("/bankCardList")
    public Result<?> bankCarList(@RequestParam Long loanUserId) {
        try {
            List<BankInfoDTO> list = this.custBankInfoService.getBankInfoByBus(loanUserId);
            return Result.ok(list);
        }catch (Exception e){
            log.error("查询用户银行卡信息异常,loanUserId:{}",loanUserId);
            return Result.failed(e.getMessage());
        }
    }


    /**
     * 根据用户 id 获取 电子账簿集合
     * @param loanUserId  贷款用户id
     * @return
     */
    @GetMapping("/accountBooksList")
    public Result<?> list(@RequestParam Long loanUserId) {
        try {
            List<LoanBalanceAcctEntity> list = this.loanBalanceAcctService.getAccountBooksListByBus(loanUserId);
            return Result.ok(list);
        }catch (Exception e){
            log.error("查询用户电子账簿异常,loanUserId:{}",loanUserId);
            return Result.failed(e.getMessage());
        }
    }


    @GetMapping("/getLoanUserInfo")
    public Result<?> getLoanUserInfo(@RequestParam Long loanUserId) {
        try {
            LoanUserInfoDTO dto = this.loanUserService.getLoanUerInfo(loanUserId);
            return Result.ok(dto);
        }catch (Exception e){
            log.error("查询用户信息异常,loanUserId:{}",loanUserId);
            return Result.failed(e.getMessage());
        }
    }
}
