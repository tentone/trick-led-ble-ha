package com.android.soundrecorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class VUMeter extends View {
    static final long ANIMATION_INTERVAL = 70;
    static final float DROPOFF_STEP = 0.18f;
    static final float PIVOT_RADIUS = 3.5f;
    static final float PIVOT_Y_OFFSET = 10.0f;
    static final float SHADOW_OFFSET = 2.0f;
    static final float SURGE_STEP = 0.35f;
    float mCurrentAngle;
    Paint mPaint;
    Recorder mRecorder;
    Paint mShadow;
    public Handler mShowHandler;

    public void setShowHandler(Handler handler) {
        this.mShowHandler = handler;
    }

    public VUMeter(Context context) {
        super(context);
        init(context);
    }

    public VUMeter(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    void init(Context context) {
        Paint paint = new Paint(1);
        this.mPaint = paint;
        paint.setColor(-1);
        Paint paint2 = new Paint(1);
        this.mShadow = paint2;
        paint2.setColor(Color.argb(60, 0, 0, 0));
        this.mRecorder = null;
        this.mCurrentAngle = 0.0f;
    }

    public void setRecorder(Recorder recorder) {
        this.mRecorder = recorder;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float maxAmplitude = this.mRecorder != null ? 0.3926991f + ((r3.getMaxAmplitude() * 2.3561947f) / 32768.0f) : 0.3926991f;
        float f = this.mCurrentAngle;
        if (maxAmplitude > f) {
            this.mCurrentAngle = maxAmplitude;
        } else {
            this.mCurrentAngle = Math.max(maxAmplitude, f - DROPOFF_STEP);
        }
        this.mCurrentAngle = Math.min(2.7488937f, this.mCurrentAngle);
        Log.e("", "getMaxAmplitude --" + ((int) (this.mCurrentAngle * SHADOW_OFFSET)));
        Handler handler = this.mShowHandler;
        if (handler != null) {
            handler.sendEmptyMessage((int) (this.mCurrentAngle * SHADOW_OFFSET));
        }
        getWidth();
        getHeight();
        Math.sin(this.mCurrentAngle);
        Math.cos(this.mCurrentAngle);
        Recorder recorder = this.mRecorder;
        if (recorder == null || recorder.state() != 1) {
            return;
        }
        postInvalidateDelayed(200L);
    }
}
