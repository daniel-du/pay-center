package com.tfjt.pay.external.unionpay.api.service.impl;

import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.api.dto.req.PaymentPasswordReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.PaymentPasswordRespDTO;
import com.tfjt.pay.external.unionpay.api.service.PaymentPasswordApiService;
import com.tfjt.pay.external.unionpay.entity.PaymentPasswordEntity;
import com.tfjt.pay.external.unionpay.service.PaymentPasswordService;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@DubboService
public class PaymentPasswordApiServiceImpl implements PaymentPasswordApiService {

    @Resource
    private PaymentPasswordService paymentPasswordService;

    @Override
    @Lock4j(keys = {"#paymentPasswordDTO.loanUserId"}, expire = 10000, acquireTimeout = 3000)
    public Result<String> savePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        try {
            PaymentPasswordEntity paymentPassword = new PaymentPasswordEntity();
            BeanUtils.copyProperties(paymentPasswordDTO, paymentPassword);
            paymentPasswordService.save(paymentPassword);
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok();
    }

    @Override
    public Result<String> updatePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        try {
            UpdateWrapper<PaymentPasswordEntity> updateWrapper = Wrappers.update();
            updateWrapper.lambda().eq(PaymentPasswordEntity::getLoanUserId, paymentPasswordDTO.getLoanUserId());
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
    public Result<PaymentPasswordRespDTO> getSalt(Long loanUserId) {
        PaymentPasswordRespDTO passwordRespDTO = null;
        try {
            PaymentPasswordEntity paymentPassword = paymentPasswordService.getOne(Wrappers.lambdaQuery(PaymentPasswordEntity.class).eq(PaymentPasswordEntity::getLoanUserId,loanUserId));
            if (ObjectUtils.isNotEmpty(paymentPassword)) {
                passwordRespDTO = new PaymentPasswordRespDTO();
                passwordRespDTO.setSalt(paymentPassword.getSalt());
            }
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok(passwordRespDTO);
    }

    @Override
    public Result<Boolean> verifyPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        boolean result = false;
        try {
            PaymentPasswordEntity paymentPassword = paymentPasswordService.getOne(Wrappers.lambdaQuery(PaymentPasswordEntity.class).eq(PaymentPasswordEntity::getLoanUserId, paymentPasswordDTO.getLoanUserId()));
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
}
