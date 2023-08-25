package com.tfjt.pay.trade.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.pay.trade.constants.Constant;
import com.tfjt.pay.trade.dao.PayApplicationDao;
import com.tfjt.pay.trade.dto.req.PayApplicationEntityDTO;
import com.tfjt.pay.trade.entity.PayApplicationEntity;
import com.tfjt.pay.trade.enums.ExceptionCodeEnum;
import com.tfjt.pay.trade.service.PayApplicationService;
import com.tfjt.pay.trade.utils.RSACoder;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Service("payApplicationService")
@Slf4j
public class PayApplicationServiceImpl extends ServiceImpl<PayApplicationDao, PayApplicationEntity> implements PayApplicationService {

    private static final String AUTH_KEY = "auth:app:";

    @Autowired
    private RedisCache redisCache;


    /**
     * 创建应用
     *
     * @param payApplicationEntityDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result savePayApplication(PayApplicationEntityDTO payApplicationEntityDTO) throws Exception {

        PayApplicationEntity payApplicationEntity = this.getOne(new LambdaQueryWrapper<PayApplicationEntity>().eq(PayApplicationEntity::getName, payApplicationEntityDTO.getName()));
        if (payApplicationEntity != null) {
            throw new TfException(ExceptionCodeEnum.DATA_EXISTS.getMsg());
        }

        payApplicationEntityDTO.setAppId(UUID.randomUUID().toString().split("-")[0]);
        payApplicationEntityDTO.setAppSecret(UUID.randomUUID().toString());
        //生成密钥对
        Map<String, Object> keyMap = RSACoder.initKey();
        //公钥
        byte[] publicKey = RSACoder.getPublicKey(keyMap);
        //私钥
        byte[] privateKey = RSACoder.getPrivateKey(keyMap);

        payApplicationEntityDTO.setAppPub(Base64.encodeBase64String(publicKey));
        payApplicationEntityDTO.setAppPri(Base64.encodeBase64String(privateKey));
        payApplicationEntityDTO.setCreateDate(new Date());


        PayApplicationEntity payApplication = new PayApplicationEntity();
        BeanUtils.copyProperties(payApplicationEntityDTO, payApplication);
        this.save(payApplication);
        //保存后redis添加secret
        redisCache.setCacheString(AUTH_KEY + payApplicationEntityDTO.getAppId(), payApplication.getAppSecret());
        return Result.ok(payApplication);
    }

    /**
     * 续签token
     *
     * @param appId
     * @param appSecret
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map getToken(String appId, String appSecret) {
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(appSecret)) {
            throw new TfException(ExceptionCodeEnum.PARAM_EXCEPTION.getMsg());
        }

        PayApplicationEntity payApplicationEntity = this.getOne(new LambdaQueryWrapper<PayApplicationEntity>().eq(PayApplicationEntity::getAppId, appId));

        if (payApplicationEntity == null) {
            throw new TfException(ExceptionCodeEnum.APP_ISNULL.getMsg());
        }

        if (!Objects.equals(appSecret, payApplicationEntity.getAppSecret())) {
            throw new TfException(ExceptionCodeEnum.APP_SECRET_ERROR.getMsg());
        }
        String key = Constant.APP_TOKEN_KEY + appId;

        String oldToken = redisCache.getCacheObject(key);
        Map map = new HashMap<>();

        if (StringUtils.isNotBlank(oldToken)) {
            map.put("token", oldToken);
            redisCache.setCacheObject(key, oldToken, 7200, TimeUnit.SECONDS);
        } else {
            String sessionKey = UUID.randomUUID().toString();
            map.put("token", sessionKey);
            redisCache.setCacheObject(key, sessionKey, 7200, TimeUnit.SECONDS);
        }
        map.put("expires_in", 7200);
        return map;
    }

    @Override
    public int loadAppSecret() {
        List<PayApplicationEntity> payApplicationEntities = this.getBaseMapper().selectList(Wrappers.lambdaQuery(PayApplicationEntity.class));
        if (CollUtil.isNotEmpty(payApplicationEntities)) {
            for (int i = 0; i < payApplicationEntities.size(); i++) {
                redisCache.setCacheString(AUTH_KEY + payApplicationEntities.get(i).getAppId(), payApplicationEntities.get(i).getAppSecret());
            }
            return payApplicationEntities.size();
        } else {
            return 0;
        }
    }
}
