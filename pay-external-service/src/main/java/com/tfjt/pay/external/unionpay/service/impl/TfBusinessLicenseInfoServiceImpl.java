package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.TfBusinessLicenseInfoDao;
import com.tfjt.pay.external.unionpay.dto.req.IncomingBusinessReqDTO;
import com.tfjt.pay.external.unionpay.entity.TfBusinessLicenseInfoEntity;
import com.tfjt.pay.external.unionpay.service.TfBusinessLicenseInfoService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 营业执照信息 服务实现类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
@Service
public class TfBusinessLicenseInfoServiceImpl extends BaseServiceImpl<TfBusinessLicenseInfoDao, TfBusinessLicenseInfoEntity> implements TfBusinessLicenseInfoService {

    /**
     * 根据营业执照号码查询当前是否存在
     * @param incomingBusinessReqDTO
     * @return
     */
    @Override
    public int queryCountByLicenseNo(IncomingBusinessReqDTO incomingBusinessReqDTO) {
        return this.baseMapper.queryCountByLicenseNo(incomingBusinessReqDTO);
    }
}
