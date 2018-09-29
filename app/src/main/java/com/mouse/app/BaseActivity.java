package com.mouse.app;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.mouse.app.utils.MathUtils;
import com.mouse.app.utils.ToastUtil;

import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * @author bobo
 * @date 2018/9/22
 */
public class BaseActivity extends AppCompatActivity {

    private StringBuffer stringBuffer;
    public StringBuffer total;
    private String crc;

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

    public final BleNotifyResponse mNotifyRsp = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            Log.e("----->", ByteUtils.byteToString(value));
//            if (service.equals(mService) && character.equals(mCharacter)) {
//                mBtnNotify.setText(String.format("%s", ByteUtils.byteToString(value)));
//            }
        }

        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                ToastUtil.showMessage("通知窗口成功");
            } else {
                ToastUtil.showMessage("通知窗口失败");
            }
        }
    };
    private String s;

    public void onWriteSucess() {

    }


    /**
     * 所有操作的命令,前进等.
     *
     * @param cmd   操作类型,这里要是十六进制的字符串,记得
     * @param speed 速度分20个等级
     * @return
     */
    public byte[] getBytes(String cmd, int speed) {
        stringBuffer = new StringBuffer();
        stringBuffer.append(cmd).append(to(speed)).append("01");

        String crc = MathUtils.makeChecksum(stringBuffer.toString());
        total = new StringBuffer();
        total.append("5a").append(cmd).append(Integer.toHexString(speed)).append("01")
                .append(crc).append("a5");
        return MathUtils.toByteArray(total.toString());
    }


    public String to(int speed) {
        s = Integer.toHexString(speed);
        if (s.length() <= 1) {
            crc = "0" + s;
        } else {
            crc = s;
        }
        return crc;
    }

}
