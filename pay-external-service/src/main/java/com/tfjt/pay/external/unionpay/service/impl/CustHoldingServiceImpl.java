package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.pay.external.unionpay.dao.CustHoldingDao;
import com.tfjt.pay.external.unionpay.dto.CustHoldingCreateDto;
import com.tfjt.pay.external.unionpay.dto.CustHoldingDeleteDto;
import com.tfjt.pay.external.unionpay.entity.CustHoldingEntity;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.pay.external.unionpay.service.CustHoldingService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service("tfCustHoldingService")
public class CustHoldingServiceImpl extends BaseServiceImpl<CustHoldingDao, CustHoldingEntity> implements CustHoldingService {

    @Override
    public CustHoldingEntity getByLoanUserId(Long loanUserId) {
        return this.getOne(new LambdaQueryWrapper<CustHoldingEntity>().eq(CustHoldingEntity::getLoanUserId, loanUserId));
    }

    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#dto.loanUserId"}, expire = 3000, acquireTimeout = 2000)
    @Override
    public Long save(CustHoldingCreateDto dto) {
        CustHoldingEntity entity = getByLoanUserId(dto.getLoanUserId());
        if(BeanUtil.isNotEmpty(entity)){
            log.error("根据loanUserId:{}新增的控股信息已存在", dto.getLoanUserId());
            throw new TfException(PayExceptionCodeEnum.REPEAT_OPERATION);
        }
        CustHoldingEntity custHoldingEntity = new CustHoldingEntity();
        BeanUtils.copyProperties(dto, custHoldingEntity);
        custHoldingEntity.setCreator(dto.getCreator());
        custHoldingEntity.setUpdater(dto.getUpdater());
        this.save(custHoldingEntity);
        return custHoldingEntity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#dto.loanUserId"}, expire = 3000, acquireTimeout = 2000)
    @Override
    public Long update(CustHoldingCreateDto dto) {
        List<CustHoldingEntity> list = this.list(new LambdaQueryWrapper<CustHoldingEntity>().eq(CustHoldingEntity::getLoanUserId, dto.getLoanUserId()));
        if(CollUtil.isEmpty(list)){
            log.error("根据loanUserId:{}查询的控股信息不存在 param:{}",dto.getLoanUserId(), JSONObject.toJSONString(dto));
            throw new TfException(PayExceptionCodeEnum.NO_DATA);
        }

        CustHoldingEntity custHoldingEntity = list.get(0);
        BeanUtils.copyProperties(dto, custHoldingEntity);
        custHoldingEntity.setUpdater(dto.getUpdater());
        this.updateById(custHoldingEntity);
        return custHoldingEntity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#dto.loanUserId"}, expire = 3000, acquireTimeout = 2000)
    @Override
    public Long delete(CustHoldingDeleteDto dto) {
        CustHoldingEntity entity = getByLoanUserId(dto.getLoanUserId());
        if(BeanUtil.isNotEmpty(entity)){
            log.error("根据loanUserId:{}查询的控股信息已存在", dto.getLoanUserId());
            throw new TfException(PayExceptionCodeEnum.REPEAT_OPERATION);
        }

        LambdaQueryWrapper<CustHoldingEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustHoldingEntity:: getLoanUserId, dto.getLoanUserId());
        this.remove(wrapper);
        return dto.getLoanUserId();
    }
}
