package com.tfjt.pay.external.unionpay.service;


import com.tfjt.pay.external.unionpay.dto.resp.BankCardDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IdCardDTO;

/**
 * @description: OcrService <br>
 * @date: 2023/5/20 10:06 <br>
 * @author: young <br>
 * @version: 1.0
 */
public interface OcrService {

    IdCardDTO ocrIdCard(String imageUrl, String side);

    BankCardDTO ocrBankCard(String imageUrl);

    String ocrInfo(String url ,String type,String side);
}
