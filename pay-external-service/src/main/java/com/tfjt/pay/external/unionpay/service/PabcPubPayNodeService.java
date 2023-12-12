package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.dto.resp.PabcProvinceInfoRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubPayNodeEntity;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:35
 */
public interface PabcPubPayNodeService extends IService<PabcPubPayNodeEntity> {
    List<PabcProvinceInfoRespDTO> getProvinceList(String name);
}
