package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.resp.BankNameAndCodeRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubAppparEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:05
 */
@Mapper
public interface PabcPubAppparDao extends BaseMapper<PabcPubAppparEntity> {
    List<BankNameAndCodeRespDTO> getBankInfoByName(@Param("name") String name);
}
