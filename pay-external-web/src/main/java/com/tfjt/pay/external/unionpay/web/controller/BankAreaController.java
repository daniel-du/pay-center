package com.tfjt.pay.external.unionpay.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.tfjt.pay.external.unionpay.entity.BankInterbankNumberEntity;
import com.tfjt.pay.external.unionpay.entity.BankAreaEntity;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.BankAreaService;
import com.tfjt.pay.external.unionpay.service.BankInterbankNumberService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("bankArea")
@Slf4j
public class BankAreaController {

    @Resource
    private BankAreaService bankAreaService;

    @Resource
    private BankInterbankNumberService bankInterbankNumberService;

    /**
     * 查询 省市 及开户行编码
     * @return
     */
    @GetMapping("/getBankArea")
    public Result<?> getBankArea(@RequestParam(value = "province",required = false) String province,@RequestParam(value = "city",required = false) String city,@RequestParam(value = "bankName",required = false) String bankName){
        try {
            if (StringUtils.isNotBlank(bankName)){
                List<BankInterbankNumberEntity> list = this.bankInterbankNumberService.getBankNameListByBank(bankName);
                return Result.ok(list);
            }
            //如果市不为空 返回 省市下所有支行编码
            if (ObjectUtil.isNotEmpty(city)){
                List<BankInterbankNumberEntity> list = this.bankInterbankNumberService.getBankNameListByCity(city);
                return Result.ok(list);
            }
            //如果省不为空  返回省下辖市信息
            if (ObjectUtil.isNotEmpty(province)){
                List<BankAreaEntity> list = this.bankAreaService.getBankAreaByPro(province);
                return Result.ok(list);
            }
            //都为空 返回 所有省
            List<BankAreaEntity> list = this.bankAreaService.getAllBankArea();
            return Result.ok(list);
        }catch (Exception e){
            throw new TfException(PayExceptionCodeEnum.QUERY_BANK_CODE_FAILED);
        }
    }
}












