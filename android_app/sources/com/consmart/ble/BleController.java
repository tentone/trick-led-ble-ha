package com.consmart.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import java.util.Hashtable;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BleController {
    private static BleController mBleController;
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private MyLeScanCallback mMyLeScanCallback;
    private String name;
    private UUID[] serviceUuids;
    private Hashtable<String, MyBluetoothGatt> mMyBluetoothGatts = new Hashtable<>();
    private Hashtable<String, String> autoConnection = new Hashtable<>();
    private boolean isOpenScan = false;
    private Handler scanHandler = new Handler();
    private Runnable scanRunnable = new Runnable() { // from class: com.consmart.ble.BleController.1
        @Override // java.lang.Runnable
        public void run() {
        }
    };
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() { // from class: com.consmart.ble.BleController.2
        @Override // android.bluetooth.BluetoothAdapter.LeScanCallback
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
            String name = bluetoothDevice.getName();
            if (BleController.this.name != null || "".equals(BleController.this.name)) {
                if (!BleController.this.name.equals(name.substring(0, BleController.this.name.length())) || BleController.this.mMyLeScanCallback == null) {
                    return;
                }
                BleController.this.mMyLeScanCallback.onLeScan(bluetoothDevice, i);
                return;
            }
            if (BleController.this.mMyLeScanCallback != null) {
                BleController.this.mMyLeScanCallback.onLeScan(bluetoothDevice, i);
            }
        }
    };

    public interface MyLeScanCallback {
        void onLeScan(BluetoothDevice bluetoothDevice, int i);
    }

    private BleController(Context context) {
        this.mBluetoothAdapter = ((BluetoothManager) context.getSystemService("bluetooth")).getAdapter();
    }

    public static BleController initialization(Context context) {
        if (context == null) {
            return null;
        }
        if (mBleController == null) {
            mBleController = new BleController(context);
        }
        return mBleController;
    }

    public void removeMyBluetoothGatt(String str) {
        if (this.mMyBluetoothGatts.containsKey(str)) {
            this.mMyBluetoothGatts.remove(str);
        }
    }

    public void setScanLeDeviceType(UUID[] uuidArr, String str) {
        this.serviceUuids = uuidArr;
        this.name = str;
    }

    public int scanLeDevice() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return 0;
        }
        UUID[] uuidArr = this.serviceUuids;
        if (uuidArr == null) {
            this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
            Log.e("", "--->> 1开启成功");
            return 1;
        }
        this.mBluetoothAdapter.startLeScan(uuidArr, this.mLeScanCallback);
        return 1;
    }

    public void stopScanLeDevice() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter != null) {
            bluetoothAdapter.stopLeScan(this.mLeScanCallback);
        }
    }

    public void setMyLeScanCallback(MyLeScanCallback myLeScanCallback) {
        this.mMyLeScanCallback = myLeScanCallback;
    }

    public void addAutoConnection(String str) {
        this.autoConnection.put(str, str);
    }

    public MyBluetoothGatt ConnectGatt(String str, MyBluetoothGattCallback myBluetoothGattCallback) {
        MyBluetoothGatt myBluetoothGatt = new MyBluetoothGatt(this.context, this.mBluetoothAdapter);
        myBluetoothGatt.setMyBluetoothGattCallback(myBluetoothGattCallback);
        myBluetoothGatt.connectGatt(str);
        return myBluetoothGatt;
    }
}
