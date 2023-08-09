package com.tfjt.pay.external.unionpay.web.controller;

import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mysql.cj.util.StringUtils;
import com.tfjt.pay.external.unionpay.entity.CustIdcardInfoEntity;
import com.tfjt.pay.external.unionpay.service.CustIdcardInfoService;
import com.tfjt.pay.external.unionpay.service.LoanUserService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import com.tfjt.tfcloud.business.dto.TfLoanUserEntityDTO;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;



/**
 * 身份信息表
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-20 09:27:39
 */
@RestController
@RequestMapping("custidcardinfo")
public class CustIdcardInfoController {
    @Autowired
    private CustIdcardInfoService custIdcardInfoService;
    @Resource
    @Lazy
    private LoanUserService loanUserService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result<?> list(@RequestParam Map<String, Object> params) {

        return Result.ok();
    }


    /**
     * 信息
     */
    @GetMapping("/info/{loanUserId}")
    public Result<?> info(@PathVariable("loanUserId") Long loanUserId) {
        CustIdcardInfoEntity custIdcardInfo = custIdcardInfoService.getBaseMapper().selectOne(Wrappers.lambdaQuery(CustIdcardInfoEntity.class).eq(CustIdcardInfoEntity::getLoanUserId, loanUserId));
        return Result.ok(custIdcardInfo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#custIdcardInfo.loanUserId"}, expire = 3000, acquireTimeout = 4000)
    public Result<?> save(@RequestBody CustIdcardInfoEntity custIdcardInfo) {
        try {
            boolean flag = DateUtil.timeComparison(null,null);
            if(!flag){
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            ValidatorUtils.validateEntity(custIdcardInfo);
            String expiryDate = custIdcardInfo.getExpiryDate();
            String effectiveDate = custIdcardInfo.getEffectiveDate();
            Integer isLongTerm = custIdcardInfo.getIsLongTerm();
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
                custIdcardInfo.setExpiryDate("长期");
            }
        } catch (Exception ex) {
            return Result.failed(ex.getMessage());
        }

        long num = custIdcardInfoService.count(new QueryWrapper<CustIdcardInfoEntity>().eq("loan_user_id",custIdcardInfo.getLoanUserId()));
        if(num>0){
            return Result.ok("当前贷款人信息已存在，请勿重复添加！");
        }
        boolean bool = custIdcardInfoService.save(custIdcardInfo);
        if (bool) {
            //同步贷款用户信息的商户简称
            TfLoanUserEntityDTO tfLoanUserEntity = new TfLoanUserEntityDTO();
            tfLoanUserEntity.setId(custIdcardInfo.getLoanUserId());
            tfLoanUserEntity.setName(custIdcardInfo.getMerchantShortName());
            return loanUserService.updateLoanUserDto(tfLoanUserEntity);
        } else {
            return Result.failed("保存失败");
        }
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#custIdcardInfo.id"}, expire = 3000, acquireTimeout = 4000)
    public Result<?> update(@RequestBody CustIdcardInfoEntity custIdcardInfo) {
        try {
            boolean flag = DateUtil.timeComparison(null,null);
            if(!flag){
                return Result.failed("0点到凌晨04点，不受理申请！");
            }
            ValidatorUtils.validateEntity(custIdcardInfo);
            String expiryDate = custIdcardInfo.getExpiryDate();
            String effectiveDate = custIdcardInfo.getEffectiveDate();
            Integer isLongTerm = custIdcardInfo.getIsLongTerm();
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
                custIdcardInfo.setExpiryDate("长期");
            }
        } catch (Exception ex) {
            return Result.failed(ex.getMessage());
        }
        boolean bool = custIdcardInfoService.updateById(custIdcardInfo);
        if (bool) {
            //同步贷款用户信息的商户简称
            TfLoanUserEntityDTO tfLoanUserEntity = new TfLoanUserEntityDTO();
            tfLoanUserEntity.setId(custIdcardInfo.getLoanUserId());
            tfLoanUserEntity.setName(custIdcardInfo.getMerchantShortName());
            return loanUserService.updateLoanUserDto(tfLoanUserEntity);
        } else {
            return Result.failed("更新失败");
        }
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    public Result<?> delete(@RequestBody Long[] ids) {
        custIdcardInfoService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
