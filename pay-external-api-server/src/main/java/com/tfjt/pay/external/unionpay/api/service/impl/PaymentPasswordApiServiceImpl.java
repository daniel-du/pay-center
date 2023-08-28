package com.tfjt.pay.external.unionpay.api.service.impl;

import com.tfjt.pay.external.unionpay.api.dto.UserTypeDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.PaymentPasswordReqDTO;
import com.tfjt.pay.external.unionpay.api.service.PaymentPasswordApiService;
import com.tfjt.pay.external.unionpay.biz.LoanUserBizService;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@Slf4j
@DubboService
public class PaymentPasswordApiServiceImpl implements PaymentPasswordApiService {

    @Resource
    private LoanUserBizService loanUserBizService;

    @Override
    public Result<String> savePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        return loanUserBizService.savePaymentPassword(paymentPasswordDTO);
    }

    @Override
    public Result<String> updatePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        return loanUserBizService.updatePaymentPassword(paymentPasswordDTO);
    }

    /**
     * @param userType
     * @return
     */
    @Override
    public Result<String> getSalt(UserTypeDTO userType) {
        return loanUserBizService.getSalt(userType);
    }

    /**
     * 查询密码是否存在
     *
     * @param userType
     * @return
     */
    @Override
    public Result<Boolean> isExist(UserTypeDTO userType) {
        return loanUserBizService.isExist(userType);
    }


    /**
     * 验证密码
     *
     * @param paymentPasswordDTO
     * @return
     */
    @Override
    public Result<Boolean> verifyPassword(PaymentPasswordReqDTO paymentPasswordDTO) {
        return loanUserBizService.verifyPassword(paymentPasswordDTO);
    }
}
