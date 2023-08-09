package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 贷款-用户
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-20 11:23:12
 */
@Mapper
public interface LoanUserDao extends BaseMapper<LoanUserEntity> {

    List<LoanUserEntity> applicationStatusNotSucceededData();
}
