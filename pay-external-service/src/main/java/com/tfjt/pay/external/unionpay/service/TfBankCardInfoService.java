package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.dto.req.IncomingSettleReqDTO;
import com.tfjt.pay.external.unionpay.entity.TfBankCardInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 银行卡信息 服务类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
public interface TfBankCardInfoService extends IService<TfBankCardInfoEntity> {

    /**
     * 根据银行卡号查询当前是否存在
     * @param incomingSettleReqDTO
     * @return
     */
    int queryCountByBankNo(IncomingSettleReqDTO incomingSettleReqDTO);
}
