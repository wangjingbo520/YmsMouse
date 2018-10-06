package com.mouse.app;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.mouse.app.adapter.DeviceListAdapter;
import com.mouse.app.utils.ClientManager;
import com.mouse.app.utils.Constants;
import com.mouse.app.utils.MathUtils;
import com.mouse.app.utils.ToastUtil;
import com.mouse.app.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;


public class BleActivity extends BaseActivity implements DeviceListAdapter.AdressListenser {
    private String TAG = BleActivity.class.getSimpleName();
    private DeviceListAdapter mAdapter;
    private List<SearchResult> mDevices;
    private TextView tvTitle;
    private ListView mListView;
    private SwipeRefreshLayout swipe;
    private LoadingDialog.Builder builder;
    private LoadingDialog dialog;
    private String macAdress;
    private ImageView ivBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        initView();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tvTitle);
        mListView = findViewById(R.id.listView);
        swipe = findViewById(R.id.swipe);
        ivBlue = findViewById(R.id.ivBlue);
        tvTitle.setText("Device List");
        mDevices = new ArrayList<>();
        builder = new LoadingDialog.Builder(this);
        builder.setMessage("Finding Device").setCancelable(false);
        dialog = builder.create();
        mAdapter = new DeviceListAdapter(this, this);
        mListView.setAdapter(mAdapter);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchDevice(1, 3000, 2000);
            }
        });

        searchDevice(2, 3000, 2000);
        ClientManager.getClient().registerBluetoothStateListener(new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                BluetoothLog.v(String.format("onBluetoothStateChanged %b", openOrClosed));
            }
        });

        findViewById(R.id.flBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //检查是否拥有全部权限
    }

    private void searchDevice(int searchTimes, int bleTimes, int jingdianTimes) {
        if (dialog != null && !dialog.isShowing()) {
            builder.setMessage("Finding Device").setCancelable(false);
            dialog = builder.create();
            dialog.show();
        }
        SearchRequest request = new SearchRequest.Builder().searchBluetoothLeDevice(bleTimes,
                searchTimes)
                // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(jingdianTimes)
                // 再扫经典蓝牙5s
//                .searchBluetoothLeDevice(bleTimes)
                // 再扫BLE设备2s
                .build();
        ClientManager.getClient().search(request, mSearchResponse);
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            BluetoothLog.w("MainActivity.onSearchStarted");
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            BluetoothLog.w("MainActivity.onDeviceFounded " + device.device.getAddress());
            if (device.getName().endsWith("pets")) {
                if (!mDevices.contains(device)) {
                    mDevices.add(device);
                    ivBlue.setVisibility(View.GONE);
                    mAdapter.setDataList(mDevices);
                }
            }
        }

        @Override
        public void onSearchStopped() {
            dialog.dismiss();
            if (swipe.isRefreshing()) {
                swipe.setRefreshing(false);
            }
            if (mAdapter.getCount() > 0) {
                ivBlue.setVisibility(View.GONE);
            } else {
                ivBlue.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onSearchCanceled() {
            dialog.dismiss();
            BluetoothLog.w("MainActivity.onSearchCanceled");
            if (swipe.isRefreshing()) {
                swipe.setRefreshing(false);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().stopSearch();
        dialog.dismiss();
    }


    @Override
    public void beginConncet(String adress) {
        builder.setMessage("Connecting.....").setCancelable(false);
        dialog = builder.create();
        dialog.show();
        connnect(adress);
    }

    private void connnect(final String macAdress) {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                // 连接如果失败重试3次
                .setConnectTimeout(30000)
                // 连接超时30s
                .setServiceDiscoverRetry(3)
                // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000)
                // 发现服务超时20s
                .build();
        ClientManager.getClient().connect(macAdress, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                if (code == REQUEST_SUCCESS) {
                    dialog.dismiss();
                    BleActivity.this.macAdress = macAdress;
                    ClientManager.getClient().write(macAdress, UUID.fromString(Constants
                                    .serviceUuid)
                            , UUID.fromString(Constants
                                    .writeUiid),
                            MathUtils.hexStringToBytes("5a" + randomHexString(12)
                                    + makeChecksum(randomHexString(12)) + "a5"), mWriteRsp);
                }
            }
        });
    }

    @Override
    public void onWriteSucess() {
        super.onWriteSucess();
        if (!TextUtils.isEmpty(macAdress)) {
            PlayActivity.start(BleActivity.this, macAdress);
        }
    }

}
