package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.fms.business.dto.req.MerchantChangeReqDTO;
import com.tfjt.fms.data.insight.api.service.SupplierApiService;
import com.tfjt.pay.external.unionpay.api.dto.req.BusinessInfoReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingModuleStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.QueryAccessBankStatueReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryAccessBankStatueRespDTO;
import com.tfjt.pay.external.unionpay.biz.PabcBizService;
import com.tfjt.pay.external.unionpay.dto.req.MerchantChangeInfoMqReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.*;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.NetworkTypeCacheUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private TfIncomingMerchantInfoService tfIncomingMerchantInfoService;
    @Autowired
    private TfIncomingSettleInfoService tfIncomingSettleInfoService;
    @Autowired
    private TfIncomingBusinessInfoService tfIncomingBusinessInfoService;
    @Autowired
    private FaStandardLocationDictService faStandardLocationDictService;
    @Autowired
    private NetworkTypeCacheUtil networkTypeCacheUtil;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private TfBankCardInfoService tfBankCardInfoService;
    @DubboReference(retries = 0)
    private SupplierApiService supplierApiService;

    @Override
    public Result<List<PabcBankNameAndCodeRespDTO>> getBankInfoByName(String name) {
        return Result.ok(pabcPubAppparService.getBankInfoByName(name));
    }

    @Override
    public Result<List<PabcProvinceInfoRespDTO>> getProvinceList(String name) {
        return Result.ok(pabcPubPayNodeService.getProvinceList(name));
    }

    @Override
    public Result<List<PabcCityInfoRespDTO>> getCityList(String provinceCode, String bankCode) {
        if (StringUtils.isBlank(provinceCode) || StringUtils.isBlank(bankCode)) {
            throw new TfException(PayExceptionCodeEnum.QUERY_PARAM_IS_NOT_NULL);
        }
        return Result.ok(pabcPubPayCityService.getCityList(provinceCode, bankCode));
    }

    @Override
    public Result<List<PabcBranchBankInfoRespDTO>> getBranchBankInfo(String bankCode, String cityCode, String branchBankName) {
        if (StringUtils.isBlank(bankCode) || StringUtils.isBlank(cityCode)) {
            throw new TfException(PayExceptionCodeEnum.QUERY_PARAM_IS_NOT_NULL);
        }
        if (StringUtils.isNotBlank(cityCode)) {
            cityCode = cityCode.substring(0, 4);
        }
        List<PabcBranchBankInfoRespDTO> list = pabcPubPayBankaService.getBranchBankInfo(bankCode, cityCode, branchBankName);
        List<String> collect = list.stream().map(PabcBranchBankInfoRespDTO::getBankDreccode).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(collect)) {
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
        return Result.ok(networkTypeCacheUtil.getNetworkTypeCacheList(code));
    }

    @Override
    public Result<MoudleStatusRespDTO> getModuleStatus(IncomingModuleStatusReqDTO incomingModuleStatusReqDTO) {
        ValidatorUtils.validateEntity(incomingModuleStatusReqDTO);
        TfIncomingInfoEntity tfIncomingInfoEntity = tfIncomingInfoService.queryIncomingInfoByMerchant(incomingModuleStatusReqDTO);
        if (ObjectUtil.isNull(tfIncomingInfoEntity)) {
            throw new TfException(ExceptionCodeEnum.INCOMING_DATA_IS_NULL);
        }

        // 通过IdcardInfo服务查询IdcardInfo实体
        TfIncomingBusinessInfoEntity tfIncomingBusinessInfoEntity = tfIncomingBusinessInfoService.queryByIncomingId(tfIncomingInfoEntity.getId());
        // 通过MerchantInfo服务查询MerchantInfo实体
        TfIncomingMerchantInfoEntity tfIncomingMerchantInfoEntity = tfIncomingMerchantInfoService.queryByIncomingId(tfIncomingInfoEntity.getId());
        // 通过SettleInfo服务查询SettleInfo实体
        TfIncomingSettleInfoEntity tfIncomingSettleInfoEntity = tfIncomingSettleInfoService.queryByIncomingId(tfIncomingInfoEntity.getId());
        //创建返回对象
        MoudleStatusRespDTO moudleStatusRespDTO = new MoudleStatusRespDTO();
        moudleStatusRespDTO.setIncomingId(tfIncomingInfoEntity.getId());
        moudleStatusRespDTO.setAccessChannelType(tfIncomingInfoEntity.getAccessChannelType());
        moudleStatusRespDTO.setAccessMainType(tfIncomingInfoEntity.getAccessMainType());
        moudleStatusRespDTO.setAccessStatus(tfIncomingInfoEntity.getAccessStatus().byteValue());


        // 如果查询到IdcardInfo实体，则设置模块状态响应DTO的cardId
        if (ObjectUtil.isNotEmpty(tfIncomingMerchantInfoEntity)) {
            moudleStatusRespDTO.setMerchantId(tfIncomingMerchantInfoEntity.getId());
        }
        // 如果查询到MerchantInfo实体，则设置模块状态响应DTO的merchantId
        if (ObjectUtil.isNotEmpty(tfIncomingBusinessInfoEntity)) {
            moudleStatusRespDTO.setBusinessId(tfIncomingBusinessInfoEntity.getId());
        }
        // 如果查询到SettleInfo实体，则设置模块状态响应DTO的settleId
        if (ObjectUtil.isNotEmpty(tfIncomingSettleInfoEntity)) {
            moudleStatusRespDTO.setSettleId(tfIncomingSettleInfoEntity.getId());
            moudleStatusRespDTO.setSettlementAccountType(tfIncomingSettleInfoEntity.getSettlementAccountType());
            TfBankCardInfoEntity tfBankCardInfoEntity = tfBankCardInfoService.getById(tfIncomingSettleInfoEntity.getBankCardId());
            if (ObjectUtil.isNotEmpty(tfBankCardInfoEntity)) {
                moudleStatusRespDTO.setBankCardMobile(tfBankCardInfoEntity.getBankCardMobile());
            }
        }
        return Result.ok(moudleStatusRespDTO);
    }

    @Override
    public com.tfjt.dto.response.Result<String> saveChangeInfo(AsyncMessageEntity asyncMessage) {
        //供应商在需求完成时会全部走一遍平安银行的进件流程
        //供应商没有销售区域，供应商身份变更为经销商或者供应商身份添加了经销商身份则根据销售区域判断是否需要平安进件。
        //身份变更或者销售区域变更需要将数据记录下来。变更记录表放在fms系统里。
        String msgBody = asyncMessage.getMsgBody();
        MerchantChangeInfoMqReqDTO dto = JSONObject.parseObject(msgBody, MerchantChangeInfoMqReqDTO.class);
        if (dto != null) {
            Long supplierId = dto.getSupplierId();
            List<String> newSaleAreas = dto.getNewSaleAreas();
            List<String> oldSaleAreas = dto.getOldSaleAreas();
            List<Integer> newIdentifyList = dto.getNewIdentifyList();
            List<Integer> oldIdentifyList = dto.getOldIdentifyList();
            //销售区域或身份发生变更记录到fms系统内
            //判断销售区域是否发生了变更，true表示没有发生变更，false表示发生了变更
            Boolean saleFlag = checkListIsEquals(newSaleAreas, oldSaleAreas);
            //判断商户身份是否发生了变更，true表示没有发生变更，false表示发生了变更
            Boolean identityFlag = checkListIsEquals(newIdentifyList, oldIdentifyList);
            List<MerchantChangeReqDTO> saveList = getSaveList(newSaleAreas, newIdentifyList, oldSaleAreas, oldIdentifyList, dto, saleFlag, identityFlag);
            if (CollectionUtil.isNotEmpty(saveList)) {
                supplierApiService.saveMerchangtChangeInfo(saveList);
            }
            asyncService.dingWarning(supplierId, newIdentifyList, newSaleAreas, saleFlag, identityFlag, oldSaleAreas, oldIdentifyList);
        }
        return com.tfjt.dto.response.Result.ok();

    }

    @Override
    public Result<Integer> getNetworkTypeByAreaCode(List<String> code) {
        Integer cityType = CityTypeEnum.OLD_CITY.getCode();

        //获取全部新城区域
        List<String> cacheList = networkTypeCacheUtil.getAllNetworkTypeCacheList();
        //判断两个list是否有交集
        Set<String> codeSet = new HashSet<>(code);
        Set<String> cacheSet = new HashSet<>(cacheList);
        codeSet.retainAll(cacheSet);
        if (CollectionUtil.isEmpty(codeSet)) {
            //此时的销售区域只有老城即银联入网
            cityType = CityTypeEnum.OLD_CITY.getCode();
        }
        if (CollectionUtil.isNotEmpty(codeSet)) {
            //交集的区域小于销售区域说明既有老城，又有新城,此时走银联进件
            if (codeSet.size() < code.size()) {
                cityType = CityTypeEnum.OLD_CITY.getCode();
            }
            //交集的区域等于销售区域说明只有新城即平安入网，此时走平安进件
            if (codeSet.size() == code.size()) {
                cityType = CityTypeEnum.NEW_CITY.getCode();
            }
        }
        return Result.ok(cityType);
    }

    @Override
    public IncomingMessageRespDTO getIncomingInfo(BusinessInfoReqDTO businessInfoReqDTO) {
        LambdaQueryWrapper<TfIncomingInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfIncomingInfoEntity::getBusinessId, businessInfoReqDTO.getBuisnessId())
                .eq(TfIncomingInfoEntity::getBusinessType, businessInfoReqDTO.getBusinessType())
                .eq(TfIncomingInfoEntity::getAccessChannelType,CityTypeEnum.NEW_CITY.getCode())
                .eq(TfIncomingInfoEntity::getIsDeleted,DeleteStatusEnum.NO.getCode());
        TfIncomingInfoEntity one = tfIncomingInfoService.getOne(wrapper);
        IncomingMessageRespDTO respDTO = new IncomingMessageRespDTO();
        if (ObjectUtil.isNotNull(one)) {
            BeanUtils.copyProperties(one,respDTO);
        }
        return respDTO;
    }

    private List<MerchantChangeReqDTO> getSaveList(List<String> newSaleAreas, List<Integer> newIdentifyList, List<String> oldSaleAreas, List<Integer> oldIdentifyList, MerchantChangeInfoMqReqDTO dto, Boolean saleFlag, Boolean identityFlag) {


        List<MerchantChangeReqDTO> saveList = new ArrayList<>();
        if (!saleFlag) {
            MerchantChangeReqDTO reqDTO = new MerchantChangeReqDTO();
            String oldSales = getSalesByCodes(oldSaleAreas);
            String newSales = getSalesByCodes(newSaleAreas);
            reqDTO.setAfterChange(newSales);
            reqDTO.setBeforChange(oldSales);
            reqDTO.setChangeField("销售区域");
            reqDTO.setChangeTime(new Date());
            reqDTO.setOperator(dto.getUserName());
            reqDTO.setOperatorId(String.valueOf(dto.getCreator()));
            reqDTO.setMerchantId(dto.getSupplierId());
            saveList.add(reqDTO);

        }
        if (!identityFlag) {
            MerchantChangeReqDTO reqDTO = new MerchantChangeReqDTO();
            String oldIdentity = getIdentityByCode(oldIdentifyList);
            String newIdentity = getIdentityByCode(newIdentifyList);
            reqDTO.setAfterChange(newIdentity);
            reqDTO.setBeforChange(oldIdentity);
            reqDTO.setChangeField("身份");
            reqDTO.setChangeTime(new Date());
            reqDTO.setOperator(dto.getUserName());
            reqDTO.setOperatorId(String.valueOf(dto.getCreator()));
            reqDTO.setMerchantId(dto.getSupplierId());
            saveList.add(reqDTO);
        }
        return saveList;
    }

    private String getIdentityByCode(List<Integer> identifyList) {
        if (CollectionUtil.isNotEmpty(identifyList)) {
            StringBuilder sb = new StringBuilder();
            for (Integer identity : identifyList) {
                String msgByCode = getMsgByCode(identity);
                sb.append(msgByCode);
                sb.append(",");
            }
            String identityStr = sb.toString();
            return identityStr.substring(0, identityStr.length() - 1);
        }
        return "";
    }

    private String getMsgByCode(Integer code) {
        for (SupplierTypeEnum value : SupplierTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getDesc();
            }
        }
        return "";
    }

    private String getSalesByCodes(List<String> saleAreas) {
        List<String> areas = faStandardLocationDictService.getAreasByCode(saleAreas);
        if (CollectionUtil.isNotEmpty(areas)) {
            return String.join(",", areas);
        }
        return "";
    }

    private Boolean checkListIsEquals(List list, List list1) {
        if (CollectionUtil.isNotEmpty(list) && CollectionUtil.isNotEmpty(list1)) {
            Collections.sort(list);
            Collections.sort(list1);
            return list.toString().equals(list1.toString());
        } else if (CollectionUtil.isEmpty(list) && CollectionUtil.isEmpty(list1)) {
            return true;
        } else {
            return false;
        }

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
            queryAccessBankStatueRespDTO.setStatus(String.valueOf(IncomingStatusEnum.INCOMING.getCode()));
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.UNIONPAY_LOAN.getName());
        } else {
            queryAccessBankStatueRespDTO.setStatus(String.valueOf(IncomingStatusEnum.NOT_INCOMING.getCode()));
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
            String code = PabcUnionNetworkStatusMappingEnum.getMsg(one.getAccessStatus());
            queryAccessBankStatueRespDTO.setStatus(code);
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.PINGAN.getName());
            queryAccessBankStatueRespDTO.setMsg(one.getFailReason());
        } else {
            queryAccessBankStatueRespDTO.setStatus(String.valueOf(IncomingStatusEnum.NOT_INCOMING.getCode()));
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
            queryAccessBankStatueRespDTO.setStatus(one.getSigningStatus());
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.UNIONPAY.getName());
            queryAccessBankStatueRespDTO.setMsg(one.getMsg());
        } else {
            queryAccessBankStatueRespDTO.setStatus(String.valueOf(IncomingStatusEnum.NOT_INCOMING.getCode()));
            queryAccessBankStatueRespDTO.setNetworkChannel(IncomingAccessChannelTypeEnum.UNIONPAY.getName());
        }
        return queryAccessBankStatueRespDTO;
    }


}
