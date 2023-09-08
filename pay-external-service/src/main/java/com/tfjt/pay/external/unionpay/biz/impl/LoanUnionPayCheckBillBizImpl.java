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
import com.tfjt.pay.external.unionpay.api.dto.req.UnionPayCheckBillReqDTO;
import com.tfjt.pay.external.unionpay.biz.LoanUnionPayCheckBillBiz;
import com.tfjt.pay.external.unionpay.config.TfAccountConfig;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillEntity;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.LoanUnionpayCheckBillService;
import com.tfjt.pay.external.unionpay.service.UnionPayService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.enums.ExceptionCodeEnum;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    @Resource
    private FileStorageService fileStorageService;

    @Override
    public LoanUnionpayCheckBillEntity downloadCheckBill(DateTime yesterday) {
        LoanUnionpayCheckBillEntity loanUnionpayCheckBillEntity = new LoanUnionpayCheckBillEntity();
        loanUnionpayCheckBillEntity.setDate(yesterday);

        loanUnionpayCheckBillEntity.setBalanceAcctId(tfAccountConfig.getBalanceAcctId());
        loanUnionpayCheckBillEntity.setCeateTime(new Date());
        try {
            String format = DateUtil.format(yesterday, DatePattern.NORM_DATE_PATTERN);
            Result<String> stringResult = unionPayService.downloadCheckBill(format);
            if (stringResult.getCode() != NumberConstant.ZERO) {
                log.error("调用银行对账单接口失败:{}", JSONObject.toJSONString(stringResult));
            } else {
                File file = new File("/tmp/checkBill");
                if (!file.exists()) {
                    file.mkdirs();
                }
                File cvsFile = new File(file, "checkbill" + format + ".csv");
                FileOutputStream fileOutputStream = new FileOutputStream(cvsFile);
                HttpUtil.download(stringResult.getData(), fileOutputStream, true);
                String absolutePath = cvsFile.getAbsolutePath();
                log.info("导入文件的CVS地址:{}", absolutePath);
//                loanUnionpayCheckBillService.loadFile(absolutePath);
                UploadPretreatment of = fileStorageService.of(cvsFile);
                FileInfo upload = of.upload();
                String url = upload.getUrl();
                loanUnionpayCheckBillEntity.setUrl(url);
                loanUnionpayCheckBillEntity.setStatus(NumberConstant.ONE);
                cvsFile.delete();
            }
        } catch (Exception e) {
            log.error("下载对账单失败");
            loanUnionpayCheckBillEntity.setReason(e.getMessage());
            loanUnionpayCheckBillEntity.setStatus(NumberConstant.ZERO);
        }
        this.loanUnionpayCheckBillService.save(loanUnionpayCheckBillEntity);
        return loanUnionpayCheckBillEntity;
    }

    @Override
    public Result<String> downloadCheckBill(UnionPayCheckBillReqDTO unionPayCheckBillReqDTO) {
        LoanUnionpayCheckBillEntity byDateAndAccountId = loanUnionpayCheckBillService.getByDateAndAccountId(unionPayCheckBillReqDTO.getDate(), tfAccountConfig.getBalanceAcctId());
        //
        if (Objects.isNull(byDateAndAccountId) || byDateAndAccountId.getStatus().equals(NumberConstant.ZERO)) {
            LoanUnionpayCheckBillEntity loanUnionpayCheckBillEntity = downloadCheckBill(DateUtil.parseDate(unionPayCheckBillReqDTO.getDate()));
            if(Objects.equals(loanUnionpayCheckBillEntity.getStatus(), NumberConstant.ONE)){
                return Result.ok(loanUnionpayCheckBillEntity.getUrl());
            }
            return Result.failed(loanUnionpayCheckBillEntity.getReason());
        }

        if (Objects.equals(NumberConstant.ONE, byDateAndAccountId.getStatus())) {
            return Result.ok(byDateAndAccountId.getUrl());
        }
        return Result.failed(byDateAndAccountId.getReason());
    }

}
