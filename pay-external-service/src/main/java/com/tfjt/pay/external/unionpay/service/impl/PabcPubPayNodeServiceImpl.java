package com.tfjt.pay.external.unionpay.service.impl;

import com.tfjt.pay.external.unionpay.dao.PabcPubPayNodeDao;
import com.tfjt.pay.external.unionpay.dto.resp.PabcProvinceInfoRespDTO;
import com.tfjt.pay.external.unionpay.entity.PabcPubPayNodeEntity;
import com.tfjt.pay.external.unionpay.service.PabcPubPayNodeService;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zxy
 * @create 2023/12/9 15:39
 */
@Service
@Slf4j
public class PabcPubPayNodeServiceImpl extends BaseServiceImpl<PabcPubPayNodeDao, PabcPubPayNodeEntity> implements PabcPubPayNodeService {
    @Override
    public List<PabcProvinceInfoRespDTO> getProvinceList(String name) {
        return baseMapper.getProvinceList(name);
    }
}
