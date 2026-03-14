package com.consmart.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import com.consmart.ble.BleController;
import com.qh.onehlight.R;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity extends Activity {
    public BleController mBleController;
    private Context mContext;
    private BleController.MyLeScanCallback mMyLeScanCallback = new BleController.MyLeScanCallback() { // from class: com.consmart.ble.MainActivity.1
        @Override // com.consmart.ble.BleController.MyLeScanCallback
        public void onLeScan(BluetoothDevice bluetoothDevice, int i) {
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.attr.actionBarDivider);
        Context applicationContext = getApplicationContext();
        this.mContext = applicationContext;
        this.mBleController = BleController.initialization(applicationContext);
        UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
        this.mBleController.setScanLeDeviceType(null, "123456");
        this.mBleController.setMyLeScanCallback(this.mMyLeScanCallback);
        this.mBleController.scanLeDevice();
    }
}
