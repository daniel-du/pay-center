package com.tfjt.pay.external.unionpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.SelfSignEntity;

import java.util.List;

/**
 * 入网表
 *
 * @author ???
 * @email 598085205@qq.com
 * @date 2022-11-09 11:57:33
 */
public interface SelfSignService extends IService<SelfSignEntity> {

    SelfSignEntity selectByMid(String mid);

    /**
     * 根据来源账户批量查询入网信息
     * @param accessAccts
     * @return
     */
    List<SelfSignEntity> querySelfSignsByAccessAccts(List<String> accessAccts);

    /**
     * 根据平台账户id查询进件信息
     * @param accessAcct
     * @return
     */
    SelfSignEntity querySelfSignByAccessAcct(String accessAcct);

    /**
     * 查询最近七天入网成功的商户
     * @return
     */

    List<SelfSignEntity> querySelfSignsBySuccess(String accesserAcct);

}

