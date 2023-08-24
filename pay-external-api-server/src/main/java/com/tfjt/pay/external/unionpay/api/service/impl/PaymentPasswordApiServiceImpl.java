package com.tfjt.pay.external.unionpay.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.api.dto.UserTypeDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.PaymentPasswordReqDTO;
import com.tfjt.pay.external.unionpay.api.service.PaymentPasswordApiService;
import com.tfjt.pay.external.unionpay.entity.PaymentPasswordEntity;
import com.tfjt.pay.external.unionpay.service.PaymentPasswordService;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@DubboService
public class PaymentPasswordApiServiceImpl implements PaymentPasswordApiService {

    @Resource
    private PaymentPasswordService paymentPasswordService;

    @Override
    public Result<String> savePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        try {
            ValidatorUtils.validateEntity(paymentPasswordDTO);
            PaymentPasswordEntity paymentPassword = new PaymentPasswordEntity();
            BeanUtils.copyProperties(paymentPasswordDTO, paymentPassword);
            paymentPasswordService.save(paymentPassword);
        } catch (Exception ex) {
            if(ex instanceof DuplicateKeyException) {
                return Result.failed("请勿重复设置支付密码");
            }
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok();
    }

    @Override
    public Result<String> updatePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
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

    /**
     *
     * @param userType
     * @return
     */
    @Override
    public Result<String> getSalt(UserTypeDTO userType) {
        String salt = null;

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

    /**
     * 查询密码是否存在
     * @param userType
     * @return
     */
    @Override
    public Result<Boolean> isExist(UserTypeDTO userType) {
        boolean result;
        try {
            result = paymentPasswordService.count(Wrappers.lambdaQuery(PaymentPasswordEntity.class).eq(PaymentPasswordEntity::getType, userType.getType()).eq(PaymentPasswordEntity::getBusId, userType.getBusId())) > 0;
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }
        return Result.ok(result);
    }


    /**
     * 验证密码
     * @param paymentPasswordDTO
     * @return
     */
    @Override
    public Result<Boolean> verifyPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
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
}
