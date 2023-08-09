package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.CustBusinessAttachInfoDao;
import com.tfjt.pay.external.unionpay.entity.CustBusinessAttachInfoEntity;
import com.tfjt.pay.external.unionpay.service.CustBusinessAttachInfoService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustBusinessAttachInfoServiceImpl extends BaseServiceImpl<CustBusinessAttachInfoDao, CustBusinessAttachInfoEntity> implements CustBusinessAttachInfoService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(CustBusinessAttachInfoEntity entity) {
        return super.save(entity);
    }
}