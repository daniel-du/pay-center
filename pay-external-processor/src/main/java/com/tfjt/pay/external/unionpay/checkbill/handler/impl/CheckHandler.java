package com.tfjt.pay.external.unionpay.checkbill.handler.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.biz.LoanUnionPayCheckBillBiz;
import com.tfjt.pay.external.unionpay.biz.LoanUnionpayCheckBillDetailsServiceBiz;
import com.tfjt.pay.external.unionpay.checkbill.handler.CheckBillHandler;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dto.CheckLoanBillDTO;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.UnionpayLoanWarningEntity;
import com.tfjt.pay.external.unionpay.enums.CheckBillTypeEnum;
import org.apache.commons.collections4.sequence.EditScript;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 核对银联与本地业务数据
 * @Auther: songx
 * @Date: 2023/10/28/09:32
 * @Description:
 */
@Order(NumberConstant.ONE)
@Component
public class CheckHandler implements CheckBillHandler {
    @Resource
    private LoanUnionpayCheckBillDetailsServiceBiz loanUnionpayCheckBillDetailsServiceBiz;

    /**
     * 核对银联与本地业务数据是否一致,
     * 如不一致则将异常数据保存在数据库并进行钉钉报警
     * @param checkLoanBillDTO 核对参数
     * @return 是否进行下一个流程
     */
    @Override
    public boolean handler(CheckLoanBillDTO checkLoanBillDTO) {
        CheckBillTypeEnum[] values = CheckBillTypeEnum.values();
        boolean result = false;
        String warnBatchNo = IdUtil.fastSimpleUUID();
        checkLoanBillDTO.setWarnBatchNo(warnBatchNo);
        for (CheckBillTypeEnum value : values) {
            Class<?> clazz = value.getClazz();
            Object bean = SpringUtil.getBean(clazz);
            String tableName = getTableName(bean);
            List<LoanUnionpayCheckBillDetailsEntity> unCheckList =  unCheckList(bean,clazz,checkLoanBillDTO.getDate());
            List<LoanUnionpayCheckBillDetailsEntity> unionpayList = loanUnionpayCheckBillDetailsServiceBiz.listUnCheckBill(checkLoanBillDTO.getDate(),value.getTypeName());
            if (CollectionUtil.isEmpty(unionpayList) && CollectionUtil.isEmpty(unCheckList)){
                continue;
            }
            if(checkList(unionpayList,unCheckList,value.getTypeName(),tableName,warnBatchNo)){
                result = true;
            }
        }
        return result;
    }

    /**
     * 获取业务所在的表的表名称
     * @param bean  表对应service
     * @return   表名称
     */
    @SuppressWarnings("all")
    private String getTableName(Object bean) {
        IService<?> iService = (IService)bean;
        Class<?> entityClass = iService.getEntityClass();
        TableName annotation = entityClass.getAnnotation(TableName.class);
        return annotation.value();
    }

    /**
     * 反射查询业务表为指定日期需要核对的数据
     * @param bean    业务service
     * @param clazz   service对应的class
     * @param date    日期
     * @return  待核对的数据列表
     */
    @SuppressWarnings("all")
    private List<LoanUnionpayCheckBillDetailsEntity> unCheckList(Object bean, Class<?> clazz, Date date) {
        try {
            Method method = clazz.getMethod("listUnCheckBill", Date.class);
            Object invoke = method.invoke(bean, date);
            return (List<LoanUnionpayCheckBillDetailsEntity>) invoke;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 核对银联和业务数据
     * @param unionpayList 银联数据
     * @param unCheckList  业务数据
     * @param typeName     业务类型
     * @param tableName    业务表名称
     */
    private boolean checkList(List<LoanUnionpayCheckBillDetailsEntity> unionpayList, List<LoanUnionpayCheckBillDetailsEntity> unCheckList,String typeName,String tableName,String warnBatchNo) {
        //1. 有差异的数据信息,需要报警的数据
        List<UnionpayLoanWarningEntity> diff = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(unionpayList) && CollectionUtil.isNotEmpty(unCheckList)){
            Iterator<LoanUnionpayCheckBillDetailsEntity> unionPayIterator = unionpayList.iterator();
            Iterator<LoanUnionpayCheckBillDetailsEntity> unCheckIterator = unCheckList.iterator();
            //2.逐条对比数据
            while (unionPayIterator.hasNext()){
                LoanUnionpayCheckBillDetailsEntity unionPay = unionPayIterator.next();
                while(unCheckIterator.hasNext()){
                    LoanUnionpayCheckBillDetailsEntity check = unCheckIterator.next();
                    //业务单号不一致跳过当前循环
                    if (!check.getPlatformOrderNo().equals(unionPay.getPlatformOrderNo())){
                        continue;
                    }
                    //金额不一致的记录到错误日志
                    if (unionPay.getOrderMoney().compareTo(check.getOrderMoney())!=NumberConstant.ZERO){
                        String cause = String.format("银联交易对账金额不一致,单号:[%s],银联金额:[%s],业务金额:[%s]", unionPay.getPlatformOrderNo(), unionPay.getOrderMoney(), check.getOrderMoney());
                        long money = unionPay.getOrderMoney().multiply(new BigDecimal("100")).longValue();
                        Long businessId = check.getId();
                        diff.add(buildeUnionpayLoanWarningEntity(unionPay.getPlatformOrderNo(),money,businessId,typeName,tableName,cause,warnBatchNo));
                    }
                    unCheckIterator.remove();
                    unionPayIterator.remove();
                }
            }
        }
        //银联数据不为空,表示银联数据多,剩余数据进行报警
        if (CollectionUtil.isNotEmpty(unionpayList)){
            for (LoanUnionpayCheckBillDetailsEntity entity : unionpayList) {
                String cause = String.format("银联存在该业务,单号:[%s],交易金额:[%s],业务数据未找到该单号!", entity.getPlatformOrderNo(), entity.getOrderMoney());
                diff.add(buildeUnionpayLoanWarningEntity(entity.getPlatformOrderNo(),entity.getOrderMoney().multiply(new BigDecimal("100")).longValue(),null,typeName,tableName,cause,warnBatchNo));
            }
        }
        //业务数据不为空,表示业务数据多,剩余数据进行报警
        if (CollectionUtil.isNotEmpty(unCheckList)){
            for (LoanUnionpayCheckBillDetailsEntity entity : unCheckList) {
                String cause = String.format("业务数据存在该业务,单号:[%s],交易金额:[%s],银联未找到该单号!", entity.getPlatformOrderNo(), entity.getOrderMoney());
                diff.add(buildeUnionpayLoanWarningEntity(entity.getPlatformOrderNo(),entity.getOrderMoney().multiply(new BigDecimal("100")).longValue(),null,typeName,tableName,cause,warnBatchNo));
            }
        }
        if (CollectionUtil.isNotEmpty(diff)){
            loanUnionpayCheckBillDetailsServiceBiz.saveBatchUnionpayLoanWarningEntity(diff);
            return true;
        }
        return false;
    }

    /**
     * 生成报警日志信息
     * @param platformOrderNo 银联/平台交易单号
     * @param money           交易金额
     * @param businessId      业务id
     * @param typeName        业务表名
     * @param tableName       业务表名
     * @param cause           报警原因
     * @return  报警信息
     */
    private UnionpayLoanWarningEntity buildeUnionpayLoanWarningEntity(String platformOrderNo,
                                                                      Long money,
                                                                      Long businessId,
                                                                      String typeName,String tableName,
                                                                      String cause,
                                                                      String warnBatchNo) {
        UnionpayLoanWarningEntity unionpayLoanWarningEntity = new UnionpayLoanWarningEntity();
        unionpayLoanWarningEntity.setBatchNo(warnBatchNo);
        unionpayLoanWarningEntity.setMoney(money);
        unionpayLoanWarningEntity.setType(typeName);
        unionpayLoanWarningEntity.setTfOrderNo(platformOrderNo);
        unionpayLoanWarningEntity.setSubUnionpayOrderNo(platformOrderNo);
        unionpayLoanWarningEntity.setIsOk(NumberConstant.ZERO);
        unionpayLoanWarningEntity.setCause(cause);
        unionpayLoanWarningEntity.setBusinessId(businessId);
        unionpayLoanWarningEntity.setTableName(tableName);
        unionpayLoanWarningEntity.setCreateTime(new Date());
        return unionpayLoanWarningEntity;
    }
}
