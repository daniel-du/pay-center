package com.tfjt.pay.external.unionpay.checkbill.handler.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tfjt.pay.external.unionpay.biz.LoanUnionpayCheckBillDetailsServiceBiz;
import com.tfjt.pay.external.unionpay.checkbill.handler.CheckBillHandler;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dto.CheckLoanBillDTO;
import com.tfjt.pay.external.unionpay.entity.LoanUnionpayCheckBillDetailsEntity;
import com.tfjt.pay.external.unionpay.entity.UnionpayLoanWarningEntity;
import com.tfjt.pay.external.unionpay.enums.CheckBillTypeEnum;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 核对银联与本地业务数据
 *
 * @Auther: songx
 * @Date: 2023/10/28/09:32
 * @Description:
 */
@Order(1)
@Component
public class CheckHandler implements CheckBillHandler {
    @Resource
    private LoanUnionpayCheckBillDetailsServiceBiz loanUnionpayCheckBillDetailsServiceBiz;

    /**
     * 分页查询每页查询数量
     */
    private final Integer pageSize = 500;

    /**
     * 核对银联与本地业务数据是否一致,
     * 如不一致则将异常数据保存在数据库并进行钉钉报警
     *
     * @param checkLoanBillDTO 核对参数
     * @return 是否进行下一个流程
     */
    @Override
    public boolean handler(CheckLoanBillDTO checkLoanBillDTO) {
        //需要核对的业务类型信息
        CheckBillTypeEnum[] values = CheckBillTypeEnum.values();
        boolean result = false;
        //异常批次号
        String warnBatchNo = IdUtil.fastSimpleUUID();
        checkLoanBillDTO.setWarnBatchNo(warnBatchNo);
        //遍历每一种业务类型按照业务类型逐一核对
        for (CheckBillTypeEnum value : values) {
            Class<?> clazz = value.getClazz();
            Object bean = SpringUtil.getBean(clazz);
            //业务对应的数据库表名称,记录到报警表中`
            String tableName = getTableName(bean);
            //业务表待核对数量
            int count = unCheckCount(bean, clazz, checkLoanBillDTO.getDate());
            if (count == NumberConstant.ZERO) {
                //业务表数据不存在,判断银联表中是否存在相同的数据,如果不存在则不需处理,否则银联所有的数据都为本地业务不存在的异常数据
                checkUnionPay(value.getTypeName(), checkLoanBillDTO.getDate(),tableName,warnBatchNo,null);
                continue;
            }
            // 计算总页数
            int totalPages = (count + pageSize - 1) / pageSize;
            for (int i = 0; i <= totalPages; i++) {
                //业务待核对数据
                List<LoanUnionpayCheckBillDetailsEntity> unCheckList = unCheckList(bean, clazz, checkLoanBillDTO.getDate(), i, pageSize);
                List<String> platformOrderNoList = unCheckList.stream().map(LoanUnionpayCheckBillDetailsEntity::getPlatformOrderNo).collect(Collectors.toList());
                //银联未核对数据list
                List<LoanUnionpayCheckBillDetailsEntity> unionpayList = loanUnionpayCheckBillDetailsServiceBiz.listUnCheckBill(checkLoanBillDTO.getDate(), value.getTypeName(), platformOrderNoList);
                if (CollectionUtil.isEmpty(unionpayList)) {
                    //业务表中存在数据,银联账单中未查到,记录错误信息
                    checkList(null, unCheckList, value.getTypeName(), tableName, warnBatchNo);
                    continue;
                }
                //都存在,逐条核对数据,并记录错误信息
                if (checkList(unionpayList, unCheckList, value.getTypeName(), tableName, warnBatchNo)) {
                    result = true;
                }
            }
            //验证是否有未核对,未打标记的银联数据,如果有则是全部为业务表中的没有的数据,记录异常信息
            checkUnionPay(value.getTypeName(), checkLoanBillDTO.getDate(),tableName,warnBatchNo,NumberConstant.ZERO);
        }
        return result;
    }

    private void checkUnionPay(String typeName, Date date,String tableName,String warnBatchNo,Integer checkStatus) {
        int count = loanUnionpayCheckBillDetailsServiceBiz.countByTradeTypeAndDate(typeName, date,checkStatus);
        if (count == NumberConstant.ZERO) {
            return;
        }
        int totalPages = (count + pageSize - 1) / pageSize;
        for (int i = 0; i <= totalPages; i++) {
            List<LoanUnionpayCheckBillDetailsEntity> list = loanUnionpayCheckBillDetailsServiceBiz.listByPage(typeName,date,checkStatus,i,pageSize);
            checkList(list,null,typeName,tableName,warnBatchNo);
        }
    }

    /**
     * 获取业务所在的表的表名称
     *
     * @param bean 表对应service
     * @return 表名称
     */
    @SuppressWarnings("all")
    private String getTableName(Object bean) {
        IService<?> iService = (IService) bean;
        Class<?> entityClass = iService.getEntityClass();
        TableName annotation = entityClass.getAnnotation(TableName.class);
        return annotation.value();
    }

    /**
     * 反射查询业务表为指定日期需要核对的数据总量
     *
     * @param bean  业务service
     * @param clazz service对应的class
     * @param date  日期
     * @return 待核对的数据条数
     */
    @SuppressWarnings("all")
    private int unCheckCount(Object bean, Class<?> clazz, Date date) {
        try {
            Method method = clazz.getMethod("countUnCheckBill", Date.class);
            Object invoke = method.invoke(bean, date);
            return (Integer) invoke;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 反射查询业务表为指定日期需要核对的数据
     *
     * @param bean  业务service
     * @param clazz service对应的class
     * @param date  日期
     * @return 待核对的数据列表
     */
    @SuppressWarnings("all")
    private List<LoanUnionpayCheckBillDetailsEntity> unCheckList(Object bean, Class<?> clazz, Date date, int pageNo, int pageSize) {
        try {
            Method method = clazz.getMethod("listUnCheckBill", Date.class, Integer.class, Integer.class);
            Object invoke = method.invoke(bean, date);
            return (List<LoanUnionpayCheckBillDetailsEntity>) invoke;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 核对银联和业务数据
     *
     * @param unionpayList 银联数据
     * @param unCheckList  业务数据
     * @param typeName     业务类型
     * @param tableName    业务表名称
     */
    private boolean checkList(List<LoanUnionpayCheckBillDetailsEntity> unionpayList, List<LoanUnionpayCheckBillDetailsEntity> unCheckList, String typeName, String tableName, String warnBatchNo) {
        //1. 有差异的数据信息,需要报警的数据
        List<UnionpayLoanWarningEntity> diff = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        if (CollectionUtil.isNotEmpty(unionpayList) && CollectionUtil.isNotEmpty(unCheckList)) {
            Iterator<LoanUnionpayCheckBillDetailsEntity> unionPayIterator = unionpayList.iterator();
            Iterator<LoanUnionpayCheckBillDetailsEntity> unCheckIterator = unCheckList.iterator();
            //2.逐条对比数据
            while (unionPayIterator.hasNext()) {
                LoanUnionpayCheckBillDetailsEntity unionPay = unionPayIterator.next();
                while (unCheckIterator.hasNext()) {
                    LoanUnionpayCheckBillDetailsEntity check = unCheckIterator.next();
                    //业务单号不一致跳过当前循环
                    if (!check.getPlatformOrderNo().equals(unionPay.getPlatformOrderNo())
                        || !check.getSystemOrderNo().equals(unionPay.getSystemOrderNo())) {
                        continue;
                    }
                    ids.add(unionPay.getId());
                    //金额不一致的记录到错误日志
                    if (unionPay.getOrderMoney().compareTo(check.getOrderMoney()) != NumberConstant.ZERO) {
                        String cause = String.format("银联交易对账金额不一致,单号:[%s],银联金额:[%s],业务金额:[%s]", unionPay.getPlatformOrderNo(), unionPay.getOrderMoney(), check.getOrderMoney());
                        long money = unionPay.getOrderMoney().multiply(new BigDecimal("100")).longValue();
                        Long businessId = check.getId();
                        diff.add(buildeUnionpayLoanWarningEntity(unionPay.getPlatformOrderNo(), money, businessId, typeName, tableName, cause, warnBatchNo));
                    }
                    unCheckIterator.remove();
                    unionPayIterator.remove();
                }
            }
        }
        //银联数据不为空,表示银联数据多,剩余数据进行报警
        if (CollectionUtil.isNotEmpty(unionpayList)) {
            for (LoanUnionpayCheckBillDetailsEntity entity : unionpayList) {
                String cause = String.format("银联存在该业务,单号:[%s],交易金额:[%s],业务数据未找到该单号!", entity.getPlatformOrderNo(), entity.getOrderMoney());
                diff.add(buildeUnionpayLoanWarningEntity(entity.getPlatformOrderNo(), entity.getOrderMoney().multiply(new BigDecimal("100")).longValue(), null, typeName, tableName, cause, warnBatchNo));
            }
        }
        //业务数据不为空,表示业务数据多,剩余数据进行报警
        if (CollectionUtil.isNotEmpty(unCheckList)) {
            for (LoanUnionpayCheckBillDetailsEntity entity : unCheckList) {
                String cause = String.format("业务数据存在该业务,单号:[%s],交易金额:[%s],银联未找到该单号!", entity.getPlatformOrderNo(), entity.getOrderMoney());
                diff.add(buildeUnionpayLoanWarningEntity(entity.getPlatformOrderNo(), entity.getOrderMoney().multiply(new BigDecimal("100")).longValue(), null, typeName, tableName, cause, warnBatchNo));
            }
        }
        //已核对的账单打标记
        if (CollectionUtil.isNotEmpty(ids)){
            loanUnionpayCheckBillDetailsServiceBiz.updateCheckStatus(ids);
        }
        if (CollectionUtil.isNotEmpty(diff)) {
            loanUnionpayCheckBillDetailsServiceBiz.saveBatchUnionpayLoanWarningEntity(diff);
            return true;
        }
        return false;
    }

    /**
     * 生成报警日志信息
     *
     * @param platformOrderNo 银联/平台交易单号
     * @param money           交易金额
     * @param businessId      业务id
     * @param typeName        业务表名
     * @param tableName       业务表名
     * @param cause           报警原因
     * @return 报警信息
     */
    private UnionpayLoanWarningEntity buildeUnionpayLoanWarningEntity(String platformOrderNo,
                                                                      Long money,
                                                                      Long businessId,
                                                                      String typeName, String tableName,
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
