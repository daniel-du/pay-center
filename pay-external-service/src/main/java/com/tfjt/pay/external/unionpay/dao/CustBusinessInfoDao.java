package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.CustBusinessCreateDto;
import com.tfjt.pay.external.unionpay.entity.CustBusinessInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 经营信息
 * 
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-20 09:27:38
 */
@Mapper
public interface CustBusinessInfoDao extends BaseMapper<CustBusinessInfoEntity> {

    List<CustBusinessCreateDto> getBusinessAttach(@Param("loanUserId") String loanUserId);
}
