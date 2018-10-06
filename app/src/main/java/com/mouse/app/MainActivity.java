package com.mouse.app;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.mouse.app.utils.ClientManager;
import com.mouse.app.utils.Constants;
import com.mouse.app.utils.MathUtils;
import com.mouse.app.utils.ToastUtil;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        MainActivityPermissionsDispatcher.needWithPermissionCheck(this);
        findViewById(R.id.tv1).setOnClickListener(this);
        findViewById(R.id.tv2).setOnClickListener(this);
        findViewById(R.id.tv3).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv1:
                byte[] bytes = MathUtils.hexStringToBytes("5a" + MathUtils.randomHexString(12)
                        + MathUtils.makeChecksum(MathUtils.randomHexString(12)) + "a5");
                Log.e("---len", bytes.length+"" );
                for (int i = 0; i < bytes.length; i++) {
                    Log.e("---->", bytes[i] + "");

                }

                for (int i = 0; i < Constants.CERTIFICATION.length; i++) {
                    Log.e("---->xieyi", Constants.CERTIFICATION[i] + "");

                }


          //      startActivity(new Intent(this, MenuActivity.class));
                break;
            case R.id.tv2:
                startActivity(new Intent(this, GuideActivity.class));
                break;
            case R.id.tv3:
                startActivity(new Intent(this, BleActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure to quit?")
                    .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
                grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH})
    void showRation(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("request for access")
                .setPositiveButton("next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();//继续执行请求
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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
        ToastUtil.showMessage("You have denied permission and the program quits automatically");
        finish();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH})
    void askAgain() {
        new AlertDialog.Builder(this)
                .setMessage("You have declined the request permission, please go to the settings " +
                        "page to open the permission")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSetting(MainActivity.this, getPackageName());

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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

}
