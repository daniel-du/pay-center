package com.tfjt.pay.external.unionpay.api.dto.req;

import java.io.Serializable;

/**
 * <p>
 * 支付密码
 * </p>
 *
 * @author young
 * @since 2023-08-11
 */
public class PaymentPasswordReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 贷款用户id
     */
    private Long loanUserId;

    /**
     * 密码
     */
    private String password;


    /**
     * 盐
     */
    private String salt;

    public Long getLoanUserId() {
        return loanUserId;
    }

    public void setLoanUserId(Long loanUserId) {
        this.loanUserId = loanUserId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public PaymentPasswordReqDTO() {
    }

    @Override
    public String toString() {
        return "PaymentPasswordReqDTO{" +
                "loanUserId=" + loanUserId +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
