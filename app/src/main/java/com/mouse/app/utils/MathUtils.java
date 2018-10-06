package com.mouse.app.utils;

import android.text.TextUtils;

import java.util.Random;

/**
 * @author bobo
 * @date 2018/9/28
 * describe
 */
public class MathUtils {
//    public static byte[] toByteArray(String hexString) {
//        if (TextUtils.isEmpty(hexString))
//            throw new IllegalArgumentException("this hexString must not be empty");
//
//        hexString = hexString.toLowerCase();
//        final byte[] byteArray = new byte[hexString.length() / 2];
//        int k = 0;
//        for (int i = 0; i < byteArray.length; i++) {
//            //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
//            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
//            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
//            byteArray[i] = (byte) (high << 4 | low);
//            k += 2;
//        }
//        return byteArray;
//    }

    public static String randomHexString(int len) {
        try {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < len; i++) {
                result.append(Integer.toHexString(new Random().nextInt(16)));
            }
            return result.toString().toUpperCase() + MathUtils.makeChecksum(result
                    .toString().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 求和
     *
     * @param data
     * @return
     */
    public static String makeChecksum(String data) {
        if (data == null || data.equals("")) {
            return "";
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            System.out.println(s);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**
         * 用256求余最大是255，即16进制的FF
         */
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


}
