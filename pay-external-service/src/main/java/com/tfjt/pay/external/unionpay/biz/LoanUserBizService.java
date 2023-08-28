package com.tfjt.pay.external.unionpay.biz;

import com.tfjt.pay.external.unionpay.api.dto.UserTypeDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.PaymentPasswordReqDTO;
import com.tfjt.tfcommon.dto.response.Result;

public interface LoanUserBizService {
    void applicationStatusUpdateJob(String jobParam);

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
     * @param userType
     * @return
     */
    public Result<String> getSalt(UserTypeDTO userType);
    /**
     * 判断密码是否存在
     */
    public Result<Boolean> isExist(UserTypeDTO userType);

    /**
     * 验证支付密码
     * @param paymentPasswordDTO
     * @return
     */
    public Result<Boolean> verifyPassword(PaymentPasswordReqDTO paymentPasswordDTO);
}
