package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.TfBankCardInfoDao;
import com.tfjt.pay.external.unionpay.entity.TfBankCardInfoEntity;
import com.tfjt.pay.external.unionpay.service.ITfBankCardInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class TfBankCardInfoServiceImpl extends BaseServiceImpl<TfBankCardInfoDao, TfBankCardInfoEntity> implements ITfBankCardInfoService {

}
