package com.qh.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.qh.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class RotateableView extends View {
    private Drawable mBackGroudDrawable;
    private int mBackGroudDrawableId;
    private int mBackGroundHeight;
    private int mBackGroundWidth;
    private float mRotateDegrees;

    public void setRotateDegrees(float f) {
        this.mRotateDegrees = f;
        invalidate();
    }

    public void setBG(int i) {
        this.mBackGroudDrawableId = i;
    }

    public RotateableView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mBackGroudDrawableId = R.drawable.ic_slideline;
        this.mBackGroudDrawable = context.getResources().getDrawable(this.mBackGroudDrawableId);
        this.mRotateDegrees = 340.0f;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float f = this.mRotateDegrees;
        if (f == 90.0f) {
            canvas.rotate(f, 0.0f, 0.0f);
            canvas.translate(0.0f, -this.mBackGroundHeight);
        } else {
            canvas.rotate(f, this.mBackGroundWidth / 2, this.mBackGroundHeight / 2);
        }
        this.mBackGroudDrawable.setBounds(0, 0, this.mBackGroundWidth, this.mBackGroundHeight);
        this.mBackGroudDrawable.draw(canvas);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mBackGroundHeight = this.mBackGroudDrawable.getMinimumHeight();
        int minimumWidth = this.mBackGroudDrawable.getMinimumWidth();
        this.mBackGroundWidth = minimumWidth;
        if (this.mRotateDegrees == 90.0f) {
            setMeasuredDimension(this.mBackGroundHeight, minimumWidth);
        } else {
            setMeasuredDimension(minimumWidth, this.mBackGroundHeight);
        }
    }
}
