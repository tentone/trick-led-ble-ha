package com.android.soundrecorder;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.File;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class MediaRecorderDemo {
    static final float DROPOFF_STEP = 0.18f;
    public static final int MAX_LENGTH = 3600000;
    private int BASE;
    private int SPACE;
    private final String TAG;
    private long endTime;
    private String filePath;
    private boolean isPlay;
    float mCurrentAngle;
    private final Handler mHandler;
    private MediaRecorder mMediaRecorder;
    public Handler mShowHandler;
    private Runnable mUpdateMicStatusTimer;
    private double old;
    private long startTime;

    public MediaRecorderDemo() {
        this.TAG = "MediaRecord";
        this.isPlay = false;
        this.mHandler = new Handler();
        this.mUpdateMicStatusTimer = new Runnable() { // from class: com.android.soundrecorder.MediaRecorderDemo.1
            @Override // java.lang.Runnable
            public void run() {
                MediaRecorderDemo.this.updateMicStatus();
            }
        };
        this.BASE = 1;
        this.SPACE = 150;
        this.old = 0.0d;
        this.filePath = "/dev/null";
    }

    public MediaRecorderDemo(File file) {
        this.TAG = "MediaRecord";
        this.isPlay = false;
        this.mHandler = new Handler();
        this.mUpdateMicStatusTimer = new Runnable() { // from class: com.android.soundrecorder.MediaRecorderDemo.1
            @Override // java.lang.Runnable
            public void run() {
                MediaRecorderDemo.this.updateMicStatus();
            }
        };
        this.BASE = 1;
        this.SPACE = 150;
        this.old = 0.0d;
        this.filePath = file.getAbsolutePath();
    }

    public void startRecord() {
        if (this.isPlay) {
            return;
        }
        if (this.mMediaRecorder == null) {
            this.mMediaRecorder = new MediaRecorder();
        }
        try {
            this.mMediaRecorder.setAudioSource(1);
            this.mMediaRecorder.setOutputFormat(0);
            this.mMediaRecorder.setAudioEncoder(1);
            this.mMediaRecorder.setOutputFile(this.filePath);
            this.mMediaRecorder.setMaxDuration(MAX_LENGTH);
            this.mMediaRecorder.prepare();
            this.mMediaRecorder.start();
            this.startTime = System.currentTimeMillis();
            updateMicStatus();
            this.isPlay = true;
        } catch (IOException e) {
            Log.i("MediaRecord", "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IllegalStateException e2) {
            Log.i("MediaRecord", "call startAmr(File mRecAudioFile) failed!" + e2.getMessage());
        }
    }

    public long stopRecord() {
        if (!this.isPlay || this.mMediaRecorder == null) {
            return 0L;
        }
        this.endTime = System.currentTimeMillis();
        Log.i("ACTION_END", "endTime" + this.endTime);
        this.mMediaRecorder.stop();
        this.mMediaRecorder.reset();
        this.mMediaRecorder.release();
        this.mMediaRecorder = null;
        Log.i("ACTION_LENGTH", "Time" + (this.endTime - this.startTime));
        this.isPlay = false;
        return this.endTime - this.startTime;
    }

    public boolean isPlay() {
        return this.isPlay;
    }

    public void setPlay(boolean z) {
        this.isPlay = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMicStatus() {
        MediaRecorder mediaRecorder = this.mMediaRecorder;
        if (mediaRecorder != null) {
            double maxAmplitude = mediaRecorder.getMaxAmplitude();
            double d = this.BASE;
            Double.isNaN(maxAmplitude);
            Double.isNaN(d);
            double d2 = maxAmplitude / d;
            double dLog10 = d2 > 1.0d ? 20.0d * Math.log10(d2) : 0.0d;
            if (this.mShowHandler != null) {
                Bundle bundle = new Bundle();
                bundle.putDouble("-db", dLog10 - this.old);
                bundle.putDouble("db", dLog10);
                Message message = new Message();
                message.setData(bundle);
                this.mShowHandler.sendMessage(message);
            }
            this.old = dLog10;
            this.mHandler.postDelayed(this.mUpdateMicStatusTimer, this.SPACE);
        }
    }
}
