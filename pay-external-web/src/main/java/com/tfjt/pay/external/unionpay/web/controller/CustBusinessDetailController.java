package com.tfjt.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.tfjt.pay.external.unionpay.dto.CustBusinessCreateDto;
import com.tfjt.pay.external.unionpay.dto.CustBusinessDeleteDto;
import com.tfjt.pay.external.unionpay.entity.CustBusinessDetailEntity;
import com.tfjt.pay.external.unionpay.service.CustBusinessDetailService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.UpdateGroup;
import com.tfjt.tfcommon.dto.response.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.tfjt.pay.external.unionpay.utils.DateUtil.timeComparison;


@Slf4j
@RestController
@RequestMapping("custBusinessDetail")
public class CustBusinessDetailController {
    @Resource
    private CustBusinessDetailService custBusinessDetailService;

    /**
     * 详情
     */
    @GetMapping("info/{loanUserId}")
    @ApiOperation("营业信息详情")
    public Result<?> info(@PathVariable Long loanUserId) {
        try {
            CustBusinessDetailEntity custBusinessDetailEntity = custBusinessDetailService.getByLoanUserId(loanUserId);
            return Result.ok(custBusinessDetailEntity);
        } catch (TfException e) {
            log.error("查询营业信息详情异常：param={}", loanUserId, e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询营业信息详情异常：param={}", loanUserId, e);
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("save")
    @ApiOperation("新增营业信息")
    public Result<?> save(@RequestBody CustBusinessCreateDto dto) {
        try {
            boolean flag = timeComparison(null,null);
            if(!flag){
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            ValidatorUtils.validateEntity(dto, AddGroup.class, UpdateGroup.class);
            //校验营业期期限
            String expiryDate = dto.getExpiryDate();
            String effectiveDate = dto.getEffectiveDate();
            Integer isLongTerm = dto.getIsLongTerm();
            //校验身份证期限
            if(isLongTerm!=null && isLongTerm != 1) {
                DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
                if(StringUtils.isNullOrEmpty(effectiveDate)){
                    return Result.failed("营业执照生效日期不能为空！");
                }
                if(StringUtils.isNullOrEmpty(expiryDate)){
                    return Result.failed("营业执照失效日期不能为空！");
                }
                Date date =  dft.parse(expiryDate);
                long num = DateUtil.differDay(null, date);
                if (num <= 60) {
                    return Result.failed("营业执照有效期到期时间必须大于60天！");
                }
            }else{
                dto.setExpiryDate("长期");
            }
            //校验营业执照号码是否已存在
            long num = custBusinessDetailService.count(new QueryWrapper<CustBusinessDetailEntity>().eq("business_num",dto.getBusinessNum()));
            if(num > 0){
                log.error("新增营业信息异常：param={}", JSONObject.toJSONString(dto));
                return Result.failed(5001,"营业执照号码已存在！");
            }
            return custBusinessDetailService.save(dto);
        } catch (TfException e) {
            log.error("新增营业信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("新增营业信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("update")
    @ApiOperation("编辑营业信息")
    public Result<?> update(@RequestBody CustBusinessCreateDto dto) {
        try {
            boolean flag = timeComparison(null,null);
            if(!flag){
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            ValidatorUtils.validateEntity(dto, AddGroup.class, UpdateGroup.class);
            //校验营业期期限
            String expiryDate = dto.getExpiryDate();
            String effectiveDate = dto.getEffectiveDate();
            Integer isLongTerm = dto.getIsLongTerm();
            //校验身份证期限
            if(isLongTerm!=null && isLongTerm != 1) {
                DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
                if(StringUtils.isNullOrEmpty(effectiveDate)){
                    return Result.failed("营业执照生效日期不能为空！");
                }
                if(StringUtils.isNullOrEmpty(expiryDate)){
                    return Result.failed("营业执照失效日期不能为空！");
                }
                Date date =  dft.parse(expiryDate);
                long num = DateUtil.differDay(null, date);
                if (num <= 60) {
                    return Result.failed("营业执照有效期到期时间必须大于60天！");
                }
            }else{
                dto.setExpiryDate("长期");
            }

            return custBusinessDetailService.update(dto);
        } catch (TfException e) {
            log.error("编辑营业信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("编辑营业信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getMessage());
        }
    }

    @PostMapping("delete")
    @ApiOperation("删除营业信息")
    public Result<?> delete(@RequestBody CustBusinessDeleteDto dto) {
        try {
            ValidatorUtils.validateEntity(dto);
            return custBusinessDetailService.delete(dto);
        } catch (TfException e) {
            log.error("删除营业信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("删除营业信息异常：param={}", JSONObject.toJSONString(dto), e);
            return Result.failed(e.getMessage());
        }
    }
}
