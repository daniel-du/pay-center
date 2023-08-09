package com.tfjt.pay.external.unionpay.service.impl;

import com.alibaba.fastjson.JSON;
import com.tfjt.pay.external.unionpay.dto.req.ReqOcrParamsVO;
import com.tfjt.pay.external.unionpay.dto.resp.BankCardDTO;
import com.tfjt.pay.external.unionpay.dto.resp.IdCardDTO;
import com.tfjt.pay.external.unionpay.enums.OcrTypeEnum;
import com.tfjt.pay.external.unionpay.service.OcrService;
import com.tfjt.pay.external.unionpay.utils.OcrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: OcrServiceImpl <br>
 * @date: 2023/5/20 11:01 <br>
 * @author: young <br>
 * @version: 1.0
 */
@Service
@Slf4j
public class OcrServiceImpl implements OcrService {

    @Value("${ali.ocr.ocrBank.url}")
    public String ocrBankUrl;

    @Value("${ali.ocr.ocrBank.uri}")
    public String ocrBankUri;

    @Value("${ali.ocr.ocrIdcard.url}")
    public String ocrIdcardUrl;

    @Value("${ali.ocr.ocrIdcard.uri}")
    public String ocrIdcardUri;

    @Value("${ali.ocr.code}")
    public String code;

    @Override
    public IdCardDTO ocrIdCard(String imageUrl, String side) {
        ReqOcrParamsVO reqOcrParamsVO = new ReqOcrParamsVO();
        reqOcrParamsVO.setImage(imageUrl);
        Map<String, Object> configure = new HashMap<>();
        configure.put("side", side);
        configure.put("quality_info", false);
        reqOcrParamsVO.setConfigure(configure);
        String str = OcrUtil.ocr(ocrIdcardUrl, ocrIdcardUri, code, JSON.toJSONString(reqOcrParamsVO));
        log.info("OCR身份证=>{}", str);
        return JSON.parseObject(str, IdCardDTO.class);
    }

    @Override
    public BankCardDTO ocrBankCard(String imageUrl) {
        ReqOcrParamsVO reqOcrParamsVO = new ReqOcrParamsVO();
        reqOcrParamsVO.setImage(imageUrl);
        Map<String, Object> configure = new HashMap<>();
        configure.put("card_type", true);
        reqOcrParamsVO.setConfigure(configure);
        String str = OcrUtil.ocr(ocrBankUrl, ocrBankUri, code, JSON.toJSONString(reqOcrParamsVO));
        log.info("OCR银行卡=>{}", str);
        return JSON.parseObject(str, BankCardDTO.class);
    }

    @Override
    public String ocrInfo(String url, String type, String side) {
        try {
            if (type.equals(OcrTypeEnum.IDCARD.getCode())) {
                if (side == null) {
                    throw new RuntimeException("正反面不能为空");
                }
                IdCardDTO idCardDTO = ocrIdCard(url, side);
                idCardDTO.setUrl(url);
                return JSON.toJSONString(idCardDTO);
            }
            if (type.equals(OcrTypeEnum.BANK.getCode())) {
                BankCardDTO bankCardDTO = ocrBankCard(url);
                bankCardDTO.setUrl(url);
                return JSON.toJSONString(bankCardDTO);
            }
        } catch (Exception ex) {
            log.error("识别失败", ex);
            throw new RuntimeException("识别失败");
        }
        return null;
    }
}
