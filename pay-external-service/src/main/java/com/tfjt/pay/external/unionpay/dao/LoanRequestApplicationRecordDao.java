package com.tfjt.pay.external.unionpay.dao;

import cn.hutool.core.date.DateTime;
import com.tfjt.pay.external.unionpay.entity.LoanRequestApplicationRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 贷款-调用回调业务日志表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-18 20:59:10
 */
@Mapper
public interface LoanRequestApplicationRecordDao extends BaseMapper<LoanRequestApplicationRecordEntity> {

    List<LoanRequestApplicationRecordEntity> listError(DateTime date);
}
