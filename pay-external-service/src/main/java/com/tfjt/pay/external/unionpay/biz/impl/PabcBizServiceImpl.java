package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.fms.business.dto.req.MerchantChangeReqDTO;
import com.tfjt.fms.data.insight.api.service.SupplierApiService;
import com.tfjt.pay.external.unionpay.api.dto.req.*;
import com.tfjt.pay.external.unionpay.api.dto.resp.AllSalesAreaRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.IncomingMessageRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.PayChannelRespDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryAccessBankStatueRespDTO;
import com.tfjt.pay.external.unionpay.biz.PabcBizService;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.constants.RedisConstant;
import com.tfjt.pay.external.unionpay.constants.UnionPayConstant;
import com.tfjt.pay.external.unionpay.dto.BusinessIsIncomingRespDTO;
import com.tfjt.pay.external.unionpay.dto.req.MerchantChangeInfoMqReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.ShopExamineMqReqDTO;
import com.tfjt.pay.external.unionpay.dto.req.ShopUpdateMqReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.*;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.NetworkTypeCacheUtil;
import com.tfjt.tfcommon.core.cache.RedisCache;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author zxy
 * @create 2023/12/9 16:05
 */
@Service
@Slf4j
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
    @Autowired
    private SalesAreaIncomingChannelService salesAreaIncomingChannelService;
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedisTemplate redisTemplate;
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
                .eq(TfIncomingInfoEntity::getAccessChannelType, CityTypeEnum.NEW_CITY.getCode())
                .eq(TfIncomingInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode());
        TfIncomingInfoEntity one = tfIncomingInfoService.getOne(wrapper);
        IncomingMessageRespDTO respDTO = new IncomingMessageRespDTO();
        if (ObjectUtil.isNotNull(one)) {
            BeanUtils.copyProperties(one, respDTO);
        }
        return respDTO;
    }

    @Override
    public List<PayChannelRespDTO> getAllSaleAreas(Integer areaLevel, String distinctName) {

        boolean blank = StringUtils.isBlank(distinctName);
        Object cacheObject = null;
        String redisKey = RedisConstant.SALE_AREA_KEY_PREFIX + areaLevel;
        if (blank) {
            cacheObject = redisCache.getCacheObject(redisKey);
        }
        List<PayChannelRespDTO> treeList;
        if (cacheObject == null || "[]".equals(cacheObject.toString())) {
            treeList = virtualAreaCode(areaLevel, distinctName);
            if (blank && !CollectionUtil.isEmpty(treeList)) {
                redisCache.setCacheObject(redisKey, JSON.toJSONString(treeList), 1, TimeUnit.HOURS);
            }
        } else {
            treeList = JSON.parseArray((String) cacheObject, PayChannelRespDTO.class);
        }
        return treeList;
    }

    @Override
    public List<AllSalesAreaRespDTO> getAllSaleAreas() {
        return salesAreaIncomingChannelService.getAllSaleAreas();
    }

    @Override
    public Boolean isIncomingByBusinessIdAndType(List<BusinessBasicInfoReqDTO> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return true;
        }
        boolean flag = true;
        List<BusinessIsIncomingRespDTO> businessList = new ArrayList<>();
        Map<String, BusinessIsIncomingRespDTO> incomingMap = new HashMap<>();
        Set<String> incomingCacheKeys = new HashSet<>();
        dtos.forEach(req -> {
            String cacheKey = RedisConstant.INCOMING_MSG_KEY_PREFIX + IncomingAccessChannelTypeEnum.PINGAN.getCode() + ":" +
                    req.getBusinessType() + ":" + req.getBusinessId();
            incomingCacheKeys.add(cacheKey);
            BusinessIsIncomingRespDTO businessIsIncomingRespDTO = BusinessIsIncomingRespDTO.builder()
                    .businessType(req.getBusinessType().byteValue())
                    .businessId(req.getBusinessId()).build();
            incomingMap.put(req.getBusinessId() + "-" + req.getBusinessType(), businessIsIncomingRespDTO);
        });

        //批量查询Redis
        List<com.alibaba.fastjson.JSONObject> incomingChannels = redisTemplate.opsForValue().multiGet(incomingCacheKeys);
        for (com.alibaba.fastjson.JSONObject json : incomingChannels) {
            if (ObjectUtils.isEmpty(json)) {
                flag = false;
                continue;
            }
            IncomingMessageRespDTO incomingMessageRespDTO = com.alibaba.fastjson.JSONObject.toJavaObject(json, IncomingMessageRespDTO.class);
            if (StringUtils.isBlank(incomingMessageRespDTO.getAccountNo())) {
                flag = false;
                continue;
            }
            BusinessIsIncomingRespDTO businessIsIncomingRespDTO = BusinessIsIncomingRespDTO.builder()
                    .businessType(incomingMessageRespDTO.getBusinessType().byteValue())
                    .businessId(incomingMessageRespDTO.getBusinessId())
                    .accountNo(incomingMessageRespDTO.getAccountNo()).build();
            incomingMap.put(incomingMessageRespDTO.getBusinessId() + "-" + incomingMessageRespDTO.getBusinessType(), businessIsIncomingRespDTO);
        }
//        for (Map.Entry<String, BusinessIsIncomingRespDTO> entry : incomingMap.entrySet()) {
//            businessList.add(entry.getValue());
//        }

//        List<BusinessIsIncomingRespDTO> businessList =  tfIncomingInfoService.isIncomingByBusinessIdAndType(dtos);
//
//        if (CollectionUtil.isEmpty(businessList) || dtos.size() != businessList.size()) {
//            flag =  false;
//        }
//        if (flag && businessList.stream().anyMatch(item->StringUtils.isBlank(item.getAccountNo()))){
//            flag =  false;
//        }
        if (!flag) {
            //钉钉报警
            asyncService.dingWarningNew(dtos, incomingMap);
        }
        return flag;
    }

    @Override
    public void saveShopExamineInfo(ShopExamineMqReqDTO dto) {
        List<MerchantChangeReqDTO> saveList = getShopExamineInfoList(dto);
        if (CollectionUtil.isNotEmpty(saveList)) {
            supplierApiService.saveMerchangtChangeInfo(saveList);
        }
    }

    @Override
    public void saveShopUpdateInfo(ShopUpdateMqReqDTO dto) {
        List<MerchantChangeReqDTO> saveList = getShopUpdateInfoList(dto);
        if (CollectionUtil.isNotEmpty(saveList)) {
            supplierApiService.saveMerchangtChangeInfo(saveList);
        }
    }

    @Override
    public Result<String> insertArea(SaleAreaInsertReqDTO dto) {
        String channelCode = dto.getChannelCode();
        if (StringUtils.isEmpty(channelCode)) {
            throw new TfException(ExceptionCodeEnum.CHANNEL_CODE_CAN_NOT_NULL);
        }
        dto.setChannelName(IncomingChannelEnum.getNameFromCode(dto.getChannelCode()));
        //
        List<SalesAreaIncomingChannelEntity> list = salesAreaIncomingChannelService.list();
        List<AreaInfoReqDTO> areaList = dto.getAreaList();
        if (CollectionUtil.isEmpty(areaList)) {
            throw new TfException(ExceptionCodeEnum.AREA_CAN_NOT_NULL);
        }
        List<SalesAreaIncomingChannelEntity> entities = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> districtsCollect = list.stream().map(SalesAreaIncomingChannelEntity::getDistrictsCode).collect(Collectors.toList());
            //判断区域是否存在
            for (AreaInfoReqDTO areaInfoReqDTO : areaList) {
                if (!districtsCollect.contains(areaInfoReqDTO.getDistrictsCode())) {
                    SalesAreaIncomingChannelEntity entity = new SalesAreaIncomingChannelEntity();
                    BeanUtils.copyProperties(areaInfoReqDTO, entity);
                    entity.setArea(areaInfoReqDTO.getProvince() + "/" + areaInfoReqDTO.getCity() + "/" + areaInfoReqDTO.getDistricts());
                    entity.setChannel(dto.getChannelName());
                    entity.setChannelCode(dto.getChannelCode());
                    entity.setCreateUser(dto.getUserName());
                    entity.setCreateUserId(dto.getUserId());
                    entity.setUpdateUser(dto.getUserName());
                    entity.setUpdateUserId(dto.getUserId());
                    entities.add(entity);
                    keys.add(RedisConstant.NETWORK_TYPE_BY_AREA_CODE+areaInfoReqDTO.getDistrictsCode());
                }
            }
        } else {
            for (AreaInfoReqDTO areaInfoReqDTO : areaList) {
                SalesAreaIncomingChannelEntity entity = new SalesAreaIncomingChannelEntity();
                BeanUtils.copyProperties(areaInfoReqDTO, entity);
                entity.setArea(areaInfoReqDTO.getProvince() + "/" + areaInfoReqDTO.getCity() + "/" + areaInfoReqDTO.getDistricts());
                entity.setChannel(dto.getChannelName());
                entity.setChannelCode(dto.getChannelCode());
                entity.setCreateUser(dto.getUserName());
                entity.setCreateUserId(dto.getUserId());
                entity.setUpdateUser(dto.getUserName());
                entity.setUpdateUserId(dto.getUserId());
                entities.add(entity);
                keys.add(RedisConstant.NETWORK_TYPE_BY_AREA_CODE+areaInfoReqDTO.getDistrictsCode());
            }
        }
        String msg = "";

        if (entities.size() != areaList.size()) {
            if (entities.size() == 0) {
               throw new TfException(ExceptionCodeEnum.AREA_REPEAT);
            } else {
                msg = "保存成功，已配置的不重复入库";
            }
        } else {
            msg = "保存成功";
        }
        if (entities.size() > 0) {
            salesAreaIncomingChannelService.saveBatch(entities);
            //清除缓存
            redisCache.deleteObject(RedisConstant.NETWORK_TYPE_BY_AREA_CODE_All);
            networkTypeCacheUtil.getAllNetworkTypeCacheList();
            for (String key : keys) {
                redisCache.deleteObject(key);
            }
        }
        return Result.ok(msg);
    }

    private List<MerchantChangeReqDTO> getShopUpdateInfoList(ShopUpdateMqReqDTO dto) {
        List<MerchantChangeReqDTO> saveList = new ArrayList<>();
        if (StringUtils.isNotBlank(dto.getAfterDistractCode())) {
            MerchantChangeReqDTO reqDTO = new MerchantChangeReqDTO();
            List<String> oldSalesList = new ArrayList<>();
            List<String> newSalesList = new ArrayList<>();
            oldSalesList.add(dto.getBeforeDistractCode());
            newSalesList.add(dto.getAfterDistractCode());
            String oldSales = getSalesByCodes(oldSalesList);
            String newSales = getSalesByCodes(newSalesList);
            reqDTO.setAfterChange(newSales);
            reqDTO.setBeforChange(oldSales);
            reqDTO.setChangeField("销售区域");
            reqDTO.setChangeTime(new Date());
            reqDTO.setOperator(dto.getOperatorName());
            reqDTO.setOperatorId(dto.getOperator());
            reqDTO.setMerchantId(dto.getShopId());
            reqDTO.setMerchantType(NumberConstant.ONE);
            saveList.add(reqDTO);
        }
        return saveList;
    }

    private List<MerchantChangeReqDTO> getShopExamineInfoList(ShopExamineMqReqDTO dto) {
        List<MerchantChangeReqDTO> saveList = new ArrayList<>();
        String shopName = dto.getShopName();
        String phone = dto.getPhone();
        String afterDistractCode = dto.getAfterDistractCode();
        String operator = dto.getOperatorName();
        String operatorId = dto.getOperatorId();
        Long shopId = dto.getShopId();
        String beforeDistractCode = dto.getBeforeDistractCode();
        if (StringUtils.isNotBlank(shopName)) {
            MerchantChangeReqDTO reqDTO = new MerchantChangeReqDTO();
            reqDTO.setAfterChange(shopName);
            reqDTO.setChangeField("商铺名称");
            reqDTO.setChangeTime(new Date());
            reqDTO.setOperator(operator);
            reqDTO.setOperatorId(operatorId);
            reqDTO.setMerchantId(shopId);
            reqDTO.setMerchantType(NumberConstant.ONE);
            saveList.add(reqDTO);
        }
        if (StringUtils.isNotBlank(phone)) {
            MerchantChangeReqDTO reqDTO = new MerchantChangeReqDTO();
            reqDTO.setAfterChange(phone);
            reqDTO.setChangeField("联系电话");
            reqDTO.setChangeTime(new Date());
            reqDTO.setOperator(operator);
            reqDTO.setOperatorId(operatorId);
            reqDTO.setMerchantId(shopId);
            reqDTO.setMerchantType(NumberConstant.ONE);
            saveList.add(reqDTO);
        }
        if (StringUtils.isNotBlank(afterDistractCode)) {
            MerchantChangeReqDTO reqDTO = new MerchantChangeReqDTO();
            List<String> newSalesList = new ArrayList<>();
            List<String> oldSalesList = new ArrayList<>();
            newSalesList.add(afterDistractCode);
            if (StringUtils.isNotBlank(beforeDistractCode)) {
                oldSalesList.add(beforeDistractCode);
                String oldSales = getSalesByCodes(oldSalesList);
                reqDTO.setBeforChange(oldSales);
            }
            String newSales = getSalesByCodes(newSalesList);
            reqDTO.setAfterChange(newSales);
            reqDTO.setChangeField("销售区域");
            reqDTO.setChangeTime(new Date());
            reqDTO.setOperator(operator);
            reqDTO.setOperatorId(operatorId);
            reqDTO.setMerchantId(shopId);
            reqDTO.setMerchantType(NumberConstant.ONE);
            saveList.add(reqDTO);
        }

        return saveList;
    }

    private List<PayChannelRespDTO> virtualAreaCode(Integer areaLevel, String distinctName) {
        QueryWrapper<SalesAreaIncomingChannelEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(distinctName)) {
            wrapper.and((w) -> {
                switch (areaLevel) {
                    case 3:
                        w.like("districts", distinctName).or();
                    case 2:
                        w.like("city", distinctName).or();
                    default:
                        w.like("province", distinctName);
                }
            });
        }
        if (areaLevel == 1) {
            wrapper.select("distinct province_code,province").orderByAsc("province_code");
        } else if (areaLevel == 2) {
            wrapper.select("distinct province_code,province, city_code,city").orderByAsc("city_code");
        } else {
            wrapper.orderByAsc("districts_code");
        }
        List<SalesAreaIncomingChannelEntity> list = salesAreaIncomingChannelService.list(wrapper);
        //解析成树形结构
        List<PayChannelRespDTO> rootList = new ArrayList<>();
        for (SalesAreaIncomingChannelEntity district : list) {
            //获取省
            List<PayChannelRespDTO> childrenCityList = getChildrenList(district.getProvinceCode(), district.getProvince(), rootList, areaLevel != 1);
            //获取市
            if (areaLevel > 1) {
                List<PayChannelRespDTO> childrenDistrictList = getChildrenList(district.getCityCode(), district.getCity(), childrenCityList, areaLevel != 2);
                if (areaLevel > 2) {
                    getChildrenList(district.getDistrictsCode(), district.getDistricts(), childrenDistrictList, false);
                }
            }
        }
        return rootList;
    }

    private List<PayChannelRespDTO> getChildrenList(String code, String name, List<PayChannelRespDTO> rootList, boolean addChildrenList) {
        int i = binarySearch(rootList, code);
        PayChannelRespDTO faStandardLocationDictDTO;
        if (i < 0) {
            faStandardLocationDictDTO = new PayChannelRespDTO(code, name, addChildrenList ? new ArrayList<>() : null);
            rootList.add(faStandardLocationDictDTO);
        } else {
            faStandardLocationDictDTO = rootList.get(i);
        }
        return faStandardLocationDictDTO.getChildrenList();
    }

    private int binarySearch(List<PayChannelRespDTO> list, String code) {
        if (list.size() == 0) {
            return -1;
        }
        int low = 0;
        int high = list.size() - 1;
        // 当low小于等于high时，继续查找
        while (low <= high) {
            // 计算中间位置
            int mid = (low + high) / 2;
            // 获取中间位置的值
            PayChannelRespDTO midVal = list.get(mid);
            // 比较中间位置的值和查找值的大小
            int cmp = midVal.getId().compareTo(code);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        // 如果查找值不存在于List中，则返回插入位置的负数形式
        return -(low + 1);
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
            reqDTO.setMerchantType(NumberConstant.TWO);
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
            reqDTO.setMerchantType(NumberConstant.TWO);
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
        if (CollectionUtil.isNotEmpty(saleAreas)) {
            List<String> areas = faStandardLocationDictService.getAreasByCode(saleAreas);
            if (CollectionUtil.isNotEmpty(areas)) {
                return String.join(",", areas);
            }
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
            queryAccessBankStatueRespDTO.setNetworkChannel(UnionPayConstant.UNIONPAY_LOAN_CHANNEL_NAME);
        } else {
            queryAccessBankStatueRespDTO.setStatus(String.valueOf(IncomingStatusEnum.NOT_INCOMING.getCode()));
            queryAccessBankStatueRespDTO.setNetworkChannel(UnionPayConstant.UNIONPAY_LOAN_CHANNEL_NAME);
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
        TfIncomingInfoEntity one = tfIncomingInfoService.getOne(new LambdaQueryWrapper<TfIncomingInfoEntity>().eq(TfIncomingInfoEntity::getBusinessId, businessId).eq(TfIncomingInfoEntity::getAccessChannelType, IncomingAccessChannelTypeEnum.PINGAN.getCode()).eq(TfIncomingInfoEntity::getAccessType, IncomingAccessTypeEnum.COMMON.getCode()).eq(TfIncomingInfoEntity::getIsDeleted, DeleteStatusEnum.NO.getCode()));
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
