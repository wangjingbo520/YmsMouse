package com.mouse.app;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.mouse.app.utils.ClientManager;
import com.mouse.app.utils.Constants;
import com.mouse.app.utils.MathUtils;
import com.mouse.app.utils.ToastUtil;

import java.util.Random;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * @author bobo
 * @date 2018/9/22
 */
public class BaseActivity extends AppCompatActivity {

    private StringBuffer stringBuffer;
    public StringBuffer total;
    private String hex;

    public final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                Log.e("----->", "writeSucess");
                onWriteSucess();
            } else {
                Log.e("----->", "writeFailed");
            }
        }
    };

    public void writeBle(String macAdress, String cmd, int speed) {
        ClientManager.getClient().write(macAdress, UUID.fromString(Constants.serviceUuid)
                , UUID.fromString(Constants.writeUiid), getBytes(cmd, speed),
                mWriteRsp);
    }

    public byte[] getBytes(String cmd, int speed) {
        stringBuffer = new StringBuffer();
        stringBuffer.append(cmd).append(toHex(speed)).append("10");
        String crc = MathUtils.makeChecksum(stringBuffer.toString());
        total = new StringBuffer();
        total.append("90").append(cmd).append(toHex(speed)).append("01")
                .append(crc).append("a5");
        return MathUtils.hexStringToBytes(total.toString());
    }

    public String toHex(int num) {
        if (num < 10) {
            hex = "0" + num;
        } else {
            hex = String.valueOf(num);
        }
        return hex;
    }

    /**
     * KEY0+KEY1+KEY2+KEY3+KEY4+KEY5  十六进制字符串随机数
     *
     * @param len
     * @return
     */
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
     * 字符串求和
     *
     * @param data
     * @return
     */
    public String makeChecksum(String data) {
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


    /**
     * 写成功的回调
     */
    public void onWriteSucess() {

    }

}
