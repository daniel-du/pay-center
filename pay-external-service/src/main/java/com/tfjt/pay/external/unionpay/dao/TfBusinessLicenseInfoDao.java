package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.req.IncomingBusinessReqDTO;
import com.tfjt.pay.external.unionpay.entity.TfBusinessLicenseInfoEntity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 营业执照信息 Mapper 接口
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
public interface TfBusinessLicenseInfoDao extends BaseMapper<TfBusinessLicenseInfoEntity> {

    int queryCountByLicenseNo(@Param("incomingBusiness")IncomingBusinessReqDTO incomingBusinessReqDTO);
}
