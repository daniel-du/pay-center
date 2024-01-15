package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfjt.entity.AsyncMessageEntity;
import com.tfjt.fms.business.dto.req.MerchantChangeReqDTO;
import com.tfjt.fms.data.insight.api.service.SupplierApiService;
import com.tfjt.pay.external.unionpay.api.dto.req.IncomingModuleStatusReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.resp.QueryAccessBankStatueRespDTO;
import com.tfjt.pay.external.unionpay.biz.PabcBizService;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dto.req.MerchantChangeInfoMqReqDTO;
import com.tfjt.pay.external.unionpay.api.dto.req.QueryAccessBankStatueReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.*;
import com.tfjt.pay.external.unionpay.entity.*;
import com.tfjt.pay.external.unionpay.enums.*;
import com.tfjt.pay.external.unionpay.service.*;
import com.tfjt.pay.external.unionpay.utils.NetworkTypeCacheUtil;
import com.tfjt.robot.common.message.ding.MarkdownMessage;
import com.tfjt.robot.service.dingtalk.DingRobotService;
import com.tfjt.tfcommon.core.exception.TfException;
import com.tfjt.tfcommon.core.validator.ValidatorUtils;
import com.tfjt.tfcommon.dto.response.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Autowired
    private TfBankCardInfoService tfBankCardInfoService;
    @Resource
    private DingRobotService dingRobotService;
    @DubboReference
    private SupplierApiService supplierApiService;
    @Value("${dingding.incoming.accessToken}")
    private String accessToken;
    @Value("${dingding.incoming.encryptKey}")
    private String encryptKey;

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
    public Result<MoudleStatusRespDTO> getModuleStatus(IncomingModuleStatusReqDTO incomingModuleStatusReqDTO) {
        ValidatorUtils.validateEntity(incomingModuleStatusReqDTO);
        TfIncomingInfoEntity tfIncomingInfoEntity = tfIncomingInfoService.queryIncomingInfoByMerchant(incomingModuleStatusReqDTO);
        if (ObjectUtil.isNull(tfIncomingInfoEntity)) {
            throw new TfException(ExceptionCodeEnum.INCOMING_DATA_IS_NULL);
        }
        //创建返回对象
        MoudleStatusRespDTO moudleStatusRespDTO = new MoudleStatusRespDTO();
        moudleStatusRespDTO.setIncomingId(tfIncomingInfoEntity.getId());
        moudleStatusRespDTO.setAccessChannelType(tfIncomingInfoEntity.getAccessChannelType());
        moudleStatusRespDTO.setAccessMainType(tfIncomingInfoEntity.getAccessMainType());
        moudleStatusRespDTO.setAccessStatus(tfIncomingInfoEntity.getAccessStatus().byteValue());
        // 通过IdcardInfo服务查询IdcardInfo实体
        TfIncomingBusinessInfoEntity tfIncomingBusinessInfoEntity = tfIncomingBusinessInfoService.queryByIncomingId(tfIncomingInfoEntity.getId());
        // 通过MerchantInfo服务查询MerchantInfo实体
        TfIncomingMerchantInfoEntity tfIncomingMerchantInfoEntity = tfIncomingMerchantInfoService.queryByIncomingId(tfIncomingInfoEntity.getId());
        // 通过SettleInfo服务查询SettleInfo实体
        TfIncomingSettleInfoEntity tfIncomingSettleInfoEntity = tfIncomingSettleInfoService.queryByIncomingId(tfIncomingInfoEntity.getId());


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
        if (dto!= null) {
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
            List<MerchantChangeReqDTO> saveList = getSaveList(newSaleAreas,newIdentifyList,oldSaleAreas,oldIdentifyList,dto,saleFlag,identityFlag);
            if (CollectionUtil.isNotEmpty(saveList)) {
                supplierApiService.saveMerchangtChangeInfo(saveList);
            }
            //钉钉报警
            dingWarning(supplierId,newIdentifyList,newSaleAreas,saleFlag,identityFlag,oldSaleAreas,oldIdentifyList);
        }
        return com.tfjt.dto.response.Result.ok();

    }

    @Override
    public Result<Integer> getNetworkTypeByAreaCode(List<String> code) {
        Integer cityType = CityTypeEnum.OLD_CITY.getCode();

        //获取全部新城区域
        List<String> cacheList = networkTypeCacheUtil.getNetworkTypeCacheList();
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

    public static void main(String[] args) {

        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list2 = Arrays.asList(4, 5, 6, 7, 8);

        Set<Integer> set1 = new HashSet<>(list1);
        Set<Integer> set2 = new HashSet<>(list2);
        set1.retainAll(set2); // 修改set1为它与set2的交集

        List<Integer> intersection = new ArrayList<>(set1); // 转换回List形式
        System.out.println(intersection);

    }

    private void dingWarning(Long supplierId, List<Integer> newIdentifyList, List<String> newSaleAreas, Boolean saleFlag, Boolean identityFlag, List<String> oldSaleAreas, List<Integer> oldIdentifyList) {
        //查询出当前用户的进件信息，如果当前用户既在银联进件了又在平安进件了，则不会触发钉钉报警。
        //查询银联进件信息
        //银联进件查询
        QueryAccessBankStatueRespDTO unionIncoming = getUnionIncoming(null, String.valueOf(supplierId));
        //平安进件查询
        QueryAccessBankStatueRespDTO pabcIncoming = getPabcIncoming(String.valueOf(supplierId));
        //判断用户身份，供应商必须要银联和平安都进件，经销商需要根据销售区域进行判断
        //供应商钉钉报警不考虑销售区域变更
        Result<String> result = supplierApiService.getSupplierNameBySupplierId(supplierId);
        String supplierName = null;
        int code = result.getCode();
        if (code == NumberConstant.ZERO) {
            supplierName = result.getData();
        }
        if (newIdentifyList.contains(SupplierTypeEnum.SUPPLIER.getCode())) {
            //身份变更
            if (!identityFlag) {
                String title = "商户身份变更";
                String oldIdentity = getIdentityByCode(oldIdentifyList);
                String newIdentity = getIdentityByCode(newIdentifyList);
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append("时间：" + DateUtil.format(new Date(), DatePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()) + "\n\n");
                msgBuilder.append("商户名称:"+supplierName+"\n\n");
                msgBuilder.append("商户ID:"+supplierId+"\n\n");
                msgBuilder.append("变更内容:商户身份由"+oldIdentity+"变更为"+newIdentity+"，请尽快【<font color=#FF0000>%s</font>】进件。");
                if (IncomingStatusEnum.NOT_INCOMING.getCode().equals(unionIncoming.getStatus())) {
                    String msg = msgBuilder.toString();
                    msg = String.format(msg, "银联");
                    sendMessage(title,msg);
                }
                if (IncomingStatusEnum.NOT_INCOMING.getCode().equals(pabcIncoming.getStatus())) {
                    String msg = msgBuilder.toString();
                    msg = String.format(msg, "平安");
                    sendMessage(title,msg);
                }
            }
        }
        if (newIdentifyList.contains(SupplierTypeEnum.DEALER.getCode())) {
            //判断经销商的销售区域是否包含新城
            //销售区域变更
            if (!saleFlag) {
                //查询新城地区code集合
                String oldSales = getSalesByCodes(oldSaleAreas);
                String newSales = getSalesByCodes(newSaleAreas);
                List<SalesAreaIncomingChannelEntity> list = salesAreaIncomingChannelService.list();
                List<String> pabcDistrictsCode = list.stream().map(SalesAreaIncomingChannelEntity::getDistrictsCode).collect(Collectors.toList());
                //判断新城地区code集合是否包含当前销售城市code
                boolean newCityFlag = pabcDistrictsCode.stream().anyMatch(e -> contains(newSaleAreas, e));
                String title = "销售区域变更";
                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append("时间：" + DateUtil.format(new Date(), DatePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()) + "\n\n");
                msgBuilder.append("商户名称:"+supplierName+"\n\n");
                msgBuilder.append("商户ID:"+supplierId+"\n\n");
                msgBuilder.append("变更内容：\n\n老销售区域:"+oldSales+";\n\n新销售区域:"+newSales+"，请尽快【<font color=#FF0000>%s</font>】进件。");
                if (newCityFlag && IncomingStatusEnum.NOT_INCOMING.getCode().equals(pabcIncoming.getStatus())) {
                    String msg = msgBuilder.toString();
                    msg = String.format(msg, "平安");
                    sendMessage(title,msg);
                }
                //此时用户还在旧城
                if (!newCityFlag && IncomingStatusEnum.NOT_INCOMING.getCode().equals(unionIncoming.getStatus())){
                    String msg = msgBuilder.toString();
                    msg = String.format(msg, "银联");
                    sendMessage(title,msg);
                }
            }
        }

    }

    private void sendMessage(String title,String msg) {

        MarkdownMessage message = MarkdownMessage.buildBizFree(title, msg);
        dingRobotService.send(message, accessToken, true, encryptKey);
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
