package com.tfjt.pay.external.unionpay.biz.impl;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.tf.user.api.DbShopService;
import com.tf.user.api.dto.resp.ShopDetailInfoRpcRespDto;
import com.tfjt.api.CheckUserPhoneRpcService;
import com.tfjt.api.TfSupplierApiService;
import com.tfjt.pay.external.unionpay.biz.DigitalUserBizService;
import com.tfjt.pay.external.unionpay.constants.NumberConstant;
import com.tfjt.pay.external.unionpay.dto.req.DigitalSelectReqDTO;
import com.tfjt.pay.external.unionpay.dto.resp.DigitalRespDTO;
import com.tfjt.pay.external.unionpay.entity.DigitalUserEntity;
import com.tfjt.pay.external.unionpay.enums.digital.*;
import com.tfjt.pay.external.unionpay.service.DigitalUserService;
import com.tfjt.supplier.dto.SupplierAddDTO;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Base64;
import java.util.Objects;

/**
 * @author songx
 * @Date: 2023/11/28/17:54
 * @Description:
 */
@Slf4j
@Component
public class DigitalUserBizServiceImpl implements DigitalUserBizService {

    @Value("${digital.union-pay-public-key}")
    private String unionPayPublicKey;

    @Value("${digital.pay-private-key}")
    private String payPrivateKey;

    @Resource
    private DigitalUserService digitalUserService;

    @DubboReference(retries = 1, timeout = 3000, check = false)
    private TfSupplierApiService tfSupplierApiService;

    @DubboReference(retries = 1, timeout = 3000, check = false)
    private CheckUserPhoneRpcService checkUserPhoneRpcService;

    @DubboReference(retries = 1, timeout = 3000, check = false)
    private DbShopService dbShopService;

    @Override
    public Result<DigitalRespDTO> selectByAccount(DigitalSelectReqDTO digitalSelectReqDTO) {
        String account = decryptStr(digitalSelectReqDTO.getMchntSideAccount());
        log.info("数字人民币查询账户参数,mchntSideAccount:{}", account);
        boolean exit = false;
        DigitalRespDTO respDTO = new DigitalRespDTO(DigitalTransactionStatusEnum.DIGITAL_SUCCESS);
        respDTO.setQueryType(digitalSelectReqDTO.getQueryType());
        respDTO.setKeySn(digitalSelectReqDTO.getKeySn());
        //供应商没有注册,查询是否商家是否注册,如果有有一个注册就返回银联成功状态
        try {
            com.tfjt.tfcommon.core.util.Result<Boolean> result = checkUserPhoneRpcService.checkUserPhone(account);
            log.info("调用接口查询数字人民币账户信息返回:{}",JSONObject.toJSONString(result));
            //接口调用失败 或者接口返回false,返回失败状态
            if(result.getCode()!=NumberConstant.ZERO || !result.getData()){
                return selectByAccountResult(false,respDTO);
            }
            Long shopId = dbShopService.getShopIdByMobile(account);
            log.info("查询数币店铺id信息:{}",shopId);
            ShopDetailInfoRpcRespDto shopDetailInfoRpcRespDto = dbShopService.searchShopDetailInfoById(shopId.intValue());
            log.info("查询数币店铺信息:{}",JSONObject.toJSONString(shopDetailInfoRpcRespDto));
            if(Objects.isNull(shopDetailInfoRpcRespDto) || StringUtils.isBlank(shopDetailInfoRpcRespDto.getCard()) ||
                StringUtils.isBlank(shopDetailInfoRpcRespDto.getName())){
                return selectByAccountResult(false,respDTO);
            }
            respDTO.setCertId(encryptBase64(shopDetailInfoRpcRespDto.getCard()));
            respDTO.setCustomerName(encryptBase64(shopDetailInfoRpcRespDto.getName()));
            respDTO.setCertType(DigitalCertTypeEnum.IT01.getCode());
        }catch (Exception e){
            log.error("查询手机是否注册dubbo异常:",e);
            respDTO.setBussReceiptStat(DigitalTransactionStatusEnum.DIGITAL_FAILED.getCode());
            return Result.ok(respDTO);
        }

        return selectByAccountResult(exit,respDTO);
    }

    private Result<DigitalRespDTO> selectByAccountResult(Boolean data, DigitalRespDTO respDTO) {
        respDTO.setMchntSideRegisterFlag(data ? DigitalCodeEnum.EF01.getCode()
                : DigitalCodeEnum.EF00.getCode());
        return Result.ok(respDTO);
    }

    @Override
    public Result<DigitalRespDTO> unbindWallet(DigitalUserEntity digitalUserEntity) {
        DigitalUserEntity old = digitalUserService.selectUserBySignContract(digitalUserEntity.getSignContract());
        if (Objects.isNull(old)){
            return Result.ok(new DigitalRespDTO(DigitalTransactionStatusEnum.DIGITAL_FAILED, DigitalErrorCodeEnum.R021));
        }
        old.setUnbindTime(DateUtil.date());
        old.setStatus(NumberConstant.ZERO);
        boolean result = digitalUserService.updateById(old);
        return Result.ok(new DigitalRespDTO(result?DigitalTransactionStatusEnum.DIGITAL_SUCCESS:DigitalTransactionStatusEnum.DIGITAL_FAILED));
    }

    @Lock4j(keys = "#digitalUserEntity.signContract",expire = 3000)
    @Override
    public Result<DigitalRespDTO> bindWallet(DigitalUserEntity digitalUserEntity) {
        String verifyType = digitalUserEntity.getVerifyType();
        //切换账号
        boolean isUpdate = DigitalCodeEnum.VT02.getCode().equals(verifyType);
        Long id = null;
        DigitalUserEntity old = digitalUserService.selectUserBySignContract(digitalUserEntity.getSignContract());
        if (isUpdate) {
            log.info("数字人民币修改之前数据:{}",JSONObject.toJSONString(old));
            //用户不存在
            if (Objects.isNull(old)) {
                return Result.ok(new DigitalRespDTO(DigitalTransactionStatusEnum.DIGITAL_FAILED));
            }
            id = old.getId();
        }else if (Objects.nonNull(old)){
            //如果之前有记录则不再更新
            return Result.ok(new DigitalRespDTO(DigitalTransactionStatusEnum.DIGITAL_SUCCESS));
        }
        digitalUserEntity.setMchntSideAccount(decryptStr(digitalUserEntity.getMchntSideAccount()));
        DigitalBankCodeEnum bank = DigitalBankCodeEnum.getByCode(digitalUserEntity.getOperatorId());
        digitalUserEntity.setOperatorName(Objects.isNull(bank) ? null : bank.getDesc());
        digitalUserEntity.setOperatorIcon(Objects.isNull(bank) ? null : bank.getIcon());
        digitalUserEntity.setCreateTime(DateUtil.date());
        digitalUserEntity.setUpdateTime(DateUtil.date());
        digitalUserEntity.setStatus(NumberConstant.ONE);
        digitalUserEntity.setId(id);
        boolean result = isUpdate ? digitalUserService.updateById(digitalUserEntity) : digitalUserService.save(digitalUserEntity);
        return Result.ok(new DigitalRespDTO(result ? DigitalTransactionStatusEnum.DIGITAL_SUCCESS : DigitalTransactionStatusEnum.DIGITAL_FAILED));
    }

    /**
     * 解密数字人民币的数据
     *
     * @param content 数字人民的密文
     * @return 解密之后的明文
     */
    private String decryptStr(String content) {
        RSA rsa = new RSA(payPrivateKey, null);
        log.info("数字人民币需要解密的密文:{}", content);
        String message = rsa.decryptStr(content, KeyType.PrivateKey);
        log.info("数字人民币解密之后的明文:{}", message);
        return message;
    }

    /**
     * 解密数字人民币的数据
     *
     * @param content 数字人民的密文
     * @return 解密之后的明文
     */
    private String encryptBase64(String content) {
        RSA rsa = new RSA(null, unionPayPublicKey);
        log.info("数字人民币加密之前的明文:{}", content);
        String message = rsa.encryptBase64(content, KeyType.PublicKey);
        log.info("数字人民币加密之后的密文:{}", message);
        return message;
    }
}
