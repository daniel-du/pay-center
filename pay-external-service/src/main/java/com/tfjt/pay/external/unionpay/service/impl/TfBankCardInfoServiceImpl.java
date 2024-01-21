package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.TfBankCardInfoDao;
import com.tfjt.pay.external.unionpay.dto.req.IncomingSettleReqDTO;
import com.tfjt.pay.external.unionpay.entity.TfBankCardInfoEntity;
import com.tfjt.pay.external.unionpay.service.TfBankCardInfoService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 银行卡信息 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
@Service
public class TfBankCardInfoServiceImpl extends BaseServiceImpl<TfBankCardInfoDao, TfBankCardInfoEntity> implements TfBankCardInfoService {

    /**
     * 根据银行卡号查询当前是否存在
     * @param incomingSettleReqDTO
     * @return
     */
    @Override
    public int queryCountByBankNo(IncomingSettleReqDTO incomingSettleReqDTO) {
        return this.baseMapper.queryCountByBankNo(incomingSettleReqDTO);
    }
}
