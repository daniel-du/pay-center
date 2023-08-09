package com.tfjt.pay.external.unionpay.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.entity.BankInterbankNumberEntity;

import java.util.List;

/**
 * 联行号表
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-21 18:51:49
 */
public interface BankInterbankNumberService extends IService<BankInterbankNumberEntity> {

    /**
     * 根据城市 code 获取 所有支行编码
     * @param city
     * @return
     */
    List<BankInterbankNumberEntity> getBankNameListByCity(String city);

    /**
     * 根据银行名字模糊搜索
     * @param bankName
     * @return
     */
    List<BankInterbankNumberEntity> getBankNameListByBank(String bankName);
}

