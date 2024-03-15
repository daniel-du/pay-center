package com.tfjt.pay.external.unionpay.service;

import com.tfjt.pay.external.unionpay.entity.TfIncomingImportEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Du Penglun
 * @since 2024-01-07
 */
public interface ITfIncomingImportService extends IService<TfIncomingImportEntity> {

    /**
     * 根据起始id查询未提交数据，每次一百条
     * @param id
     * @return
     */
    List<TfIncomingImportEntity> queryListByStartId(Long id);

    /**
     * 查询未提交状态最小id数据
     * @return
     */
    TfIncomingImportEntity queryNotSubmitMinIdData();
}
