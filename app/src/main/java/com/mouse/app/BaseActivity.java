package com.mouse.app;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.mouse.app.utils.ClientManager;
import com.mouse.app.utils.Constants;
import com.mouse.app.utils.MathUtils;

import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.mouse.app.utils.MathUtils.convertDecimalToBinary;
import static com.mouse.app.utils.MathUtils.hexStringToBytes;

/**
 * @author bobo
 * @date 2018/9/22
 */
public class BaseActivity extends AppCompatActivity {
    private String XX = "00";

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

    public void playBle(String macAdress, String cmd, int speed) {
        //低八位
        String crc = MathUtils.makeChecksum(cmd + convertDecimalToBinary(speed) + XX);
        String data = "5a" + cmd + convertDecimalToBinary(speed) + XX + crc + "a5";
        ClientManager.getClient().write(macAdress, UUID.fromString(Constants.serviceUuid)
                , UUID.fromString(Constants.writeUiid), hexStringToBytes(data),
                mWriteRsp);
    }

    /**
     * 写成功的回调
     */
    public void onWriteSucess() {

    }

}
