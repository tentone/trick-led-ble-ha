package com.android.soundrecorder;

import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class AudioRecordDemo {
    static final float DROPOFF_STEP = 0.18f;
    private static final String TAG = "AudioRecord";
    AudioRecord mAudioRecord;
    float mCurrentAngle;
    public Handler mShowHandler;
    static final int SAMPLE_RATE_IN_HZ = 8000;
    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, 1, 2);
    public boolean isPlay = false;
    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() { // from class: com.android.soundrecorder.AudioRecordDemo.1
        @Override // java.lang.Runnable
        public void run() {
            AudioRecordDemo.this.updateMicStatus();
        }
    };
    private int BASE = 1;
    private int SPACE = 100;
    private double old = 0.0d;
    Object mLock = new Object();

    public void startRecord() {
        if (this.isPlay) {
            return;
        }
        this.mAudioRecord = new AudioRecord(1, SAMPLE_RATE_IN_HZ, 1, 2, BUFFER_SIZE);
        this.isPlay = true;
        this.mAudioRecord.startRecording();
        this.mHandler.post(this.mUpdateMicStatusTimer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMicStatus() {
        AudioRecord audioRecord = this.mAudioRecord;
        if (audioRecord != null) {
            int i = BUFFER_SIZE;
            short[] sArr = new short[i];
            int i2 = audioRecord.read(sArr, 0, i);
            long j = 0;
            for (int i3 = 0; i3 < i; i3++) {
                j += (long) (sArr[i3] * sArr[i3]);
            }
            double d = j;
            double d2 = i2;
            Double.isNaN(d);
            Double.isNaN(d2);
            double dLog10 = Math.log10(d / d2) * 10.0d;
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

    public void stopRecord() {
        AudioRecord audioRecord;
        if (this.isPlay && (audioRecord = this.mAudioRecord) != null) {
            audioRecord.stop();
            this.mAudioRecord.release();
            this.mAudioRecord = null;
            this.isPlay = false;
            Log.e("", "stopRecord");
        }
    }
}
