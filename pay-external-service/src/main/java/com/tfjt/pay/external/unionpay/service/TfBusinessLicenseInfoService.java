package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.dto.req.IncomingBusinessReqDTO;
import com.tfjt.pay.external.unionpay.entity.TfBusinessLicenseInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 营业执照信息 服务类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
public interface TfBusinessLicenseInfoService extends IService<TfBusinessLicenseInfoEntity> {

    /**
     * 根据营业执照号码查询当前是否存在
     * @param incomingBusinessReqDTO
     * @return
     */
    int queryCountByLicenseNo(IncomingBusinessReqDTO incomingBusinessReqDTO);
}
