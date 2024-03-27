package com.tfjt.pay.external.unionpay.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName:  DESedeUtil
 * @Description: DES加密、解密工具类
 */
public class DESUtil {
	/**对称加密DESede*/
	public static final String KEY_ALGORITHM = "DESede";
	public static final String ENCODE = "UTF-8";

	/**
	 *
	 * @Title: encrypt
	 * @Description: 加密
	 */
	public static String encrypt(String data, String key)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException,
			NoSuchAlgorithmException, NoSuchPaddingException {
		byte[] keyByte = key.getBytes();
		SecretKey sk = new SecretKeySpec(keyByte, KEY_ALGORITHM);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, sk);
		return CommonUtil.toHexString(cipher.doFinal(data.getBytes(ENCODE)));
	}

	/**
	 *
	 * @Title: decrypt
	 * @Description: 解密
	 */
	public static String decrypt(String data, String key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException {
		byte[] keyByte = key.getBytes();
		SecretKey sk = new SecretKeySpec(keyByte, KEY_ALGORITHM);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, sk);
		return new String(cipher.doFinal(CommonUtil.toBytes(data)), ENCODE);
	}

}
