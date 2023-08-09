package com.tfjt.pay.external.unionpay.web.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.tfcloud.business.api.TfLoanBalanceRpcService;
import com.tfjt.tfcloud.business.dto.TfLoanBalanceCreateDto;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

import static com.tfjt.pay.external.unionpay.utils.DateUtil.timeComparison;

/**
 * 贷款-用户
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-20 11:23:12
 */
@Slf4j
@RestController
@RequestMapping("loanuser")
public class LoanUserController {

    @Resource
    LoanUserService loanUserService;
    @DubboReference(retries = 1, timeout = 60000, check = false)
    private TfLoanBalanceRpcService tfLoanBalanceRpcService;

    /**
     * @param loanUserEntity
     * @return
     */
    @PostMapping("/save")
    public Result<?> save(@RequestBody LoanUserEntity loanUserEntity) {
        try {
            boolean flag = timeComparison(null, null);
            if (!flag) {
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            ValidatorUtils.validateEntity(loanUserEntity);
            return loanUserService.saveLoanUser(loanUserEntity);
        } catch (TfException e) {
            log.error("保存loanUserEntity异常:", e.getMessage());
            return Result.failed(e.getMessage());
        } catch (Exception ex) {
            log.error("保存loanUserEntity异常:", ex);
            return Result.failed(ex);
        }

    }

    /**
     * @param id
     * @return
     */
    @PostMapping("/updateLoanUser")
    public Result<?> updateLoanUser(@RequestParam(value = "id") Long id, @RequestParam(value = "loanUserType") Integer loanUserType) {
        try {
            boolean flag = timeComparison(null, null);
            if (!flag) {
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            if (id == null) {
                return Result.failed("id不能为空！");
            }
            if (loanUserType == null) {
                return Result.failed("用户进件类型不能为空！");
            }

            return loanUserService.updateLoanUser(id, loanUserType);
        } catch (TfException e) {
            log.error("保存loanUserEntity异常:", e.getMessage());
            return Result.failed(e.getMessage());
        } catch (Exception ex) {
            log.error("保存loanUserEntity异常:", ex);
            return Result.failed(ex);
        }

    }

    /**
     * @param params
     * @return
     */
    @PostMapping("/info")
    public Result<?> info(@RequestBody Map<String, Object> params) {
        Integer type;
        String busId;
        if (params.get("type") != null) {
            type = Integer.valueOf(params.get("type").toString());
        } else {
            return Result.failed("请填写类型");
        }

        if (params.get("busId") != null) {
            busId = params.get("busId").toString();
        } else {
            return Result.failed("请填写业务ID");
        }
        LoanUserEntity tfLoanUser = loanUserService.getBaseMapper().selectOne(Wrappers.lambdaQuery(LoanUserEntity.class).eq(LoanUserEntity::getType, type).eq(LoanUserEntity::getBusId, busId));

        return Result.ok(tfLoanUser);
    }

    /**
     * 判断是否进件
     *
     * @param busId 类型1商家2供应商
     * @return
     * @Param busId 业务ID
     */
    @GetMapping("/isIncoming")
    public Result<?> isIncoming(@RequestParam String busId) {
        try {

            com.tfjt.tfcloud.business.result.Result<TfLoanBalanceCreateDto> result = tfLoanBalanceRpcService.getLoanBalanceByBusId(busId);
            if (0 != result.getCode()) {
                return Result.failed(result.getCode(), result.getMsg());
            }
            return Result.ok(result.getData());
            //效验数据
        } catch (TfException e) {
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("判断是否进件：param={}", JSON.toJSONString(busId), e);
            return Result.failed(e.getMessage());
        }
    }

}
