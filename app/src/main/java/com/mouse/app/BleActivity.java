package com.mouse.app;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.mouse.app.adapter.DeviceListAdapter;
import com.mouse.app.utils.ClientManager;
import com.mouse.app.utils.ToastUtil;
import com.mouse.app.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class BleActivity extends AppCompatActivity {
    private String TAG = BleActivity.class.getSimpleName();
    private DeviceListAdapter mAdapter;
    private List<SearchResult> mDevices;
    private TextView tvTitle;
    private ListView mListView;
    private SwipeRefreshLayout swipe;
    private LoadingDialog.Builder builder;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        BleActivityPermissionsDispatcher.needWithPermissionCheck(this);
        initView();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tvTitle);
        mListView = findViewById(R.id.listView);
        swipe = findViewById(R.id.swipe);
        tvTitle.setText("Ble");
        mDevices = new ArrayList<>();
        builder = new LoadingDialog.Builder(this);
        builder.setMessage("Finding Device").setCancelable(false);
        dialog = builder.create();
        mAdapter = new DeviceListAdapter(this);
        mListView.setAdapter(mAdapter);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchDevice(1, 3000, 2000);
            }
        });

        findViewById(R.id.tvConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BleActivity.this, PlayActivity.class));
//                int selectPosition = mAdapter.getSelectPosition();
//                Log.e(TAG, "onClick: " + selectPosition);
//                if (selectPosition != -1) {
//                    SearchResult searchResult = mAdapter.getmDataList().get(selectPosition);
//                    String address = searchResult.getAddress();
//                    ToastUtil.showMessage(address);
//                } else {
//                    ToastUtil.showMessage("no device or has no device selected!");
//                }
            }
        });
        searchDevice(3, 3000, 2000);
        ClientManager.getClient().registerBluetoothStateListener(new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                BluetoothLog.v(String.format("onBluetoothStateChanged %b", openOrClosed));
            }
        });
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH})
    void need() {
        if (!ClientManager.getClient().isBluetoothOpened()) {
            ClientManager.getClient().openBluetooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BleActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
                grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH})
    void showRation(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("使用向阳宝贝需要申请相关权限，下一步将继续请求权限")
                .setPositiveButton("下一步", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();//继续执行请求
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.cancel();//取消执行请求
            }
        }).show();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH})
    void denied() {
        //您已经拒绝权限,继续申请
        ToastUtil.showMessage("您已经拒绝权限,程序自动退出");
        finish();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH})
    void askAgain() {
        new AlertDialog.Builder(this)
                .setMessage("您已经拒绝请求权限,请到设置页面打开权限")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSetting(BleActivity.this, getPackageName());

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();
    }

    private void startSetting(Context context, String packageName) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", packageName, null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", packageName);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查是否拥有全部权限
    }

    private void searchDevice(int searchTimes, int bleTimes, int jingdianTimes) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        SearchRequest request = new SearchRequest.Builder().searchBluetoothLeDevice(bleTimes,
                searchTimes)
                // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(jingdianTimes)
                // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(bleTimes)
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
            if (!mDevices.contains(device)) {
                mDevices.add(device);
                mAdapter.setDataList(mDevices);
            }
        }

        @Override
        public void onSearchStopped() {
            dialog.dismiss();
            if (swipe.isRefreshing()) {
                swipe.setRefreshing(false);
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
    }


}
