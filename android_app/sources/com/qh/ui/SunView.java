package com.qh.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.core.view.ViewCompat;
import com.xiaoyu.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class SunView extends View {
    private int color;
    private int color2;
    private int mHeight;
    private int mWidth;

    public void setColor(int i) {
        int i2 = i & ViewCompat.MEASURED_SIZE_MASK;
        this.color = ViewCompat.MEASURED_STATE_MASK + i2;
        this.color2 = i2 + 16777216;
        invalidate();
    }

    public SunView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHeight = 1000;
        this.mWidth = 1000;
        this.color = 16767744;
        this.color2 = 16767744;
    }

    public SunView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHeight = 1000;
        this.mWidth = 1000;
        this.color = 16767744;
        this.color2 = 16767744;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MyView);
        this.mWidth = (int) typedArrayObtainStyledAttributes.getDimension(3, 1000.0f);
        this.mHeight = (int) typedArrayObtainStyledAttributes.getDimension(0, 1000.0f);
        Log.e("", "mWidth = " + this.mWidth + " mHeight = " + this.mHeight);
    }

    public SunView(Context context) {
        super(context);
        this.mHeight = 1000;
        this.mWidth = 1000;
        this.color = 16767744;
        this.color2 = 16767744;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.translate(this.mWidth / 2, this.mHeight / 2);
        int i = this.color;
        RadialGradient radialGradient = new RadialGradient(0.0f, 0.0f, this.mWidth / 2, new int[]{i, i, this.color2}, (float[]) null, Shader.TileMode.CLAMP);
        Paint paint = new Paint(1);
        paint.setShader(radialGradient);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((this.mWidth / 2) - 40);
        canvas.drawCircle(0.0f, 0.0f, this.mWidth / 2, paint);
        Paint paint2 = new Paint(1);
        paint2.setColor(this.color);
        paint2.setStrokeWidth((this.mWidth / 4) + 30);
        canvas.drawCircle(0.0f, 0.0f, (this.mWidth / 4) + 30, paint2);
        canvas.drawCircle(0.0f, 0.0f, (this.mWidth / 4) + 30, paint2);
        super.onDraw(canvas);
    }
}
