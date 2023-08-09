package com.tfjt.pay.external.unionpay.service.impl;

import com.alibaba.fastjson.JSON;
import com.tfjt.pay.external.unionpay.dao.UnionPayLoanReqLogDao;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseReturn;
import com.tfjt.pay.external.unionpay.entity.UnionPayLoanReqLogEntity;
import com.tfjt.pay.external.unionpay.service.UnionPayLoanReqLogService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class UnionPayLoanReqLogServiceImpl extends BaseServiceImpl<UnionPayLoanReqLogDao, UnionPayLoanReqLogEntity> implements UnionPayLoanReqLogService {


    @Override
    @Async("asyncServiceExecutor")
    public void asyncSaveLog(UnionPayLoansBaseReturn unionPayLoansBaseReturn, Object reqParams, Date req, Long loanUserId) {
        UnionPayLoanReqLogEntity unionPayLoanReqLogEntity = new UnionPayLoanReqLogEntity();
        BeanUtils.copyProperties(unionPayLoansBaseReturn, unionPayLoanReqLogEntity);
        unionPayLoanReqLogEntity.setReqParams(JSON.toJSONString(reqParams));
        unionPayLoanReqLogEntity.setRequestTime(req);
        unionPayLoanReqLogEntity.setResponseTime(new Date());
        unionPayLoanReqLogEntity.setLwzData(unionPayLoansBaseReturn.getLwzRespData());
        unionPayLoanReqLogEntity.setLoanUserId(loanUserId);
        unionPayLoanReqLogEntity.setResult(JSON.toJSONString(unionPayLoansBaseReturn));
        this.save(unionPayLoanReqLogEntity);
    }
}
