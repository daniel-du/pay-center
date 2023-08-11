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
     * 类型1商家2供应商
     */
    private Integer type;

    /**
     * 业务ID
     */
    private String busId;

    /**
     * 密码
     */
    private String password;


    /**
     * 盐
     */
    private String salt;


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
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

    public PaymentPasswordReqDTO(Integer type, String busId, String password, String salt) {
        this.type = type;
        this.busId = busId;
        this.password = password;
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "PaymentPasswordDTO{" +
                "type=" + type +
                ", busId='" + busId + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
