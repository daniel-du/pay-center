package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.entity.TfIncomingExtendInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 入网扩展信息表 服务类
 * </p>
 *
 * @author Du Penglun
 * @since 2024-03-21
 */
public interface TfIncomingExtendInfoService extends IService<TfIncomingExtendInfoEntity> {

    boolean updateByIncomingId(TfIncomingExtendInfoEntity extendInfo);

    TfIncomingExtendInfoEntity queryNotSignMinIdData();



}
