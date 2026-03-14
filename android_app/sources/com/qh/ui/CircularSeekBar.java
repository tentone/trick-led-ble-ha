package com.qh.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatImageView;
import com.qh.onehlight.R;
import com.qh.tools.Tool;

/* JADX INFO: loaded from: classes.dex */
public class CircularSeekBar extends AppCompatImageView {
    private float height;
    public boolean isDraw;
    private Context mContext;
    Paint mRectPaint;
    private Resources mResources;
    public int midcolor;
    private float mx;
    private float my;
    private OnChangeListener myOnChangeListener;
    public RectF oval;
    public RectF oval2;
    public float ringPadding;
    public Paint ringPaint;
    public Paint ringPaint2;
    public float ringR;
    public int ringcolor;
    public float ringwidth;
    private Bitmap thumbBitmap;
    public float vacancyAngle;
    private float width;

    public interface OnChangeListener {
        void onChange(int i);

        void onStopTrackingTouch(int i);
    }

    private float calculateX(float f, float f2, float f3) {
        return (f * f3) / f2;
    }

    private float calculateY(float f, float f2, float f3) {
        return (f3 * f) / f2;
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.myOnChangeListener = onChangeListener;
    }

    public CircularSeekBar(Context context) {
        this(context, null);
    }

    public CircularSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CircularSeekBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public CircularSeekBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i);
        this.oval = new RectF();
        this.oval2 = new RectF();
        this.ringR = 0.0f;
        this.ringPadding = 0.0f;
        this.ringwidth = 0.0f;
        this.vacancyAngle = 6.0f;
        this.ringPaint = new Paint();
        this.ringPaint2 = new Paint();
        this.mRectPaint = new Paint();
        this.ringcolor = -1721144983;
        this.midcolor = -8531;
        this.isDraw = false;
        this.mx = 0.0f;
        this.my = 0.0f;
        this.mContext = context;
        Resources resources = context.getResources();
        this.mResources = resources;
        this.ringPadding = resources.getDimension(R.dimen.huadongpadding);
        this.thumbBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_seek_thumb);
        float fDp2px = Tool.dp2px(this.mContext, 42.0f);
        this.ringwidth = fDp2px;
        this.thumbBitmap = scaleBitmap(this.thumbBitmap, (int) (fDp2px - Tool.dp2px(this.mContext, 2.5f)), (int) (this.ringwidth - Tool.dp2px(this.mContext, 2.5f)));
        this.ringPaint.setAntiAlias(true);
        this.ringPaint.setStrokeWidth(this.ringwidth);
        this.ringPaint.setStyle(Paint.Style.STROKE);
        this.ringPaint.setColor(this.ringcolor);
        this.ringPaint2.setAntiAlias(true);
        this.ringPaint2.setStrokeWidth(this.ringwidth);
        this.ringPaint2.setStyle(Paint.Style.STROKE);
        this.ringPaint2.setColor(this.midcolor);
        this.mRectPaint.setColor(this.midcolor);
        this.mRectPaint.setStrokeWidth(this.ringwidth);
        this.mRectPaint.setStyle(Paint.Style.FILL);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(this.width / 2.0f, this.height / 2.0f);
        canvas.drawArc(this.oval, 0.0f, 360.0f, false, this.ringPaint);
        float degrees = getDegrees(this.mx, this.my);
        canvas.drawArc(this.oval, -90.0f, (degrees < 180.0f || degrees > 360.0f) ? degrees + 180.0f : degrees - 180.0f, false, this.ringPaint2);
        canvas.drawCircle(0.0f, ((-this.height) / 2.0f) + this.ringPadding, this.ringwidth / 2.0f, this.mRectPaint);
        drawthemCircle(this.mx, this.my, canvas);
    }

    public void setPro(int i) {
        float f = (this.height / 2.0f) - this.ringPadding;
        float f2 = i * 3.6f;
        if (f2 == 360.0f) {
            f2 = 359.8f;
        }
        if (f2 <= 90.0f) {
            double d = f;
            double d2 = f2 / 180.0f;
            Double.isNaN(d2);
            double d3 = d2 * 3.141592653589793d;
            double dSin = Math.sin(d3);
            Double.isNaN(d);
            this.mx = (float) (dSin * d);
            double dCos = Math.cos(d3);
            Double.isNaN(d);
            this.my = -((float) (d * dCos));
        } else if (f2 > 90.0f && f2 <= 180.0f) {
            double d4 = f;
            double d5 = (f2 - 90.0f) / 180.0f;
            Double.isNaN(d5);
            double d6 = d5 * 3.141592653589793d;
            double dSin2 = Math.sin(d6);
            Double.isNaN(d4);
            this.my = (float) (dSin2 * d4);
            double dCos2 = Math.cos(d6);
            Double.isNaN(d4);
            this.mx = (float) (d4 * dCos2);
        } else if (f2 > 180.0f && f2 < 270.0f) {
            double d7 = f;
            double d8 = (360.0f - f2) / 180.0f;
            Double.isNaN(d8);
            double d9 = d8 * 3.141592653589793d;
            double dSin3 = Math.sin(d9);
            Double.isNaN(d7);
            this.mx = -((float) (dSin3 * d7));
            double dCos3 = Math.cos(d9);
            Double.isNaN(d7);
            this.my = -((float) (d7 * dCos3));
        } else {
            double d10 = f;
            double d11 = (360.0f - f2) / 180.0f;
            Double.isNaN(d11);
            double d12 = d11 * 3.141592653589793d;
            double dSin4 = Math.sin(d12);
            Double.isNaN(d10);
            this.mx = -((float) (dSin4 * d10));
            double dCos4 = Math.cos(d12);
            Double.isNaN(d10);
            this.my = -((float) (d10 * dCos4));
        }
        Log.e("setPro", "setPro=" + i + " mx=" + this.mx + " my=" + this.my);
        this.isDraw = true;
        invalidate();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (getMeasuredWidth() <= getMeasuredHeight()) {
            this.width = getMeasuredWidth();
            this.height = getMeasuredWidth();
        } else {
            this.width = getMeasuredHeight();
            this.height = getMeasuredHeight();
        }
        float f = this.width;
        float f2 = this.ringPadding;
        this.oval = new RectF(((-f) / 2.0f) + f2, ((-f) / 2.0f) + f2, (f / 2.0f) - f2, (f / 2.0f) - f2);
        float f3 = this.width / 2.0f;
        float f4 = this.ringPadding;
        this.ringR = f3 - f4;
        if (this.isDraw) {
            return;
        }
        this.mx = 0.0f;
        this.my = (this.height / 2.0f) - f4;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int iRound;
        OnChangeListener onChangeListener;
        motionEvent.getX();
        motionEvent.getY();
        float degrees = getDegrees(motionEvent.getX() - (this.width / 2.0f), motionEvent.getY() - (this.height / 2.0f));
        if (degrees >= 180.0f && degrees <= 360.0f) {
            iRound = Math.round(((degrees - 180.0f) * 100.0f) / 360.0f);
        } else {
            iRound = Math.round(((degrees + 180.0f) * 100.0f) / 360.0f);
        }
        int action = motionEvent.getAction();
        if (action == 1) {
            OnChangeListener onChangeListener2 = this.myOnChangeListener;
            if (onChangeListener2 != null) {
                onChangeListener2.onStopTrackingTouch(iRound);
            }
        } else if (action == 2 && (onChangeListener = this.myOnChangeListener) != null) {
            onChangeListener.onChange(iRound);
        }
        this.mx = (int) calculateX(this.ringR, getR(r0, r1), r0);
        this.my = (int) calculateY(this.ringR, getR(r0, r1), r1);
        invalidate();
        return true;
    }

    private float getR(float f, float f2) {
        return (float) Math.sqrt((f * f) + (f2 * f2));
    }

    public float getDegrees(float f, float f2) {
        double d = (f * f) + (f2 * f2);
        float fSqrt = ((this.width / 2.0f) / ((float) Math.sqrt(d))) * f;
        float fSqrt2 = ((this.width / 2.0f) / ((float) Math.sqrt(d))) * f2;
        float fAbs = (fSqrt > 0.0f || fSqrt2 < 0.0f) ? 0.0f : (float) Math.abs(Math.toDegrees(Math.atan(fSqrt / fSqrt2)));
        if (fSqrt <= 0.0f && fSqrt2 <= 0.0f) {
            fAbs = 180.0f - ((float) Math.abs(Math.toDegrees(Math.atan(fSqrt / fSqrt2))));
        }
        if (fSqrt >= 0.0f && fSqrt2 <= 0.0f) {
            fAbs = ((float) Math.abs(Math.toDegrees(Math.atan(fSqrt / fSqrt2)))) + 180.0f;
        }
        return (fSqrt < 0.0f || fSqrt2 < 0.0f) ? fAbs : 360.0f - ((float) Math.abs(Math.toDegrees(Math.atan(fSqrt / fSqrt2))));
    }

    public void drawthemCircle(float f, float f2, Canvas canvas) {
        canvas.drawCircle(f, f2, this.ringwidth / 2.0f, this.mRectPaint);
        canvas.drawBitmap(this.thumbBitmap, f - (r0.getWidth() / 2), f2 - (this.thumbBitmap.getHeight() / 2), new Paint(1));
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            return null;
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(i / width, i2 / height);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bitmapCreateBitmap;
    }
}
