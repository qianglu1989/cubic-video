package com.cubic.viedo.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * @ClassName Md5Utils
 * @Author QIANGLU
 * @Date 2019/12/30 9:05 上午
 * @Version 1.0
 */
public class Md5Utils {

    private static final int HEX_VALUE_COUNT = 16;

    public Md5Utils() {
    }

    public static String getMD5(byte[] bytes) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] str = new char[32];

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            byte[] tmp = md.digest();
            int k = 0;

            for(int i = 0; i < HEX_VALUE_COUNT; ++i) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return new String(str);
    }

    public static String getMD5(String value, String encode) {
        String result = "";

        try {
            result = getMD5(value.getBytes(encode));
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return result;
    }
}
