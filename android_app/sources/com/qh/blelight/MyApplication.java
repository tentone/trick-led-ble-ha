package com.qh.blelight;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.soundrecorder.AudioRecordDemo;
import com.consmart.ble.AES2;
import com.qh.onehlight.R;
import java.util.HashMap;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class MyApplication extends Application {
    public static final String COMPANY_NAME = "^Color\\+|^Color-|^QHM";
    public static final String COMPANY_NAME_Trick = "^Trick-";
    public static final String COMPANY_NAME_XX = "^Flash";
    public static final int MAXCOLOR = 254;
    public static int blue = 0;
    public static int calpha = 0;
    public static int cblue = 0;
    public static int cgreen = 50;
    public static int changenum = 5;
    public static int cred = 150;
    public static int green = 50;
    public static int red = 150;
    public Handler AdjustHandler;
    public Handler ModHandler;
    public Handler MusicHandler;
    public Handler RecordingHandler;
    public Handler TimingHandler;
    public Handler errorHandler;
    public BluetoothLeService mBluetoothLeService;
    public AudioRecordDemo mMediaRecorderDemo;
    public MyExpandableListAdapter mMyExpandableListAdapter;
    public Handler mainHandler;
    public Handler popHandler;
    public SharedPreferences settings;
    public Handler soundControlHandler;
    public boolean isshow = false;
    public boolean isopenmic = false;
    public boolean isMic = false;
    public HashMap<String, BluetoothDevice> errorDevices = new HashMap<>();
    public int[] bgsrc = {R.drawable.ic_bg, R.drawable.background_1, R.drawable.background_2, R.drawable.background_3, R.drawable.background_4};
    public boolean ishaveDream = false;
    public boolean ishaveT = false;
    public boolean ishaveqicai = false;
    public boolean ishaveColor = false;
    private Handler myHandler = new Handler();
    private Runnable myRunnable = new Runnable() { // from class: com.qh.blelight.MyApplication.1
        @Override // java.lang.Runnable
        public void run() {
            MyApplication.this.reSetCMD();
        }
    };
    public boolean isOpenVisualizer = true;
    private boolean isOpenMusicHop = false;
    public int limitdb = 30;
    public int typebg = 0;
    public HashMap<String, String> mBadPhone = new HashMap<>();
    public Handler mShowHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.MyApplication.2
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            Bundle data = message.getData();
            if (data == null) {
                return false;
            }
            MyApplication.this.setCircleShow(data.getDouble("-db"), data.getDouble("db"));
            return false;
        }
    });
    private int num = 0;
    private int a = 0;
    private int dred = 0;
    private int dgreen = 0;
    private int dblue = 0;
    private boolean isfrst = true;
    private double cachesrcdb = 80.0d;
    private int cachenum = 0;
    private double cachedb = 5.0d;
    private double min = 1.0d;
    private long showtime = 0;
    public boolean isallopen = true;

    private int addcolor(int i, int i2) {
        int i3 = i + i2;
        if (i3 > 255) {
            i3 = 255;
        }
        if (i3 < 0) {
            return 0;
        }
        return i3;
    }

    public boolean isOpenMusicHop() {
        return true;
    }

    public void reSetCMD() {
    }

    public void setMusicHop(boolean z) {
        this.isOpenMusicHop = z;
    }

    public void setMusicHop(boolean z, boolean z2) {
        this.isOpenMusicHop = z;
        if (z2) {
            this.myHandler.removeCallbacks(this.myRunnable);
            this.myHandler.postDelayed(this.myRunnable, 500L);
        }
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        Log.e("", "-----myApplication-----");
        AudioRecordDemo audioRecordDemo = new AudioRecordDemo();
        this.mMediaRecorderDemo = audioRecordDemo;
        audioRecordDemo.mShowHandler = this.mShowHandler;
        this.mBadPhone.put("HUAWEI P7-L091", "HUAWEI P7-L091");
        if (this.mBadPhone.containsKey(Build.MODEL)) {
            this.isOpenVisualizer = false;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("BleLight", 0);
        this.settings = sharedPreferences;
        this.limitdb = sharedPreferences.getInt("limitdb", 30);
        this.typebg = this.settings.getInt("typebg", 0);
        AES2.setKey(new byte[]{-48, -7, -12, -116, 89, -94, 105, 29, 32, 83, -53, -38, -128, -124, 67, -109});
    }

    public void SMG(Message message) {
        Handler handler = this.AdjustHandler;
        if (handler != null) {
            handler.sendMessage(message);
        }
        Handler handler2 = this.ModHandler;
        if (handler2 != null) {
            handler2.sendMessage(message);
        }
        Handler handler3 = this.MusicHandler;
        if (handler3 != null) {
            handler3.sendMessage(message);
        }
        Handler handler4 = this.TimingHandler;
        if (handler4 != null) {
            handler4.sendMessage(message);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCircleShow(double d, double d2) {
        int i = this.limitdb;
        if (d2 <= 55 - i) {
            cblue = 5;
            return;
        }
        int i2 = (int) ((d * 7.0d) + d2);
        if (d2 <= 80 - i) {
            cblue = 5;
            return;
        }
        if (cblue <= 5) {
            cblue = (int) (i2 * 2.0f);
        } else {
            cblue = 5;
        }
        setMusicColor(cblue);
    }

    private void addColor() {
        int iFloor = (int) Math.floor(Math.random() * 6.0d);
        this.a = iFloor;
        if (iFloor < 0) {
            iFloor = 0;
        }
        this.a = iFloor;
        if (iFloor > 5) {
            iFloor = 5;
        }
        this.a = iFloor;
        if (iFloor == 0) {
            red = 254;
            blue = 0;
            int i = green + changenum;
            green = i;
            if (i >= 254) {
                green = 254;
                this.a = 1;
                return;
            }
            return;
        }
        if (iFloor == 1) {
            green = 254;
            int i2 = red - changenum;
            red = i2;
            blue = 0;
            if (i2 <= 0) {
                red = 0;
                this.a = 2;
                return;
            }
            return;
        }
        if (iFloor == 2) {
            green = 254;
            int i3 = blue + changenum;
            blue = i3;
            red = 0;
            if (i3 >= 254) {
                blue = 200;
                this.a = 3;
                return;
            }
            return;
        }
        if (iFloor == 3) {
            blue = 254;
            int i4 = green - changenum;
            green = i4;
            red = 0;
            if (i4 <= 0) {
                green = 0;
                this.a = 4;
                return;
            }
            return;
        }
        if (iFloor == 4) {
            blue = 254;
            int i5 = red + changenum;
            red = i5;
            green = 0;
            if (i5 >= 254) {
                red = 254;
                this.a = 5;
                return;
            }
            return;
        }
        if (iFloor != 5) {
            if (iFloor == 6) {
                int i6 = changenum;
                red = 100 - i6;
                blue = 100 - i6;
                green = 100 - i6;
                return;
            }
            return;
        }
        red = 254;
        int i7 = blue - changenum;
        blue = i7;
        green = 0;
        if (i7 <= 0) {
            blue = 0;
            this.a = 0;
        }
    }

    private synchronized void setcolor(double d, double d2) {
        cred = 0;
        cgreen = 0;
        cblue = 0;
        calpha = 0;
        int i = (int) ((d * 7.0d) + d2);
        if (d2 <= 80 - this.limitdb) {
            cblue = 5;
            return;
        }
        cblue = (int) (i * 2.0f);
        cred = addcolor(0, 0);
        cgreen = addcolor(cgreen, 0);
        int iAddcolor = addcolor(cblue, 0);
        cblue = iAddcolor;
        this.dred = cred;
        this.dgreen = cgreen;
        this.dblue = iAddcolor;
    }

    private void setMusicColor(int i) {
        MyBluetoothGatt myBluetoothGatt;
        MyBluetoothGatt myBluetoothGatt2;
        BluetoothLeService bluetoothLeService = this.mBluetoothLeService;
        if (bluetoothLeService == null) {
            return;
        }
        for (String str : bluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt2 = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null) {
                byte b = myBluetoothGatt2.datas[2];
            }
            int iArgb = Color.argb(255, Color.red(i), Color.green(i), Color.blue(i));
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2 && MainActivity.ControlMACs.containsKey(str)) {
                myBluetoothGatt.setMusicColor(iArgb);
            }
        }
    }

    public void openAll(boolean z) {
        MyBluetoothGatt myBluetoothGatt;
        Log.e("", "mac = " + z);
        this.isallopen = z;
        Log.e("-", "isallopen = " + this.isallopen);
        for (String str : this.mBluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && MainActivity.ControlMACs.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.openLight(z);
            }
        }
    }

    public void setData(byte[] bArr) {
        MyBluetoothGatt myBluetoothGatt;
        if (this.mBluetoothLeService == null) {
            return;
        }
        for (String str : MainActivity.ControlMACs.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.setValues(bArr);
            }
        }
    }

    public void setData(String str, byte[] bArr) {
        MyBluetoothGatt myBluetoothGatt;
        BluetoothLeService bluetoothLeService = this.mBluetoothLeService;
        if (bluetoothLeService != null && bluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
            myBluetoothGatt.setValues(bArr);
        }
    }

    public void setBG(int i) {
        this.typebg = i;
        SharedPreferences.Editor editorEdit = this.settings.edit();
        editorEdit.putInt("typebg", i);
        editorEdit.commit();
    }

    public void readhuancai(String str) {
        MyBluetoothGatt myBluetoothGatt;
        BluetoothLeService bluetoothLeService = this.mBluetoothLeService;
        if (bluetoothLeService != null && bluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
            myBluetoothGatt.readhuancai();
        }
    }

    public void disconn(String str) {
        MyBluetoothGatt myBluetoothGatt;
        this.mBluetoothLeService.unlinkBleDevices.put(str, str);
        if (!this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) || (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) == null) {
            return;
        }
        myBluetoothGatt.stopLEService();
    }

    public void checkpwd(String str, String str2) {
        MyBluetoothGatt myBluetoothGatt;
        BluetoothLeService bluetoothLeService = this.mBluetoothLeService;
        if (bluetoothLeService != null && bluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
            myBluetoothGatt.checkpwd(str2);
        }
    }

    public boolean setqicaidata(String str, int i, int i2, boolean z) {
        MyBluetoothGatt myBluetoothGatt;
        BluetoothLeService bluetoothLeService = this.mBluetoothLeService;
        if (bluetoothLeService == null || !bluetoothLeService.MyBluetoothGatts.containsKey(str) || (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) == null || myBluetoothGatt.mConnectionState != 2) {
            return false;
        }
        myBluetoothGatt.setqicaidata(i, i2, z);
        return true;
    }

    public void resetpwd(String str, String str2) {
        MyBluetoothGatt myBluetoothGatt;
        BluetoothLeService bluetoothLeService = this.mBluetoothLeService;
        if (bluetoothLeService != null && bluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
            myBluetoothGatt.setpwd(str2);
        }
    }

    public boolean ishavess() {
        MyBluetoothGatt myBluetoothGatt;
        for (String str : this.mBluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2 && myBluetoothGatt.mLEdevice.getName().contains("Triones#")) {
                return true;
            }
        }
        return false;
    }

    public boolean isopenmic() {
        MyBluetoothGatt myBluetoothGatt;
        BluetoothLeService bluetoothLeService = this.mBluetoothLeService;
        if (bluetoothLeService == null) {
            return false;
        }
        for (String str : bluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2 && (myBluetoothGatt.mLEdevice.getName().contains("Triones^") || myBluetoothGatt.mLEdevice.getName().contains("Color+"))) {
                return true;
            }
        }
        return false;
    }

    public void openmic(boolean z) {
        MyBluetoothGatt myBluetoothGatt;
        for (String str : this.mBluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2 && (myBluetoothGatt.mLEdevice.getName().contains("Triones^") || myBluetoothGatt.mLEdevice.getName().contains("Color+"))) {
                myBluetoothGatt.openmic(z);
            }
        }
    }

    public boolean isHaveColor_A_or_QHM() {
        MyBluetoothGatt myBluetoothGatt;
        Pattern patternCompile = Pattern.compile(COMPANY_NAME);
        for (String str : this.mBluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2 && myBluetoothGatt.mLEdevice != null && myBluetoothGatt.mLEdevice.getName() != null && patternCompile.matcher(myBluetoothGatt.mLEdevice.getName()).find()) {
                return true;
            }
        }
        return false;
    }

    public void sendColor_m_data(boolean z, int i, int i2) {
        MyBluetoothGatt myBluetoothGatt;
        for (String str : this.mBluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2 && myBluetoothGatt.mLEdevice != null && myBluetoothGatt.mLEdevice.getName() != null) {
                myBluetoothGatt.sendColor_m_data(z, i, i2);
            }
        }
    }

    public boolean isHaveX() {
        MyBluetoothGatt myBluetoothGatt;
        Pattern patternCompile = Pattern.compile(COMPANY_NAME_XX);
        for (String str : this.mBluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2 && myBluetoothGatt.mLEdevice != null && myBluetoothGatt.mLEdevice.getName() != null && patternCompile.matcher(myBluetoothGatt.mLEdevice.getName()).find()) {
                return true;
            }
        }
        return false;
    }

    public boolean isOnlyHaveX() {
        MyBluetoothGatt myBluetoothGatt;
        Pattern patternCompile = Pattern.compile(COMPANY_NAME_XX);
        for (String str : this.mBluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2 && myBluetoothGatt.mLEdevice != null && myBluetoothGatt.mLEdevice.getName() != null && !patternCompile.matcher(myBluetoothGatt.mLEdevice.getName()).find()) {
                return false;
            }
        }
        return true;
    }

    public boolean isOnlyHaveTrick() {
        MyBluetoothGatt myBluetoothGatt;
        Pattern patternCompile = Pattern.compile(COMPANY_NAME_Trick);
        for (String str : this.mBluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2 && myBluetoothGatt.mLEdevice != null && myBluetoothGatt.mLEdevice.getName() != null && patternCompile.matcher(myBluetoothGatt.mLEdevice.getName()).find()) {
                return true;
            }
        }
        return false;
    }

    public void sendNewMod(int i, int i2, int i3, int i4) {
        MyBluetoothGatt myBluetoothGatt;
        Log.e("--", "speed=" + i2 + " brightness=" + i3);
        Pattern patternCompile = Pattern.compile(COMPANY_NAME_XX);
        for (String str : this.mBluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2 && myBluetoothGatt.mLEdevice != null && myBluetoothGatt.mLEdevice.getName() != null && MainActivity.ControlMACs.containsKey(str) && patternCompile.matcher(myBluetoothGatt.mLEdevice.getName()).find()) {
                myBluetoothGatt.sendNewMod(i + 1, i2, i3, i4);
            }
        }
    }
}
