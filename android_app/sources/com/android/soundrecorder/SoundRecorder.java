package com.android.soundrecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import com.android.soundrecorder.Recorder;
import com.qh.blelight.MyApplication;
import com.qh.blelight.MyBluetoothGatt;
import com.qh.onehlight.R;
import com.qh.tools.DBTable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: classes.dex */
public class SoundRecorder extends Activity implements Recorder.OnStateChangedListener {
    private static final int ANIMATIONEACHOFFSET = 600;
    static final String ANY_ANY = "*/*";
    static final String AUDIO_3GPP = "audio/3gpp";
    static final String AUDIO_AMR = "audio/amr";
    static final String AUDIO_ANY = "audio/*";
    static final int BITRATE_3GPP = 5900;
    static final int BITRATE_AMR = 5900;
    public static final int MAXCOLOR = 254;
    static final String MAX_FILE_SIZE_KEY = "max_file_size";
    static final String RECORDER_STATE_KEY = "recorder_state";
    static final String SAMPLE_INTERRUPTED_KEY = "sample_interrupted";
    static final String STATE_FILE_NAME = "soundrecorder.state";
    static final String TAG = "SoundRecorder";
    public static int changenum = 10;
    private AnimationSet aniSet;
    private AnimationSet aniSet2;
    private AnimationSet aniSet3;
    private ImageView btn;
    public ImageView img_microphone;
    public MyApplication mMyApplication;
    Recorder mRecorder;
    RemainingTimeCalculator mRemainingTimeCalculator;
    String mTimerFormat;
    VUMeter mVUMeter;
    PowerManager.WakeLock mWakeLock;
    private ImageView wave1;
    private ImageView wave2;
    private ImageView wave3;
    public boolean isOk = false;
    String mRequestedType = AUDIO_ANY;
    boolean mSampleInterrupted = false;
    String mErrorUiMessage = null;
    long mMaxFileSize = -1;
    final Handler mHandler = new Handler();
    Runnable mUpdateTimer = new Runnable() { // from class: com.android.soundrecorder.SoundRecorder.1
        @Override // java.lang.Runnable
        public void run() {
            SoundRecorder.this.updateTimerView();
        }
    };
    public Handler mShowHandler = new Handler(new Handler.Callback() { // from class: com.android.soundrecorder.SoundRecorder.2
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            SoundRecorder.this.setCircleShow(message.what);
            return false;
        }
    });
    public Handler soundControlHandler = new Handler(new Handler.Callback() { // from class: com.android.soundrecorder.SoundRecorder.3
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what != 0) {
                return false;
            }
            SoundRecorder.this.mRecorder.stop();
            SoundRecorder.this.mRecorder.clear();
            SoundRecorder.this.finish();
            return false;
        }
    });
    private BroadcastReceiver mSDCardMountEventReceiver = null;
    private Handler handler = new Handler() { // from class: com.android.soundrecorder.SoundRecorder.4
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 546) {
                SoundRecorder.this.wave2.startAnimation(SoundRecorder.this.aniSet2);
            } else if (message.what == 819) {
                SoundRecorder.this.wave3.startAnimation(SoundRecorder.this.aniSet3);
            }
            super.handleMessage(message);
        }
    };
    public int red = 255;
    public int green = 0;
    public int blue = 0;
    private int num = 0;
    private int a = 0;

    private void init() {
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        Bundle bundle2;
        super.onCreate(bundle);
        requestWindowFeature(1);
        Intent intent = getIntent();
        if (intent != null) {
            String type = intent.getType();
            if (AUDIO_AMR.equals(type) || AUDIO_3GPP.equals(type) || AUDIO_ANY.equals(type) || ANY_ANY.equals(type)) {
                this.mRequestedType = type;
            } else if (type != null) {
                setResult(0);
                finish();
                return;
            }
            this.mMaxFileSize = intent.getLongExtra("android.provider.MediaStore.extra.MAX_BYTES", -1L);
        }
        if (AUDIO_ANY.equals(this.mRequestedType) || ANY_ANY.equals(this.mRequestedType)) {
            this.mRequestedType = AUDIO_3GPP;
        }
        setContentView(R.layout.activity_microphone);
        this.aniSet = getNewAnimationSet();
        this.aniSet2 = getNewAnimationSet();
        this.aniSet3 = getNewAnimationSet();
        this.btn = (ImageView) findViewById(R.id.btn);
        this.wave1 = (ImageView) findViewById(R.id.wave1);
        this.wave2 = (ImageView) findViewById(R.id.wave2);
        this.wave3 = (ImageView) findViewById(R.id.wave3);
        showWaveAnimation();
        MyApplication myApplication = (MyApplication) getApplication();
        this.mMyApplication = myApplication;
        myApplication.soundControlHandler = this.soundControlHandler;
        Recorder recorder = new Recorder();
        this.mRecorder = recorder;
        recorder.setOnStateChangedListener(this);
        this.mRemainingTimeCalculator = new RemainingTimeCalculator();
        this.mWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(6, TAG);
        initResourceRefs();
        setResult(0);
        registerExternalStorageListener();
        if (bundle != null && (bundle2 = bundle.getBundle(RECORDER_STATE_KEY)) != null) {
            this.mRecorder.restoreState(bundle2);
            this.mSampleInterrupted = bundle2.getBoolean(SAMPLE_INTERRUPTED_KEY, false);
            this.mMaxFileSize = bundle2.getLong(MAX_FILE_SIZE_KEY, -1L);
        }
        updateUi();
        this.isOk = true;
        init();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCircleShow(int i) {
        if (this.num != i) {
            int iArgb = Color.argb(255, this.red, this.green, this.blue);
            double d = i;
            Double.isNaN(d);
            setMusicColor(iArgb, (int) (d * 50.5d));
        }
        this.num = i;
        addColor();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        setContentView(R.layout.activity_microphone);
        initResourceRefs();
        updateUi();
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mRecorder.sampleLength() == 0) {
            return;
        }
        Bundle bundle2 = new Bundle();
        this.mRecorder.saveState(bundle2);
        bundle2.putBoolean(SAMPLE_INTERRUPTED_KEY, this.mSampleInterrupted);
        bundle2.putLong(MAX_FILE_SIZE_KEY, this.mMaxFileSize);
        bundle.putBundle(RECORDER_STATE_KEY, bundle2);
    }

    private void initResourceRefs() {
        VUMeter vUMeter = (VUMeter) findViewById(R.id.uvMeter);
        this.mVUMeter = vUMeter;
        vUMeter.setRecorder(this.mRecorder);
        this.mVUMeter.setShowHandler(this.mShowHandler);
    }

    private void stopAudioPlayback() {
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "pause");
        sendBroadcast(intent);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
    }

    @Override // android.app.Activity
    protected void onResume() {
        if (this.isOk) {
            openRecorder();
        }
        cancalWaveAnimation();
        showWaveAnimation();
        super.onResume();
    }

    public void openRecorder() {
        this.mRemainingTimeCalculator.reset();
        if (!Environment.getExternalStorageState().equals("mounted")) {
            this.mSampleInterrupted = true;
            this.mErrorUiMessage = "insert_sd_card";
            updateUi();
            return;
        }
        if (!this.mRemainingTimeCalculator.diskSpaceAvailable()) {
            this.mSampleInterrupted = true;
            this.mErrorUiMessage = "insert_sd_card";
            updateUi();
            return;
        }
        stopAudioPlayback();
        if (AUDIO_AMR.equals(this.mRequestedType)) {
            this.mRemainingTimeCalculator.setBitRate(5900);
            this.mRecorder.startRecording(3, ".amr", this);
        } else if (AUDIO_3GPP.equals(this.mRequestedType)) {
            this.mRemainingTimeCalculator.setBitRate(5900);
            this.mRecorder.startRecording(1, ".3gpp", this);
        } else {
            throw new IllegalArgumentException("Invalid output file type requested");
        }
        if (this.mMaxFileSize != -1) {
            this.mRemainingTimeCalculator.setFileSizeLimit(this.mRecorder.sampleFile(), this.mMaxFileSize);
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.mSampleInterrupted = this.mRecorder.state() == 1;
        super.onPause();
    }

    private void saveSample() {
        if (this.mRecorder.sampleLength() == 0) {
            return;
        }
        try {
            Uri uriAddToMediaDB = addToMediaDB(this.mRecorder.sampleFile());
            if (uriAddToMediaDB == null) {
                return;
            }
            setResult(-1, new Intent().setData(uriAddToMediaDB));
        } catch (UnsupportedOperationException unused) {
        }
    }

    @Override // android.app.Activity
    public void onDestroy() {
        BroadcastReceiver broadcastReceiver = this.mSDCardMountEventReceiver;
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            this.mSDCardMountEventReceiver = null;
        }
        super.onDestroy();
    }

    private void registerExternalStorageListener() {
        if (this.mSDCardMountEventReceiver == null) {
            this.mSDCardMountEventReceiver = new BroadcastReceiver() { // from class: com.android.soundrecorder.SoundRecorder.5
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals("android.intent.action.MEDIA_EJECT")) {
                        SoundRecorder.this.mRecorder.delete();
                    } else if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                        SoundRecorder.this.mSampleInterrupted = false;
                        SoundRecorder.this.updateUi();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MEDIA_EJECT");
            intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
            intentFilter.addDataScheme("file");
            if (Build.VERSION.SDK_INT > 33) {
                registerReceiver(this.mSDCardMountEventReceiver, intentFilter, 4);
            } else {
                registerReceiver(this.mSDCardMountEventReceiver, intentFilter);
            }
        }
    }

    private Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        try {
            ContentResolver contentResolver = getContentResolver();
            if (contentResolver == null) {
                return null;
            }
            return contentResolver.query(uri, strArr, str, strArr2, str2);
        } catch (UnsupportedOperationException unused) {
            return null;
        }
    }

    private void addToPlaylist(ContentResolver contentResolver, int i, long j) {
        Uri contentUri = MediaStore.Audio.Playlists.Members.getContentUri("external", j);
        Cursor cursorQuery = contentResolver.query(contentUri, new String[]{"count(*)"}, null, null, null);
        cursorQuery.moveToFirst();
        int i2 = cursorQuery.getInt(0);
        cursorQuery.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("play_order", Integer.valueOf(i2 + i));
        contentValues.put("audio_id", Integer.valueOf(i));
        contentResolver.insert(contentUri, contentValues);
    }

    private int getPlaylistId(Resources resources) {
        Cursor cursorQuery = query(MediaStore.Audio.Playlists.getContentUri("external"), new String[]{DBTable.ID}, "name=?", new String[]{"My recordings"}, null);
        if (cursorQuery == null) {
            Log.v(TAG, "query returns null");
        }
        int i = -1;
        if (cursorQuery != null) {
            cursorQuery.moveToFirst();
            if (!cursorQuery.isAfterLast()) {
                i = cursorQuery.getInt(0);
            }
        }
        cursorQuery.close();
        return i;
    }

    private Uri createPlaylist(Resources resources, ContentResolver contentResolver) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "My recordings");
        return contentResolver.insert(MediaStore.Audio.Playlists.getContentUri("external"), contentValues);
    }

    private Uri addToMediaDB(File file) {
        Resources resources = getResources();
        ContentValues contentValues = new ContentValues();
        long jCurrentTimeMillis = System.currentTimeMillis();
        long jLastModified = file.lastModified();
        String str = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(jCurrentTimeMillis));
        long jSampleLength = ((long) this.mRecorder.sampleLength()) * 1000;
        contentValues.put("is_music", "0");
        contentValues.put("title", str);
        contentValues.put("_data", file.getAbsolutePath());
        contentValues.put("date_added", Integer.valueOf((int) (jCurrentTimeMillis / 1000)));
        contentValues.put("date_modified", Integer.valueOf((int) (jLastModified / 1000)));
        contentValues.put("duration", Long.valueOf(jSampleLength));
        contentValues.put("mime_type", this.mRequestedType);
        Log.d(TAG, "Inserting audio record: " + contentValues.toString());
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "ContentURI: " + uri);
        Uri uriInsert = contentResolver.insert(uri, contentValues);
        if (uriInsert == null) {
            new AlertDialog.Builder(this).setTitle(R.string.app_name).setCancelable(false).show();
            return null;
        }
        if (getPlaylistId(resources) == -1) {
            createPlaylist(resources, contentResolver);
        }
        addToPlaylist(contentResolver, Integer.valueOf(uriInsert.getLastPathSegment()).intValue(), getPlaylistId(resources));
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", uriInsert));
        return uriInsert;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTimerView() {
        getResources();
        int iState = this.mRecorder.state();
        boolean z = iState == 1 || iState == 2;
        Recorder recorder = this.mRecorder;
        if (z) {
            recorder.progress();
        } else {
            recorder.sampleLength();
        }
        if (iState != 2 && iState == 1) {
            updateTimeRemaining();
        }
        if (z) {
            this.mHandler.postDelayed(this.mUpdateTimer, 1000L);
        }
    }

    private void updateTimeRemaining() {
        if (this.mRemainingTimeCalculator.timeRemaining() <= 0) {
            this.mSampleInterrupted = true;
            int iCurrentLowerLimit = this.mRemainingTimeCalculator.currentLowerLimit();
            if (iCurrentLowerLimit == 1) {
                this.mErrorUiMessage = "";
                return;
            } else if (iCurrentLowerLimit == 2) {
                this.mErrorUiMessage = "";
                return;
            } else {
                this.mErrorUiMessage = null;
                return;
            }
        }
        getResources();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUi() {
        getResources();
        int iState = this.mRecorder.state();
        if (iState != 0) {
            if (iState == 1) {
                this.mVUMeter.setVisibility(0);
            } else if (iState == 2) {
                this.mVUMeter.setVisibility(4);
            }
        } else if (this.mRecorder.sampleLength() == 0) {
            this.mVUMeter.setVisibility(0);
        } else {
            this.mVUMeter.setVisibility(4);
        }
        updateTimerView();
        this.mVUMeter.invalidate();
        float maxAmplitude = this.mRecorder != null ? 0.3926991f + ((r2.getMaxAmplitude() * 2.3561947f) / 32768.0f) : 0.3926991f;
        if (maxAmplitude > 0.0f) {
            return;
        }
        Math.max(maxAmplitude, -0.18f);
    }

    @Override // com.android.soundrecorder.Recorder.OnStateChangedListener
    public void onStateChanged(int i) {
        if (i == 2 || i == 1) {
            this.mSampleInterrupted = false;
            this.mErrorUiMessage = null;
            this.mWakeLock.acquire();
        } else if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
        updateUi();
    }

    @Override // com.android.soundrecorder.Recorder.OnStateChangedListener
    public void onError(int i) {
        getResources();
        String str = "";
        if (i != 1 && i != 2 && i != 3) {
            str = null;
        }
        if (str != null) {
            new AlertDialog.Builder(this).setTitle(R.string.app_name).setMessage(str).setPositiveButton("OK", (DialogInterface.OnClickListener) null).setCancelable(false).show();
        }
    }

    private void setMusicColor(int i, int i2) {
        MyBluetoothGatt myBluetoothGatt;
        MyBluetoothGatt myBluetoothGatt2;
        for (String str : this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.keySet()) {
            if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt2 = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null) {
                byte b = myBluetoothGatt2.datas[2];
            }
            int iArgb = Color.argb(255, (Color.red(i) * i2) / 255, (Color.green(i) * i2) / 255, (Color.blue(i) * i2) / 255);
            if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.setMusicColor(iArgb);
            }
        }
    }

    private void addColor() {
        int i = this.a;
        if (i == 0) {
            this.red = 254;
            this.blue = 0;
            int i2 = this.green + changenum;
            this.green = i2;
            if (i2 >= 254) {
                this.green = 254;
                this.a = 1;
                return;
            }
            return;
        }
        if (i == 1) {
            this.green = 254;
            int i3 = this.red - changenum;
            this.red = i3;
            if (i3 <= 0) {
                this.red = 0;
                this.a = 2;
                return;
            }
            return;
        }
        if (i == 2) {
            this.green = 254;
            int i4 = this.blue + changenum;
            this.blue = i4;
            if (i4 >= 254) {
                this.blue = 200;
                this.a = 3;
                return;
            }
            return;
        }
        if (i == 3) {
            this.blue = 254;
            int i5 = this.green - changenum;
            this.green = i5;
            if (i5 <= 0) {
                this.green = 0;
                this.a = 4;
                return;
            }
            return;
        }
        if (i == 4) {
            this.blue = 254;
            int i6 = this.red + changenum;
            this.red = i6;
            if (i6 >= 254) {
                this.red = 254;
                this.a = 5;
                return;
            }
            return;
        }
        if (i == 5) {
            this.red = 254;
            int i7 = this.blue - changenum;
            this.blue = i7;
            if (i7 <= 0) {
                this.blue = 0;
                this.a = 0;
            }
        }
    }

    private void showWaveAnimation() {
        this.wave1.startAnimation(this.aniSet);
        this.handler.sendEmptyMessageDelayed(546, 600L);
        this.handler.sendEmptyMessageDelayed(819, 1200L);
    }

    private void cancalWaveAnimation() {
        this.wave1.clearAnimation();
        this.wave2.clearAnimation();
        this.wave3.clearAnimation();
    }

    private AnimationSet getNewAnimationSet() {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 3.3f, 1.0f, 3.3f, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setDuration(1800L);
        scaleAnimation.setRepeatCount(-1);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        alphaAnimation.setRepeatCount(-1);
        animationSet.setDuration(1800L);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        return animationSet;
    }
}
