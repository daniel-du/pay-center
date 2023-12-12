package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.resp.PabcProvinceInfoRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubPayNodeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:33
 */
@Mapper

public interface PabcPubPayNodeDao extends BaseMapper<PabcPubPayNodeEntity> {
    List<PabcProvinceInfoRespDTO> getProvinceList(@Param(("name")) String name);
}
