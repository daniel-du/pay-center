package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.resp.PabcBranchBankInfoRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubPayBankaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:32
 */
@Mapper

public interface PabcPubPayBankaDao extends BaseMapper<PabcPubPayBankaEntity> {
    List<PabcBranchBankInfoRespDTO> getBranchBankInfo(@Param("bankCode") String bankCode, @Param("cityCode") String cityCode, @Param("branchBankName") String branchBankName);
}
