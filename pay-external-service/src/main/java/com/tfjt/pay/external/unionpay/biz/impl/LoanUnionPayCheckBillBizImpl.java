package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageProperties;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.biz.LoanUnionPayCheckBillBiz;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillEntity;
import com.tfjt.pay.external.unionpay.service.LoanUnionpayCheckBillService;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Objects;

/**
 * @author songx
 * @date 2023-08-18 21:33
 * @email 598482054@qq.com
 */
@Slf4j
@Component
public class LoanUnionPayCheckBillBizImpl implements LoanUnionPayCheckBillBiz {

    @Resource
    private TfAccountConfig tfAccountConfig;
    @Resource
    private UnionPayService unionPayService;
    @Resource
    private LoanUnionpayCheckBillService loanUnionpayCheckBillService;

    @Override
    public void downloadCheckBill(DateTime yesterday, int number) {
        if (number>=NumberConstant.THREE){
            log.error("下载对账单失败:{},次数:{}",yesterday,number);
            throw new TfException(ExceptionCodeEnum.FAIL);
        }
        String format = DateUtil.format(yesterday, DatePattern.NORM_DATE_PATTERN);
        Result<String> stringResult = unionPayService.downloadCheckBill(format);
        if (stringResult.getCode()!= NumberConstant.ZERO){
            log.error("调用银行对账单接口失败:{}", JSONObject.toJSONString(stringResult));
            this.downloadCheckBill(yesterday,++number);
        }else {
            FileStorageService fileStorageService = new FileStorageService();
            UploadPretreatment of = fileStorageService.of(stringResult.getData());
            FileInfo upload = of.upload();
            if(Objects.isNull(upload)){
                this.downloadCheckBill(yesterday,++number);
            }
            String url = upload.getUrl();
            LoanUnionpayCheckBillEntity loanUnionpayCheckBillEntity = new LoanUnionpayCheckBillEntity();
            loanUnionpayCheckBillEntity.setDate(yesterday);
            loanUnionpayCheckBillEntity.setUrl(url);
            loanUnionpayCheckBillEntity.setBalanceAcctId(tfAccountConfig.getBalanceAcctId());
            loanUnionpayCheckBillEntity.setCeateTime(new Date());
            this.loanUnionpayCheckBillService.save(loanUnionpayCheckBillEntity);
        }


    }

}
