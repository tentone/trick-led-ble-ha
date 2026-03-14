package com.qh.blelight;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.qh.tools.DBAdapter;
import com.qh.tools.SampleGattAttributes;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothLeService<IWindowManager> extends Service {
    public static final String COMPANY_NAME = "^Trick-|^Trick#";
    public static final int INT_PHOTOGRAPH = 2;
    public static final int SCAN_PERIOD = 4000;
    private static final int SREVICE_UPDATA = 5;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_DISCONNECTED = 0;
    private static final String TAG = "BluetoothLeService";
    public static final UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    public static final boolean isDebug = true;
    private static boolean isDofindService = false;
    private static boolean isFindService = false;
    private static boolean isStatr = false;
    public static String link_Addr = "";
    public static long time;
    private BluetoothDevice LEdevice;
    public BluetoothGattCharacteristic connect_state;
    public Context context;
    public DBAdapter dbAdapter;
    public BluetoothGattCharacteristic device_addr;
    public BluetoothGattCharacteristic device_name;
    private AudioManager mAudiomanager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    public BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    public Checkpwd mCheckpwd;
    public String mDeviceAddr;
    private BluetoothDevice mLEdevice;
    public Handler mScanHandler;
    public BluetoothGattCharacteristic manufacturer_name;
    public MyApplication myApplication;
    public BluetoothLeService myBluetoothLeService;
    public Handler openScanHandler;
    private Handler operateHandler;
    public BluetoothGattCharacteristic photoCharacteristic;
    public BluetoothGattCharacteristic power_level;
    public SharedPreferences settings;
    private boolean mScanning = false;
    private boolean isLEenabled = false;
    private Handler mHandler = new Handler();
    private int mConnectionState = 0;
    public int linkNum = 0;
    public Hashtable<String, BluetoothDevice> mDevices = new Hashtable<>();
    public Hashtable<String, BluetoothDevice> mBindingDevices = new Hashtable<>();
    public Hashtable<String, String> mConnectedDevices = new Hashtable<>();
    public Hashtable<String, String> unlinkBleDevices = new Hashtable<>();
    public Hashtable<String, MyBluetoothGatt> MyBluetoothGatts = new Hashtable<>();
    private final IBinder mBinder = new LocalBinder();
    public Qicaidata mQicaidata = null;
    public Resetpwd mresetpwd = null;
    private Runnable scanRunnable = new Runnable() { // from class: com.qh.blelight.BluetoothLeService.3
        @Override // java.lang.Runnable
        public void run() {
            BluetoothLeService.this.scanLeDevice();
            BluetoothLeService.this.openScanHandler.postDelayed(BluetoothLeService.this.scanRunnable, 2000L);
        }
    };
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() { // from class: com.qh.blelight.BluetoothLeService.4
        public BluetoothDevice mdevice;
        private int num = 0;

        @Override // android.bluetooth.BluetoothAdapter.LeScanCallback
        public synchronized void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
            String name = bluetoothDevice.getName();
            if (name == null) {
                return;
            }
            if (Pattern.compile(BluetoothLeService.COMPANY_NAME).matcher(name).find()) {
                if (!BluetoothLeService.this.mDevices.containsKey(bluetoothDevice.getAddress())) {
                    BluetoothLeService.this.mDeviceAddr = bluetoothDevice.getAddress();
                    BluetoothLeService.this.mDevices.put(bluetoothDevice.getAddress(), bluetoothDevice);
                    Log.e("--", "---" + bluetoothDevice.getAddress());
                    MainActivity.ControlMACs.put(bluetoothDevice.getAddress(), bluetoothDevice.getAddress());
                    if (BluetoothLeService.this.operateHandler != null) {
                        Message message = new Message();
                        message.what = 1;
                        BluetoothLeService.this.operateHandler.sendMessage(message);
                    }
                }
                BluetoothLeService.this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.BluetoothLeService.4.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (BluetoothLeService.this.unlinkBleDevices.containsKey(bluetoothDevice.getAddress())) {
                            return;
                        }
                        BluetoothLeService.this.connBLE(bluetoothDevice.getAddress());
                        BluetoothLeService.this.linkNum++;
                        if (BluetoothLeService.this.linkNum > 5) {
                            BluetoothLeService.this.linkNum = 0;
                        }
                    }
                }, (BluetoothLeService.this.linkNum * 150) + ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
            }
        }
    };
    public Hashtable<String, Long> connTime = new Hashtable<>();

    public interface Checkpwd {
        void checkpwd(String str, int i, String str2);
    }

    public interface Qicaidata {
        void readqicai(String str, int i, int i2, boolean z);
    }

    public interface Resetpwd {
        void resetpwd(String str, int i, String str2);
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void setOperateHandler(Handler handler) {
        this.operateHandler = handler;
    }

    public void addBindingDevices(String str) {
        BluetoothDevice bluetoothDevice;
        if (!this.mDevices.containsKey(str) || (bluetoothDevice = this.mDevices.get(str)) == null) {
            return;
        }
        this.mBindingDevices.put(str, bluetoothDevice);
    }

    @Override // android.app.Service
    public void onCreate() {
        this.context = this;
        this.openScanHandler = new Handler();
        initialize();
        this.settings = getSharedPreferences("BleLight", 0);
        this.dbAdapter = DBAdapter.init(this);
        this.myApplication = (MyApplication) getApplication();
        this.dbAdapter.open();
        this.myBluetoothLeService = this;
        super.onCreate();
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public Hashtable<String, BluetoothDevice> getBluetoothDevice() {
        Hashtable<String, BluetoothDevice> hashtable = new Hashtable<>(this.mDevices);
        Iterator<String> it = this.mBindingDevices.keySet().iterator();
        while (it.hasNext()) {
            hashtable.remove(it.next());
        }
        return hashtable;
    }

    public Hashtable<String, BluetoothDevice> getBindingDevice() {
        return new Hashtable<>(this.mBindingDevices);
    }

    private void initialize() {
        this.mBluetoothAdapter = ((BluetoothManager) getSystemService("bluetooth")).getAdapter();
        this.mAudiomanager = (AudioManager) getSystemService("audio");
        isStatr = true;
    }

    public boolean scanLeDevice() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled() || this.mScanning) {
            return false;
        }
        try {
            this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
            this.mScanning = true;
        } catch (Exception unused) {
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.BluetoothLeService.1
            @Override // java.lang.Runnable
            public void run() {
                BluetoothLeService.this.mBluetoothAdapter.stopLeScan(BluetoothLeService.this.mLeScanCallback);
            }
        }, 4000L);
        this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.BluetoothLeService.2
            @Override // java.lang.Runnable
            public void run() {
                BluetoothLeService.this.mScanning = false;
            }
        }, 6000L);
        return true;
    }

    public void scanLeDevice(boolean z) {
        this.openScanHandler.postDelayed(this.scanRunnable, 100L);
    }

    public void StopScanLeDevice() {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled() && this.mScanning) {
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
            this.mScanning = false;
        }
    }

    public int connBLE(final String str) {
        if (this.connTime.containsKey(str)) {
            if (System.currentTimeMillis() - this.connTime.get(str).longValue() <= 2000) {
                return 0;
            }
        }
        if (this.MyBluetoothGatts.size() >= 4) {
            return 1;
        }
        if (this.MyBluetoothGatts.containsKey(str)) {
            MyBluetoothGatt myBluetoothGatt = this.MyBluetoothGatts.get(str);
            if (new Date().getTime() - myBluetoothGatt.linktime > 19000 && myBluetoothGatt.mConnectionState != 2) {
                myBluetoothGatt.stopLEService();
                this.MyBluetoothGatts.remove(str);
                this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.BluetoothLeService.5
                    @Override // java.lang.Runnable
                    public void run() {
                        Log.e("", "------" + str);
                        MyBluetoothGatt myBluetoothGatt2 = new MyBluetoothGatt(BluetoothLeService.this.context, BluetoothLeService.this.mBluetoothAdapter, BluetoothLeService.this.myBluetoothLeService, BluetoothLeService.this.operateHandler, BluetoothLeService.this.dbAdapter);
                        myBluetoothGatt2.connectGatt(str);
                        BluetoothLeService.this.MyBluetoothGatts.put(str, myBluetoothGatt2);
                        BluetoothLeService.this.connTime.put(str, Long.valueOf(System.currentTimeMillis()));
                    }
                }, 120L);
            }
        } else {
            if (this.MyBluetoothGatts.size() > 5) {
                return 1;
            }
            MyBluetoothGatt myBluetoothGatt2 = new MyBluetoothGatt(this.context, this.mBluetoothAdapter, this.myBluetoothLeService, this.operateHandler, this.dbAdapter);
            myBluetoothGatt2.connectGatt(str);
            this.MyBluetoothGatts.put(str, myBluetoothGatt2);
            this.connTime.put(str, Long.valueOf(System.currentTimeMillis()));
        }
        return 0;
    }

    public void stopLEService() {
        this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        this.mScanning = false;
    }
}
