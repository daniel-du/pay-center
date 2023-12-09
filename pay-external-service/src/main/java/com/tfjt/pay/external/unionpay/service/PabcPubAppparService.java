package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.resp.BankNameAndCodeRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubAppparEntity;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:10
 */
public interface PabcPubAppparService extends IService<PabcPubAppparEntity> {
    List<BankNameAndCodeRespDTO> getBankInfoByName(String name);
}
