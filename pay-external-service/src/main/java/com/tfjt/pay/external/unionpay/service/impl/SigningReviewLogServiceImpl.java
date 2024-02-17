package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.SigningReviewLogDao;
import com.tfjt.pay.external.unionpay.entity.SigningReviewLogEntity;
import com.tfjt.pay.external.unionpay.service.SigningReviewLogService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class SigningReviewLogServiceImpl extends BaseServiceImpl<SigningReviewLogDao, SigningReviewLogEntity> implements SigningReviewLogService {

    @Override
    @Async
    public void saveLog(SigningReviewLogEntity entity) {
        super.save(entity);
    }
}
