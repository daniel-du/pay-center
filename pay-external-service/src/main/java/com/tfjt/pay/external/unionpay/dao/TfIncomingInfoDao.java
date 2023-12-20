package com.tfjt.pay.external.unionpay.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 入网信息 Mapper 接口
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
public interface TfIncomingInfoDao extends BaseMapper<TfIncomingInfoEntity> {

    IncomingSubmitMessageDTO queryIncomingMessage(@Param("id") Long id);

}
