package com.mouse.app.utils;

/**
 * @author bobo
 * @date 2018/9/28
 * describe
 */
public class Constants {
    public static StringBuffer stringBuffer = new StringBuffer();
    public static StringBuffer total = new StringBuffer();
    public static String serviceUuid = "00001800-0000-1000-8000-00805f9b34fb";
    public static String writeUiid = "00001801-0000-1000-8000-00805f9b34fb";
    public static String xieyi = "5a12ac3498f35ddaa5";
    /**
     * 认证协议
     */
    public static byte[] CERTIFICATION = {(byte) (0x5a), (byte) (0x12), (byte) (0xac), (byte)
            (0x34),
            (byte) (0x98), (byte) (0xf3), (byte) (0x5d), (byte) (0xda), (byte) (0xa5)};


    /**
     * 所有操作的命令,前进等.
     *
     * @param cmd   操作类型,这里要是十六进制的字符串,记得
     * @param speed 速度分20个等级
     * @return
     */
    public static byte[] getBytes(String cmd, int speed) {
        stringBuffer = null;
        stringBuffer.append(cmd).append(Integer.toHexString(speed)).append("01");
        String crc = MathUtils.makeChecksum(stringBuffer.toString());
        total = null;
        total.append("5a").append(cmd).append(Integer.toHexString(speed)).append("01")
                .append(crc).append("a5");
        return MathUtils.toByteArray(total.toString());
    }



}
