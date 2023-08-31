package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dao.LoanBalanceDao;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceEntity;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.LoanBalanceService;
import com.tfjt.tfcloud.business.api.TfLoanBalanceRpcService;
import com.tfjt.pay.external.unionpay.dto.LoanBalanceCreateDto;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service("tfLoanBalanceService")
public class LoanBalanceServiceImpl extends BaseServiceImpl<LoanBalanceDao, LoanBalanceEntity> implements LoanBalanceService {

    @DubboReference
    private TfLoanBalanceRpcService loanBalanceRpcService;

    @Override
    public LoanBalanceEntity getByShopId(Integer shopId) {
        return this.getOne(new LambdaQueryWrapper<LoanBalanceEntity>().eq(LoanBalanceEntity::getShopId, shopId));
    }

    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#dto.shopId"}, expire = 3000, acquireTimeout = 2000)
    @Override
    public void update(LoanBalanceCreateDto dto) {
        boolean b = false;
        LoanBalanceEntity tfLoanBalanceEntity = new LoanBalanceEntity();

        List<LoanBalanceEntity> list = this.list(new LambdaQueryWrapper<LoanBalanceEntity>().eq(LoanBalanceEntity::getShopId, dto.getShopId()));
        if(CollUtil.isEmpty(list)){

            BeanUtils.copyProperties(dto, tfLoanBalanceEntity);
            b = this.save(tfLoanBalanceEntity);
        }else{
            tfLoanBalanceEntity = list.get(0);
            BeanUtils.copyProperties(dto, tfLoanBalanceEntity);
            b = this.update(tfLoanBalanceEntity, new LambdaQueryWrapper<LoanBalanceEntity>().eq(LoanBalanceEntity::getShopId, dto.getShopId()));
        }

        com.tfjt.tfcloud.business.dto.TfLoanBalanceCreateDto tfLoanBalanceCreateDto = new com.tfjt.tfcloud.business.dto.TfLoanBalanceCreateDto();
        BeanUtil.copyProperties(tfLoanBalanceEntity, tfLoanBalanceCreateDto);
        Long update = loanBalanceRpcService.update(tfLoanBalanceCreateDto);
        if(!b || update == null){
            throw new TfException(PayExceptionCodeEnum.UPDATE_DATA_ERROR.getMsg());
        }
    }
}
