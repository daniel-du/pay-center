package com.tfjt.pay.external.unionpay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.dto.LoanBalanceCreateDto;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceEntity;
import com.tfjt.pay.external.unionpay.service.LoanBalanceService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("loanBalance")
public class LoanBalanceController {
    @Resource
    private LoanBalanceService tfLoanBalanceService;

    /**
     * 信息详情
     */
    @GetMapping("info/{shopId}")
    @ApiOperation("详情")
    public Result<?> info(@PathVariable Integer shopId) {
        try {
            LoanBalanceEntity custHoldingEntity = tfLoanBalanceService.getByShopId(shopId);
            return Result.ok(custHoldingEntity);
        } catch (TfException e) {
            log.error("查询贷款信息详情异常：param={}", shopId, e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询贷款信息详情异常：param={}", shopId, e);
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("update")
    @ApiOperation("编辑贷款信息")
    public Result<?> update(@RequestBody LoanBalanceCreateDto dto) {
        try {
            ValidatorUtils.validateEntity(dto);
            tfLoanBalanceService.update(dto);
            return Result.ok();
        } catch (TfException e) {
            log.error("编辑贷款信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("编辑贷款信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getMessage());
        }
    }
}
