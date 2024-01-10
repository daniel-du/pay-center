package com.tfjt.pay.external.unionpay.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.FaStandardLocationDictEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 银行表
 * 
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-21 18:51:49
 */
@Mapper
public interface FaStandardLocationDictDao extends BaseMapper<FaStandardLocationDictEntity> {

    List<String> getAreasByCode(List<String> saleAreas);
}
