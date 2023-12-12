package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.TfIncomingBusinessInfoDao;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingBusinessRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingBusinessInfoEntity;
import com.tfjt.pay.external.unionpay.service.TfIncomingBusinessInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 银行入网-营业信息 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Service
public class TfIncomingBusinessInfoServiceImpl extends BaseServiceImpl<TfIncomingBusinessInfoDao, TfIncomingBusinessInfoEntity> implements TfIncomingBusinessInfoService {

    /**
     * 根据id查询商户营业信息
     * @param id
     * @return
     */
    @Override
    public IncomingBusinessRespDTO queryBusinessById(Long id) {
        return this.baseMapper.queryBusinessById(id);
    }
}
