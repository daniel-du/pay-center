package com.tfjt.pay.external.unionpay.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tfjt.pay.external.unionpay.config.ExecutorConfig;
import com.tfjt.pay.external.unionpay.dao.LoanUserDao;
import com.tfjt.pay.external.unionpay.dto.IncomingReturn;
import com.tfjt.pay.external.unionpay.dto.LoanUserInfoDTO;
import com.tfjt.pay.external.unionpay.dto.resp.UnionPayLoanUserRespDTO;
import com.tfjt.pay.external.unionpay.entity.CustBankInfoEntity;
import com.tfjt.pay.external.unionpay.entity.CustIdcardInfoEntity;
import com.tfjt.pay.external.unionpay.entity.LoanUserEntity;
import com.tfjt.pay.external.unionpay.entity.SupplierUuidEntity;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.StringUtil;
import com.tfjt.tfcloud.business.api.TfLoanUserRpcService;
import com.tfjt.tfcloud.business.dto.TfLoanUserEntityDTO;
import com.tfjt.tfcommon.core.util.BeanUtils;
import com.tfjt.tfcommon.dto.response.Result;
import com.tfjt.tfcommon.mybatis.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class LoanUserServiceImpl extends BaseServiceImpl<LoanUserDao, LoanUserEntity> implements LoanUserService {

    @Resource
    private CustIdcardInfoService custIdcardInfoService;
    @Resource
    private CustBankInfoService custBankInfoService;


    @DubboReference
    private TfLoanUserRpcService tfLoanUserRpcService;

    @Resource
    private SupplierUuidService tfSupplierUuidService;

    @Autowired
    private LoanUserDao tfLoanUserDao;

    @Resource
    private ExecutorConfig executorConfig;

    @Autowired
    private UnionPayLoansApiService unionPayLoansApiService;

    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#loanUserEntity.busId"}, expire = 3000, acquireTimeout = 4000)
    @Override
    public Result<?> saveLoanUser(LoanUserEntity loanUserEntity) {
        //bus_id唯一
        LoanUserEntity tfLoanUserEntity = this.getOne(new LambdaQueryWrapper<LoanUserEntity>().eq(LoanUserEntity::getBusId, loanUserEntity.getBusId()));
        TfLoanUserEntityDTO tfLoanUserEntityDTO = new TfLoanUserEntityDTO();
        boolean bool = false;
        Long save = null;
        if (tfLoanUserEntity == null) {
            bool = this.save(loanUserEntity);

            if (2 == loanUserEntity.getType()) {
                if (loanUserEntity.getSupplierId() == null) {
                    return Result.failed(5001, "供应商的supplierId不能为空！" + null);
                }
                SupplierUuidEntity tfSupplierUuid = new SupplierUuidEntity();
                tfSupplierUuid.setSupplierUuid(loanUserEntity.getBusId());
                tfSupplierUuid.setSupplierId(loanUserEntity.getSupplierId());
                tfSupplierUuidService.save(tfSupplierUuid);
            }

            BeanUtil.copyProperties(loanUserEntity, tfLoanUserEntityDTO);
            save = tfLoanUserRpcService.save(tfLoanUserEntityDTO);
        }


        if (bool && save != null) {
            return Result.ok(loanUserEntity);
        } else {
            return Result.failed("保存失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Lock4j(keys = {"#id"}, expire = 3000, acquireTimeout = 4000)
    @Override
    public Result<?> updateLoanUser(Long id, Integer loanUserType) {

        boolean bool = false;

        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("loan_user_type", loanUserType);
        updateWrapper.eq("id", id);
        bool = this.update(updateWrapper);
        LoanUserEntity loanUserEntity = this.getById(id);
        //TODO 同步user信息到商家后台项目
        if (loanUserEntity != null) {
            com.tfjt.tfcloud.business.result.Result<?> result = tfLoanUserRpcService.updateLoanUserType(loanUserEntity.getBusId(), loanUserType);
            if (0 != result.getCode()) {
                return Result.failed(result.getMsg());
            }
        }

        return Result.ok();

    }

    @Override
    public LoanUserInfoDTO getLoanUerInfo(Long loanUserId) {
        LoanUserEntity loanUser = this.getById(loanUserId);
        if (ObjectUtil.isEmpty(loanUser)) {
            return null;
        }
        LoanUserInfoDTO dto = new LoanUserInfoDTO();
        dto.setOutRequestNo(loanUser.getOutRequestNo());
        dto.setType(loanUser.getType());
        dto.setLoanUserType(loanUser.getLoanUserType());

        if (Objects.equals(loanUser.getApplicationStatus(), "succeeded")) {
            dto.setApplicationStatus(loanUser.getApplicationStatus());
            dto.setErrMsg(loanUser.getFailureMsgsReason());
        } else {
            //实时查询
            if (StringUtils.isNotBlank(loanUser.getOutRequestNo()) && StringUtils.isNotBlank(loanUser.getMchApplicationId())) {
                IncomingReturn incomingReturn = unionPayLoansApiService.getTwoIncomingInfo(loanUser.getOutRequestNo());
                dto.setApplicationStatus(incomingReturn.getApplicationStatus());
                if (ObjectUtil.isNotEmpty(incomingReturn.getFailureMsgs())) {
                    dto.setErrMsg(incomingReturn.getFailureMsgs());
                }
            }
        }

        if (StringUtils.isNotBlank(loanUser.getCusId())) {
            dto.setCusId(loanUser.getCusId());
        }
        if (StringUtils.isNotBlank(loanUser.getMchApplicationId())) {
            dto.setMchApplicationId(loanUser.getMchApplicationId());
        }
        CustIdcardInfoEntity idcardInfoEntity = this.custIdcardInfoService.getOne(new LambdaQueryWrapper<CustIdcardInfoEntity>()
                .eq(CustIdcardInfoEntity::getLoanUserId, loanUserId));
        if (!ObjectUtil.isEmpty(idcardInfoEntity)) {
            dto.setMerchantShortName(idcardInfoEntity.getMerchantShortName());
        }
        CustBankInfoEntity custBankInfoEntity = custBankInfoService.getOne(new LambdaQueryWrapper<CustBankInfoEntity>().eq(CustBankInfoEntity::getLoanUserId, loanUserId));
        if (custBankInfoEntity != null) {
            dto.setPhone(custBankInfoEntity.getPhone());
        }

        return dto;
    }

    @Override
    public LoanUserEntity getLoanUserByBusIdAndType(String busId, Integer type) {
        return this.getOne(Wrappers.lambdaQuery(LoanUserEntity.class).eq(LoanUserEntity::getBusId, busId).eq(LoanUserEntity::getType, type));
    }

    /**
     * 贷款签约状态修改定时
     *
     * @param jobParam
     */
    @Override
    public void applicationStatusUpdateJob(String jobParam) {
        //查询进件没成功得数据
        List<LoanUserEntity> tfLoanUserEntityList = this.applicationStatusNotSucceededData();
        log.info("--------------------------进件没成功长度" + tfLoanUserEntityList.size() + "----------------------");
        List<List<LoanUserEntity>> batchList = StringUtil.split(tfLoanUserEntityList, 100);

        if (batchList != null && batchList.size() > 0) {
            batchList.forEach(curList -> executorConfig.asyncServiceExecutor().execute(() -> {
                log.info("线程名称{}", Thread.currentThread().getName());//打印线程名称
                log.info("线程名称-" + Thread.currentThread().getName() + "处理数据长度" + curList.size());
                applicationStatusUpdate(curList);
            }));
        }
    }

    @Override
    public LoanUserEntity getBySettleAcctId(String settleAcctId) {
        return this.getOne(new LambdaQueryWrapper<LoanUserEntity>().eq(LoanUserEntity::getSettleAcctId, settleAcctId));
    }

    @Override
    public Result<?> updateLoanUserDto(TfLoanUserEntityDTO tfLoanUserEntity) {
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set(tfLoanUserEntity.getType() != null, "type", tfLoanUserEntity.getType());
        updateWrapper.set(StringUtils.isNotEmpty(tfLoanUserEntity.getApplicationStatus()), "application_status", tfLoanUserEntity.getApplicationStatus());
        updateWrapper.set(tfLoanUserEntity.getSucceededAt() != null, "succeeded_at", tfLoanUserEntity.getSucceededAt());
        updateWrapper.set(tfLoanUserEntity.getFailedAt() != null, "failed_at", tfLoanUserEntity.getFailedAt());
        updateWrapper.set(StringUtils.isNotEmpty(tfLoanUserEntity.getFailureMsgs()), "failure_msgs", tfLoanUserEntity.getFailureMsgs());
        updateWrapper.set(StringUtils.isNotEmpty(tfLoanUserEntity.getFailureMsgsParam()), "failure_msgs_param", tfLoanUserEntity.getFailureMsgsParam());
        updateWrapper.set(StringUtils.isNotEmpty(tfLoanUserEntity.getFailureMsgsReason()), "failure_msgs_reason", tfLoanUserEntity.getFailureMsgsReason());
        updateWrapper.set(StringUtils.isNotEmpty(tfLoanUserEntity.getOutRequestNo()), "out_request_no", tfLoanUserEntity.getOutRequestNo());
        updateWrapper.set(tfLoanUserEntity.getCusApplicationId() != null, "cus_application_id", tfLoanUserEntity.getCusApplicationId());
        updateWrapper.set(StringUtils.isNotEmpty(tfLoanUserEntity.getUpdater()), "updater", tfLoanUserEntity.getUpdater());
        updateWrapper.set("update_date", new Date());
        updateWrapper.set(StringUtils.isNotEmpty(tfLoanUserEntity.getName()), "name", tfLoanUserEntity.getName());
        updateWrapper.set(StringUtils.isNotEmpty(tfLoanUserEntity.getSettleAcctId()), "settle_acct_id", tfLoanUserEntity.getSettleAcctId());
        updateWrapper.set(StringUtils.isNotEmpty(tfLoanUserEntity.getBindAcctName()), "bind_acct_name", tfLoanUserEntity.getBindAcctName());
        updateWrapper.set(StringUtils.isNotEmpty(tfLoanUserEntity.getMchApplicationId()), "mch_application_id", tfLoanUserEntity.getMchApplicationId());
        updateWrapper.set(tfLoanUserEntity.getAuditedAt() != null, "audited_at", tfLoanUserEntity.getAuditedAt());
        updateWrapper.eq("id", tfLoanUserEntity.getId());

        boolean flag = this.update(updateWrapper);
        //TODO 同步user信息到商家后台项目
        if (flag) {
            com.tfjt.tfcloud.business.result.Result<?> result = tfLoanUserRpcService.updateLoanUserDto(tfLoanUserEntity);
            if (0 != result.getCode()) {
                return Result.failed(result.getMsg());
            }
        } else {
            return Result.failed("更新贷款用户信息失败！");
        }
        return Result.ok();
    }


    @Async("asyncServiceExecutor")
    @Override
    public void asynNotice(LoanUserEntity tfLoanUserEntity) {
        //同步业务用户表
        TfLoanUserEntityDTO tfLoanUserEntityDTO = new TfLoanUserEntityDTO();
        BeanUtil.copyProperties(tfLoanUserEntity, tfLoanUserEntityDTO);
        List<TfLoanUserEntityDTO> tfLoanUserEntityDTOList = new ArrayList<>();
        tfLoanUserEntityDTOList.add(tfLoanUserEntityDTO);
        tfLoanUserRpcService.updateBatch(tfLoanUserEntityDTOList);
    }

    @Override
    public List<UnionPayLoanUserRespDTO> listLoanUserByBusId(String type, List<String> busIds) {
        return this.getBaseMapper().listLoanUserByBusId(type,busIds);
    }

    @Override
    public LoanUserEntity getByBalanceAcctId(String balanceAcctId) {
        return this.getBaseMapper().getByBalanceAcctId(balanceAcctId);
    }

    /**
     * 查询并更新贷款用户-状态
     *
     * @param curList
     */
    @Transactional(rollbackFor = Exception.class)
    public void applicationStatusUpdate(List<LoanUserEntity> curList) {
        List<LoanUserEntity> upList = new ArrayList<>();
        curList.forEach(tfLoanUserEntity -> {
            //1 一级
            IncomingReturn incomingReturn = unionPayLoansApiService.getTwoIncomingInfo(tfLoanUserEntity.getOutRequestNo());
            if (Objects.equals(incomingReturn.getMchApplicationId(), tfLoanUserEntity.getMchApplicationId()) && !Objects.equals(tfLoanUserEntity.getApplicationStatus(), incomingReturn.getApplicationStatus())) {
                tfLoanUserEntity.setApplicationStatus(incomingReturn.getApplicationStatus());
                if (ObjectUtil.isNotEmpty(incomingReturn.getFailureMsgs())) {
                    tfLoanUserEntity.setFailureMsgs(incomingReturn.getFailureMsgs());
                }
                upList.add(tfLoanUserEntity);
            }
        });
        if (upList.size() > 0) {
            log.info("线程名称-" + Thread.currentThread().getName() + "处理贷款更新数据长度" + curList.size());
            this.updateBatchById(upList);
            //rpc 同步 业务库
            List<TfLoanUserEntityDTO> tfLoanUserEntityDTOList = BeanUtils.copyList2Other(TfLoanUserEntityDTO.class, upList);
            tfLoanUserRpcService.updateBatch(tfLoanUserEntityDTOList);
        }
    }

    private List<LoanUserEntity> applicationStatusNotSucceededData() {
        return tfLoanUserDao.applicationStatusNotSucceededData();
    }
}
