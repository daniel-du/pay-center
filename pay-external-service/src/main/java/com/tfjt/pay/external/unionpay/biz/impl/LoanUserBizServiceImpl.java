package com.tfjt.pay.external.unionpay.biz.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.api.dto.UserTypeDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.PaymentPasswordReqDTO;
import com.tfjt.pay.external.unionpay.biz.LoanUserBizService;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.entity.PaymentPasswordEntity;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.pay.external.unionpay.service.PaymentPasswordService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Component
public class LoanUserBizServiceImpl implements LoanUserBizService {

    @Autowired
    private LoanUserService loanUserService;

    @Resource
    private PaymentPasswordService paymentPasswordService;


    @Override
    public void applicationStatusUpdateJob(String jobParam) {
        loanUserService.applicationStatusUpdateJob(jobParam);
    }

    @Override
    public Result<String> savePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        checkLoanUser(paymentPasswordDTO.getBusId(), paymentPasswordDTO.getType());
        try {
            ValidatorUtils.validateEntity(paymentPasswordDTO);
            PaymentPasswordEntity paymentPassword = new PaymentPasswordEntity();
            BeanUtils.copyProperties(paymentPasswordDTO, paymentPassword);
            paymentPasswordService.save(paymentPassword);
        } catch (Exception ex) {
            if (ex instanceof DuplicateKeyException) {
                return Result.failed("请勿重复设置支付密码");
            }
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok();
    }

    @Override
    public Result<String> updatePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        checkLoanUser(paymentPasswordDTO.getBusId(), paymentPasswordDTO.getType());
        try {
            UpdateWrapper<PaymentPasswordEntity> updateWrapper = Wrappers.update();
            updateWrapper.lambda().eq(PaymentPasswordEntity::getType, paymentPasswordDTO.getType()).eq(PaymentPasswordEntity::getBusId, paymentPasswordDTO.getBusId());
            PaymentPasswordEntity paymentPasswordEntity = new PaymentPasswordEntity();
            paymentPasswordEntity.setPassword(paymentPasswordDTO.getPassword());
            paymentPasswordService.update(paymentPasswordEntity, updateWrapper);
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok();
    }

    @Override
    public Result<String> getSalt(UserTypeDTO userType) {
        String salt = null;
        checkLoanUser(userType.getBusId(), userType.getType());
        try {
            PaymentPasswordEntity paymentPassword = paymentPasswordService.getOne(Wrappers.lambdaQuery(PaymentPasswordEntity.class).eq(PaymentPasswordEntity::getType, userType.getType()).eq(PaymentPasswordEntity::getBusId, userType.getBusId()));
            if (ObjectUtils.isNotEmpty(paymentPassword)) {
                salt = paymentPassword.getSalt();
            }
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok(salt);
    }

    @Override
    public Result<Boolean> isExist(UserTypeDTO userType) {
        checkLoanUser(userType.getBusId(), userType.getType());
        boolean result;
        try {
            result = paymentPasswordService.count(Wrappers.lambdaQuery(PaymentPasswordEntity.class).eq(PaymentPasswordEntity::getType, userType.getType()).eq(PaymentPasswordEntity::getBusId, userType.getBusId())) > 0;
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok(result);
    }

    @Override
    public Result<Boolean> verifyPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        checkLoanUser(paymentPasswordDTO.getBusId(), paymentPasswordDTO.getType());
        boolean result = false;
        try {
            PaymentPasswordEntity paymentPassword = paymentPasswordService.getOne(Wrappers.lambdaQuery(PaymentPasswordEntity.class).eq(PaymentPasswordEntity::getType, paymentPasswordDTO.getType()).eq(PaymentPasswordEntity::getBusId, paymentPasswordDTO.getBusId()));
            if (ObjectUtils.isNotEmpty(paymentPassword)) {
                result = Objects.equals(paymentPasswordDTO.getPassword(), paymentPassword.getPassword());
                return Result.ok(result);
            }
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok(result);
    }


    private void checkLoanUser(String busId, Integer type) {
        LoanUserEntity loanUser = loanUserService.getLoanUserByBusIdAndType(busId, type);
        if (loanUser == null) {
            throw new TfException("未找到贷款用户");
        }
    }
}
