package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.PabcPubPayBankaDao;
import com.tfjt.pay.external.unionpay.dto.resp.PabcBranchBankInfoRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubPayBankaEntity;
import com.tfjt.pay.external.unionpay.service.PabcPubPayBankaService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:37
 */
@Service
@Slf4j
public class PabcPubPayBankaServiceImpl extends BaseServiceImpl<PabcPubPayBankaDao, PabcPubPayBankaEntity> implements PabcPubPayBankaService {
    @Override
    public List<PabcBranchBankInfoRespDTO> getBranchBankInfo(String bankCode, String cityCode, String branchBankName) {
        return baseMapper.getBranchBankInfo(bankCode,cityCode,branchBankName);
    }
}
