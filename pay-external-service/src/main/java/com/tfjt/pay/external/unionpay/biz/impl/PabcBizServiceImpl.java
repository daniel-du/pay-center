package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.fms.data.insight.api.service.SupplierApiService;
import com.tfjt.pay.external.unionpay.biz.PabcBizService;
import com.tfjt.pay.external.unionpay.dto.req.QueryAccessBankStatueReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.*;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.response.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zxy
 * @create 2023/12/9 16:05
 */
@Service
public class PabcBizServiceImpl implements PabcBizService {

    @Autowired
    private PabcPubAppparService pabcPubAppparService;
    @Autowired
    private PabcPubPayNodeService pabcPubPayNodeService;
    @Autowired
    private PabcPubPayCityService pabcPubPayCityService;
    @Autowired
    private PabcPubPayBankaService pabcPubPayBankaService;
    @Autowired
    private PabcSuperbankcodeService pabcSuperbankcodeService;
    @Autowired
    private SelfSignService selfSignService;
    @Autowired
    private TfIncomingInfoService tfIncomingInfoService;
    @Autowired
    private LoanUserService loanUserService;
    @Autowired
    private SalesAreaIncomingChannelService salesAreaIncomingChannelService;
    @Autowired
    private TfIdcardInfoService tfIdcardInfoService;
    @Autowired
    private TfIncomingMerchantInfoService tfIncomingMerchantInfoService;
    @Autowired
    private TfIncomingSettleInfoService tfIncomingSettleInfoService;
    @Autowired
    private RedisCache redisCache;
    @DubboReference
    private SupplierApiService supplierApiService;

    Map<String, String> areaChannelMap;

    @Override
    public Result<List<PabcBankNameAndCodeRespDTO>> getBankInfoByName(String name) {
        List<PabcBankNameAndCodeRespDTO> list = pabcPubAppparService.getBankInfoByName(name);
        return Result.ok(list);
    }

    @Override
    public Result<List<PabcProvinceInfoRespDTO>> getProvinceList(String name) {
        List<PabcProvinceInfoRespDTO> list = pabcPubPayNodeService.getProvinceList(name);
        return Result.ok(list);
    }

    @Override
    public Result<List<PabcCityInfoRespDTO>> getCityList(String provinceCode, String bankCode) {
        if (StringUtils.isBlank(provinceCode) || StringUtils.isBlank(bankCode)) {
            throw new TfException(PayExceptionCodeEnum.QUERY_PARAM_IS_NOT_NULL);
        }
        List<PabcCityInfoRespDTO> list = pabcPubPayCityService.getCityList(provinceCode, bankCode);
        return Result.ok(list);
    }

    @Override
    public Result<List<PabcBranchBankInfoRespDTO>> getBranchBankInfo(String bankCode, String cityCode, String branchBankName) {
        if (StringUtils.isBlank(bankCode) || StringUtils.isBlank(cityCode)) {
            throw new TfException(PayExceptionCodeEnum.QUERY_PARAM_IS_NOT_NULL);
        }
        List<PabcBranchBankInfoRespDTO> list = pabcPubPayBankaService.getBranchBankInfo(bankCode, cityCode, branchBankName);
        List<String> collect = list.stream().map(PabcBranchBankInfoRespDTO::getBankDreccode).collect(Collectors.toList());
        List<PabcSuperbankcodeEntity> superbankcodeEntityList = pabcSuperbankcodeService.list(new LambdaQueryWrapper<PabcSuperbankcodeEntity>().in(PabcSuperbankcodeEntity::getAgentbankcode, collect));
        Map<String, List<PabcSuperbankcodeEntity>> map = superbankcodeEntityList.stream().collect(Collectors.groupingBy(PabcSuperbankcodeEntity::getAgentbankcode));
        for (PabcBranchBankInfoRespDTO pabcBranchBankInfoRespDTO : list) {
            String bankDreccode = pabcBranchBankInfoRespDTO.getBankDreccode();
            if (StringUtils.isNotBlank(bankDreccode)) {
                List<PabcSuperbankcodeEntity> entityList = map.get(bankDreccode);
                if (CollectionUtil.isNotEmpty(entityList)) {
                    String bankno = entityList.get(0).getBankno();
                    pabcBranchBankInfoRespDTO.setBankNo(bankno);
                }
            }
        }
        return Result.ok(list);
    }

    @Override
    public Result<List<QueryAccessBankStatueRespDTO>> getNetworkStatus(QueryAccessBankStatueReqDTO queryAccessBankStatueReqDTO) {
        List<QueryAccessBankStatueRespDTO> list = new ArrayList<>();
        //入网状态查询：1、贷款进件；2、商户进件；
        Integer networkType = queryAccessBankStatueReqDTO.getNetworkType();
        //系统来源：1、经销商；2、供应商；3、商家
        Integer businessType = queryAccessBankStatueReqDTO.getBusinessType();
        //经销商/供应商id/店铺id
        String businessId = queryAccessBankStatueReqDTO.getBusinessId();
        //判断查询参数是否为空
        if (ObjectUtil.isNull(networkType) || ObjectUtil.isNull(businessType) || StringUtils.isBlank(businessId)) {
            throw new TfException(PayExceptionCodeEnum.QUERY_PARAM_IS_NOT_NULL);
        }
        //商户进件查询
        if (IncomingAccessTypeEnum.COMMON.getCode().equals(networkType)) {
            //银联进件查询
            QueryAccessBankStatueRespDTO unionIncoming = getUnionIncoming(businessType, businessId);
            //平安进件查询
            QueryAccessBankStatueRespDTO pabcIncoming = getPabcIncoming(businessId);
            list.add(unionIncoming);
            list.add(pabcIncoming);
        }
        //贷款进件查询
        if (IncomingAccessTypeEnum.LOAN.getCode().equals(networkType)) {
            //银联贷款进件查询(目前只有银联有贷款进件)
            QueryAccessBankStatueRespDTO unionLoanIncoming = getUnionLoanIncoming(businessType, businessId);
            list.add(unionLoanIncoming);
        }
        return Result.ok(list);
    }

    @Override
    public Result<Integer> getNetworkTypeByAreaCode(String code) {
        int status;
        if (this.areaChannelMap == null) {
            List<SalesAreaIncomingChannelEntity> list = salesAreaIncomingChannelService.list();
            this.areaChannelMap = list.stream().collect(Collectors.toMap(SalesAreaIncomingChannelEntity::getDistrictsCode, SalesAreaIncomingChannelEntity::getDistricts));
        }
        String districts = this.areaChannelMap.get(code);
        if (StringUtils.isNotBlank(districts)) {
            status = 2;
        } else {
            status = 1;
        }
        return Result.ok(status);
    }

    @Override
    public Result<MoudleStatusRespDTO> getModuleStatus(Long incomingId) {
        //创建返回对象
        MoudleStatusRespDTO moudleStatusRespDTO = new MoudleStatusRespDTO();
        //根据入网id查询身份信息、营业信息、结算信息
        if (ObjectUtil.isNotNull(incomingId)) {
            throw new TfException(PayExceptionCodeEnum.QUERY_PARAM_IS_NOT_NULL);
        }
        // 创建IdcardInfo实体查询对象
        LambdaQueryWrapper<TfIdcardInfoEntity> idcardInfoEntityQueryWrapper = new LambdaQueryWrapper<>();
        // 创建MerchantInfo实体查询对象
        LambdaQueryWrapper<TfIncomingMerchantInfoEntity> merchantInfoEntityQueryWrapper = new LambdaQueryWrapper<>();
        // 创建SettleInfo实体查询对象
        LambdaQueryWrapper<TfIncomingSettleInfoEntity> settleInfoEntityQueryWrapper = new LambdaQueryWrapper<>();

        // 根据incomingId和未删除状态筛选IdcardInfo实体
        idcardInfoEntityQueryWrapper.eq(TfIdcardInfoEntity::getId, incomingId).eq(TfIdcardInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        // 根据incomingId和未删除状态筛选MerchantInfo实体
        merchantInfoEntityQueryWrapper.eq(TfIncomingMerchantInfoEntity::getId, incomingId).eq(TfIncomingMerchantInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        // 根据incomingId和未删除状态筛选SettleInfo实体
        settleInfoEntityQueryWrapper.eq(TfIncomingSettleInfoEntity::getId, incomingId).eq(TfIncomingSettleInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        // 通过IdcardInfo服务查询IdcardInfo实体
        TfIdcardInfoEntity tfIdcardInfoEntity = tfIdcardInfoService.getOne(idcardInfoEntityQueryWrapper);
        // 通过MerchantInfo服务查询MerchantInfo实体
        TfIncomingMerchantInfoEntity tfIncomingMerchantInfoEntity = tfIncomingMerchantInfoService.getOne(merchantInfoEntityQueryWrapper);
        // 通过SettleInfo服务查询SettleInfo实体
        TfIncomingSettleInfoEntity tfIncomingSettleInfoEntity = tfIncomingSettleInfoService.getOne(settleInfoEntityQueryWrapper);

        // 如果查询到IdcardInfo实体，则设置模块状态响应DTO的cardId
        if (null != tfIdcardInfoEntity) {
            moudleStatusRespDTO.setCardId(tfIdcardInfoEntity.getId());
        }
        // 如果查询到MerchantInfo实体，则设置模块状态响应DTO的merchantId
        if (null != tfIncomingMerchantInfoEntity) {
            moudleStatusRespDTO.setMerchantId(tfIncomingMerchantInfoEntity.getId());
        }
        // 如果查询到SettleInfo实体，则设置模块状态响应DTO的settleId
        if (null != tfIncomingSettleInfoEntity) {
            moudleStatusRespDTO.setSettleId(tfIncomingSettleInfoEntity.getId());
        }

        return Result.ok(moudleStatusRespDTO);
    }


    private QueryAccessBankStatueRespDTO getUnionLoanIncoming(Integer businessType, String businessId) {
        QueryAccessBankStatueRespDTO queryAccessBankStatueRespDTO = new QueryAccessBankStatueRespDTO();
        LambdaQueryWrapper<LoanUserEntity> wrapper = new LambdaQueryWrapper<>();
        //查询商家贷款进件情况
        if (BusinessUserTypeEnum.BUSINESS.getCode().equals(businessType)) {
            wrapper.eq(LoanUserEntity::getCusId, businessId).eq(LoanUserEntity::getType, BusinessUserTypeEnum.BUSINESS.getCode());
        }
        //查询经销商/供应商贷款进件情况
        if (BusinessUserTypeEnum.SUPPLIER.getCode().equals(businessType) || BusinessUserTypeEnum.DEALER.getCode().equals(businessType)) {
            //根据businessId获取经销商/供应商的supplier_id
            Result<String> result = supplierApiService.getSupplierIdById(businessId);
            if (null != result) {
                String supplierId = result.getData();
                if (StringUtils.isNotBlank(supplierId)) {
                    wrapper.eq(LoanUserEntity::getCusId, supplierId).eq(LoanUserEntity::getType, BusinessUserTypeEnum.SUPPLIER.getCode());
                }
            } else {
                throw new TfException(PayExceptionCodeEnum.QUERY_PARAM_ERROR);
            }
        }

        LoanUserEntity loanUser = loanUserService.getOne(wrapper);
        if (null != loanUser) {
            queryAccessBankStatueRespDTO.setStatus(IncomingStatusEnum.INCOMING.getCode());
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.UNIONPAY_LOAN.getName());
        } else {
            queryAccessBankStatueRespDTO.setStatus(IncomingStatusEnum.NOT_INCOMING.getCode());
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.UNIONPAY_LOAN.getName());
        }


        return queryAccessBankStatueRespDTO;
    }

    /**
     * 查询平安进件信息
     *
     * @param businessId 经销商/供应商id/店铺id
     * @return
     */
    private QueryAccessBankStatueRespDTO getPabcIncoming(String businessId) {
        QueryAccessBankStatueRespDTO queryAccessBankStatueRespDTO = new QueryAccessBankStatueRespDTO();
        TfIncomingInfoEntity one = tfIncomingInfoService.getOne(new LambdaQueryWrapper<TfIncomingInfoEntity>().eq(TfIncomingInfoEntity::getBusinessId, businessId).eq(TfIncomingInfoEntity::getAccessChannelType, IncomingAccessChannelTypeEnum.PINGAN.getCode()).eq(TfIncomingInfoEntity::getAccessType, IncomingAccessTypeEnum.COMMON.getCode()));
        if (null != one) {
            queryAccessBankStatueRespDTO.setStatus(IncomingStatusEnum.INCOMING.getCode());
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.PINGAN.getName());
        } else {
            queryAccessBankStatueRespDTO.setStatus(IncomingStatusEnum.NOT_INCOMING.getCode());
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.PINGAN.getName());
        }
        return queryAccessBankStatueRespDTO;
    }

    /**
     * 查询银联进件信息
     *
     * @param businessType 经销商/供应商id/店铺id
     * @param businessId   判断查询参数是否为空
     * @return
     */
    private QueryAccessBankStatueRespDTO getUnionIncoming(Integer businessType, String businessId) {
        QueryAccessBankStatueRespDTO queryAccessBankStatueRespDTO = new QueryAccessBankStatueRespDTO();
        String buisnessNo = businessId;
        if (BusinessUserTypeEnum.BUSINESS.getCode().equals(businessType)) {
            buisnessNo = "tfys" + businessId;
        }
        SelfSignEntity one = selfSignService.getOne(new LambdaQueryWrapper<SelfSignEntity>().eq(SelfSignEntity::getAccesserAcct, buisnessNo));
        if (null != one) {
            queryAccessBankStatueRespDTO.setStatus(IncomingStatusEnum.INCOMING.getCode());
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.UNIONPAY.getName());
        } else {
            queryAccessBankStatueRespDTO.setStatus(IncomingStatusEnum.NOT_INCOMING.getCode());
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.UNIONPAY.getName());
        }
        return queryAccessBankStatueRespDTO;
    }




}
