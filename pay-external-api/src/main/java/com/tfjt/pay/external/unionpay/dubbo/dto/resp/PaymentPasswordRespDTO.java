package com.tfjt.pay.external.unionpay.dubbo.dto.resp;

import java.io.Serializable;

/**
 * <p>
 * 支付密码
 * </p>
 *
 * @author young
 * @since 2023-08-11
 */
public class PaymentPasswordRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 密码
     */
    private String password;


    /**
     * 盐
     */
    private String salt;

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

    public PaymentPasswordRespDTO() {
    }

    public PaymentPasswordRespDTO(Integer type, String busId, String password, String salt) {
        this.password = password;
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "PaymentPasswordDTO{" +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
