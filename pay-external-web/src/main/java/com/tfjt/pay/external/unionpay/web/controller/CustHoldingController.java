package com.tfjt.pay.external.unionpay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.tfjt.pay.external.unionpay.constants.RegularConstants;
import com.tfjt.pay.external.unionpay.dto.CustHoldingCreateDto;
import com.tfjt.pay.external.unionpay.dto.CustHoldingDeleteDto;
import com.tfjt.pay.external.unionpay.entity.CustHoldingEntity;
import com.tfjt.pay.external.unionpay.service.CustHoldingService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.tfjt.pay.external.unionpay.utils.DateUtil.timeComparison;

@Slf4j
@RestController
@RequestMapping("custHolding")
public class CustHoldingController {
    @Resource
    private CustHoldingService tfCustHoldingService;

    @Value("${unionPay.isTest:false}")
    boolean isTest;

    /**
     * 信息
     */
    @GetMapping("info/{loanUserId}")
    @ApiOperation("控股信息详情")
    public Result<?> info(@PathVariable Long loanUserId) {
        try {
            CustHoldingEntity custHoldingEntity = tfCustHoldingService.getByLoanUserId(loanUserId);
            return Result.ok(custHoldingEntity);
        } catch (TfException e) {
            log.error("查询营业信息详情异常：param={}", loanUserId, e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询营业信息详情异常：param={}", loanUserId, e);
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("save")
    @ApiOperation("新增控股信息")
    public Result<?> save(@RequestBody CustHoldingCreateDto dto) {
        try {
            boolean flag = timeComparison(null,null, isTest);
            if(!flag){
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            ValidatorUtils.validateEntity(dto);
            Integer holdingType = dto.getHoldingType();

            //判断有效期
            if(holdingType != null) {
                String holdingName = dto.getHoldingName();
                String holdingNum = dto.getHoldingNum();
                if(!holdingName.matches(RegularConstants.NO_SPECIAL_CHAR_PATTERN)){
                    return Result.failed("企业名称不支持符号！");
                }
                String expiryDate = dto.getExpiryDate();
                String effectiveDate = dto.getEffectiveDate();
                Integer isLongTerm = dto.getIsLongTerm();
                if(isLongTerm ==null && expiryDate == null){
                    return Result.failed("长期选项和失效日期不能同时为空！");
                }
                if(holdingType == 1){

                    if(holdingName.length()>50){
                        return Result.failed("企业名称不能超过50个字符！");
                    }
                    if(!holdingNum.matches(RegularConstants.LICENSE_PATTERN)){
                        return Result.failed("营业执照号码格式不正确！");
                    }

                    //校验企业营业期限
                    if(isLongTerm!=null && isLongTerm != 1) {
                        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
                        if(StringUtils.isNullOrEmpty(effectiveDate)){
                            return Result.failed("企业营业期限生效日期不能为空！");
                        }
                        if(StringUtils.isNullOrEmpty(expiryDate)){
                            return Result.failed("企业营业期限失效日期不能为空！");
                        }
                        Date date =  dft.parse(expiryDate);
                        long num = DateUtil.differDay(null, date);
                        if (num <= 60) {
                            return Result.failed("营业执照有效期到期时间必须大于60天！");
                        }
                    }else{
                        dto.setExpiryDate("长期");
                    }
                }else{
                    if(holdingName.length()>20){
                        return Result.failed("控股股东不能超过50个字符！");
                    }
                    if(!holdingNum.matches(RegularConstants.IDCARD_CHECK)){
                        return Result.failed("身份证号码格式不正确！");
                    }

                    //校验身份证期限
                    if(isLongTerm!=null && isLongTerm != 1) {
                        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
                        if(StringUtils.isNullOrEmpty(effectiveDate)){
                            return Result.failed("身份证生效日期不能为空！");
                        }
                        if(StringUtils.isNullOrEmpty(expiryDate)){
                            return Result.failed("身份证失效日期不能为空！");
                        }
                        Date date =  dft.parse(expiryDate);
                        long num = DateUtil.differDay(null, date);
                        if (num <= 60) {
                            return Result.failed("身份证有效期到期时间必须大于60天！");
                        }
                    }else{
                        dto.setExpiryDate("长期");
                    }
                }

            }
            return Result.ok(tfCustHoldingService.save(dto));
        } catch (TfException e) {
            log.error("新增控股信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("新增控股信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("update")
    @ApiOperation("编辑控股信息")
    public Result<?> update(@RequestBody CustHoldingCreateDto dto) {
        try {
            boolean flag = timeComparison(null,null, isTest);
            if(!flag){
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            ValidatorUtils.validateEntity(dto);
            Integer holdingType = dto.getHoldingType();
            //判断有效期
            if(holdingType != null) {
                String holdingName = dto.getHoldingName();
                String holdingNum = dto.getHoldingNum();
                if(!holdingName.matches(RegularConstants.NO_SPECIAL_CHAR_PATTERN)){
                    return Result.failed("企业名称不支持符号！");
                }
                String expiryDate = dto.getExpiryDate();
                String effectiveDate = dto.getEffectiveDate();
                Integer isLongTerm = dto.getIsLongTerm();
                if(isLongTerm ==null && expiryDate == null){
                    return Result.failed("长期选项和失效日期不能同时为空！");
                }
                if(holdingType == 1){

                    if(holdingName.length()>50){
                        return Result.failed("企业名称不能超过50个字符！");
                    }
                    if(!holdingNum.matches(RegularConstants.LICENSE_PATTERN)){
                        return Result.failed("营业执照号码格式不正确！");
                    }

                    //校验企业营业期限
                    if(isLongTerm!=null && isLongTerm != 1) {
                        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
                        if(StringUtils.isNullOrEmpty(effectiveDate)){
                            return Result.failed("企业营业执照生效日期不能为空！");
                        }
                        if(StringUtils.isNullOrEmpty(expiryDate)){
                            return Result.failed("企业营业执照失效日期不能为空！");
                        }
                        Date date =  dft.parse(expiryDate);
                        long num = DateUtil.differDay(null, date);
                        if (num <= 60) {
                            return Result.failed("营业执照有效期到期时间必须大于60天！");
                        }
                    }else{
                        dto.setExpiryDate("长期");
                    }
                }else{
                    if(holdingName.length()>20){
                        return Result.failed("控股股东不能超过50个字符！");
                    }
                    if(!holdingNum.matches(RegularConstants.IDCARD_CHECK)){
                        return Result.failed("身份证号码格式不正确！");
                    }

                    //校验身份证期限
                    if(isLongTerm!=null && isLongTerm != 1) {
                        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
                        if(StringUtils.isNullOrEmpty(effectiveDate)){
                            return Result.failed("身份证生效日期不能为空！");
                        }
                        if(StringUtils.isNullOrEmpty(expiryDate)){
                            return Result.failed("身份证失效日期不能为空！");
                        }
                        Date date =  dft.parse(expiryDate);
                        long num = DateUtil.differDay(null, date);
                        if (num <= 60) {
                            return Result.failed("身份证有效期到期时间必须大于60天！");
                        }
                    }else{
                        dto.setExpiryDate("长期");
                    }
                }
            }
            return Result.ok(tfCustHoldingService.update(dto));
        } catch (TfException e) {
            log.error("编辑控股信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("编辑控股信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("delete")
    @ApiOperation("删除控股信息")
    public Result<?> delete(@RequestBody CustHoldingDeleteDto dto) {
        try {
            ValidatorUtils.validateEntity(dto);
            return Result.ok(tfCustHoldingService.delete(dto));
        } catch (TfException e) {
            log.error("删除控股信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除控股信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getMessage());
        }
    }
}
