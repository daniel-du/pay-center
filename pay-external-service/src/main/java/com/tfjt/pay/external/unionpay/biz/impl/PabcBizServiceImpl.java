package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.fms.business.dto.req.MerchantChangeReqDTO;
import com.tfjt.fms.data.insight.api.service.SupplierApiService;
import com.tfjt.pay.external.unionpay.biz.PabcBizService;
import com.tfjt.pay.external.unionpay.dto.req.MerchantChangeInfoMqReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.QueryAccessBankStatueReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.*;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.NetworkTypeCacheUtil;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.dto.response.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
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
    private SalesAreaIncomingChannelService salesAreaIncomingChannelService;
    @Autowired
    private TfIdcardInfoService tfIdcardInfoService;
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
    @DubboReference
    private SupplierApiService supplierApiService;

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
        List<String> cacheList = networkTypeCacheUtil.getNetworkTypeCacheList();
        int status;
        if (cacheList.contains(code)) {
            //新城
            status = CityTypeEnum.NEW_CITY.getCode();
        } else {
            //老城
            status = CityTypeEnum.OLD_CITY.getCode();
        }
        return Result.ok(status);
    }

    @Override
    public Result<MoudleStatusRespDTO> getModuleStatus(Long incomingId) {
        //创建返回对象
        MoudleStatusRespDTO moudleStatusRespDTO = new MoudleStatusRespDTO();
        //根据入网id查询身份信息、营业信息、结算信息
        if (ObjectUtil.isNull(incomingId)) {
            throw new TfException(PayExceptionCodeEnum.QUERY_PARAM_IS_NOT_NULL);
        }
        // 创建IdcardInfo实体查询对象
        LambdaQueryWrapper<TfIncomingBusinessInfoEntity> businessInfoEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 创建MerchantInfo实体查询对象
        LambdaQueryWrapper<TfIncomingMerchantInfoEntity> merchantInfoEntityQueryWrapper = new LambdaQueryWrapper<>();
        // 创建SettleInfo实体查询对象
        LambdaQueryWrapper<TfIncomingSettleInfoEntity> settleInfoEntityQueryWrapper = new LambdaQueryWrapper<>();

        // 根据incomingId和未删除状态筛选IdcardInfo实体
        businessInfoEntityLambdaQueryWrapper.eq(TfIncomingBusinessInfoEntity::getIncomingId, incomingId).eq(TfIncomingBusinessInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        // 根据incomingId和未删除状态筛选MerchantInfo实体
        merchantInfoEntityQueryWrapper.eq(TfIncomingMerchantInfoEntity::getIncomingId, incomingId).eq(TfIncomingMerchantInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        // 根据incomingId和未删除状态筛选SettleInfo实体
        settleInfoEntityQueryWrapper.eq(TfIncomingSettleInfoEntity::getIncomingId, incomingId).eq(TfIncomingSettleInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        // 通过IdcardInfo服务查询IdcardInfo实体
        TfIncomingBusinessInfoEntity tfIncomingBusinessInfoEntity = tfIncomingBusinessInfoService.getOne(businessInfoEntityLambdaQueryWrapper);
        // 通过MerchantInfo服务查询MerchantInfo实体
        TfIncomingMerchantInfoEntity tfIncomingMerchantInfoEntity = tfIncomingMerchantInfoService.getOne(merchantInfoEntityQueryWrapper);
        // 通过SettleInfo服务查询SettleInfo实体
        TfIncomingSettleInfoEntity tfIncomingSettleInfoEntity = tfIncomingSettleInfoService.getOne(settleInfoEntityQueryWrapper);

        // 如果查询到IdcardInfo实体，则设置模块状态响应DTO的cardId
        if (null != tfIncomingMerchantInfoEntity) {
            moudleStatusRespDTO.setCardId(tfIncomingMerchantInfoEntity.getId());
        }
        // 如果查询到MerchantInfo实体，则设置模块状态响应DTO的merchantId
        if (null != tfIncomingBusinessInfoEntity) {
            moudleStatusRespDTO.setMerchantId(tfIncomingBusinessInfoEntity.getId());
        }
        // 如果查询到SettleInfo实体，则设置模块状态响应DTO的settleId
        if (null != tfIncomingSettleInfoEntity) {
            moudleStatusRespDTO.setSettleId(tfIncomingSettleInfoEntity.getId());
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
        if (dto!= null) {
            Long supplierId = dto.getSupplierId();
            List<String> newSaleAreas = dto.getNewSaleAreas();
            List<String> oldSaleAreas = dto.getOldSaleAreas();
            List<Integer> newIdentifyList = dto.getNewIdentifyList();
            List<Integer> oldIdentifyList = dto.getOldIdentifyList();
            //销售区域或身份发生变更记录到fms系统内
            List<MerchantChangeReqDTO> saveList = getSaveList(newSaleAreas,newIdentifyList,oldSaleAreas,oldIdentifyList,dto);
            if (CollectionUtil.isNotEmpty(saveList)) {
                supplierApiService.saveMerchangtChangeInfo(saveList);
            }
            //钉钉报警
            dingWarning(supplierId,newIdentifyList,newSaleAreas);
        }
        return com.tfjt.dto.response.Result.ok();

    }

    private void dingWarning(Long supplierId, List<Integer> newIdentifyList, List<String> newSaleAreas) {
        //查询出当前用户的进件信息，如果当前用户既在银联进件了又在平安进件了，则不会触发钉钉报警。
        //查询银联进件信息
        //银联进件查询
        QueryAccessBankStatueRespDTO unionIncoming = getUnionIncoming(null, String.valueOf(supplierId));
        //平安进件查询
        QueryAccessBankStatueRespDTO pabcIncoming = getPabcIncoming(String.valueOf(supplierId));
        //判断用户身份，供应商必须要银联和平安都进件，经销商需要根据销售区域进行判断
        if (newIdentifyList.contains(SupplierTypeEnum.SUPPLIER.getCode())) {
            if (IncomingStatusEnum.NOT_INCOMING.getCode().equals(unionIncoming.getStatus())) {
                //todo 钉钉报警，银联进件通知
            }
            if (IncomingStatusEnum.NOT_INCOMING.getCode().equals(pabcIncoming.getStatus())) {
                //todo 钉钉报警，平安进件通知
            }
        }else if (newIdentifyList.contains(SupplierTypeEnum.DEALER.getCode())) {
            //判断经销商的销售区域是否包含新城
            //查询新城地区code集合
            List<SalesAreaIncomingChannelEntity> list = salesAreaIncomingChannelService.list();
            List<String> pabcDistrictsCode = list.stream().map(SalesAreaIncomingChannelEntity::getDistrictsCode).collect(Collectors.toList());
            //判断新城地区code集合是否包含当前销售城市code
            boolean newCityFlag = pabcDistrictsCode.stream().anyMatch(e -> contains(newSaleAreas, e));
            if (newCityFlag && IncomingStatusEnum.NOT_INCOMING.getCode().equals(pabcIncoming.getStatus())) {
                //todo 此时触发钉钉报警，平安进件通知
            }
            //此时用户还在旧城
            if (!newCityFlag && IncomingStatusEnum.NOT_INCOMING.getCode().equals(unionIncoming.getStatus())){
                //todo 此时触发钉钉报警，银联进件通知
            }
        }
    }

    private List<MerchantChangeReqDTO> getSaveList(List<String> newSaleAreas, List<Integer> newIdentifyList, List<String> oldSaleAreas, List<Integer> oldIdentifyList, MerchantChangeInfoMqReqDTO dto) {

        //判断销售区域是否发生了变更，true表示没有发生变更，false表示发生了变更
        Boolean saleFlag = checkListIsEquals(newSaleAreas, oldSaleAreas);
        //判断商户身份是否发生了变更，true表示没有发生变更，false表示发生了变更
        Boolean identityFlag = checkListIsEquals(newIdentifyList, oldIdentifyList);
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
            saveList.add(reqDTO);
        }
        return saveList;
    }

    private String getIdentityByCode(List<Integer> identifyList) {
        if (CollectionUtil.isNotEmpty(identifyList)){
            StringBuilder sb = new StringBuilder();
            for (Integer identity : identifyList) {
                String msgByCode = getMsgByCode(identity);
                sb.append(msgByCode);
                sb.append(",");
            }
            String identityStr = sb.toString();
            return identityStr.substring(0, identityStr.length() - 1);
        }
        return null;
    }

    private String getMsgByCode(Integer code){
        for (SupplierTypeEnum value : SupplierTypeEnum.values()) {
            if (value.getCode().equals(code)){
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
        return null;
    }

    private boolean contains(List<String> cus, String value){
        return cus.stream().filter(f -> f.equals(value)).findAny().isPresent();
    }




    private Boolean checkListIsEquals(List list,List list1){
        if (CollectionUtil.isNotEmpty(list) && CollectionUtil.isNotEmpty(list1)) {
            Collections.sort(list);
            Collections.sort(list1);
            return list.toString().equals(list1.toString());
        }else if (CollectionUtil.isEmpty(list) && CollectionUtil.isEmpty(list1)) {
            return true;
        }else {
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
