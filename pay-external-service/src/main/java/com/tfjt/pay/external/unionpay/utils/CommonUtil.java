package com.tfjt.pay.external.unionpay.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class CommonUtil {

	private static final char[] HEXCHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 *
	 * @Title: toHexString
	 * @Description: byte数组转化为十六进制
	 */
	public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
		for (byte value : b) {
			sb.append(HEXCHAR[(value & 0xf0) >>> 4]);
			sb.append(HEXCHAR[value & 0x0f]);
		}
        return sb.toString();
    }

	/**
	 *
	 * @Title: toBytes
	 * @Description: 十六进制转化为btye数组
	 */
	public static byte[] toBytes(String s) {
        byte[] bytes;
        bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2),16);
        }
        return bytes;
    }


	/**
	 * 判断是否为空.
	 */
	public static boolean isEmpty(Object o) {
		if (o instanceof String) {
			return null == o || "".equals(((String) o).trim());
		} else {
			return null == o;
		}
	}

	public static String formatJson(String json){
		JSONObject jsonObj = JSON.parseObject(json);
		return JSON.toJSONString(json, SerializerFeature.PrettyFormat);
	}
}
