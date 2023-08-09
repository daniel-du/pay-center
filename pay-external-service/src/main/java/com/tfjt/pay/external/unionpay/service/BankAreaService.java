package com.tfjt.pay.external.unionpay.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.BankAreaEntity;

import java.util.List;

/**
 * 联行号表-城市代码
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-21 18:51:49
 */
public interface BankAreaService extends IService<BankAreaEntity> {

    /**
     * 根据省 code获取所有的下辖 市 县
     * @param province
     * @return
     */
    List<BankAreaEntity> getBankAreaByPro(String province);

    /**
     * 获取所有省级编码
     * @return
     */
    List<BankAreaEntity> getAllBankArea();
}

