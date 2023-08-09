package com.tfjt.pay.external.unionpay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tfjt.pay.external.unionpay.dao.NcomingReasonDao;
import com.tfjt.pay.external.unionpay.dto.resp.ReasonCountVO;
import com.tfjt.pay.external.unionpay.entity.NcomingReasonEntity;
import com.tfjt.pay.external.unionpay.service.NcomingReasonService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service("ncomingReasonService")
public class NcomingReasonServiceImpl extends ServiceImpl<NcomingReasonDao, NcomingReasonEntity> implements NcomingReasonService {

    @Resource
    NcomingReasonDao ncomingReasonDao;

    @Override
    public List<ReasonCountVO> getReasonCount(Integer loanUserId) {

        return  ncomingReasonDao.getReasonCount(loanUserId);
    }
}
