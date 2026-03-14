package com.qh.blelight;

import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.view.PointerIconCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.android.soundrecorder.RecordingActivity;
import com.qh.blelight.BluetoothLeService;
import com.qh.blelight.scroll.SwitchViewDemoActivity;
import com.qh.data.SwitchInterface;
import com.qh.managegroup.DragListActivity;
import com.qh.onehlight.R;
import com.qh.tools.DBAdapter;
import com.qh.tools.DBTable;
import com.qh.tools.Tool;
import com.qh.tools.tools;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity extends TabActivity {
    public static Hashtable<String, String> ControlMACs = new Hashtable<>();
    public static int heightPixels = 0;
    public static TabHost mTabHost = null;
    public static final int mic_msg = 60001;
    public static int widthPixels;
    private Context context;
    public DBAdapter dbAdapter;
    private DrawerLayout drawerLayout;
    private ImageView img_adjust;
    private ImageView img_list;
    private ImageView img_mid;
    private ImageView img_mod;
    private ImageView img_music;
    private ImageView img_recording;
    private ImageView img_set;
    private ImageView img_timing;
    private RelativeLayout leftLayout;
    private RelativeLayout lin_about;
    private RelativeLayout lin_change;
    private RelativeLayout lin_directions;
    private RelativeLayout lin_sethuancai;
    private RelativeLayout lin_setqicai;
    private RelativeLayout lin_yaoyiyao;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService mBluetoothLeService;
    public MyApplication mMyApplication;
    public MyExpandableListAdapter mMyExpandableListAdapter;
    public Resources mResources;
    private RadioGroup main_radio;
    public ExpandableListView myExpandableListView;
    private RelativeLayout rb_adjust;
    private RelativeLayout rb_mod;
    private RelativeLayout rb_music;
    private RelativeLayout rb_recording;
    private RelativeLayout rb_timing;
    private RelativeLayout rel_management;
    private RelativeLayout rel_math;
    private RelativeLayout rel_permission;
    private RelativeLayout rightLayout;
    private ServiceConnection sc;
    private SensorManager sensorManager;
    public SharedPreferences settings;
    private TextView tv_finish;
    private TextView tv_goto;
    private TextView tx_adjust;
    private TextView tx_mod;
    private TextView tx_music;
    private TextView tx_recording;
    private TextView tx_timing;
    private Vibrator vibrator;
    private ImageView yao_open;
    private int type = 0;
    public int isExpandedID = -1;
    public int len = 5;
    private ArrayList<String> groupNames = new ArrayList<>();
    private ArrayList<Integer> groupIDs = new ArrayList<>();
    private boolean isOpenyaoyiyao = false;
    private int rb_num = 0;
    public BluetoothLeService.Checkpwd mCheckpwd = new BluetoothLeService.Checkpwd() { // from class: com.qh.blelight.MainActivity.1
        @Override // com.qh.blelight.BluetoothLeService.Checkpwd
        public void checkpwd(String str, int i, String str2) {
            if (i != 0) {
                if (i == 1) {
                    MainActivity.this.mHandler.sendEmptyMessage(4003);
                }
            } else {
                Message message = new Message();
                message.what = 4001;
                message.obj = str;
                MainActivity.this.mHandler.sendMessage(message);
            }
        }
    };
    public Handler mainHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.MainActivity.2
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what == 60001) {
                MainActivity.this.mMyApplication.isopenmic = false;
                MainActivity.this.uimic();
                if (MainActivity.this.mMyApplication.AdjustHandler != null) {
                    MainActivity.this.mMyApplication.AdjustHandler.sendEmptyMessage(5);
                }
            }
            return false;
        }
    });
    public Handler mHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.MainActivity.3
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        int i2 = MainActivity.this.rb_num;
                        if (i2 != 0) {
                            if (i2 == 1) {
                                if (MainActivity.this.mMyApplication.MusicHandler != null) {
                                    MainActivity.this.mMyApplication.MusicHandler.sendEmptyMessage(0);
                                }
                            } else if (i2 == 2 && MainActivity.this.mMyApplication.ModHandler != null) {
                                MainActivity.this.mMyApplication.ModHandler.sendEmptyMessage(0);
                            }
                        } else if (MainActivity.this.mMyApplication.AdjustHandler != null) {
                            MainActivity.this.mMyApplication.AdjustHandler.sendEmptyMessage(0);
                        }
                    } else {
                        switch (i) {
                            case 4001:
                                MainActivity.this.setpwd((String) message.obj);
                                break;
                            case 4002:
                                String str = (String) message.obj;
                                Intent intent = new Intent(MainActivity.this, (Class<?>) ResetpwdActivity.class);
                                intent.putExtra("addr", str);
                                MainActivity.this.startActivity(intent);
                                break;
                            case 4003:
                                Toast.makeText(MainActivity.this.context, MainActivity.this.mResources.getString(R.string.pwdisok), 0).show();
                                break;
                        }
                    }
                } else if (MainActivity.this.groupNames.size() > message.arg1) {
                    MainActivity.this.myExpandableListView.collapseGroup(message.arg1);
                }
            } else if (MainActivity.this.groupNames.size() > message.arg1) {
                MainActivity.this.myExpandableListView.expandGroup(message.arg1);
            }
            return false;
        }
    });
    public Handler mOperateHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.MainActivity.4
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            MyBluetoothGatt myBluetoothGatt;
            MyBluetoothGatt myBluetoothGatt2;
            int i = message.what;
            if (i == 0) {
                MainActivity.this.mMyExpandableListAdapter.notifyDataSetChanged();
            } else if (i == 1) {
                MainActivity.this.setListData();
                MainActivity.this.mMyExpandableListAdapter.setgroupNames(MainActivity.this.groupNames, MainActivity.this.groupIDs);
                MainActivity.this.mMyExpandableListAdapter.notifyDataSetChanged();
            } else if (i == 3) {
                Log.e("", "--" + message.getData().getString("deviceAddr", ""));
                MainActivity.this.mMyExpandableListAdapter.notifyDataSetChanged();
                if (MainActivity.this.mMyApplication.TimingHandler != null) {
                    String string = message.getData().getString("deviceAddr", "");
                    Message message2 = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("deviceAddr", "" + string);
                    message2.setData(bundle);
                    message2.what = 3;
                    MainActivity.this.mMyApplication.TimingHandler.sendMessage(message2);
                }
                MainActivity.this.mMyApplication.ishaveDream = false;
                MainActivity.this.mMyApplication.ishaveT = false;
                MainActivity.this.mMyApplication.ishaveqicai = false;
                MainActivity.this.mMyApplication.ishaveColor = false;
                for (String str : MainActivity.this.mBluetoothLeService.MyBluetoothGatts.keySet()) {
                    if (MainActivity.this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = MainActivity.this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null) {
                        if (myBluetoothGatt.mConnectionState == 2) {
                            try {
                                if (Pattern.compile("^Color\\+|^Color-").matcher(myBluetoothGatt.mLEdevice.getName()).find()) {
                                    MainActivity.this.mMyApplication.ishaveColor = true;
                                } else if (myBluetoothGatt.mLEdevice.getName().contains("Dream")) {
                                    MainActivity.this.mMyApplication.ishaveDream = true;
                                } else if (!myBluetoothGatt.mLEdevice.getName().contains("Flash")) {
                                    MainActivity.this.mMyApplication.ishaveT = true;
                                }
                                if (myBluetoothGatt.mLEdevice.getName().contains("Triones:") || myBluetoothGatt.mLEdevice.getName().contains("Triones#")) {
                                    MainActivity.this.mMyApplication.ishaveqicai = true;
                                }
                            } catch (Exception unused) {
                            }
                        }
                    }
                }
                if (MainActivity.this.mMyApplication.ishaveDream || MainActivity.this.mMyApplication.isHaveX()) {
                    MainActivity.this.lin_sethuancai.setVisibility(0);
                } else {
                    MainActivity.this.lin_sethuancai.setVisibility(8);
                }
                if (MainActivity.this.mMyApplication.ishaveqicai) {
                    MainActivity.this.lin_setqicai.setVisibility(0);
                } else {
                    MainActivity.this.lin_setqicai.setVisibility(8);
                }
                if (MainActivity.this.mMyApplication.AdjustHandler != null) {
                    MainActivity.this.mMyApplication.AdjustHandler.sendEmptyMessage(4);
                }
                Log.e("--", "ishaveDream=" + MainActivity.this.mMyApplication.ishaveDream);
                if (MainActivity.this.mMyApplication.ModHandler != null) {
                    MainActivity.this.mMyApplication.ModHandler.sendEmptyMessage(1);
                }
                if (MainActivity.this.mMyApplication.RecordingHandler != null) {
                    MainActivity.this.mMyApplication.RecordingHandler.sendEmptyMessage(0);
                }
            } else if (i == 4) {
                MainActivity.this.mMyExpandableListAdapter.notifyDataSetChanged();
                if (MainActivity.this.mMyApplication.TimingHandler != null) {
                    String string2 = message.getData().getString("deviceAddr", "");
                    Message message3 = new Message();
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("deviceAddr", "" + string2);
                    message3.setData(bundle2);
                    message3.what = 4;
                    MainActivity.this.mMyApplication.TimingHandler.sendMessage(message3);
                }
                try {
                    MainActivity.this.mMyApplication.ishaveDream = false;
                    MainActivity.this.mMyApplication.ishaveT = false;
                    MainActivity.this.mMyApplication.ishaveqicai = false;
                    for (String str2 : MainActivity.this.mBluetoothLeService.MyBluetoothGatts.keySet()) {
                        if (MainActivity.this.mBluetoothLeService.MyBluetoothGatts.containsKey(str2) && (myBluetoothGatt2 = MainActivity.this.mBluetoothLeService.MyBluetoothGatts.get(str2)) != null && myBluetoothGatt2.mConnectionState == 2) {
                            try {
                                if (Pattern.compile("^Color\\+|^Color-").matcher(myBluetoothGatt2.mLEdevice.getName()).find()) {
                                    MainActivity.this.mMyApplication.ishaveColor = true;
                                } else if (myBluetoothGatt2.mLEdevice.getName().contains("Dream")) {
                                    MainActivity.this.mMyApplication.ishaveDream = true;
                                } else if (!myBluetoothGatt2.mLEdevice.getName().contains("Flash")) {
                                    MainActivity.this.mMyApplication.ishaveT = true;
                                }
                                if (myBluetoothGatt2.mLEdevice.getName().contains("Triones:") || myBluetoothGatt2.mLEdevice.getName().contains("Triones#")) {
                                    MainActivity.this.mMyApplication.ishaveqicai = true;
                                }
                            } catch (Exception unused2) {
                            }
                        }
                    }
                    if (MainActivity.this.mMyApplication.ishaveDream || MainActivity.this.mMyApplication.isHaveX()) {
                        MainActivity.this.lin_sethuancai.setVisibility(0);
                    } else {
                        MainActivity.this.lin_sethuancai.setVisibility(8);
                    }
                    if (MainActivity.this.mMyApplication.ishaveqicai) {
                        MainActivity.this.lin_setqicai.setVisibility(0);
                    } else {
                        MainActivity.this.lin_setqicai.setVisibility(8);
                    }
                    if (MainActivity.this.mMyApplication.AdjustHandler != null) {
                        MainActivity.this.mMyApplication.AdjustHandler.sendEmptyMessage(4);
                    }
                    Log.e("--", "ishaveDream=" + MainActivity.this.mMyApplication.ishaveDream);
                    if (MainActivity.this.mMyApplication.ModHandler != null) {
                        MainActivity.this.mMyApplication.ModHandler.sendEmptyMessage(1);
                    }
                } catch (ConcurrentModificationException unused3) {
                }
                if (MainActivity.this.mMyApplication.RecordingHandler != null) {
                    MainActivity.this.mMyApplication.RecordingHandler.sendEmptyMessage(0);
                }
            } else if (i == 400) {
                String string3 = message.getData().getString("deviceAddr", "");
                if (MainActivity.this.mMyApplication.mBluetoothLeService.mDevices.containsKey(string3)) {
                    MainActivity.this.mMyApplication.errorDevices.put(string3, MainActivity.this.mMyApplication.mBluetoothLeService.mDevices.get(string3));
                }
                if (MainActivity.this.mMyApplication.errorHandler != null) {
                    MainActivity.this.mMyApplication.errorHandler.sendEmptyMessage(0);
                }
            }
            return false;
        }
    });
    private SwitchInterface msetSwitchInterface = new SwitchInterface() { // from class: com.qh.blelight.MainActivity.26
        @Override // com.qh.data.SwitchInterface
        public void LightSwitch(String str, boolean z) {
            MyBluetoothGatt myBluetoothGatt;
            if (MainActivity.this.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = MainActivity.this.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.openLight(z);
            }
        }
    };
    private boolean isopenGPS = false;
    private SensorEventListener sensorEventListener = new SensorEventListener() { // from class: com.qh.blelight.MainActivity.27
        private long time = 0;

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] fArr = sensorEvent.values;
            float f = fArr[0];
            float f2 = fArr[1];
            float f3 = fArr[2];
            if (Math.abs(f) > 7.3f) {
                Date date = new Date();
                if (date.getTime() - this.time >= 700) {
                    MainActivity.this.vibrator.vibrate(100L);
                    Message message = new Message();
                    message.what = 3;
                    MainActivity.this.mHandler.sendMessage(message);
                    this.time = date.getTime();
                }
            }
        }
    };
    public boolean isRequest = false;
    private int isStart = 0;

    public void uimic() {
    }

    public static TabHost getmTabHost() {
        return mTabHost;
    }

    @Override // android.app.ActivityGroup, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main1);
        this.context = this;
        this.mResources = getResources();
        TabHost tabHost = getTabHost();
        mTabHost = tabHost;
        tabHost.getTabWidget().setStripEnabled(false);
        this.mMyApplication = (MyApplication) getApplication();
        DBAdapter dBAdapterInit = DBAdapter.init(this);
        this.dbAdapter = dBAdapterInit;
        dBAdapterInit.open();
        setListData();
        SharedPreferences sharedPreferences = getSharedPreferences("BleLight", 0);
        this.settings = sharedPreferences;
        this.mMyApplication.setMusicHop(sharedPreferences.getBoolean("isOpenMusicHop", false), true);
        this.mMyApplication.mainHandler = this.mainHandler;
        getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthPixels = displayMetrics.widthPixels;
        heightPixels = displayMetrics.heightPixels;
        Log.e("--", "widthPixels=" + widthPixels + " heightPixels=" + heightPixels);
        initialize();
        TabHost tabHost2 = mTabHost;
        tabHost2.addTab(tabHost2.newTabSpec("TAG1").setIndicator("0").setContent(new Intent(this, (Class<?>) AdjustActivity.class)));
        TabHost tabHost3 = mTabHost;
        tabHost3.addTab(tabHost3.newTabSpec("TAG2").setIndicator("1").setContent(new Intent(this, (Class<?>) MusicActivity.class)));
        TabHost tabHost4 = mTabHost;
        tabHost4.addTab(tabHost4.newTabSpec("TAG3").setIndicator("2").setContent(new Intent(this, (Class<?>) ModActivity.class)));
        TabHost tabHost5 = mTabHost;
        tabHost5.addTab(tabHost5.newTabSpec("TAG4").setIndicator("3").setContent(new Intent(this, (Class<?>) RecordingActivity.class)));
        TabHost tabHost6 = mTabHost;
        tabHost6.addTab(tabHost6.newTabSpec("TAG5").setIndicator("4").setContent(new Intent(this, (Class<?>) TimingActivity.class)));
        this.main_radio = (RadioGroup) findViewById(R.id.main_radio);
        this.main_radio = (RadioGroup) findViewById(R.id.main_radio);
        this.rb_adjust = (RelativeLayout) findViewById(R.id.adjust_activity);
        this.rb_music = (RelativeLayout) findViewById(R.id.music_activity);
        this.rb_mod = (RelativeLayout) findViewById(R.id.mod_activity);
        this.rb_recording = (RelativeLayout) findViewById(R.id.recording_activity);
        this.rb_timing = (RelativeLayout) findViewById(R.id.timing_activity);
        this.rel_management = (RelativeLayout) findViewById(R.id.rel_management);
        this.lin_directions = (RelativeLayout) findViewById(R.id.lin_directions);
        this.lin_sethuancai = (RelativeLayout) findViewById(R.id.lin_sethuancai);
        this.lin_setqicai = (RelativeLayout) findViewById(R.id.lin_setqicai);
        this.rel_math = (RelativeLayout) findViewById(R.id.rel_math);
        this.lin_change = (RelativeLayout) findViewById(R.id.lin_change);
        this.lin_about = (RelativeLayout) findViewById(R.id.lin_about);
        this.lin_yaoyiyao = (RelativeLayout) findViewById(R.id.lin_yaoyiyao);
        this.yao_open = (ImageView) findViewById(R.id.yao_open);
        this.img_adjust = (ImageView) findViewById(R.id.img_adjust);
        this.img_music = (ImageView) findViewById(R.id.img_music);
        this.img_mod = (ImageView) findViewById(R.id.img_mod);
        this.img_recording = (ImageView) findViewById(R.id.img_recording);
        this.img_timing = (ImageView) findViewById(R.id.img_timing);
        this.tx_adjust = (TextView) findViewById(R.id.tx_adjust);
        this.tx_music = (TextView) findViewById(R.id.tx_music);
        this.tx_mod = (TextView) findViewById(R.id.tx_mod);
        this.tx_recording = (TextView) findViewById(R.id.tx_recording);
        this.tx_timing = (TextView) findViewById(R.id.tx_timing);
        this.img_list = (ImageView) findViewById(R.id.img_list);
        this.img_set = (ImageView) findViewById(R.id.img_set);
        this.img_mid = (ImageView) findViewById(R.id.img_mid);
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        this.leftLayout = (RelativeLayout) findViewById(R.id.left);
        this.rightLayout = (RelativeLayout) findViewById(R.id.right);
        this.drawerLayout.setScrimColor(0);
        if (this.mMyApplication.bgsrc.length > this.mMyApplication.typebg) {
            this.rel_math.setBackgroundResource(this.mMyApplication.bgsrc[this.mMyApplication.typebg]);
        }
        this.tx_adjust.setTextColor(-11872414);
        this.rb_adjust.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.mMyApplication.isMic = false;
                MainActivity.mTabHost.setCurrentTab(0);
                MainActivity mainActivity = MainActivity.this;
                mainActivity.setTabBackground(mainActivity.type);
                MainActivity.this.rb_adjust.setBackgroundColor(0);
                MainActivity.this.img_adjust.setImageResource(R.drawable.ic_adjust_n);
                MainActivity.this.type = 0;
                MainActivity.this.tx_adjust.setTextColor(-11872414);
                MainActivity.this.rb_num = 0;
                boolean z = MainActivity.this.mMyApplication.mMediaRecorderDemo.isPlay;
                MainActivity.this.mMyApplication.mMediaRecorderDemo.stopRecord();
                if (z) {
                    MainActivity.this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MainActivity.5.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.this.mMyApplication.reSetCMD();
                        }
                    }, 500L);
                }
            }
        });
        this.rb_music.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.mMyApplication.isMic = false;
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        String[] strArr = {"android.permission.READ_MEDIA_AUDIO"};
                        if (MainActivity.this.checkSelfPermission("android.permission.READ_MEDIA_AUDIO") != 0) {
                            MainActivity.this.requestPermissions(strArr, 2012);
                            return;
                        }
                    } else {
                        String[] strArr2 = {"android.permission.READ_EXTERNAL_STORAGE"};
                        if (MainActivity.this.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                            MainActivity.this.requestPermissions(strArr2, PointerIconCompat.TYPE_NO_DROP);
                            return;
                        }
                    }
                    String[] strArr3 = {"android.permission.RECORD_AUDIO"};
                    if (MainActivity.this.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                        MainActivity.this.requestPermissions(strArr3, PointerIconCompat.TYPE_VERTICAL_TEXT);
                        return;
                    }
                }
                if (!Environment.getExternalStorageState().equals("mounted")) {
                    Toast.makeText(MainActivity.this.context, "SD card Error !", 1).show();
                    return;
                }
                Environment.getExternalStorageDirectory();
                try {
                    new Visualizer(new MediaPlayer().getAudioSessionId());
                    new Visualizer.MeasurementPeakRms();
                    MainActivity.mTabHost.setCurrentTab(1);
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.setTabBackground(mainActivity.type);
                    MainActivity.this.rb_music.setBackgroundColor(0);
                    MainActivity.this.img_music.setImageResource(R.drawable.ic_music_n);
                    MainActivity.this.type = 1;
                    MainActivity.this.tx_music.setTextColor(-11872414);
                    MainActivity.this.rb_num = 1;
                    boolean z = MainActivity.this.mMyApplication.mMediaRecorderDemo.isPlay;
                    MainActivity.this.mMyApplication.mMediaRecorderDemo.stopRecord();
                    if (z) {
                        MainActivity.this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MainActivity.6.1
                            @Override // java.lang.Runnable
                            public void run() {
                                MainActivity.this.mMyApplication.reSetCMD();
                            }
                        }, 500L);
                    }
                } catch (Exception unused) {
                    Toast.makeText(MainActivity.this.context, MainActivity.this.mResources.getString(R.string.notsupport), 1).show();
                }
            }
        });
        this.rb_mod.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.mMyApplication.isMic = false;
                MainActivity.mTabHost.setCurrentTab(2);
                MainActivity mainActivity = MainActivity.this;
                mainActivity.setTabBackground(mainActivity.type);
                MainActivity.this.rb_mod.setBackgroundColor(0);
                MainActivity.this.img_mod.setImageResource(R.drawable.ic_mod_n);
                MainActivity.this.type = 2;
                MainActivity.this.tx_mod.setTextColor(-11872414);
                MainActivity.this.rb_num = 2;
                boolean z = MainActivity.this.mMyApplication.mMediaRecorderDemo.isPlay;
                MainActivity.this.mMyApplication.mMediaRecorderDemo.stopRecord();
                if (z) {
                    MainActivity.this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MainActivity.7.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.this.mMyApplication.reSetCMD();
                        }
                    }, 500L);
                }
            }
        });
        this.rb_recording.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    String[] strArr = {"android.permission.RECORD_AUDIO"};
                    if (MainActivity.this.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                        MainActivity.this.requestPermissions(strArr, PointerIconCompat.TYPE_VERTICAL_TEXT);
                        return;
                    }
                }
                MainActivity.mTabHost.setCurrentTab(3);
                MainActivity mainActivity = MainActivity.this;
                mainActivity.setTabBackground(mainActivity.type);
                MainActivity.this.rb_recording.setBackgroundColor(0);
                MainActivity.this.img_recording.setImageResource(R.drawable.ic_recording_n);
                MainActivity.this.type = 3;
                MainActivity.this.tx_recording.setTextColor(-11872414);
                MainActivity.this.rb_num = 3;
                if (MainActivity.this.mMyApplication.MusicHandler != null) {
                    MainActivity.this.mMyApplication.MusicHandler.sendEmptyMessage(2);
                }
                MainActivity.this.mMyApplication.isopenmic = false;
                MainActivity.this.mMyApplication.isMic = true;
                MainActivity.this.mMyApplication.openmic(MainActivity.this.mMyApplication.isopenmic);
                if (MainActivity.this.mMyApplication.AdjustHandler != null) {
                    MainActivity.this.mMyApplication.AdjustHandler.sendEmptyMessage(5);
                }
                if (MainActivity.this.mMyApplication.RecordingHandler != null) {
                    MainActivity.this.mMyApplication.RecordingHandler.sendEmptyMessageDelayed(0, 500L);
                    Log.e("88", " modid RecordingHandler!=null");
                } else {
                    Log.e("88", " modid RecordingHandler==null");
                }
            }
        });
        this.rb_timing.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.mMyApplication.isMic = false;
                MainActivity.mTabHost.setCurrentTab(4);
                MainActivity mainActivity = MainActivity.this;
                mainActivity.setTabBackground(mainActivity.type);
                MainActivity.this.rb_timing.setBackgroundColor(0);
                MainActivity.this.img_timing.setImageResource(R.drawable.ic_timing_n);
                MainActivity.this.type = 4;
                MainActivity.this.tx_timing.setTextColor(-11872414);
                MainActivity.this.rb_num = 4;
                boolean z = MainActivity.this.mMyApplication.mMediaRecorderDemo.isPlay;
                MainActivity.this.mMyApplication.mMediaRecorderDemo.stopRecord();
            }
        });
        this.img_mid.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.mMyApplication.openAll(!MainActivity.this.mMyApplication.isallopen);
                new Handler().postDelayed(new Runnable() { // from class: com.qh.blelight.MainActivity.10.1
                    @Override // java.lang.Runnable
                    public void run() {
                        MainActivity.this.mMyApplication.openAll(MainActivity.this.mMyApplication.isallopen);
                    }
                }, 100L);
                new Handler().postDelayed(new Runnable() { // from class: com.qh.blelight.MainActivity.10.2
                    @Override // java.lang.Runnable
                    public void run() {
                        MainActivity.this.mMyApplication.openAll(MainActivity.this.mMyApplication.isallopen);
                    }
                }, 200L);
                if (MainActivity.this.mMyApplication.isallopen) {
                    MainActivity.this.img_mid.setImageResource(R.drawable.ic_all_open);
                } else {
                    MainActivity.this.img_mid.setImageResource(R.drawable.ic_all_close);
                }
                if (MainActivity.this.mOperateHandler != null) {
                    MainActivity.this.mOperateHandler.sendEmptyMessageDelayed(0, 400L);
                }
                MainActivity.this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MainActivity.10.3
                    @Override // java.lang.Runnable
                    public void run() {
                        MainActivity.this.mMyApplication.reSetCMD();
                    }
                }, 500L);
            }
        });
        this.img_list.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.drawerLayout.openDrawer((View) MainActivity.this.leftLayout, false);
            }
        });
        this.img_set.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.drawerLayout.openDrawer((View) MainActivity.this.rightLayout, false);
            }
        });
        this.rel_management.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.13
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.startActivityForResult(new Intent(MainActivity.this, (Class<?>) DragListActivity.class), 100);
            }
        });
        this.lin_yaoyiyao.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.14
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.isOpenyaoyiyao = !r2.isOpenyaoyiyao;
                MainActivity mainActivity = MainActivity.this;
                mainActivity.setSensorManagerListener(mainActivity.isOpenyaoyiyao);
                if (MainActivity.this.isOpenyaoyiyao) {
                    MainActivity.this.yao_open.setImageResource(R.drawable.ic_open);
                } else {
                    MainActivity.this.yao_open.setImageResource(R.drawable.ic_close);
                }
            }
        });
        this.lin_directions.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.15
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) SwitchViewDemoActivity.class));
            }
        });
        this.lin_about.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.16
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) AboutActivity.class));
            }
        });
        this.lin_sethuancai.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.17
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) SethuancaiActivity.class));
            }
        });
        this.lin_setqicai.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.18
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) SetqicaiActivity.class));
            }
        });
        this.sensorManager = (SensorManager) getSystemService("sensor");
        this.vibrator = (Vibrator) getSystemService("vibrator");
        this.rb_num = 0;
        if (this.mMyApplication.isallopen) {
            this.img_mid.setImageResource(R.drawable.ic_all_open);
        } else {
            this.img_mid.setImageResource(R.drawable.ic_all_close);
        }
        this.lin_change.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.19
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, (Class<?>) ChangeBgActivity.class));
            }
        });
        findViewById(R.id.rigpanelContent1).setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.20
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
            }
        });
    }

    public void setTabBackground(int i) {
        if (i == 0) {
            this.rb_adjust.setBackgroundColor(0);
            this.img_adjust.setImageResource(R.drawable.ic_adjust_u);
            this.tx_adjust.setTextColor(-1);
            return;
        }
        if (i == 1) {
            this.rb_music.setBackgroundColor(0);
            this.img_music.setImageResource(R.drawable.ic_music_u);
            this.tx_music.setTextColor(-1);
            return;
        }
        if (i == 2) {
            this.rb_mod.setBackgroundColor(0);
            this.img_mod.setImageResource(R.drawable.ic_mod_u);
            this.tx_mod.setTextColor(-1);
        } else if (i == 3) {
            this.rb_recording.setBackgroundColor(0);
            this.img_recording.setImageResource(R.drawable.ic_recording_u);
            this.tx_recording.setTextColor(-1);
        } else {
            if (i != 4) {
                return;
            }
            this.rb_timing.setBackgroundColor(0);
            this.img_timing.setImageResource(R.drawable.ic_timing_u);
            this.tx_timing.setTextColor(-1);
        }
    }

    public void setListData() {
        this.groupNames.clear();
        this.groupIDs.clear();
        this.groupNames.add(this.mResources.getString(R.string.My_device));
        this.groupIDs.add(0);
        Cursor cursorQueryAllData = this.dbAdapter.queryAllData();
        while (cursorQueryAllData.moveToNext()) {
            String string = cursorQueryAllData.getString(cursorQueryAllData.getColumnIndex(DBTable.GROUP_NAME));
            int i = cursorQueryAllData.getInt(cursorQueryAllData.getColumnIndex(DBTable.ID));
            this.groupNames.add(string);
            this.groupIDs.add(Integer.valueOf(i));
            Log.e("", "-id = " + i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void init() {
        this.myExpandableListView = (ExpandableListView) findViewById(R.id.left_ExpandableListView);
        MyExpandableListAdapter myExpandableListAdapter = new MyExpandableListAdapter(this.context, this, this.dbAdapter, this.mHandler, this.mBluetoothLeService);
        this.mMyExpandableListAdapter = myExpandableListAdapter;
        myExpandableListAdapter.setSwitchInterface(this.msetSwitchInterface);
        this.mMyExpandableListAdapter.setgroupNames(this.groupNames, this.groupIDs);
        this.myExpandableListView.setAdapter(this.mMyExpandableListAdapter);
        this.mMyApplication.mMyExpandableListAdapter = this.mMyExpandableListAdapter;
        this.myExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: com.qh.blelight.MainActivity.21
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int i) {
                int groupCount = MainActivity.this.myExpandableListView.getExpandableListAdapter().getGroupCount();
                for (int i2 = 0; i2 < groupCount; i2++) {
                    if (i != i2) {
                        MainActivity.this.myExpandableListView.collapseGroup(i2);
                    }
                }
                MainActivity.this.isExpandedID = i;
            }
        });
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel_permission);
        this.rel_permission = relativeLayout;
        relativeLayout.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.22
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
            }
        });
        TextView textView = (TextView) findViewById(R.id.tv_goto);
        this.tv_goto = textView;
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.23
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.addFlags(536870912);
                if (Build.VERSION.SDK_INT >= 9) {
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                    MainActivity.this.startActivity(intent);
                }
            }
        });
        TextView textView2 = (TextView) findViewById(R.id.tv_finish);
        this.tv_finish = textView2;
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MainActivity.24
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setFlags(268435456);
                intent.addCategory("android.intent.category.HOME");
                MainActivity.this.startActivity(intent);
            }
        });
    }

    public void initialize() {
        try {
            this.mBluetoothAdapter = ((BluetoothManager) getSystemService("bluetooth")).getAdapter();
            this.sc = new ServiceConnection() { // from class: com.qh.blelight.MainActivity.25
                @Override // android.content.ServiceConnection
                public void onServiceDisconnected(ComponentName componentName) {
                }

                @Override // android.content.ServiceConnection
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    MainActivity.this.mBluetoothLeService = ((BluetoothLeService.LocalBinder) iBinder).getService();
                    if (MainActivity.this.mBluetoothLeService == null) {
                        return;
                    }
                    MainActivity.this.mMyApplication.mBluetoothLeService = MainActivity.this.mBluetoothLeService;
                    MainActivity.this.mBluetoothLeService.scanLeDevice(true);
                    MainActivity.this.mBluetoothLeService.setOperateHandler(MainActivity.this.mOperateHandler);
                    MainActivity.this.mBluetoothLeService.mCheckpwd = MainActivity.this.mCheckpwd;
                    MainActivity.this.init();
                }
            };
            getApplicationContext().bindService(new Intent(getApplicationContext(), (Class<?>) BluetoothLeService.class), this.sc, 1);
        } catch (Exception unused) {
        }
    }

    @Override // android.app.ActivityGroup, android.app.Activity
    protected void onResume() {
        Log.e("--", "-Build.VERSION.SDK_INT -" + Build.VERSION.SDK_INT);
        if (this.mMyApplication.bgsrc.length > this.mMyApplication.typebg) {
            this.rel_math.setBackgroundResource(this.mMyApplication.bgsrc[this.mMyApplication.typebg]);
        }
        this.rel_permission = (RelativeLayout) findViewById(R.id.rel_permission);
        if (Build.VERSION.SDK_INT >= 31) {
            String[] strArr = {"android.permission.BLUETOOTH_SCAN", "android.permission.BLUETOOTH_CONNECT"};
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission("android.permission.BLUETOOTH_SCAN") != 0) {
                    this.isStart = 0;
                    requestPermissions(strArr, PointerIconCompat.TYPE_COPY);
                } else {
                    if (tools.isHarmonyOs() && !Tool.isOPen(getApplicationContext()) && !this.isopenGPS) {
                        Tool.openGPS(this);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.open_gps_msg), 1).show();
                        this.isopenGPS = true;
                    }
                    openble();
                }
            }
        } else {
            if (!Tool.isOPen(getApplicationContext()) && !this.isopenGPS) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.open_gps_msg), 1).show();
                Tool.openGPS(this);
                this.isopenGPS = true;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    if (!this.isRequest) {
                        requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 1);
                    } else {
                        this.rel_permission.setVisibility(0);
                    }
                    Log.e("--", "-requestPermissions-" + checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION"));
                } else {
                    if (Build.VERSION.SDK_INT >= 26 && !Tool.isOPen(getApplicationContext()) && !this.isopenGPS) {
                        Tool.openGPS(this);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.open_gps_msg), 1).show();
                        this.isopenGPS = true;
                    }
                    this.rel_permission.setVisibility(8);
                }
            } else {
                this.rel_permission.setVisibility(8);
            }
            openble();
        }
        super.onResume();
    }

    public void openble() {
        if (this.mBluetoothAdapter == null) {
            try {
                this.mBluetoothAdapter = ((BluetoothManager) getSystemService("bluetooth")).getAdapter();
            } catch (Exception unused) {
            }
        }
        try {
            BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
            if (bluetoothAdapter != null) {
                if (bluetoothAdapter.isEnabled()) {
                    return;
                }
                startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 11);
                Toast.makeText(this, this.mResources.getText(R.string.open_bluetooth), 1).show();
                return;
            }
            Toast.makeText(this, this.mResources.getText(R.string.open_bluetooth), 1).show();
        } catch (Exception unused2) {
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == 100) {
            setListData();
            this.mMyExpandableListAdapter.setgroupNames(this.groupNames, this.groupIDs);
            this.myExpandableListView.setAdapter(this.mMyExpandableListAdapter);
            this.mMyApplication.mMyExpandableListAdapter = this.mMyExpandableListAdapter;
        }
        super.onActivityResult(i, i2, intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSensorManagerListener(boolean z) {
        SensorManager sensorManager = this.sensorManager;
        if (sensorManager != null) {
            if (z) {
                sensorManager.registerListener(this.sensorEventListener, sensorManager.getDefaultSensor(1), 3);
            } else {
                sensorManager.unregisterListener(this.sensorEventListener);
            }
        }
    }

    private void showMultiBtnDialog() {
        if (this.mMyApplication.isshow) {
            return;
        }
        startActivity(new Intent(this, (Class<?>) UnlawfulActivity.class));
        this.mMyApplication.isshow = true;
    }

    public void setpwd(String str) {
        Intent intent = new Intent(this, (Class<?>) InputpwdActivity.class);
        intent.putExtra("addr", str);
        startActivity(intent);
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        Log.e("--", "requestCode=" + i);
        if (i == 1011) {
            if (iArr.length > 0 && iArr[0] == 0) {
                openble();
                return;
            }
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.ble_permissions), 1).show();
            if (this.isStart == 1) {
                return;
            }
            try {
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                this.isStart = 1;
                return;
            } catch (Exception unused) {
                return;
            }
        }
        if (i == 1) {
            this.isRequest = true;
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    this.rel_permission.setVisibility(0);
                } else {
                    this.rel_permission.setVisibility(8);
                }
            }
        }
        super.onRequestPermissionsResult(i, strArr, iArr);
    }
}
