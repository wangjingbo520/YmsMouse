package com.mouse.app;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.mouse.app.utils.BluetoothUtils;
import com.mouse.app.utils.ClientManager;
import com.mouse.app.view.BatteryView;
import com.mouse.app.view.VerticalSeekBar;

import java.lang.ref.WeakReference;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;


public class PlayActivity extends BaseActivity implements View.OnClickListener, VerticalSeekBar
        .SlideChangeListener, View.OnTouchListener {
    private BatteryView horizontalBattery;
    private String macAdress;
    private BluetoothDevice mDevice;
    private boolean mConnected;
    private VerticalSeekBar verticalSeekBar;
    private int speed = 10;
    /**
     * 静止的命令
     */
    public static String STILL_CODE = "00";
    private String cmd = STILL_CODE;
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
        horizontalBattery = findViewById(R.id.horizontalBattery);
        findViewById(R.id.llguide).setOnClickListener(this);
        findViewById(R.id.llhome).setOnClickListener(this);
        findViewById(R.id.lltop).setOnTouchListener(this);
        findViewById(R.id.llbottom).setOnTouchListener(this);
        findViewById(R.id.llleft).setOnTouchListener(this);
        findViewById(R.id.llright).setOnTouchListener(this);
        macAdress = getIntent().getStringExtra("macAdress");
        mDevice = BluetoothUtils.getRemoteDevice(macAdress);
        ClientManager.getClient().registerConnectStatusListener(macAdress,
                mConnectStatusListener);
        verticalSeekBar = findViewById(R.id.verticalSeekBar);
        verticalSeekBar.setMaxProgress(19);
        verticalSeekBar.setProgress(speed);
        verticalSeekBar.setOnSlideChangeListener(this);
        myMainHandler = new MyMainHandler(this);
        notifi(macAdress);
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
                startActivity(new Intent(this, GuideActivity.class));
                break;
            case R.id.llhome:
                startActivity(new Intent(this, MainActivity.class));
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
        this.speed = progress;
    }

    @Override
    public void onStop(VerticalSeekBar slideView, int progress) {

    }

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            myMainHandler.sendEmptyMessage(1);
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int id = view.getId();
        myMainHandler.removeCallbacks(task);
        if (action == MotionEvent.ACTION_DOWN) {
            //按下去
            switch (id) {
                case R.id.lltop:
                    //前进
                    this.cmd = "01";
                    break;
                case R.id.llbottom:
                    //后退
                    this.cmd = "02";
                    break;
                case R.id.llleft:
                    //向左
                    this.cmd = "04";
                    break;
                case R.id.llright:
                    //向右
                    this.cmd = "08";
                    break;
                default:
                    break;
            }
        } else if (action == MotionEvent.ACTION_UP) {
            // 松开
            switch (id) {
                case R.id.lltop:
                case R.id.llbottom:
                case R.id.llleft:
                case R.id.llright:
                    this.cmd = STILL_CODE;
                default:
                    this.cmd = STILL_CODE;
                    break;
            }
            speed = 0;
            verticalSeekBar.setProgress(0);
        } else if (action == MotionEvent.ACTION_CANCEL) {
            switch (id) {
                case R.id.lltop:
                case R.id.llbottom:
                case R.id.llleft:
                case R.id.llright:
                    this.cmd = STILL_CODE;
                default:
                    this.cmd = STILL_CODE;
                    break;
            }
        }
        speed = 0;
        verticalSeekBar.setProgress(0);
        myMainHandler.sendEmptyMessage(1);
        return true;
    }

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
                    mActivityReference.get().playBle(mActivityReference.get()
                            .macAdress, mActivityReference.get().cmd, mActivityReference.get()
                            .speed);
                    mActivityReference.get().myMainHandler.postDelayed(mActivityReference.get()
                            .task, 100);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            new AlertDialog.Builder(this)
                    .setMessage("The device will be disconnected,Are you sure to quit?")
                    .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClientManager.getClient().disconnect(macAdress);
                            finish();
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setCancelable(false).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myMainHandler.removeCallbacks(task);
    }

    @Override
    public void onNotifiSucess(byte[] value) {
        super.onNotifiSucess(value);
        //电量显示
        Integer x = Integer.parseInt(String.valueOf(value[1]), 16);
        horizontalBattery.setPower(x);
    }
}
