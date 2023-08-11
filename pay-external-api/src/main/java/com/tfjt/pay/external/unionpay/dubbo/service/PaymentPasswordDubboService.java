package com.tfjt.pay.external.unionpay.dubbo.service;

import com.tfjt.pay.external.unionpay.dubbo.dto.req.PaymentPasswordReqDTO;
import com.tfjt.pay.external.unionpay.dubbo.dto.resp.PaymentPasswordRespDTO;
import com.tfjt.tfcommon.dto.response.Result;

public interface PaymentPasswordDubboService {
    /**
     * 设置支付密码
     * @param paymentPasswordDTO
     * @return
     */
    public Result<String> savePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO);

    /**
     * 更新支付密码
     * @param paymentPasswordDTO
     * @return
     */
    public Result<String> updatePaymentPassword(PaymentPasswordReqDTO paymentPasswordDTO);

    /**
     *
     * @param paymentPasswordDTO
     * @return
     */
    public Result<PaymentPasswordRespDTO> getSalt(PaymentPasswordReqDTO paymentPasswordDTO);

    /**
     * 验证支付密码
     * @param paymentPasswordDTO
     * @return
     */
    public Result<Boolean> verifyPassword(PaymentPasswordReqDTO paymentPasswordDTO);
}
