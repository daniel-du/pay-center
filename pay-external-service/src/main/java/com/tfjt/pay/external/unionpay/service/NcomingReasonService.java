package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.resp.ReasonCountVO;
import com.tfjt.pay.external.unionpay.entity.NcomingReasonEntity;

import java.util.List;

/**
 * 进件失败原因
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-24 09:00:44
 */

public interface NcomingReasonService extends IService<NcomingReasonEntity>  {
    List<ReasonCountVO> getReasonCount(Integer loanUserId);
}

