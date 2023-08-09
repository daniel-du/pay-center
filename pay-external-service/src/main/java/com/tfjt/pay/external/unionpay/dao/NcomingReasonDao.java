package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.resp.ReasonCountVO;
import com.tfjt.pay.external.unionpay.entity.NcomingReasonEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 进件失败原因
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-24 09:00:44
 */
@Mapper
public interface NcomingReasonDao extends BaseMapper<NcomingReasonEntity> {

    List<ReasonCountVO> getReasonCount(Integer loanUserId);
}
