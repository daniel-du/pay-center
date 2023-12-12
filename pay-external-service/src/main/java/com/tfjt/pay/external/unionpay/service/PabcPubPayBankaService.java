package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.resp.PabcBranchBankInfoRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubPayBankaEntity;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:36
 */
public interface PabcPubPayBankaService extends IService<PabcPubPayBankaEntity> {
    List<PabcBranchBankInfoRespDTO> getBranchBankInfo(String bankCode, String cityCode, String branchBankName);
}
