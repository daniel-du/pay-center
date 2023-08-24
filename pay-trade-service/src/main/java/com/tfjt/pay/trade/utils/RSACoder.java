package com.tfjt.pay.trade.utils;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


/**
 * 非对称加密算法RSA算法组件
 * 非对称算法一般是用来传送对称加密算法的密钥来使用的，相对于DH算法，RSA算法只需要一方构造密钥，不需要
 * 大费周章的构造各自本地的密钥对了。DH算法只能算法非对称算法的底层实现。而RSA算法算法实现起来较为简单
 *
 * @author kongqz
 */
@Slf4j
public class RSACoder {
    //非对称密钥算法
    public static final String KEY_ALGORITHM = "RSA";


    /**
     * 密钥长度，DH算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 512;
    //公钥
    private static final String PUBLIC_KEY = "RSAPublicKey";

    //私钥
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * 初始化密钥对
     *
     * @return Map 甲方密钥的Map
     */
    public static Map<String, Object> initKey() throws Exception {
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //甲方公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //甲方私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //将密钥存储在map中
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;

    }


    /**
     * 私钥加密
     *
     * @param data 待加密数据
     * @param key       密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {

        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param key       密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {

        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {

        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    /**
     * 取得私钥
     *
     * @param keyMap 密钥map
     * @return byte[] 私钥
     */
    public static byte[] getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return key.getEncoded();
    }

    /**
     * 取得公钥
     *
     * @param keyMap 密钥map
     * @return byte[] 公钥
     */
    public static byte[] getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return key.getEncoded();
    }

    /**
     * @param args
     * @throws Exception
     */
//    public static void main(String[] args) throws Exception {
//        //初始化密钥
//        //生成密钥对
//        Map<String, Object> keyMap = RSACoder.initKey();
//        //公钥
//		log.info("===========甲方生成公钥==============");
//        byte[] publicKey = RSACoder.getPublicKey(keyMap);
//		log.info(publicKey);
//        //私钥
//		log.info("===========甲方生成私钥==============");
//        byte[] privateKey = RSACoder.getPrivateKey(keyMap);
//		log.info(privateKey);
////        String gy = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAImBfktP4NrGKAqz7PChIZWE2CNiluTrvLM5DYrHrkGLMpXwOxpi/d7SicGmhl9RqUt5sUN8v36y8BrxWd1vO7MCAwEAAQ==";
////        String sy = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAiYF+S0/g2sYoCrPs8KEhlYTYI2KW5Ou8szkNiseuQYsylfA7GmL93tKJwaaGX1GpS3mxQ3y/frLwGvFZ3W87swIDAQABAkAGkTKw8h2xFSmwaSt1ypxKAdA40GqYgFHA1Ya6DBNcefGBbgrGf9gNGQpYiBMQBRxDDjNXRhBn7uHa3aRiIY7xAiEAv/i7vXPraqEsqX9/P421OxTHGwyKhxuYqQhfLwNDy5cCIQC3XkOv0xLEEt1f4mz+gmzOYlXOM49IF0Ky5CfnJj0ERQIgLKHi3FBIKplgbeQDrL4ApcTV6dcHmhPzcVFN9/s1Vu8CIQCyTRjoKQhAXE+iYtu1B0Gh1YC2BvC2GtgidzRN0aLXOQIhAL3OMQBYrRtUD68fjFymZ17xYW6XcI6wITwShLWLTQI6";
//        String gy= Base64.encodeBase64String(publicKey);
//		String sy=Base64.encodeBase64String(privateKey);
//		log.info("公钥转string：" + gy);
//        log.info("私钥转string：" + sy);
//
//		log.info("string转公钥：" + Base64.decodeBase64(gy));
//		log.info("string转私钥：" + Base64.decodeBase64(sy));
//
//        String str = "A1B2C3D4E5F60708";
//
//        log.info("原文:" + str);
//
//        //乙方使用公钥对数据进行加密
//        byte[] code2 = RSACoder.encryptByPublicKey(str.getBytes(), Base64.decodeBase64(gy));
//        log.info("===========乙方使用公钥对数据进行加密==============");
//        String jh = Base64.encodeBase64String(code2);
//        log.info("加密后的数据：" + jh);
//
//        log.info("=============乙方将数据传送给甲方======================");
//        log.info("===========甲方使用私钥对数据进行解密==============");
//
//        //甲方使用私钥对数据进行解密
//        byte[] decode2 = RSACoder.decryptByPrivateKey(Base64.decodeBase64(jh), Base64.decodeBase64(sy));
//
//        log.info("甲方解密后的数据：" + new String(decode2));
//    }
}
