package com.tfjt.pay.external.unionpay.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dao.LoanCallbackDao;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseCallBackDTO;
import com.tfjt.pay.external.unionpay.entity.LoanCallbackEntity;
import com.tfjt.pay.external.unionpay.service.LoanCallbackService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service("tfLoanCallbackService")
public class LoanCallbackServiceImpl extends BaseServiceImpl<LoanCallbackDao, LoanCallbackEntity> implements LoanCallbackService {

    @Override
    public LoanCallbackEntity saveLog(Long loanUserId, UnionPayLoansBaseCallBackDTO unionPayLoansBaseCallBackDTO, Integer type, String destAcctNo) {
        LoanCallbackEntity tfLoanCallbackEntity = new LoanCallbackEntity();
        tfLoanCallbackEntity.setLoanUserId(loanUserId);
        tfLoanCallbackEntity.setEventId(unionPayLoansBaseCallBackDTO.getEventId());
        tfLoanCallbackEntity.setEventType(unionPayLoansBaseCallBackDTO.getEventType());
        tfLoanCallbackEntity.setEventData( JSONObject.toJSON(unionPayLoansBaseCallBackDTO.getEventData()).toString());
        tfLoanCallbackEntity.setCreatedAt(unionPayLoansBaseCallBackDTO.getCreatedAt());
        tfLoanCallbackEntity.setCreateDate(new Date());
        if(StringUtils.isNotBlank(destAcctNo)){
            tfLoanCallbackEntity.setDestAcctNo(destAcctNo);
        }
        if (ObjectUtil.isNotNull(type)) {
            tfLoanCallbackEntity.setType(type);
        }
        this.save(tfLoanCallbackEntity);
        return tfLoanCallbackEntity;
    }

    @Override
    public void updateNoticeStatus(Long id, boolean result, String tradeOrderNo) {
        LoanCallbackEntity loanCallbackEntity = new LoanCallbackEntity();
        loanCallbackEntity.setId(id);
        loanCallbackEntity.setNoticeStatus(result? NumberConstant.TWO:NumberConstant.ONE);
        loanCallbackEntity.setTreadOrderNo(tradeOrderNo);
        if(!this.updateById(loanCallbackEntity)){
            log.error("更新通知状态信息失败:{}", JSONObject.toJSONString(loanCallbackEntity));
        }
    }
}
