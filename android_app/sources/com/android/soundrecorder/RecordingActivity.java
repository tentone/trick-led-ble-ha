package com.android.soundrecorder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.qh.blelight.MyApplication;
import com.qh.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class RecordingActivity extends Activity {
    private static final int ANIMATIONEACHOFFSET = 600;
    private AnimationSet aniSet;
    private AnimationSet aniSet2;
    private AnimationSet aniSet3;
    private ImageView btn;
    public LinearLayout lin_phone_mic;
    public LinearLayout lin_rmod;
    private MyApplication mMyApplication;
    private SeekBar seekbar_speed;
    public TextView tv_mod1;
    public TextView tv_mod2;
    public TextView tv_shouji;
    public TextView tv_waim;
    private ImageView wave1;
    private ImageView wave2;
    private ImageView wave3;
    private Handler handler = new Handler() { // from class: com.android.soundrecorder.RecordingActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 546) {
                RecordingActivity.this.wave2.startAnimation(RecordingActivity.this.aniSet2);
            } else if (message.what == 819) {
                RecordingActivity.this.wave3.startAnimation(RecordingActivity.this.aniSet3);
            }
            super.handleMessage(message);
        }
    };
    public boolean isfrist = true;
    public Handler mRecordinghandler = new Handler(new Handler.Callback() { // from class: com.android.soundrecorder.RecordingActivity.2
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            Log.e("88", "88 " + RecordingActivity.this.mMyApplication.isOnlyHaveTrick());
            Log.e("88", "isMic " + RecordingActivity.this.mMyApplication.isMic);
            if (RecordingActivity.this.mMyApplication.isMic) {
                if (RecordingActivity.this.mMyApplication.isOnlyHaveTrick()) {
                    RecordingActivity.this.iswaiM = true;
                    RecordingActivity.this.lin_phone_mic.setVisibility(0);
                    RecordingActivity.this.sendColor_m_data();
                } else {
                    RecordingActivity.this.iswaiM = false;
                    RecordingActivity.this.sendColor_m_data();
                    RecordingActivity.this.lin_phone_mic.setVisibility(8);
                }
            }
            return false;
        }
    });
    public int modid = 0;
    public boolean iswaiM = false;
    public View.OnClickListener myOnClickListener = new View.OnClickListener() { // from class: com.android.soundrecorder.RecordingActivity.7
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_mod1 /* 2131231096 */:
                    RecordingActivity.this.modid = 0;
                    break;
                case R.id.tv_mod2 /* 2131231097 */:
                    RecordingActivity.this.modid = 1;
                    break;
                case R.id.tv_shouji /* 2131231101 */:
                    RecordingActivity.this.lin_rmod.setVisibility(8);
                    RecordingActivity.this.iswaiM = false;
                    break;
                case R.id.tv_waim /* 2131231106 */:
                    RecordingActivity.this.iswaiM = true;
                    break;
            }
            RecordingActivity.this.sendColor_m_data();
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_microphone);
        this.lin_rmod = (LinearLayout) findViewById(R.id.lin_rmod);
        this.lin_phone_mic = (LinearLayout) findViewById(R.id.lin_phone_mic);
        this.tv_shouji = (TextView) findViewById(R.id.tv_shouji);
        this.tv_waim = (TextView) findViewById(R.id.tv_waim);
        this.tv_mod1 = (TextView) findViewById(R.id.tv_mod1);
        this.tv_mod2 = (TextView) findViewById(R.id.tv_mod2);
        this.tv_shouji.setOnClickListener(this.myOnClickListener);
        this.tv_waim.setOnClickListener(this.myOnClickListener);
        this.tv_mod1.setOnClickListener(this.myOnClickListener);
        this.tv_mod2.setOnClickListener(this.myOnClickListener);
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
        myApplication.mMediaRecorderDemo.startRecord();
        this.mMyApplication.RecordingHandler = this.mRecordinghandler;
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar_speed);
        this.seekbar_speed = seekBar;
        seekBar.setProgress(this.mMyApplication.limitdb);
        this.seekbar_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.android.soundrecorder.RecordingActivity.3
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
                RecordingActivity.this.mMyApplication.limitdb = seekBar2.getProgress();
                SharedPreferences.Editor editorEdit = RecordingActivity.this.mMyApplication.settings.edit();
                editorEdit.putInt("limitdb", RecordingActivity.this.mMyApplication.limitdb);
                editorEdit.commit();
                RecordingActivity.this.sendColor_m_data();
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                Log.e("1", "onProgressChanged = " + seekBar2.getProgress());
                RecordingActivity.this.mMyApplication.limitdb = seekBar2.getProgress();
            }
        });
    }

    @Override // android.app.Activity
    protected void onResume() {
        Log.e("--", "-onResume-");
        cancalWaveAnimation();
        showWaveAnimation();
        this.handler.postDelayed(new Runnable() { // from class: com.android.soundrecorder.RecordingActivity.4
            @Override // java.lang.Runnable
            public void run() {
                if (RecordingActivity.this.iswaiM) {
                    RecordingActivity.this.sendColor_m_data();
                }
            }
        }, 80L);
        this.handler.postDelayed(new Runnable() { // from class: com.android.soundrecorder.RecordingActivity.5
            @Override // java.lang.Runnable
            public void run() {
                RecordingActivity.this.mMyApplication.setData(new byte[]{-86, 0, -16, 85});
            }
        }, 150L);
        this.handler.postDelayed(new Runnable() { // from class: com.android.soundrecorder.RecordingActivity.6
            @Override // java.lang.Runnable
            public void run() {
                RecordingActivity.this.mMyApplication.mMediaRecorderDemo.startRecord();
            }
        }, 500L);
        Log.e("", "onResume-==");
        super.onResume();
    }

    @Override // android.app.Activity
    protected void onStop() {
        cancalWaveAnimation();
        super.onStop();
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

    public void sendColor_m_data() {
        if (this.iswaiM) {
            this.tv_shouji.setBackgroundColor(0);
            this.tv_waim.setBackgroundResource(R.drawable.btn_selectrec_bg);
            this.lin_rmod.setVisibility(0);
        } else {
            this.tv_waim.setBackgroundColor(0);
            this.tv_shouji.setBackgroundResource(R.drawable.btn_selectrec_bg);
            this.lin_rmod.setVisibility(8);
        }
        if (this.modid == 0) {
            this.tv_mod1.setBackgroundResource(R.drawable.btn_select_mod_bg);
            this.tv_mod2.setBackgroundResource(R.drawable.btn_unselect_mod_bg);
        } else {
            this.tv_mod2.setBackgroundResource(R.drawable.btn_select_mod_bg);
            this.tv_mod1.setBackgroundResource(R.drawable.btn_unselect_mod_bg);
        }
        this.mMyApplication.sendColor_m_data(this.iswaiM, this.seekbar_speed.getProgress(), this.modid);
        Log.e("id", "modid=" + this.modid + " iswaiM=" + this.iswaiM);
    }
}
