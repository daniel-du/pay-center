package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.pay.external.unionpay.dao.CustBusinessDetailDao;
import com.tfjt.pay.external.unionpay.dto.CustBusinessCreateDto;
import com.tfjt.pay.external.unionpay.dto.CustBusinessDeleteDto;
import com.tfjt.pay.external.unionpay.entity.CustBusinessDetailEntity;
import com.tfjt.pay.external.unionpay.service.CustBusinessDetailService;
import com.tfjt.tfcommon.dto.response.Result;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Slf4j
@Service
public class CustBusinessDetailServiceImpl extends BaseServiceImpl<CustBusinessDetailDao, CustBusinessDetailEntity> implements CustBusinessDetailService {
    @Override
    public CustBusinessDetailEntity getByLoanUserId(Long loanUserId) {
        return this.getOne(new LambdaQueryWrapper<CustBusinessDetailEntity>().eq(CustBusinessDetailEntity::getLoanUserId, loanUserId));
    }

    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#dto.loanUserId"}, expire = 3000, acquireTimeout = 2000)
    @Override
    public Result<?> save(CustBusinessCreateDto dto) {
        CustBusinessDetailEntity entity = getByLoanUserId(dto.getLoanUserId());
        if(BeanUtil.isNotEmpty(entity)){
            log.error("根据loanUserId:{}新增的运营信息已存在", dto.getLoanUserId());
            return Result.failed(5001,"运营信息已存在");
        }
        CustBusinessDetailEntity custBusinessDetailEntity = new CustBusinessDetailEntity();
        BeanUtils.copyProperties(dto, custBusinessDetailEntity);
        custBusinessDetailEntity.setCreator(dto.getCreator());
        custBusinessDetailEntity.setUpdater(dto.getUpdater());
        this.save(custBusinessDetailEntity);
        return Result.ok(custBusinessDetailEntity.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#dto.loanUserId"}, expire = 3000, acquireTimeout = 2000)
    @Override
    public Result<?> update(CustBusinessCreateDto dto) {
        CustBusinessDetailEntity oldcustBusinessDetailEntity = this.getOne(new LambdaQueryWrapper<CustBusinessDetailEntity>().eq(CustBusinessDetailEntity::getLoanUserId, dto.getLoanUserId()));
        if(oldcustBusinessDetailEntity==null){
            log.error("根据loanUserId:{}查询的营业信息不存在 param:{}",dto.getLoanUserId(), JSONObject.toJSONString(dto));
            return Result.failed(5001,"营业信息不存在");
        }

        //校验营业执照号码是否已存在
        long num = this.count(new QueryWrapper<CustBusinessDetailEntity>()
                .eq("business_num",dto.getBusinessNum())
                .ne("id",oldcustBusinessDetailEntity.getId()));
        if(num > 0){
            log.error("businessNum:{}营业执照号码已存在！ param:{}",dto.getLoanUserId(), JSONObject.toJSONString(dto));
            return Result.failed(5001,"营业执照号码已存在！");
        }
        BeanUtils.copyProperties(dto, oldcustBusinessDetailEntity);

        if(!Objects.equals(oldcustBusinessDetailEntity.getBusinessImgMediaId(), dto.getBusinessImgMediaId())){
            oldcustBusinessDetailEntity.setBusinessImgMediaId(dto.getBusinessImgMediaId());
        }
        oldcustBusinessDetailEntity.setUpdater(dto.getUpdater());
        this.updateById(oldcustBusinessDetailEntity);
        return Result.ok(oldcustBusinessDetailEntity.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#dto.loanUserId"}, expire = 3000, acquireTimeout = 2000)
    @Override
    public Result<?> delete(CustBusinessDeleteDto dto) {

        LambdaQueryWrapper<CustBusinessDetailEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustBusinessDetailEntity :: getLoanUserId, dto.getLoanUserId());
        this.remove(wrapper);
        return Result.ok(dto.getLoanUserId());
    }
}