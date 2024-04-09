package com.tfjt.pay.external.unionpay.dto.resp;

import com.tfjt.pay.external.unionpay.enums.digital.DigitalCodeEnum;
import com.tfjt.pay.external.unionpay.enums.digital.DigitalErrorCodeEnum;
import com.tfjt.pay.external.unionpay.enums.digital.DigitalTransactionStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @Date: 2023/11/28/18:13
 * @Description: 数字人民查询响应
 */
@Data
public class DigitalRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bussReceiptStat;

    private String bussReceiptCode;
    /**
     * 拒绝吗
     */
    private String bussRejectCode;
    /**
     * 错误码
     */
    private String bussReceiptDesc;
    /**
     * 查询类型
     */
    private String queryType;
    /**
     * 商户注册标识
     */
    private String mchntSideRegisterFlag;
    /**
     *加密证书序
     * 列号
     */
    private Integer keySn;
    /**
     * 证件类型
     */
    private String certType;
    /**
     * 证件号码
     */
    private String certId;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 签约号
     */
    private String signContract;

    public DigitalRespDTO(DigitalTransactionStatusEnum digitalTransactionStatusEnum){
        this.bussReceiptStat = digitalTransactionStatusEnum.getCode();
    }
    public DigitalRespDTO(DigitalTransactionStatusEnum digitalTransactionStatusEnum, DigitalErrorCodeEnum digitalCodeEnum){
        this.bussReceiptStat = digitalTransactionStatusEnum.getCode();
        this.bussReceiptCode = digitalCodeEnum.getCode();
        this.bussReceiptDesc = digitalCodeEnum.getDesc();
    }

}
