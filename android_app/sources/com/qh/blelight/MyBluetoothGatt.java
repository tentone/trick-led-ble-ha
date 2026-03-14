package com.qh.blelight;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.consmart.ble.AES2;
import com.qh.data.MyColor;
import com.qh.data.NewTime;
import com.qh.tools.DBAdapter;
import com.qh.tools.DeviceUUID;
import com.qh.tools.SampleGattAttributes;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class MyBluetoothGatt {
    public static final String COMPANY_NAME = "Consmart";
    public static final int INT_PHOTOGRAPH = 2;
    public static final int SREVICE_UPDATA = 5;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCONNECTED = 0;
    public static final byte[] TIME_HEAD = {35, 37, 39, 67, 69, 71};
    public static final byte[] TIME_TAIL = {50, 82, 114, 52, 84, 116};
    public static final String TRIONES_NAME = "^Triones-|^Triones\\+|^Triones";
    public BluetoothAdapter bluetoothAdapter;
    private byte[] cachecmd;
    public Context context;
    private DBAdapter dbAdapter;
    private BluetoothGatt mBluetoothGatt;
    public BluetoothLeService mBluetoothLeService;
    private BluetoothGattCallback mGattCallback;
    private Handler mHandler;
    public BluetoothDevice mLEdevice;
    public BluetoothGattCharacteristic photoCharacteristic;
    public MyColor savecolor;
    public SharedPreferences settings;
    public MediaPlayer waitiingMP;
    public int num = 0;
    public byte[] huancaidata = {0, 0, 0, 0, 0, 0, 0, 0};
    private Handler connHandler = new Handler();
    public int mConnectionState = 0;
    public String mAddr = "";
    public long linktime = 0;
    Queue<MyColor> writeQueue = new LinkedList();
    public byte[] datas = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public byte[] timedata = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public final byte[] Mods = {37, 38, 39, 40, 57};
    public int modId = -1;
    public boolean isTriones = false;
    public boolean isLong = false;
    public Hashtable<Integer, NewTime> mNewTimelist = new Hashtable<>();
    private Handler mTimeHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.MyBluetoothGatt.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            return false;
        }
    });
    public String pwd = "1234";
    private Handler checkHandler = new Handler();
    private int checkNum = 0;
    public Runnable checkRunnable = new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.10
        @Override // java.lang.Runnable
        public void run() {
            if (MyBluetoothGatt.this.checkNum < 5) {
                MyBluetoothGatt.this.setAES();
                MyBluetoothGatt.this.checkHandler.postDelayed(MyBluetoothGatt.this.checkRunnable, 1000L);
                MyBluetoothGatt.access$708(MyBluetoothGatt.this);
            } else {
                if (MyBluetoothGatt.this.isread180a) {
                    return;
                }
                MyBluetoothGatt.this.mBluetoothLeService.unlinkBleDevices.put(MyBluetoothGatt.this.mAddr, MyBluetoothGatt.this.mAddr);
                MyBluetoothGatt.this.stopLEService();
                MyBluetoothGatt myBluetoothGatt = MyBluetoothGatt.this;
                myBluetoothGatt.setMsg(myBluetoothGatt.mAddr, 400);
            }
        }
    };
    private byte[] sendsrcAES = {-5, 2, 5, 5, 16, 8, 35, 1, 2, 0, 5, 85, 34, 1, 18, 19, 20, -6};
    private byte[] srcAES = {2, 5, 5, 16, 8, 35, 1, 2, 0, 5, 85, 34, 1, 18, 19, 20};
    public boolean isread180a = false;
    public String scheckpwd = "1234";
    public String cachenewpwd = "1234";
    boolean isopenmyMIC = false;

    public void reSetCMD() {
    }

    static /* synthetic */ int access$708(MyBluetoothGatt myBluetoothGatt) {
        int i = myBluetoothGatt.checkNum;
        myBluetoothGatt.checkNum = i + 1;
        return i;
    }

    public MyBluetoothGatt(Context context, BluetoothAdapter bluetoothAdapter, final BluetoothLeService bluetoothLeService, final Handler handler, DBAdapter dBAdapter) {
        this.context = context;
        this.settings = context.getSharedPreferences("setting", 0);
        this.dbAdapter = dBAdapter;
        this.bluetoothAdapter = bluetoothAdapter;
        this.mBluetoothLeService = bluetoothLeService;
        Log.e("MyBluetoothGatt", "MyBluetoothGatt 1");
        updataSrc();
        this.mHandler = handler;
        this.mGattCallback = new BluetoothGattCallback() { // from class: com.qh.blelight.MyBluetoothGatt.2
            private long fasttime = 0;
            private long fastdata = 0;
            boolean flay = true;
            private int timeDataNum = 0;
            private boolean timeflay = false;

            @Override // android.bluetooth.BluetoothGattCallback
            public void onConnectionStateChange(final BluetoothGatt bluetoothGatt, int i, int i2) {
                MyBluetoothGatt.this.mConnectionState = i;
                Log.e("--", "__" + i);
                if (i2 == 2) {
                    MyBluetoothGatt.this.linktime = new Date().getTime();
                    if (MyBluetoothGatt.this.mBluetoothGatt != null) {
                        MyBluetoothGatt.this.mConnectionState = 1;
                        handler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                bluetoothGatt.discoverServices();
                            }
                        }, 500L);
                        return;
                    }
                    return;
                }
                if (i2 != 0) {
                    if (i == 133) {
                        MyBluetoothGatt.this.mBluetoothGatt.connect();
                        MyBluetoothGatt.this.mBluetoothGatt.close();
                        MyBluetoothGatt.this.mBluetoothGatt = null;
                        return;
                    }
                    return;
                }
                MyBluetoothGatt.this.mConnectionState = 0;
                Log.e("", "---" + bluetoothGatt.getDevice().getAddress());
                bluetoothLeService.MyBluetoothGatts.remove(bluetoothGatt.getDevice().getAddress());
                MyBluetoothGatt.this.setMsg(bluetoothGatt.getDevice().getAddress(), 4);
                try {
                    MyBluetoothGatt.this.mBluetoothGatt.close();
                    MyBluetoothGatt.this.mBluetoothGatt = null;
                    if (bluetoothLeService == null || MyBluetoothGatt.this.mAddr == null || !"null".equals(MyBluetoothGatt.this.mAddr) || bluetoothLeService.unlinkBleDevices.containsKey(MyBluetoothGatt.this.mAddr)) {
                        return;
                    }
                    MyBluetoothGatt.this.connHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.2.2
                        @Override // java.lang.Runnable
                        public void run() {
                            bluetoothLeService.connBLE(MyBluetoothGatt.this.mAddr);
                        }
                    }, 1000L);
                } catch (Exception unused) {
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
                if (bluetoothGatt != null) {
                    MyBluetoothGatt.this.mBluetoothGatt = bluetoothGatt;
                }
                if (i == 0) {
                    MyBluetoothGatt.this.mConnectionState = 2;
                    handler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.2.3
                        @Override // java.lang.Runnable
                        public void run() {
                            MyBluetoothGatt.this.setNotify();
                        }
                    }, 200L);
                    handler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.2.4
                        @Override // java.lang.Runnable
                        public void run() {
                            MyBluetoothGatt.this.getLightData();
                        }
                    }, 600L);
                    handler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.2.5
                        @Override // java.lang.Runnable
                        public void run() {
                            MyBluetoothGatt.this.getTimeData();
                        }
                    }, 900L);
                    handler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.2.6
                        @Override // java.lang.Runnable
                        public void run() {
                            MyBluetoothGatt.this.setDate();
                        }
                    }, 1300L);
                    handler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.2.7
                        @Override // java.lang.Runnable
                        public void run() {
                            MyBluetoothGatt.this.read180a();
                        }
                    }, 1500L);
                    if (MyBluetoothGatt.this.mLEdevice.getName().contains("Dream") || MyBluetoothGatt.this.mLEdevice.getName().contains("Flash")) {
                        handler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.2.8
                            @Override // java.lang.Runnable
                            public void run() {
                                MyBluetoothGatt.this.readhuancai();
                            }
                        }, 2000L);
                    }
                    Message message = new Message();
                    message.what = 3;
                    Bundle bundle = new Bundle();
                    bundle.putString("deviceAddr", "" + MyBluetoothGatt.this.mAddr);
                    message.setData(bundle);
                    handler.sendMessageDelayed(message, 500L);
                    MyBluetoothGatt.this.num = 0;
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
                String name = MyBluetoothGatt.this.mLEdevice.getName();
                if (name.length() == 20 && MyBluetoothGatt.this.isTriones && name.contains("-FFFFF10000")) {
                    Integer numValueOf = Integer.valueOf(name.substring(18, 20), 16);
                    if (numValueOf.intValue() >= 0) {
                        numValueOf.intValue();
                    }
                }
                name.contains("Triones~");
                if (name.contains(":")) {
                    MyBluetoothGatt.this.mTimeHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.2.9
                        @Override // java.lang.Runnable
                        public void run() {
                            MyBluetoothGatt.this.checkpwd(MyBluetoothGatt.this.pwd);
                        }
                    }, 1000L);
                }
                super.onDescriptorWrite(bluetoothGatt, bluetoothGattDescriptor, i);
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                if (DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_DATA_UUID.equals(bluetoothGattCharacteristic.getUuid().toString())) {
                    byte[] value = bluetoothGattCharacteristic.getValue();
                    MyBluetoothGatt.this.mConnectionState = 2;
                    String str = "";
                    for (byte b : value) {
                        str = str + String.format("%x", Byte.valueOf(b)) + " ";
                    }
                    Log.e("--", "sdata=" + str + " mac=" + MyBluetoothGatt.this.mAddr);
                    if (value != null && value.length == 8 && value[0] == -123 && value[7] == 88) {
                        for (int i = 0; i < MyBluetoothGatt.this.huancaidata.length; i++) {
                            MyBluetoothGatt.this.huancaidata[i] = value[i];
                        }
                        Log.e("-", "-huancai-");
                        return;
                    }
                    if (value != null && value.length == 4 && value[0] == -80 && value[3] == 11) {
                        if (value[1] == -16) {
                            MyBluetoothGatt myBluetoothGatt = MyBluetoothGatt.this;
                            myBluetoothGatt.pwd = myBluetoothGatt.cachenewpwd;
                            if (bluetoothLeService.mresetpwd != null) {
                                bluetoothLeService.mresetpwd.resetpwd(MyBluetoothGatt.this.mAddr, 1, MyBluetoothGatt.this.cachenewpwd);
                            }
                            if (MyBluetoothGatt.this.settings != null) {
                                SharedPreferences.Editor editorEdit = MyBluetoothGatt.this.settings.edit();
                                editorEdit.putString("pwd" + MyBluetoothGatt.this.mAddr, MyBluetoothGatt.this.cachenewpwd);
                                editorEdit.commit();
                            }
                        }
                        if (value[1] == 15 && bluetoothLeService.mresetpwd != null) {
                            bluetoothLeService.mresetpwd.resetpwd(MyBluetoothGatt.this.mAddr, 0, MyBluetoothGatt.this.cachenewpwd);
                        }
                        if (value[1] != -18 || bluetoothLeService.mresetpwd == null) {
                            return;
                        }
                        bluetoothLeService.mresetpwd.resetpwd(MyBluetoothGatt.this.mAddr, 2, MyBluetoothGatt.this.cachenewpwd);
                        return;
                    }
                    if (value != null && value.length == 10 && value[0] == -48 && value[9] == 13 && bluetoothLeService.mQicaidata != null) {
                        bluetoothLeService.mQicaidata.readqicai(MyBluetoothGatt.this.mAddr, value[1], value[2], value[3] != 0);
                    }
                    if (value != null && value.length == 4 && value[0] == -60 && value[3] == 76 && bluetoothLeService.mQicaidata != null) {
                        bluetoothLeService.mQicaidata.readqicai(MyBluetoothGatt.this.mAddr, value[1], value[2], false);
                    }
                    if (value != null && value.length == 4 && value[0] == -96 && value[3] == 10) {
                        if (value[1] == -16) {
                            Log.e("mima", "mima= ok" + MyBluetoothGatt.this.scheckpwd);
                            if (bluetoothLeService.mCheckpwd != null) {
                                bluetoothLeService.mCheckpwd.checkpwd(MyBluetoothGatt.this.mAddr, 1, MyBluetoothGatt.this.scheckpwd);
                            }
                            MyBluetoothGatt myBluetoothGatt2 = MyBluetoothGatt.this;
                            myBluetoothGatt2.pwd = myBluetoothGatt2.scheckpwd;
                            if (MyBluetoothGatt.this.settings != null) {
                                SharedPreferences.Editor editorEdit2 = MyBluetoothGatt.this.settings.edit();
                                editorEdit2.putString("pwd" + MyBluetoothGatt.this.mAddr, MyBluetoothGatt.this.scheckpwd);
                                editorEdit2.commit();
                            }
                            Log.e("11", "scheckpwd " + MyBluetoothGatt.this.scheckpwd);
                        } else if (value[1] == 15) {
                            Log.e("mima", "mima= false");
                            if (bluetoothLeService.mCheckpwd != null) {
                                bluetoothLeService.mCheckpwd.checkpwd(MyBluetoothGatt.this.mAddr, 0, MyBluetoothGatt.this.scheckpwd);
                            }
                        } else if (value[1] == -18) {
                            Log.e("mima", "mima= ee");
                            if (bluetoothLeService.mCheckpwd != null) {
                                bluetoothLeService.mCheckpwd.checkpwd(MyBluetoothGatt.this.mAddr, 2, MyBluetoothGatt.this.scheckpwd);
                            }
                        }
                    }
                    if (value != null && value.length == 8) {
                        MyBluetoothGatt.this.reSetData(value);
                    }
                    if (value != null) {
                        if (this.timeflay) {
                            for (byte b2 : value) {
                                if (MyBluetoothGatt.this.timedata.length > this.timeDataNum && this.timeflay) {
                                    byte[] bArr = MyBluetoothGatt.this.timedata;
                                    int i2 = this.timeDataNum;
                                    bArr[i2] = b2;
                                    this.timeDataNum = i2 + 1;
                                }
                            }
                            if (value.length == 7) {
                                this.timeflay = false;
                            }
                        }
                        if (value[0] == 37) {
                            this.timeDataNum = 0;
                            this.timeflay = true;
                            for (byte b3 : value) {
                                if (MyBluetoothGatt.this.timedata.length > this.timeDataNum && this.timeflay) {
                                    byte[] bArr2 = MyBluetoothGatt.this.timedata;
                                    int i3 = this.timeDataNum;
                                    bArr2[i3] = b3;
                                    this.timeDataNum = i3 + 1;
                                }
                            }
                            if (value.length == 20) {
                                MyBluetoothGatt.this.isLong = true;
                            }
                        } else if (value[0] == 102) {
                            for (int i4 = 0; i4 < value.length; i4++) {
                                MyBluetoothGatt.this.datas[i4] = value[i4];
                            }
                            Log.e("66", "data[2] " + MyBluetoothGatt.this.mAddr);
                            if (value[2] == 35) {
                                MyBluetoothGatt myBluetoothGatt3 = MyBluetoothGatt.this;
                                myBluetoothGatt3.setMsg(myBluetoothGatt3.mAddr, 0);
                            } else if (value[2] == 36) {
                                MyBluetoothGatt myBluetoothGatt4 = MyBluetoothGatt.this;
                                myBluetoothGatt4.setMsg(myBluetoothGatt4.mAddr, 0);
                            }
                            if (value.length == 12) {
                                MyBluetoothGatt.this.savecolor = new MyColor(Color.argb(255, value[6] & 255, value[7] & 255, value[8] & 255), value[9], 100);
                                MyBluetoothGatt.this.synTimedata((byte) (value[3] & 255), (byte) (value[5] & 255), (byte) (value[6] & 255), (byte) (value[7] & 255), (byte) (value[8] & 255), (byte) (value[9] & 255));
                            }
                        }
                    }
                    if (value.length == 18 && value[0] == -7 && value[17] == -8) {
                        Log.e("--", "sdata=" + str);
                        try {
                            byte[] bArrEncrypt = AES2.Encrypt(MyBluetoothGatt.this.srcAES);
                            if (bArrEncrypt != null) {
                                int i5 = 0;
                                boolean z = true;
                                while (i5 < bArrEncrypt.length) {
                                    byte b4 = bArrEncrypt[i5];
                                    i5++;
                                    if (b4 != value[i5]) {
                                        z = false;
                                    }
                                }
                                if (z) {
                                    MyBluetoothGatt.this.checkHandler.removeCallbacks(MyBluetoothGatt.this.checkRunnable);
                                } else {
                                    MyBluetoothGatt myBluetoothGatt5 = MyBluetoothGatt.this;
                                    myBluetoothGatt5.setMsg(myBluetoothGatt5.mAddr, 400);
                                    MyBluetoothGatt.this.stopLEService();
                                }
                                Log.e("-", "ischeck = " + z + " " + MyBluetoothGatt.this.mAddr);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                byte[] value = bluetoothGattCharacteristic.getValue();
                String str = "";
                for (byte b : value) {
                    str = str + String.format("%02x", Integer.valueOf(b & 255));
                }
                Log.e("-Write-", "length=" + value.length + " Write = " + str);
                DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID.equals(bluetoothGattCharacteristic.getUuid().toString());
                super.onCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, i);
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
                if (DeviceUUID.CONSMART_BLE_2a25_UUID.equals(bluetoothGattCharacteristic.getUuid().toString())) {
                    byte[] value = bluetoothGattCharacteristic.getValue();
                    Log.e("a", "CONSMART_BLE_2a25_UUID " + MyBluetoothGatt.this.mAddr);
                    if (value != null && value.length > 0) {
                        String str = "";
                        for (byte b : value) {
                            str = str + " " + String.format("%02x", Integer.valueOf(b & 255));
                        }
                    }
                    if (value.length == 12 && value[0] == 81 && value[1] == 72 && value[2] == 45 && value[3] == 81 && value[4] == 88 && value[5] == 68 && value[6] == 45 && value[7] == 85 && value[8] == 65 && value[9] == 82 && value[10] == 84 && value[11] == 0) {
                        MyBluetoothGatt.this.isread180a = true;
                    }
                }
            }
        };
    }

    public BluetoothGattCallback getmGattCallback() {
        return this.mGattCallback;
    }

    public void setmGattCallback(BluetoothGattCallback bluetoothGattCallback) {
        this.mGattCallback = bluetoothGattCallback;
    }

    public BluetoothGatt getmBluetoothGatt() {
        return this.mBluetoothGatt;
    }

    public BluetoothDevice getmLEdevice() {
        return this.mLEdevice;
    }

    public void connectGatt(String str) {
        if (this.bluetoothAdapter == null) {
            return;
        }
        this.linktime = new Date().getTime();
        this.mAddr = str;
        String string = this.settings.getString("pwd" + this.mAddr, "1234");
        this.pwd = string;
        this.cachenewpwd = string;
        Log.e("pwd", "pwd=" + this.pwd);
        BluetoothDevice remoteDevice = this.bluetoothAdapter.getRemoteDevice(str);
        this.mLEdevice = remoteDevice;
        this.mBluetoothGatt = remoteDevice.connectGatt(this.context, false, this.mGattCallback);
        this.mConnectionState = 1;
        setMsg(this.mAddr, 0);
        Log.e("--", "----put----" + this.mAddr);
        BluetoothDevice bluetoothDevice = this.mLEdevice;
        if (bluetoothDevice == null || bluetoothDevice.getName() == null) {
            this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.3
                @Override // java.lang.Runnable
                public void run() {
                    if (MyBluetoothGatt.this.mConnectionState == 1) {
                        MyBluetoothGatt.this.mConnectionState = 4;
                        MyBluetoothGatt myBluetoothGatt = MyBluetoothGatt.this;
                        myBluetoothGatt.setMsg(myBluetoothGatt.mAddr, 4);
                    }
                }
            }, 2000L);
            return;
        }
        if (Pattern.compile(TRIONES_NAME).matcher(this.mLEdevice.getName()).find()) {
            this.isTriones = true;
        } else {
            this.isTriones = false;
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.4
            @Override // java.lang.Runnable
            public void run() {
                if (MyBluetoothGatt.this.mConnectionState == 1) {
                    MyBluetoothGatt.this.mConnectionState = 4;
                    MyBluetoothGatt.this.mBluetoothLeService.unlinkBleDevices.put(MyBluetoothGatt.this.mAddr, MyBluetoothGatt.this.mAddr);
                    MyBluetoothGatt.this.stopLEService();
                    MyBluetoothGatt myBluetoothGatt = MyBluetoothGatt.this;
                    myBluetoothGatt.setMsg(myBluetoothGatt.mAddr, 4);
                }
            }
        }, 16000L);
    }

    public void stopLEService() {
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.disconnect();
    }

    public void setNotify() {
        BluetoothGattService service;
        UUID uuidFromString = UUID.fromString("0000ffd0-0000-1000-8000-00805f9b34fb");
        UUID uuidFromString2 = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_DATA_UUID);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null || (service = bluetoothGatt.getService(uuidFromString)) == null) {
            return;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidFromString2);
        this.photoCharacteristic = characteristic;
        this.mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = this.photoCharacteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor == null) {
            return;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        this.mBluetoothGatt.writeDescriptor(descriptor);
    }

    public void setMsg(String str, int i) {
        Message message = new Message();
        message.what = i;
        Bundle bundle = new Bundle();
        bundle.putString("deviceAddr", "" + str);
        message.setData(bundle);
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.sendMessage(message);
        }
        if (this.mBluetoothLeService.myApplication == null || this.mBluetoothLeService.myApplication.popHandler == null) {
            return;
        }
        Message message2 = new Message();
        message2.what = i;
        Bundle bundle2 = new Bundle();
        bundle2.putString("deviceAddr", "" + str);
        message2.setData(bundle2);
        this.mBluetoothLeService.myApplication.popHandler.sendMessage(message2);
    }

    public synchronized void writeCharacteristic(String str, String str2, byte[] bArr) {
        UUID uuidFromString = UUID.fromString(str);
        UUID uuidFromString2 = UUID.fromString(str2);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return;
        }
        BluetoothGattService service = bluetoothGatt.getService(uuidFromString);
        if (service == null) {
            return;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidFromString2);
        if (characteristic == null) {
            return;
        }
        characteristic.setValue(bArr);
        new Thread(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.5
            @Override // java.lang.Runnable
            public void run() {
                if (MyBluetoothGatt.this.mBluetoothGatt != null) {
                    MyBluetoothGatt.this.mBluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }).start();
    }

    public synchronized void writeCharacteristic(String str, String str2, byte[] bArr, final boolean z) {
        UUID uuidFromString = UUID.fromString(str);
        UUID uuidFromString2 = UUID.fromString(str2);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return;
        }
        BluetoothGattService service = bluetoothGatt.getService(uuidFromString);
        if (service == null) {
            return;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidFromString2);
        if (characteristic == null) {
            return;
        }
        if (bArr != null && bArr.length <= 20) {
            characteristic.setWriteType(2);
        }
        characteristic.setValue(bArr);
        new Thread(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.6
            @Override // java.lang.Runnable
            public void run() {
                if (MyBluetoothGatt.this.mBluetoothGatt == null) {
                    return;
                }
                boolean zWriteCharacteristic = MyBluetoothGatt.this.mBluetoothGatt.writeCharacteristic(characteristic);
                while (!zWriteCharacteristic && z) {
                    try {
                        Thread.sleep(220L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e("-", "nosend = " + zWriteCharacteristic);
                    if (MyBluetoothGatt.this.mBluetoothGatt == null) {
                        return;
                    } else {
                        zWriteCharacteristic = MyBluetoothGatt.this.mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                }
            }
        }).start();
    }

    public synchronized void writeCharacteristic_no_response(String str, String str2, byte[] bArr, final boolean z) {
        UUID uuidFromString = UUID.fromString(str);
        UUID uuidFromString2 = UUID.fromString(str2);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return;
        }
        BluetoothGattService service = bluetoothGatt.getService(uuidFromString);
        if (service == null) {
            return;
        }
        final BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidFromString2);
        if (characteristic == null) {
            return;
        }
        if (bArr != null && bArr.length <= 20) {
            characteristic.setWriteType(1);
        }
        characteristic.setValue(bArr);
        new Thread(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.7
            @Override // java.lang.Runnable
            public void run() {
                if (MyBluetoothGatt.this.mBluetoothGatt == null) {
                    return;
                }
                boolean zWriteCharacteristic = MyBluetoothGatt.this.mBluetoothGatt.writeCharacteristic(characteristic);
                while (!zWriteCharacteristic && z) {
                    try {
                        Thread.sleep(220L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e("-", "nosend = " + zWriteCharacteristic);
                    if (MyBluetoothGatt.this.mBluetoothGatt == null) {
                        return;
                    } else {
                        zWriteCharacteristic = MyBluetoothGatt.this.mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                }
            }
        }).start();
    }

    public synchronized boolean writeCharacteristic(String str, String str2, byte[] bArr, int i) {
        UUID uuidFromString = UUID.fromString(str);
        UUID uuidFromString2 = UUID.fromString(str2);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null) {
            return false;
        }
        BluetoothGattService service = bluetoothGatt.getService(uuidFromString);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(uuidFromString2);
        if (characteristic == null) {
            return false;
        }
        if (bArr != null && bArr.length <= 20) {
            characteristic.setWriteType(2);
        }
        characteristic.setValue(bArr);
        BluetoothGatt bluetoothGatt2 = this.mBluetoothGatt;
        if (bluetoothGatt2 == null) {
            return false;
        }
        return bluetoothGatt2.writeCharacteristic(characteristic);
    }

    public void setColor(int i) {
        BluetoothGattCharacteristic characteristic;
        if (this.mBluetoothGatt == null) {
            return;
        }
        Log.e("pro", "pro=" + i);
        int i2 = (int) (((float) i) * 2.55f);
        if (i2 >= 254) {
            i2 = 255;
        }
        UUID uuidFromString = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID);
        UUID uuidFromString2 = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID);
        BluetoothGattService service = this.mBluetoothGatt.getService(uuidFromString);
        if (service == null || (characteristic = service.getCharacteristic(uuidFromString2)) == null) {
            return;
        }
        byte[] bArr = {86, (byte) i2, 0, 0, 0, -16, -86};
        this.cachecmd = bArr;
        characteristic.setValue(bArr);
        this.mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public void getLightData() {
        writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, new byte[]{-17, 1, 119}, true);
    }

    public void getTimeData() {
        writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, new byte[]{36, 42, 43, 66}, true);
    }

    public void openLight(boolean z) {
        byte[] bArr = this.datas;
        if (bArr == null || bArr.length < 4) {
            return;
        }
        byte[] bArr2 = {-52, 35, 51};
        if (!z) {
            bArr2 = new byte[]{-52, 36, 51};
            bArr[2] = 36;
            Handler handler = this.mHandler;
            if (handler != null) {
                handler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.8
                    @Override // java.lang.Runnable
                    public void run() {
                        MyBluetoothGatt.this.writeCharacteristic_no_response(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, new byte[]{-52, 36, 51}, true);
                    }
                }, 300L);
            }
        } else {
            bArr[2] = 35;
        }
        writeCharacteristic_no_response(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr2, true);
    }

    public void setMod(int i, int i2) {
        if (i >= 0) {
            byte[] bArr = this.Mods;
            if (i >= bArr.length) {
                return;
            }
            this.modId = i;
            byte b = (byte) (i2 & 255);
            byte[] bArr2 = {-69, bArr[i], b, 68};
            this.cachecmd = bArr2;
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr2);
            Log.e("setMod", "setMod Speed=" + i2 + " mod=" + ((int) this.Mods[i]));
            synTimedata(this.Mods[i], b, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        }
    }

    public void setSpeed(int i) {
        int i2 = this.modId;
        if (i2 >= 0) {
            byte[] bArr = this.Mods;
            if (i2 >= bArr.length) {
                return;
            }
            byte b = (byte) (i & 255);
            byte[] bArr2 = {-69, bArr[i2], b, 68};
            this.cachecmd = bArr2;
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr2);
            Log.e("setMod", "setMod Speed=" + i + " mod=" + ((int) this.Mods[this.modId]));
            synTimedata(this.Mods[this.modId], b, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        }
    }

    public void setSpeed(int i, int i2, int i3) {
        int i4 = i2 + 1;
        if (i4 <= 1) {
            i4 = 1;
        }
        if (i4 >= 255) {
            i4 = 255;
        }
        byte[] bArr = {-98, 0, (byte) (i & 255), (byte) (i4 & 255), (byte) ((i3 + 25) & 255), 0, -23};
        this.cachecmd = bArr;
        writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr);
    }

    public void setMusicColor(int i) {
        BluetoothGattCharacteristic characteristic;
        if (this.mBluetoothGatt == null || this.isopenmyMIC) {
            return;
        }
        int i2 = i & 255;
        UUID uuidFromString = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID);
        UUID uuidFromString2 = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID);
        BluetoothGattService service = this.mBluetoothGatt.getService(uuidFromString);
        if (service == null || (characteristic = service.getCharacteristic(uuidFromString2)) == null) {
            return;
        }
        if (i2 <= 3) {
            i2 = 3;
        }
        int i3 = (int) (i2 * 2.55f);
        if (i3 >= 254) {
            i3 = 255;
        }
        characteristic.setValue(new byte[]{120, (byte) (i3 & 255), 0, 0, 0, -16, -18});
        this.mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public void setDate() {
        byte b;
        BluetoothGattService service;
        final BluetoothGattCharacteristic characteristic;
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int i = calendar.get(7) - 1;
        if (i < 0) {
            i = 0;
        }
        int i2 = calendar.get(1) % 100;
        int i3 = calendar.get(2) + 1;
        int i4 = calendar.get(5);
        int i5 = calendar.get(11);
        int i6 = calendar.get(12);
        int i7 = calendar.get(13);
        BluetoothDevice bluetoothDevice = this.mLEdevice;
        if (bluetoothDevice == null || bluetoothDevice.getName() == null) {
            return;
        }
        if (!Pattern.compile("^Dream|^Flash").matcher(this.mLEdevice.getName()).find()) {
            switch (i) {
                case 0:
                    b = 7;
                    break;
                case 1:
                    b = 1;
                    break;
                case 2:
                    b = 2;
                    break;
                case 3:
                    b = 3;
                    break;
                case 4:
                    b = 4;
                    break;
                case 5:
                    b = 5;
                    break;
                case 6:
                    b = 6;
                    break;
                default:
                    b = 0;
                    break;
            }
        } else {
            b = (byte) i;
        }
        byte[] bArr = {16, 20, (byte) (i2 & 255), (byte) (i3 & 255), (byte) (i4 & 255), (byte) (i5 & 255), (byte) (i6 & 255), (byte) (i7 & 255), b, 0, 1};
        UUID uuidFromString = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID);
        UUID uuidFromString2 = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null || (service = bluetoothGatt.getService(uuidFromString)) == null || (characteristic = service.getCharacteristic(uuidFromString2)) == null) {
            return;
        }
        characteristic.setWriteType(2);
        characteristic.setValue(bArr);
        new Thread(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.9
            @Override // java.lang.Runnable
            public void run() {
                boolean zWriteCharacteristic = MyBluetoothGatt.this.mBluetoothGatt != null ? MyBluetoothGatt.this.mBluetoothGatt.writeCharacteristic(characteristic) : false;
                while (!zWriteCharacteristic) {
                    try {
                        Thread.sleep(120L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (MyBluetoothGatt.this.mBluetoothGatt != null) {
                        zWriteCharacteristic = MyBluetoothGatt.this.mBluetoothGatt.writeCharacteristic(characteristic);
                    }
                }
            }
        }).start();
    }

    public void setDayData() {
        byte[] bArr;
        BluetoothGattService service;
        BluetoothGattCharacteristic characteristic;
        byte[] bArr2 = {35, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50};
        bArr2[0] = 35;
        int i = 1;
        while (true) {
            bArr = this.timedata;
            if (i >= bArr.length) {
                break;
            }
            bArr2[i] = bArr[i];
            i++;
        }
        bArr2[bArr.length - 1] = 50;
        UUID uuidFromString = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID);
        UUID uuidFromString2 = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null || (service = bluetoothGatt.getService(uuidFromString)) == null || (characteristic = service.getCharacteristic(uuidFromString2)) == null) {
            return;
        }
        characteristic.setWriteType(2);
        characteristic.setValue(bArr2);
        Log.e("f", "f = " + this.mBluetoothGatt.writeCharacteristic(characteristic));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void synTimedata(byte b, byte b2, byte b3, byte b4, byte b5, byte b6) {
        if (b == 65) {
            for (int i = 0; i < 6; i++) {
                byte[] bArr = this.timedata;
                int i2 = i * 14;
                int i3 = i2 + 10;
                bArr[i3] = b3;
                bArr[i2 + 11] = b4;
                bArr[i2 + 12] = b5;
                bArr[i2 + 13] = b6;
                bArr[i2 + 9] = 65;
                bArr[i3] = b3;
            }
            return;
        }
        for (int i4 = 0; i4 < 6; i4++) {
            byte[] bArr2 = this.timedata;
            int i5 = i4 * 14;
            int i6 = i5 + 10;
            bArr2[i6] = b2;
            bArr2[i5 + 11] = 0;
            bArr2[i5 + 12] = 0;
            bArr2[i5 + 13] = 0;
            bArr2[i5 + 9] = b;
            bArr2[i6] = b2;
        }
    }

    public void setNewTime(int i, boolean z, int i2, int i3, int i4, byte b, boolean z2) {
        this.mNewTimelist.put(Integer.valueOf(i), new NewTime(z, i2, i3, i4, b, z2));
    }

    public void sendNewTime(int i) {
        NewTime newTime;
        BluetoothGattCharacteristic characteristic;
        byte[] bArr = {35, 35, 35, 35, 35, 35, 35, 35};
        if (!this.mNewTimelist.containsKey(Integer.valueOf(i)) || (newTime = this.mNewTimelist.get(Integer.valueOf(i))) == null) {
            return;
        }
        bArr[0] = TIME_HEAD[i];
        bArr[7] = TIME_TAIL[i];
        bArr[1] = (byte) (newTime.valid ? 240 : 15);
        bArr[2] = (byte) (newTime.h & 255);
        bArr[3] = (byte) (newTime.m & 255);
        bArr[4] = (byte) (newTime.s & 255);
        bArr[5] = newTime.w;
        bArr[6] = (byte) (newTime.open ? 240 : 15);
        UUID uuidFromString = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID);
        UUID uuidFromString2 = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID);
        BluetoothGattService service = this.mBluetoothGatt.getService(uuidFromString);
        if (service == null || (characteristic = service.getCharacteristic(uuidFromString2)) == null) {
            return;
        }
        characteristic.setValue(bArr);
        this.mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public void sendNewTime(int i, boolean z) {
        BluetoothGattCharacteristic characteristic;
        Log.e("--", "sendNewTime");
        byte[] bArr = {35, 35, 35, 35, 35, 35, 35, 35};
        if (this.mNewTimelist.containsKey(Integer.valueOf(i))) {
            NewTime newTime = this.mNewTimelist.get(Integer.valueOf(i));
            if (newTime != null) {
                bArr[0] = TIME_HEAD[i];
                bArr[7] = TIME_TAIL[i];
                newTime.valid = z;
                bArr[1] = (byte) (newTime.valid ? 240 : 15);
                bArr[2] = (byte) (newTime.h & 255);
                bArr[3] = (byte) (newTime.m & 255);
                bArr[4] = (byte) (newTime.s & 255);
                bArr[5] = newTime.w;
                bArr[6] = (byte) (newTime.open ? 240 : 15);
                UUID uuidFromString = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID);
                UUID uuidFromString2 = UUID.fromString(DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID);
                BluetoothGattService service = this.mBluetoothGatt.getService(uuidFromString);
                if (service != null && (characteristic = service.getCharacteristic(uuidFromString2)) != null) {
                    characteristic.setValue(bArr);
                    this.mBluetoothGatt.writeCharacteristic(characteristic);
                }
            }
            this.mNewTimelist.put(Integer.valueOf(i), newTime);
        }
    }

    public void reSetData(byte[] bArr) {
        if (bArr == null || bArr.length != 8) {
            return;
        }
        int i = 0;
        while (true) {
            byte[] bArr2 = TIME_TAIL;
            if (i >= bArr2.length) {
                return;
            }
            if (bArr2[i] == bArr[0]) {
                byte[] bArr3 = this.timedata;
                int i2 = i * 14;
                bArr3[i2 + 5] = bArr[2];
                bArr3[i2 + 6] = bArr[3];
                bArr3[i2 + 8] = bArr[5];
                bArr3[i2 + 1] = bArr[1];
                bArr3[i2 + 14] = bArr[6];
            }
            i++;
        }
    }

    public void setValues(byte[] bArr) {
        writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAES() {
        try {
            Log.e("-", "check aes!");
            if (this.isTriones) {
                if (this.mLEdevice.getName().length() == 16) {
                    byte[] bArr = this.sendsrcAES;
                    bArr[0] = -53;
                    bArr[17] = -54;
                } else {
                    byte[] bArr2 = this.sendsrcAES;
                    bArr2[0] = -5;
                    bArr2[17] = -6;
                }
            }
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, this.sendsrcAES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updataSrc() {
        Random random = new Random();
        int i = 0;
        while (i < this.srcAES.length) {
            byte bNextInt = (byte) ((random.nextInt(90) + 1) & 255);
            this.srcAES[i] = bNextInt;
            i++;
            this.sendsrcAES[i] = bNextInt;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void read180a() {
        readData("0000180a-0000-1000-8000-00805f9b34fb", DeviceUUID.CONSMART_BLE_2a25_UUID);
    }

    public void readData(String str, String str2) {
        BluetoothGattService service;
        BluetoothGattCharacteristic characteristic;
        UUID uuidFromString = UUID.fromString(str);
        UUID uuidFromString2 = UUID.fromString(str2);
        BluetoothGatt bluetoothGatt = this.mBluetoothGatt;
        if (bluetoothGatt == null || (service = bluetoothGatt.getService(uuidFromString)) == null || (characteristic = service.getCharacteristic(uuidFromString2)) == null) {
            return;
        }
        this.mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void readhuancai() {
        Log.e("--", "readhuancai");
        writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, new byte[]{29, -16, 0, -15}, true);
    }

    public void checkpwd(String str) {
        if (str == null || str.length() != 4) {
            return;
        }
        try {
            int i = Integer.parseInt(str);
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, new byte[]{-49, (byte) (i / 1000), (byte) ((i % 1000) / 100), (byte) ((i % 100) / 10), (byte) (i % 10), -4}, true);
            this.scheckpwd = str;
        } catch (NumberFormatException unused) {
        }
    }

    public void readqicai() {
        if (this.mLEdevice.getName().contains("Triones:")) {
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, new byte[]{-27, -16, 94}, true);
        } else if (this.mLEdevice.getName().contains("Triones#")) {
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, new byte[]{105, 0, 0, -16, -106}, true);
        }
    }

    public void setqicaidata(int i, int i2, boolean z) {
        if (this.mLEdevice.getName().contains("Triones:")) {
            byte[] bArr = {-10, 0, 0, 0, 0, 0, 0, 0, 0, 111};
            bArr[1] = (byte) i;
            bArr[2] = (byte) i2;
            if (z) {
                bArr[3] = 1;
            } else {
                bArr[3] = 0;
            }
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr, true);
            return;
        }
        if (this.mLEdevice.getName().contains("Triones#")) {
            byte[] bArr2 = {105, 0, 0, 15, -106};
            bArr2[1] = (byte) i;
            bArr2[2] = (byte) i2;
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr2, true);
        }
    }

    public void setpwd(String str) {
        String str2 = this.pwd;
        if (str2 == null || str2.length() != 4 || str == null || str.length() != 4) {
            return;
        }
        byte[] bArr = {-33, 0, 0, 0, 0, 0, 0, 0, 0, -3};
        try {
            int i = Integer.parseInt(this.pwd);
            int i2 = Integer.parseInt(str);
            bArr[1] = (byte) (i / 1000);
            bArr[2] = (byte) ((i % 1000) / 100);
            bArr[3] = (byte) ((i % 100) / 10);
            bArr[4] = (byte) (i % 10);
            bArr[5] = (byte) (i2 / 1000);
            bArr[6] = (byte) ((i2 % 1000) / 100);
            bArr[7] = (byte) ((i2 % 100) / 10);
            bArr[8] = (byte) (i2 % 10);
            writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr, true);
            this.cachenewpwd = str;
        } catch (NumberFormatException unused) {
        }
    }

    public void openmic(boolean z) {
        byte[] bArr = {-122, -16, 0, 104};
        if (z) {
            bArr[1] = -16;
        } else {
            bArr[1] = 15;
        }
        writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr, true);
        if (z) {
            return;
        }
        this.mTimeHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MyBluetoothGatt.11
            @Override // java.lang.Runnable
            public void run() {
                if (MyBluetoothGatt.this.savecolor != null) {
                    MyBluetoothGatt.this.savecolor.warmWhite = (byte) 0;
                    MyBluetoothGatt.this.savecolor.color = Color.argb(255, 128, 128, 128);
                    MyBluetoothGatt.this.setColor(128);
                }
            }
        }, 500L);
    }

    public void sendColor_m_data(boolean z, int i, int i2) {
        byte[] bArr = {1, -16, 0, 0, 0, 0};
        if (z) {
            bArr[1] = -16;
            this.isopenmyMIC = true;
        } else {
            this.isopenmyMIC = false;
            bArr[1] = 15;
        }
        bArr[2] = (byte) (i & 255);
        bArr[3] = (byte) (i2 & 255);
        bArr[4] = 0;
        bArr[5] = 24;
        writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, bArr, true);
    }

    public void sendNewMod(int i, int i2, int i3, int i4) {
        writeCharacteristic(DeviceUUID.CONSMART_BLE_NOTIFICATION_SERVICE_WRGB_UUID, DeviceUUID.CONSMART_BLE_NOTIFICATION_CHARACTERISTICS_WRGB_UUID, new byte[]{-102, 0, (byte) i, (byte) i2, (byte) i3, (byte) i4, -87});
    }
}
