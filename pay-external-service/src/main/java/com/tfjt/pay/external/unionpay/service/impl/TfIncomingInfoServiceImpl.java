package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.TfIncomingInfoDao;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.tfjt.pay.external.unionpay.service.TfIncomingInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 入网信息 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Service
public class TfIncomingInfoServiceImpl extends BaseServiceImpl<TfIncomingInfoDao, TfIncomingInfoEntity> implements TfIncomingInfoService {


    @Override
    public TfIncomingInfoEntity queryIncomingInfoById(Long id) {
        return null;
    }

    /**
     * 根据id查询进件提交所需信息
     * @param id
     * @return IncomingSubmitMessageDTO
     */
    @Override
    public IncomingSubmitMessageDTO queryIncomingMessage(Long id) {
        return this.baseMapper.queryIncomingMessage(id);
    }
}
