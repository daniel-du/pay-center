package com.tfjt.pay.external.unionpay.biz.impl;

import com.tfjt.pay.external.unionpay.biz.PabcBizService;
import com.tfjt.pay.external.unionpay.dto.resp.BankNameAndCodeRespDTO;
import com.tfjt.pay.external.unionpay.service.PabcPubAppparService;
import com.tfjt.tfcommon.dto.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 16:05
 */
@Service
public class PabcBizServiceImpl implements PabcBizService {

    @Autowired
    private PabcPubAppparService pabcPubAppparService;
    @Override
    public Result<List<BankNameAndCodeRespDTO>> getBankInfoByName(String name) {
        List<BankNameAndCodeRespDTO> list = pabcPubAppparService.getBankInfoByName(name);
        return Result.ok(list);
    }
}
