package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.TfIncomingSettleInfoDao;
import com.tfjt.pay.external.unionpay.dto.resp.IncomingSettleRespDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingSettleInfoEntity;
import com.tfjt.pay.external.unionpay.service.TfIncomingSettleInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 银行入网-结算信息 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Service
public class TfIncomingSettleInfoServiceImpl extends BaseServiceImpl<TfIncomingSettleInfoDao, TfIncomingSettleInfoEntity> implements TfIncomingSettleInfoService {

    /**
     * 根据进件id查询结算信息
     * @param incomingId
     * @return
     */
    @Override
    public TfIncomingSettleInfoEntity querySettleInfoByIncomingId(Long incomingId) {
        return null;
    }

    /**
     * 根据结算信息id查询结算信息
     * @param id
     * @return
     */
    @Override
    public IncomingSettleRespDTO querySettleById(Long id) {
        return this.baseMapper.querySettleById(id);
    }
}
