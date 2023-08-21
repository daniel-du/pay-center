package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tfjt.pay.external.unionpay.dao.LoanBalanceDivideDao;
import com.tfjt.pay.external.unionpay.dao.LoanBalanceDivideDetailsDao;
import com.tfjt.pay.external.unionpay.dto.EventDataDTO;
import com.tfjt.pay.external.unionpay.entity.LoadBalanceDivideEntity;
import com.tfjt.pay.external.unionpay.entity.LoanBalanceDivideDetailsEntity;
import com.tfjt.pay.external.unionpay.service.LoanBalanceDivideService;
import com.tfjt.pay.external.unionpay.utils.DateUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;


@Service("payBalanceDivideService")
public class LoanBalanceDivideServiceImpl extends ServiceImpl<LoanBalanceDivideDao, LoadBalanceDivideEntity> implements LoanBalanceDivideService {
    @Resource
    private LoanBalanceDivideDetailsDao loanBalanceDivideDetailsDao;

    @Override
    public boolean checkExistBusinessOrderNo(String businessOrderNo) {
        LambdaQueryWrapper<LoadBalanceDivideEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoadBalanceDivideEntity::getBusinessOrderNo,businessOrderNo)
                .last("limit 1");
        return this.count(queryWrapper)>1;
    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    @Override
    public LoadBalanceDivideEntity divideNotice(EventDataDTO eventDataDTO) {
        LambdaQueryWrapper<LoadBalanceDivideEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoadBalanceDivideEntity::getTradeOrderNo,eventDataDTO.getOutOrderNo());
        LoadBalanceDivideEntity one = this.getOne(wrapper);
        try {
            Date finishAt = DateUtil.parseDate(eventDataDTO.getFinishedAt(), DateUtil.YYYY_MM_DD_T_HH_MM_SS_SSSXXX);
            one.setFinishedAt(finishAt);
            one.setStatus(eventDataDTO.getStatus());
            this.updateById(one);
            LoanBalanceDivideDetailsEntity loanBalanceDivideDetailsEntity = new LoanBalanceDivideDetailsEntity();
            loanBalanceDivideDetailsEntity.setStatus(eventDataDTO.getStatus());
            loanBalanceDivideDetailsEntity.setFinishedAt(finishAt);
            this.loanBalanceDivideDetailsDao.update(loanBalanceDivideDetailsEntity,
                    new LambdaUpdateWrapper<LoanBalanceDivideDetailsEntity>().eq(LoanBalanceDivideDetailsEntity::getDivideId,one.getId()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return one;
    }
}