package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.entity.TfIncomingApiLogEntity;
import com.tfjt.pay.external.unionpay.dao.TfIncomingApiLogDao;
import com.tfjt.pay.external.unionpay.service.TfIncomingApiLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 进件日志记录表 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2024-01-03
 */
@Service
public class TfIncomingApiLogServiceImpl extends BaseServiceImpl<TfIncomingApiLogDao, TfIncomingApiLogEntity> implements TfIncomingApiLogService {

}
