package com.mouse.app.utils;

import com.inuker.bluetooth.library.BluetoothClient;
import com.mouse.app.MyApplication;


/**
 * @author bobo
 * @date 2018/9/24
 */
public class ClientManager {

    private static BluetoothClient mClient;

    public static BluetoothClient getClient() {
        if (mClient == null) {
            synchronized (ClientManager.class) {
                if (mClient == null) {
                    mClient = new BluetoothClient(MyApplication.getInstance());
                }
            }
        }
        return mClient;
    }
}
