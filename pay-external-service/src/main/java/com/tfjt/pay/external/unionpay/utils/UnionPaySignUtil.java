package com.tfjt.pay.external.unionpay.utils;

import com.alibaba.fastjson.JSON;
import com.tfjt.pay.external.unionpay.config.UnionPayCertificateConfig;
import com.tfjt.pay.external.unionpay.dto.UnionPayLoansBaseReq;
import com.tfjt.pay.external.unionpay.enums.PayExceptionCodeEnum;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.*;

import static com.tfjt.pay.external.unionpay.utils.SftpUtil.ALGORITHM_SHA256WITHRSA;

/**
 * @Author: suxiaochang
 * @date: 2022/11/7
 */
@Slf4j
public class UnionPaySignUtil {



    /**
     * 证书类型，值为{@value}
     */
    public static final String CERT_TYPE_PKCS12 = "PKCS12";

    private static KeyStore keyStore = null;//签名证书
    /**
     * 证书类型，值为{@value}
     */
    public static final String CERT_TYPE_JKS = "JKS";

    public static byte[] sign(byte[] rawData, PrivateKey privateKey, String algorithm) {
        try {
            Signature e = Signature.getInstance(algorithm);
            e.initSign(privateKey);
            e.update(rawData);
            return e.sign();
        } catch (Exception e) {
            log.error("签名失败",e);
            return null;
        }
    }

    /**
     * 从私钥证书中获取PKCS12标准私钥<br/>
     *
     * @param keyFileIn 私钥文件流 .pfx
     * @param keypwd    证书密码
     * @return：私钥
     */
    public static PrivateKey getPriKeyPkcs12(InputStream keyFileIn, String keypwd) {
        return getPriKey(keyFileIn, keypwd, CERT_TYPE_PKCS12);
    }

    /**
     * 从私钥证书中获取私钥
     *
     * @param keyFileIn 证书文件流.pfx, .keystore)
     * @param keypwd    证书密码
     * @param type      证书类型
     * @return：私钥
     */
    public static PrivateKey getPriKey(InputStream keyFileIn, String keypwd, String type) {
        log.info("读取公钥："+ JSON.toJSONString(keyFileIn)+",密码："+keypwd+"，类型:"+type);
        PrivateKey privateKey = null;
        getKeyStore(keyFileIn, keypwd, type);
        try {
            Enumeration<String> aliasenum = keyStore.aliases();
            String keyAlias = null;
            if (aliasenum.hasMoreElements()) {
                // 第一个条目
                keyAlias = (String) aliasenum.nextElement();
            }
            privateKey = (PrivateKey) keyStore.getKey(keyAlias, keypwd.toCharArray());
        } catch (Exception e) {
            log.error("Fail: get private key from private certificate", e);
        }
        return privateKey;
    }

    /**
     * 将证书文件读取为证书存储对象：证书文件类型可为：JKS（.keystore等），PKCS12（.pfx）
     *
     * @param keyFileIn 证书文件文件流
     * @param keypwd    证书密码
     * @param type      证书类型
     * @return 证书对象
     */
    public static KeyStore getKeyStore(InputStream keyFileIn, String keypwd, String type) {
        try {
            if (CERT_TYPE_JKS.equals(type)) {
                keyStore = KeyStore.getInstance(type);
            } else if (CERT_TYPE_PKCS12.equals(type)) {
                /**
                 * 动态注册SUN JCE<br/>
                 * JCE（Java Cryptography Extension）是一组包，它们提供用于加密、密钥生成和协商以及 Message
                 * Authentication Code（MAC）算法的框架和实现。<br/>
                 * 设置安全提供者为BouncyCastleProvider
                 */
                if (Security.getProvider("BC") == null) {
                    // 将提供程序添加到下一个可用位置。
                    Security.addProvider(new BouncyCastleProvider());
                } else {
                    Security.removeProvider("BC");
                    Security.addProvider(new BouncyCastleProvider());
                }
                keyStore = KeyStore.getInstance(type);
            }
            char[] nPassword = null;
            nPassword = null == keypwd || "".equals(keypwd.trim()) ? null : keypwd.toCharArray();
            keyStore.load(keyFileIn, nPassword);
            if (keyFileIn != null) {
                keyFileIn.close();
            }
            return keyStore;
        } catch (Exception e) {
            if (Security.getProvider("BC") == null) {
                log.info("BC Provider not installed.");
            }
            log.error("Fail: load privateKey certificate", e);
        } finally {
            if (keyFileIn != null) {
                try {
                    keyFileIn.close();
                } catch (IOException e) {
                    log.error("keyFileIn.close error", e);
                }
            }
        }
        return null;
    }

    public static Boolean verify(byte[] rawData, byte[] signature, PublicKey publicKey, String algorithm) {
        try {
            Signature e = Signature.getInstance(algorithm);
            e.initVerify(publicKey);
            e.update(rawData);
            return e.verify(signature);
        } catch (Exception e) {
            log.error("验签失败",e);
            return null;
        }
    }

    public static byte[] hexString2ByteArr(String hexstring) {
        if(hexstring != null && hexstring.length() % 2 == 0) {
            byte[] dest = new byte[hexstring.length() / 2];

            for(int i = 0; i < dest.length; ++i) {
                String val = hexstring.substring(2 * i, 2 * i + 2);
                dest[i] = (byte)Integer.parseInt(val, 16);
            }

            return dest;
        } else {
            return new byte[0];
        }
    }

    /**
     * 银联SM2加密
     * @param encodedPub
     * @param data
     * @return
     */
    public static String SM2(String encodedPub, String data){
        final String SPEC_NAME = "sm2p256v1";
        final X9ECParameters x9ECParameters =
                GMNamedCurves.getByName(SPEC_NAME);
        final ECDomainParameters ecDomainParameters = new
                ECDomainParameters(x9ECParameters.getCurve(),
                x9ECParameters.getG(), x9ECParameters.getN());
        // 公钥数据
        byte[] pointBytes = Hex.decode(encodedPub);
        ECPoint q =
                x9ECParameters.getCurve().decodePoint(pointBytes);
        ECPublicKeyParameters ecPublicKeyParameters = new
                ECPublicKeyParameters(q, ecDomainParameters);
// 采用 C1 || C3 || C2 的模式
        SM2Engine sm2Engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
        sm2Engine.init(true, new
                ParametersWithRandom(ecPublicKeyParameters));
        byte[] dataBytes = data.getBytes();
        byte[] cipherBytes = new byte[0];
        try {
            cipherBytes = sm2Engine.processBlock(dataBytes, 0,
                    dataBytes.length);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
            log.error("银联SM2加密失败{}：", e);
        }
        String encryptedData = new
                String(Base64.encode(cipherBytes));
        return encryptedData;
    }
    /**
     * 签名
     *
     * @param unionPayLoansBaseReq
     */
    public static String sign(UnionPayLoansBaseReq unionPayLoansBaseReq) {
        String signature = "";
        Map<String, String> srcMap = getSrcMap(unionPayLoansBaseReq);
        List<String> sortKeys = new ArrayList<>(srcMap.keySet());
        Collections.sort(sortKeys);
        StringBuilder sb = new StringBuilder();
        for (String key : sortKeys) {
            sb.append(key);
            sb.append("=");
            sb.append(srcMap.get(key));
            sb.append("&");
        }
        String rawData = sb.toString();
        String str = rawData.substring(0, rawData.length() - 1);
        try {
            byte[] signB = signRsa(str.getBytes(),  UnionPayCertificateConfig.batchPrivateKey, ALGORITHM_SHA256WITHRSA);
            signature = byteArr2HexString(signB);
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 签名
     *
     * @param rawData    签名裸数据
     * @param privateKey 私钥
     * @param algorithm  签名验签算法
     * @return
     */
    public static byte[] signRsa(byte[] rawData, PrivateKey privateKey, String algorithm){
        try {
            Signature instance = Signature.getInstance(algorithm);
            instance.initSign(privateKey);
            instance.update(rawData);
            return instance.sign();
        } catch (Exception e) {
            e.printStackTrace();
            throw new TfException(PayExceptionCodeEnum.SIGN_ERROR.getMsg());
        }
    }


    private static Map<String, String> getSrcMap(UnionPayLoansBaseReq unionPayLoansBaseReq) {
        Map<String, String> srcMap = new HashMap<>();

        srcMap.put("transCode", unionPayLoansBaseReq.getTransCode());
        srcMap.put("verNo", unionPayLoansBaseReq.getVerNo());
        srcMap.put("channelId", unionPayLoansBaseReq.getChannelId());
        srcMap.put("groupId", unionPayLoansBaseReq.getGroupId());
        //请求系统日期
        srcMap.put("srcReqDate", unionPayLoansBaseReq.getSrcReqDate());
        //请求系统时间
        srcMap.put("srcReqTime", unionPayLoansBaseReq.getSrcReqTime());
        //请求系统流水号，须唯一，该处取时间戳以表示不重复序列，生产环境不建议使用
        srcMap.put("srcReqId",unionPayLoansBaseReq.getSrcReqId());
        srcMap.put("lwzBussCode",unionPayLoansBaseReq.getLwzBussCode());
        srcMap.put("lwzChannelType", unionPayLoansBaseReq.getLwzChannelType());
        srcMap.put("lwzData",unionPayLoansBaseReq.getLwzData());

        return srcMap;
    }

    /**
     * 字节数组转换为十六进制字符串
     *
     * @param bytearr 字节数组
     * @return 十六进制字符串
     */
    public static String byteArr2HexString(byte[] bytearr) {
        if (bytearr == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();

        for (byte b : bytearr) {
            if ((b & 0xFF) < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(b & 0xFF, 16));
        }
        return sb.toString();
    }
}
