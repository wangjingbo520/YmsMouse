package com.mouse.app;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.mouse.app.utils.BluetoothUtils;
import com.mouse.app.utils.ClientManager;
import com.mouse.app.view.VerticalSeekBar;

import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;


public class PlayActivity extends BaseActivity implements View.OnClickListener {
    private String macAdress;
    private BluetoothDevice mDevice;
    private boolean mConnected;
    private VerticalSeekBar verticalSeekBar;
    private UUID mService;

    public static void start(Context context, String macAdress) {
        Intent starter = new Intent(context, PlayActivity.class);
        starter.putExtra("macAdress", macAdress);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
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
//                ClientManager.getClient().write(macAdress, mService, mCharacter,
//                        ByteUtils.stringToBytes(mEtInput.getText().toString()), mWriteRsp);
                break;
            case R.id.llbottom:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.llleft:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.llright:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                break;
        }
    }


}
