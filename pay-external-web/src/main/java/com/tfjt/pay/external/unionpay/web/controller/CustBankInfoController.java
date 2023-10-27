package com.tfjt.pay.external.unionpay.web.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.api.dto.resp.BankInfoReqDTO;
import com.tfjt.pay.external.unionpay.biz.UnionPayLoansBizService;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.CaptchaService;
import com.tfjt.pay.external.unionpay.service.CustBankInfoService;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.pay.external.unionpay.service.UnionPayLoansApiService;
import com.tfjt.pay.external.unionpay.utils.MobileUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

import static com.tfjt.pay.external.unionpay.utils.DateUtil.timeComparison;


/**
 * 客户银行信息
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-20 09:27:39
 */
@RestController
@RequestMapping("custbankinfo")
@Slf4j
public class CustBankInfoController {
    @Autowired
    private CustBankInfoService custBankInfoService;
    @Resource
    CaptchaService captchaService;

    @Resource
    private UnionPayLoansApiService yinLianLoansApiService;

    @Resource
    LoanUserService loanUserService;

    @Resource
    UnionPayLoansBizService unionPayLoansBizService;

    @Value("${unionPay.isTest:false}")
    boolean isTest;

    /**
     * 获取用户银行卡信息
     *
     * @param busId 业务 id
     * @param type  登录类型 1 = 云商 2 = 供应商
     * @return
     */
    @GetMapping("/list")
    public Result<?> list(@RequestParam Long busId, @RequestParam Integer type) {
        return null;
    }


    /**
     * 信息
     */
    @GetMapping("/info/{loanUserId}")
    public Result<?> info(@PathVariable("loanUserId") Long loanUserId) {
        CustBankInfoEntity custBankInfo = custBankInfoService.getByLoanUserId(loanUserId);
        log.info("获取客户银行信息:{}",custBankInfo);
        return Result.ok(custBankInfo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public Result<?> save(@RequestBody CustBankInfoEntity custBankInfo) {
        try {
            boolean flag = timeComparison(null, null, isTest);
            if (!flag) {
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            ValidatorUtils.validateEntity(custBankInfo);
            //手机号校验
            flag = MobileUtil.checkPhone(custBankInfo.getPhone());
            if (!flag) {
                return Result.failed("手机号格式错误！");
            }
        } catch (Exception ex) {
            return Result.failed(ex.getMessage());
        }
        //判断银行卡是否重复
        long count = custBankInfoService.count(new LambdaQueryWrapper<CustBankInfoEntity>().eq(CustBankInfoEntity::getBankCardNo,
                custBankInfo.getBankCardNo()));
        if (count > 0) {
            throw new TfException(PayExceptionCodeEnum.EXISTED_BANK_CARD);
        }
        //查询贷款用户类型
        LoanUserEntity loanUerInfo = loanUserService.getById(custBankInfo.getLoanUserId());
        long loanUserType = loanUerInfo.getLoanUserType();
        int settlementType = custBankInfo.getSettlementType();
        if (loanUserType == 1) {

            if (settlementType != 2) {
                return Result.failed("结算类型错误");
            }
        } else if (loanUserType == 2) {
            if (settlementType == 0) {
                return Result.failed("结算类型错误");
            }
        }
        boolean bool = custBankInfoService.save(custBankInfo);
        if (bool) {
            return Result.ok("保存成功");
        } else {
            return Result.failed("保存失败");
        }
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody CustBankInfoEntity custBankInfo) {
        try {
            boolean flag = timeComparison(null, null, isTest);
            if (!flag) {
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            //手机号校验
            flag = MobileUtil.checkPhone(custBankInfo.getPhone());
            if (!flag) {
                return Result.failed("手机号格式错误！");
            }
            ValidatorUtils.validateEntity(custBankInfo);
            //判断卡号是否存在
            CustBankInfoEntity bankInfoByBankCardNoAndLoanUserId = custBankInfoService.getBankInfoByBankCardNoAndLoanUserId(custBankInfo.getBankCardNo(), custBankInfo.getLoanUserId());
            if (bankInfoByBankCardNoAndLoanUserId != null) {
                if (!custBankInfo.getId().equals(bankInfoByBankCardNoAndLoanUserId.getId())) {
                    return Result.failed("该卡号已存在");
                }
            }

            //查询贷款用户类型
            LoanUserEntity loanUerInfo = loanUserService.getById(custBankInfo.getLoanUserId());
            long loanUserType = loanUerInfo.getLoanUserType();
            int settlementType = custBankInfo.getSettlementType();
            if (loanUserType == 1) {

                if (settlementType != 2) {
                    return Result.failed("结算类型错误");
                }
            } else if (loanUserType == 2) {
                if (settlementType == 0) {
                    return Result.failed("结算类型错误");
                }
            }
            return Result.ok(custBankInfoService.updateCustBankInfo(custBankInfo));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.getIncomingInfo.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-结算信息-修改：param={}", JSON.toJSONString(custBankInfo), e);
            return Result.failed(e.getMessage());
        }
    }


    /**
     * 查询账号信息
     */
    @GetMapping("/querySettleAcct")
    public Result<?> querySettleAcct(Integer id) {

        try {
            return Result.ok(yinLianLoansApiService.querySettleAcct(id));
        } catch (TfException e) {
            log.error("YinLianLoansApiController.getIncomingInfo.err:{}", e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("银联-货款-账号信息：param={}", JSON.toJSONString(id), e);
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    public Result<?> delete(@RequestBody Integer[] ids) {
        custBankInfoService.removeByIds(Arrays.asList(ids));
        return Result.ok();
    }


    @PostMapping("/unbindSettleAcct")
    public Result<String> unbindSettleAcct(@RequestBody BankInfoReqDTO bankInfoReqDTO) {
        try {
            unionPayLoansBizService.unbindSettleAcct(bankInfoReqDTO);
        } catch (TfException ex) {
            return Result.failed(ex.getMessage());
        }
        return Result.ok();
    }


    @PostMapping("/bindSettleAcct")
    public Result<String> bindSettleAcct(@RequestBody BankInfoReqDTO bankInfoReqDTO) {
        unionPayLoansBizService.bindSettleAcct(bankInfoReqDTO);
        return Result.ok();
    }


}
