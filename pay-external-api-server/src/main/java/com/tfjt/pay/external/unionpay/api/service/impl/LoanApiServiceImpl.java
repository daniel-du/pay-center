package com.tfjt.pay.external.unionpay.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tfjt.pay.external.unionpay.dao.LoanUserDao;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.tfcommon.dto.response.Result;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lzh
 * @version 1.0
 * @title 进件是否完成
 * @description
 * @Date 2023/8/11 14:44
 */
public class LoanApiServiceImpl extends BaseServiceImpl<LoanUserDao, LoanUserEntity> implements LoanApiService {
    @Override
    public Result<LoanTransferToTfDTO> getBalanceAcctId(String type, String bid) {
        return null;
    }

    @Override
    public Result<Map<String,Object>> incomingIsFinish(String type, String bid) {
        Map<String,Object> result = new HashMap<>();
        LoanUserEntity loanUser = this.baseMapper.selectOne(new QueryWrapper<LoanUserEntity>()
                .eq("loan_user_type",type).eq("bus_id",bid));
        if(ObjectUtils.isNotEmpty(loanUser)){
            result.put("isIncoming",true);
            result.put("settledAmount",10);
            return Result.ok(result);
        }
        result.put("isIncoming",false);
        result.put("settledAmount",0);
        return Result.ok(result);
    }
}
