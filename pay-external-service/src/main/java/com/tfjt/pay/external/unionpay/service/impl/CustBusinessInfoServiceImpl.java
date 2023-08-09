package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.CustBusinessInfoDao;
import com.tfjt.pay.external.unionpay.dto.CustBusinessCreateDto;
import com.tfjt.pay.external.unionpay.entity.CustBusinessInfoEntity;
import com.tfjt.pay.external.unionpay.service.CustBusinessInfoService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
public class CustBusinessInfoServiceImpl extends BaseServiceImpl<CustBusinessInfoDao, CustBusinessInfoEntity> implements CustBusinessInfoService {

    @Resource
    private  CustBusinessInfoDao custBusinessInfoDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(CustBusinessInfoEntity entity) {
        return super.save(entity);
    }

    @Override
    public List<CustBusinessCreateDto> getBusinessAttach(String loanUserId) {
        return custBusinessInfoDao.getBusinessAttach(loanUserId);
    }
}