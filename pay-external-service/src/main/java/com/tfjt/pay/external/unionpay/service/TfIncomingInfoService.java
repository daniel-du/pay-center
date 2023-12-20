package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.pay.external.unionpay.entity.TfIncomingInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 入网信息 服务类
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
public interface TfIncomingInfoService extends IService<TfIncomingInfoEntity> {

    TfIncomingInfoEntity queryIncomingInfoById(Long id);

    /**
     * 根据id查询进件提交所需信息
     * @param id
     * @return
     */
    IncomingSubmitMessageDTO queryIncomingMessage(Long id);

}
