package com.mouse.app;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.mouse.app.utils.BluetoothUtils;
import com.mouse.app.utils.ClientManager;
import com.mouse.app.utils.Constants;
import com.mouse.app.utils.MathUtils;
import com.mouse.app.view.VerticalSeekBar;

import java.lang.ref.WeakReference;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;


public class PlayActivity extends BaseActivity implements View.OnClickListener, VerticalSeekBar
        .SlideChangeListener {
    private String macAdress;
    private BluetoothDevice mDevice;
    private boolean mConnected;
    private VerticalSeekBar verticalSeekBar;
    private int speed = 10;
    private String cmd = "00";
    private MyMainHandler myMainHandler;

    public static void start(Context context, String macAdress) {
        Intent starter = new Intent(context, PlayActivity.class);
        starter.putExtra("macAdress", macAdress);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        verticalSeekBar = findViewById(R.id.verticalSeekBar);
        findViewById(R.id.llguide).setOnClickListener(this);
        findViewById(R.id.llhome).setOnClickListener(this);
        findViewById(R.id.lltop).setOnClickListener(this);
        findViewById(R.id.llbottom).setOnClickListener(this);
        findViewById(R.id.llleft).setOnClickListener(this);
        findViewById(R.id.llright).setOnClickListener(this);
        macAdress = getIntent().getStringExtra("macAdress");
        mDevice = BluetoothUtils.getRemoteDevice(macAdress);
        ClientManager.getClient().registerConnectStatusListener(macAdress,
                mConnectStatusListener);
        verticalSeekBar = findViewById(R.id.verticalSeekBar);
        verticalSeekBar.setMaxProgress(19);
        verticalSeekBar.setProgress(speed);
        verticalSeekBar.setOnSlideChangeListener(this);
        myMainHandler = new MyMainHandler(this);
        startPlay();
    }

    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            mConnected = (status == STATUS_CONNECTED);
            connectDeviceIfNeeded();
        }
    };

    private void connectDeviceIfNeeded() {
        if (!mConnected) {
            connectDevice();
        }
    }

    private void connectDevice() {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();

        ClientManager.getClient().connect(mDevice.getAddress(), options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                BluetoothLog.v(String.format("profile:\n%s", profile));
                if (code == REQUEST_SUCCESS) {
                    //重连成功
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llguide:
                startActivity(new Intent(this, MenuActivity.class));
                break;
            case R.id.llhome:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.lltop:
                //前进
                cmd = "01";
                ClientManager.getClient().write(macAdress, UUID.fromString(Constants.serviceUuid)
                        , UUID.fromString(Constants.writeUiid), getBytes(cmd, speed),
                        mWriteRsp);
                break;
            case R.id.llbottom:
                cmd = "02";
                ClientManager.getClient().write(macAdress, UUID.fromString(Constants.serviceUuid)
                        , UUID.fromString(Constants.writeUiid), getBytes(cmd, speed),
                        mWriteRsp);
                break;
            case R.id.llleft:
                cmd = "04";
                ClientManager.getClient().write(macAdress, UUID.fromString(Constants.serviceUuid)
                        , UUID.fromString(Constants.writeUiid), getBytes(cmd, speed),
                        mWriteRsp);
                break;
            case R.id.llright:
                cmd = "08";
                ClientManager.getClient().write(macAdress, UUID.fromString(Constants.serviceUuid)
                        , UUID.fromString(Constants.writeUiid), getBytes(cmd, speed),
                        mWriteRsp);
                break;
            default:
                break;
        }
    }


    @Override
    public void onStart(VerticalSeekBar slideView, int progress) {

    }

    @Override
    public void onProgress(VerticalSeekBar slideView, int progress) {

    }

    @Override
    public void onStop(VerticalSeekBar slideView, int progress) {
        Log.e("onStop", "onStop: " + progress);
        speed = progress;
        go();
    }

    private void go() {
        ClientManager.getClient().write(macAdress, UUID.fromString(Constants.serviceUuid)
                , UUID.fromString(Constants.writeUiid), getBytes(cmd, speed),
                mWriteRsp);

    }


    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            myMainHandler.sendEmptyMessage(1);
        }
    };


    public static class MyMainHandler extends Handler {
        WeakReference<PlayActivity> mActivityReference;

        MyMainHandler(PlayActivity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mActivityReference.get() == null) {
                return;
            }
            switch (msg.what) {
                case 1:
                    mActivityReference.get().go();
                    mActivityReference.get().myMainHandler.postDelayed(mActivityReference.get()
                            .task, 100);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myMainHandler.removeCallbacks(task);
    }

    /**
     * 开启线程刷新
     */
    private void startPlay() {
        myMainHandler.postDelayed(task, 100);
    }

}
