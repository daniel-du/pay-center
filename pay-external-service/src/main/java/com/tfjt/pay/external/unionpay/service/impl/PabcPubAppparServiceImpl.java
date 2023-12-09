package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.PabcPubAppparDao;
import com.tfjt.pay.external.unionpay.dto.resp.BankNameAndCodeRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubAppparEntity;
import com.tfjt.pay.external.unionpay.service.PabcPubAppparService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:11
 */
@Service
@Slf4j
public class PabcPubAppparServiceImpl extends BaseServiceImpl<PabcPubAppparDao, PabcPubAppparEntity> implements PabcPubAppparService {
    @Override
    public List<BankNameAndCodeRespDTO> getBankInfoByName(String name) {
        return baseMapper.getBankInfoByName(name);
    }
}
