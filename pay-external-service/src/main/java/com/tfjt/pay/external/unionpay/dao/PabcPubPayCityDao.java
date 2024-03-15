package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.resp.PabcCityInfoRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubPayCityEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:32
 */
@Mapper

public interface PabcPubPayCityDao extends BaseMapper<PabcPubPayCityEntity> {
    List<PabcCityInfoRespDTO> getCityList(@Param("provinceCode") String provinceCode, @Param("bankCode") String bankCode);
}
