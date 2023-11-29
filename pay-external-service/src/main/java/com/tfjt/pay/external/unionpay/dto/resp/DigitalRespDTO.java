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

    private String bussReceiptDesc;

    private String queryType;

    private String mchntSideRegisterFlag;

    private String keySn;

    public DigitalRespDTO(DigitalTransactionStatusEnum digitalTransactionStatusEnum){
        this.bussReceiptStat = digitalTransactionStatusEnum.getCode();
    }
    public DigitalRespDTO(DigitalTransactionStatusEnum digitalTransactionStatusEnum, DigitalErrorCodeEnum digitalCodeEnum){
        this.bussReceiptStat = digitalTransactionStatusEnum.getCode();
        this.bussReceiptCode = digitalCodeEnum.getCode();
        this.bussReceiptDesc = digitalCodeEnum.getDesc();
    }

}
