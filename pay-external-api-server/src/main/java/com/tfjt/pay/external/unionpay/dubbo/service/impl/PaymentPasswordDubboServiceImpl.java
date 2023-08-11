package com.tfjt.pay.external.unionpay.dubbo.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.dubbo.dto.req.PaymentPasswordReqDTO;
import com.tfjt.pay.external.unionpay.dubbo.dto.resp.PaymentPasswordRespDTO;
import com.tfjt.pay.external.unionpay.dubbo.service.PaymentPasswordDubboService;
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
public class PaymentPasswordDubboServiceImpl implements PaymentPasswordDubboService {

    @Resource
    private PaymentPasswordService paymentPasswordService;

    @Override
    public Result<String> savePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        try {
            PaymentPasswordEntity paymentPassword = new PaymentPasswordEntity();
            BeanUtils.copyProperties(paymentPasswordDTO, paymentPassword);
            paymentPasswordService.save(paymentPassword);
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed("保存支付密码异常！");
        }
        return Result.ok();
    }

    @Override
    public Result<String> updatePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        try {
            UpdateWrapper<PaymentPasswordEntity> updateWrapper = Wrappers.update();
            updateWrapper.lambda().eq(PaymentPasswordEntity::getBusId, paymentPasswordDTO.getBusId()).eq(PaymentPasswordEntity::getType, paymentPasswordDTO.getType());
            PaymentPasswordEntity paymentPasswordEntity = new PaymentPasswordEntity();
            paymentPasswordEntity.setPassword(paymentPasswordDTO.getPassword());
            paymentPasswordService.update(paymentPasswordEntity, updateWrapper);
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed("修改失败！");
        }
        return Result.ok();
    }

    @Override
    public Result<PaymentPasswordRespDTO> getSalt(PaymentPasswordReqDTO paymentPasswordDTO) {
        PaymentPasswordRespDTO passwordRespDTO = null;
        try {
            PaymentPasswordEntity paymentPassword = paymentPasswordService.getOne(Wrappers.lambdaQuery(PaymentPasswordEntity.class).eq(PaymentPasswordEntity::getBusId, paymentPasswordDTO.getBusId()).eq(PaymentPasswordEntity::getType, paymentPasswordDTO.getType()));
            if (ObjectUtils.isNotEmpty(paymentPassword)) {
                passwordRespDTO = new PaymentPasswordRespDTO();
                passwordRespDTO.setSalt(paymentPasswordDTO.getSalt());
            }
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(passwordRespDTO);
        }
        return Result.ok(passwordRespDTO);
    }

    @Override
    public Result<Boolean> verifyPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        Boolean result = false;
        PaymentPasswordRespDTO passwordRespDTO = null;
        try {
            PaymentPasswordEntity paymentPassword = paymentPasswordService.getOne(Wrappers.lambdaQuery(PaymentPasswordEntity.class).eq(PaymentPasswordEntity::getBusId, paymentPasswordDTO.getBusId()).eq(PaymentPasswordEntity::getType, paymentPasswordDTO.getType()));
            if (ObjectUtils.isNotEmpty(paymentPassword)) {

                result = Objects.equals(paymentPasswordDTO.getPassword(), paymentPassword.getPassword());
                return Result.ok(result);
            }
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(result);
        }
        return Result.ok(result);
    }
}
