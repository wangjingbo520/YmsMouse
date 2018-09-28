package com.mouse.app;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.mouse.app.utils.ToastUtil;

import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * @author bobo
 * @date 2018/9/22
 */
public class BaseActivity extends AppCompatActivity {

//    private final BleReadResponse mReadRsp = new BleReadResponse() {
//        @Override
//        public void onResponse(int code, byte[] data) {
//            if (code == REQUEST_SUCCESS) {
//                mBtnRead.setText(String.format("read: %s", ByteUtils.byteToString(data)));
//                CommonUtils.toast("success");
//            } else {
//                CommonUtils.toast("failed");
//                mBtnRead.setText("read");
//            }
//        }
//    };

    public final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                ToastUtil.showMessage("写入成功");
            } else {
                ToastUtil.showMessage("写入失败");
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

}
