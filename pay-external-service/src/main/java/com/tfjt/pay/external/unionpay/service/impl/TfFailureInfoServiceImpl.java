package com.tfjt.pay.external.unionpay.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.pay.external.unionpay.dao.TfFailureInfoDao;
import com.tfjt.pay.external.unionpay.entity.TfFailureInfoEntity;
import com.tfjt.pay.external.unionpay.service.TfFailureInfoService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 *
 */
@Service
public class TfFailureInfoServiceImpl extends BaseServiceImpl<TfFailureInfoDao, TfFailureInfoEntity> implements TfFailureInfoService {

    @Resource
    private TfFailureInfoDao failureInfoDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("asyncServiceExecutor")
    public void saveLog(String keyName, String name, Object[] arguments) {
        TfFailureInfoEntity info = new TfFailureInfoEntity();
        if (StringUtils.isNotEmpty(keyName)){
            info.setKeyName(keyName);
        }
        if (StringUtils.isNotEmpty(name)){
            info.setMethod(name);
        }
        if (ObjectUtils.isNotEmpty(arguments)){
            info.setArguments(JSON.toJSONString(arguments));
        }
        info.setCreateTime(new Date());

        this.failureInfoDao.insert(info);
    }
}




