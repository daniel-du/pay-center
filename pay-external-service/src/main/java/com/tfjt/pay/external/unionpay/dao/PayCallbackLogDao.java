package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.PayCallbackLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回调日志表
 *
 * @author ???
 * @email 598085205@qq.com
 * @date 2022-11-05 10:11:22
 */
@Mapper
public interface PayCallbackLogDao extends BaseMapper<PayCallbackLogEntity> {

}
