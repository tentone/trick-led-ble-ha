package com.qh.blelight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.qh.onehlight.R;
import com.qh.ui.CircularSeekBar;

/* JADX INFO: loaded from: classes.dex */
public class AdjustActivity extends Activity {
    public ImageView cacheimg;
    public Context context;
    public ImageView ic_hook1;
    public ImageView ic_hook2;
    public ImageView ic_hook3;
    public ImageView ic_hook4;
    public ImageView ic_hook5;
    public ImageView ic_hook6;
    public TextView img_color1;
    public TextView img_color2;
    public TextView img_color3;
    public TextView img_color4;
    public TextView img_color5;
    public TextView img_color6;
    public CircularSeekBar mCircularSeekBar;
    public MyApplication mMyApplication;
    public SeekBar seekbar;
    public SharedPreferences settings;
    public TextView tv_pro;
    public Handler mAdjustHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.AdjustActivity.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            return false;
        }
    });
    public boolean isPicture = false;
    public View.OnClickListener myOnClickListener = new View.OnClickListener() { // from class: com.qh.blelight.AdjustActivity.6
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (AdjustActivity.this.cacheimg != null) {
                AdjustActivity.this.cacheimg.setVisibility(8);
            }
            int i = 100;
            switch (view.getId()) {
                case R.id.img_color1 /* 2131230871 */:
                    AdjustActivity.this.ic_hook1.setVisibility(0);
                    AdjustActivity adjustActivity = AdjustActivity.this;
                    adjustActivity.cacheimg = adjustActivity.ic_hook1;
                    break;
                case R.id.img_color2 /* 2131230872 */:
                    AdjustActivity.this.ic_hook2.setVisibility(0);
                    AdjustActivity adjustActivity2 = AdjustActivity.this;
                    adjustActivity2.cacheimg = adjustActivity2.ic_hook2;
                    i = 75;
                    break;
                case R.id.img_color3 /* 2131230873 */:
                    AdjustActivity.this.ic_hook3.setVisibility(0);
                    AdjustActivity adjustActivity3 = AdjustActivity.this;
                    adjustActivity3.cacheimg = adjustActivity3.ic_hook3;
                    i = 50;
                    break;
                case R.id.img_color4 /* 2131230874 */:
                    AdjustActivity.this.ic_hook4.setVisibility(0);
                    AdjustActivity adjustActivity4 = AdjustActivity.this;
                    adjustActivity4.cacheimg = adjustActivity4.ic_hook4;
                    i = 25;
                    break;
                case R.id.img_color5 /* 2131230875 */:
                    AdjustActivity.this.ic_hook5.setVisibility(0);
                    AdjustActivity adjustActivity5 = AdjustActivity.this;
                    adjustActivity5.cacheimg = adjustActivity5.ic_hook5;
                    i = 10;
                    break;
                case R.id.img_color6 /* 2131230876 */:
                    AdjustActivity.this.ic_hook6.setVisibility(0);
                    AdjustActivity adjustActivity6 = AdjustActivity.this;
                    adjustActivity6.cacheimg = adjustActivity6.ic_hook6;
                    i = 5;
                    break;
            }
            AdjustActivity.this.tv_pro.setText("" + i + "%");
            AdjustActivity.this.mCircularSeekBar.setPro(i);
            AdjustActivity.this.setColors(i, false);
            AdjustActivity.this.seekbar.setProgress(i);
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_adjust);
        this.context = this;
        MyApplication myApplication = (MyApplication) getApplication();
        this.mMyApplication = myApplication;
        myApplication.AdjustHandler = this.mAdjustHandler;
        this.settings = getSharedPreferences("BleLight", 0);
        initView();
        setListener();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (4 == i) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(268435456);
            intent.addCategory("android.intent.category.HOME");
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    public void initView() {
        this.seekbar = (SeekBar) findViewById(R.id.seekbar);
        this.tv_pro = (TextView) findViewById(R.id.tv_pro);
        this.mCircularSeekBar = (CircularSeekBar) findViewById(R.id.mCircularSeekBar);
        this.img_color1 = (TextView) findViewById(R.id.img_color1);
        this.img_color2 = (TextView) findViewById(R.id.img_color2);
        this.img_color3 = (TextView) findViewById(R.id.img_color3);
        this.img_color4 = (TextView) findViewById(R.id.img_color4);
        this.img_color5 = (TextView) findViewById(R.id.img_color5);
        this.img_color6 = (TextView) findViewById(R.id.img_color6);
        this.ic_hook1 = (ImageView) findViewById(R.id.ic_hook1);
        this.ic_hook2 = (ImageView) findViewById(R.id.ic_hook2);
        this.ic_hook3 = (ImageView) findViewById(R.id.ic_hook3);
        this.ic_hook4 = (ImageView) findViewById(R.id.ic_hook4);
        this.ic_hook5 = (ImageView) findViewById(R.id.ic_hook5);
        this.ic_hook6 = (ImageView) findViewById(R.id.ic_hook6);
        this.mAdjustHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.AdjustActivity.2
            @Override // java.lang.Runnable
            public void run() {
                AdjustActivity.this.mCircularSeekBar.setPro(50);
            }
        }, 1000L);
    }

    public void setListener() {
        this.tv_pro.setOnTouchListener(new View.OnTouchListener() { // from class: com.qh.blelight.AdjustActivity.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.qh.blelight.AdjustActivity.4
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (z) {
                    AdjustActivity.this.tv_pro.setText("" + i + "%");
                    AdjustActivity.this.mCircularSeekBar.setPro(i);
                    AdjustActivity.this.setColors(i, false);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                AdjustActivity.this.tv_pro.setText("" + progress + "%");
                AdjustActivity.this.mCircularSeekBar.setPro(progress);
                AdjustActivity.this.setColors(progress, true);
            }
        });
        this.mCircularSeekBar.setOnChangeListener(new CircularSeekBar.OnChangeListener() { // from class: com.qh.blelight.AdjustActivity.5
            @Override // com.qh.ui.CircularSeekBar.OnChangeListener
            public void onChange(int i) {
                AdjustActivity.this.tv_pro.setText("" + i + "%");
                AdjustActivity.this.seekbar.setProgress(i);
                AdjustActivity.this.setColors(i, false);
            }

            @Override // com.qh.ui.CircularSeekBar.OnChangeListener
            public void onStopTrackingTouch(int i) {
                AdjustActivity.this.tv_pro.setText("" + i + "%");
                AdjustActivity.this.setColors(i, true);
            }
        });
        this.img_color1.setOnClickListener(this.myOnClickListener);
        this.img_color2.setOnClickListener(this.myOnClickListener);
        this.img_color3.setOnClickListener(this.myOnClickListener);
        this.img_color4.setOnClickListener(this.myOnClickListener);
        this.img_color5.setOnClickListener(this.myOnClickListener);
        this.img_color6.setOnClickListener(this.myOnClickListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setColors(final int i, boolean z) {
        final MyBluetoothGatt myBluetoothGatt;
        MyBluetoothGatt myBluetoothGatt2;
        if (this.mMyApplication.isOpenMusicHop()) {
            this.mMyApplication.setMusicHop(false, true);
        }
        if (this.mMyApplication.MusicHandler != null) {
            this.mMyApplication.MusicHandler.sendEmptyMessage(2);
        }
        for (String str : MainActivity.ControlMACs.keySet()) {
            if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt2 = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null) {
                byte b = myBluetoothGatt2.datas[2];
            }
            if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.setColor(i);
                if (z) {
                    this.mAdjustHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.AdjustActivity.7
                        @Override // java.lang.Runnable
                        public void run() {
                            myBluetoothGatt.setColor(i);
                        }
                    }, 300L);
                }
            }
        }
    }
}
