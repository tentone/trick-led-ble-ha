package com.qh.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.ViewCompat;
import com.qh.tools.tools;
import com.xiaoyu.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class ColorWheelView extends View {
    private final String TAG;
    public int alpha;
    public float angle;
    private int bigColor;
    public int blue;
    public Bitmap bmp;
    private float bmpBlackX;
    private float bmpBlackY;
    public Bitmap bmpSlide;
    public Bitmap bmpSlideLine;
    public float bmpSlidePR;
    private float bmpSlideR;
    private float bmpSlideX;
    private float bmpSlideY;
    private int color;
    private int color2;
    private Context context;
    public boolean debug;
    public float fprogress;
    public int green;
    private boolean isDown;
    private boolean isPicture;
    private boolean isRing;
    private boolean isSet;
    private Bitmap mBitmap;
    private Canvas mBitmapCanvas;
    private Paint mBlackp;
    private Bitmap mCanvasBitmap;
    private float mCanvasBitmapR;
    private int[] mCircleColors;
    private int[] mColors;
    private ComposeShader mCombinedShader;
    private Paint mCursorPaint;
    public Handler mHandler;
    private int mHeight;
    private float[] mHsv;
    private Paint mHsvPaint;
    private Shader mRadialGradient;
    private RadialGradient mRadialShader;
    private Paint mSlidePaint;
    private SweepGradient mSweepShader;
    private int mWidth;
    private Matrix matrix;
    private Paint mbigPaint;
    private Bitmap mgetBitmap;
    private int mh;
    private int mw;
    public ColorChangeInterface myColorChangeInterface;
    private int myoffsetcenter;
    private int myprogress;
    private int mysetcenter;
    private float oldangle;
    private long oldtime;
    public int p1;
    public int p2;
    public int p3;
    public int p4;
    public int p5;
    public int p6;
    public int p7;
    public int progress;
    private float r;
    public int red;
    long timet;
    private int wx;
    private int wy;

    public interface ColorChangeInterface {
        void colorChange(int i, float f, boolean z);
    }

    private float calculateX(float f, float f2, float f3) {
        return (f3 * f) / f2;
    }

    private float calculateY(float f, float f2, float f3) {
        return (f3 * f) / f2;
    }

    private boolean inCenter(float f, float f2, float f3) {
        return true;
    }

    public void setOnColorChangeInterface(ColorChangeInterface colorChangeInterface) {
        this.myColorChangeInterface = colorChangeInterface;
    }

    public void setSunColor(int i) {
        int i2 = i & ViewCompat.MEASURED_SIZE_MASK;
        this.color = ViewCompat.MEASURED_STATE_MASK + i2;
        this.color2 = i2 + 16777216;
    }

    public void setRing(boolean z) {
        this.isRing = z;
        invalidate();
    }

    public boolean getRing() {
        return this.isRing;
    }

    public void setPicture(boolean z, Bitmap bitmap) {
        this.isPicture = z;
        this.mBitmap = bitmap;
        invalidate();
    }

    public boolean getPicture() {
        return this.isPicture;
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float f) {
        this.angle = f;
    }

    public ColorWheelView(Context context) {
        super(context);
        this.debug = false;
        this.TAG = "ColorPicker";
        this.alpha = 0;
        this.red = 0;
        this.green = 0;
        this.blue = 0;
        this.mColors = new int[13];
        this.mHsv = new float[3];
        this.bigColor = -1;
        this.mysetcenter = 160;
        this.bmpSlideR = 340.0f;
        this.angle = 0.0f;
        this.progress = 0;
        this.fprogress = 0.0f;
        this.isRing = true;
        this.matrix = new Matrix();
        this.mCanvasBitmap = null;
        this.mBitmapCanvas = null;
        this.mRadialShader = null;
        this.mCanvasBitmapR = 0.0f;
        this.mSweepShader = null;
        this.mCombinedShader = null;
        this.mHsvPaint = new Paint();
        this.mCursorPaint = new Paint();
        this.isPicture = false;
        this.isSet = false;
        this.color = 16767744;
        this.color2 = 16767744;
        this.mh = 0;
        this.mw = 0;
        this.oldtime = 0L;
        this.oldangle = 0.0f;
        this.isDown = true;
        this.wx = 0;
        this.wy = 0;
        this.timet = -1L;
        getContext();
        init();
    }

    public ColorWheelView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.debug = false;
        this.TAG = "ColorPicker";
        this.alpha = 0;
        this.red = 0;
        this.green = 0;
        this.blue = 0;
        this.mColors = new int[13];
        this.mHsv = new float[3];
        this.bigColor = -1;
        this.mysetcenter = 160;
        this.bmpSlideR = 340.0f;
        this.angle = 0.0f;
        this.progress = 0;
        this.fprogress = 0.0f;
        this.isRing = true;
        this.matrix = new Matrix();
        this.mCanvasBitmap = null;
        this.mBitmapCanvas = null;
        this.mRadialShader = null;
        this.mCanvasBitmapR = 0.0f;
        this.mSweepShader = null;
        this.mCombinedShader = null;
        this.mHsvPaint = new Paint();
        this.mCursorPaint = new Paint();
        this.isPicture = false;
        this.isSet = false;
        this.color = 16767744;
        this.color2 = 16767744;
        this.mh = 0;
        this.mw = 0;
        this.oldtime = 0L;
        this.oldangle = 0.0f;
        this.isDown = true;
        this.wx = 0;
        this.wy = 0;
        this.timet = -1L;
        TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.MyView);
        this.mWidth = (int) typedArrayObtainStyledAttributes.getDimension(3, 1000.0f);
        this.mHeight = (int) typedArrayObtainStyledAttributes.getDimension(0, 1000.0f);
        this.myprogress = (int) typedArrayObtainStyledAttributes.getDimension(2, 1000.0f);
        this.myoffsetcenter = typedArrayObtainStyledAttributes.getInteger(1, 50);
        this.r = this.mHeight / 2;
        float[] fArr = this.mHsv;
        fArr[0] = 0.0f;
        fArr[1] = 1.0f;
        fArr[2] = 1.0f;
        for (int i = 0; i < 12; i++) {
            this.mColors[i] = Color.HSVToColor(this.mHsv);
            float[] fArr2 = this.mHsv;
            fArr2[0] = fArr2[0] + 30.0f;
        }
        int[] iArr = this.mColors;
        iArr[12] = iArr[0];
        this.mHsvPaint.setDither(true);
        init();
    }

    public ColorWheelView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.debug = false;
        this.TAG = "ColorPicker";
        this.alpha = 0;
        this.red = 0;
        this.green = 0;
        this.blue = 0;
        this.mColors = new int[13];
        this.mHsv = new float[3];
        this.bigColor = -1;
        this.mysetcenter = 160;
        this.bmpSlideR = 340.0f;
        this.angle = 0.0f;
        this.progress = 0;
        this.fprogress = 0.0f;
        this.isRing = true;
        this.matrix = new Matrix();
        this.mCanvasBitmap = null;
        this.mBitmapCanvas = null;
        this.mRadialShader = null;
        this.mCanvasBitmapR = 0.0f;
        this.mSweepShader = null;
        this.mCombinedShader = null;
        this.mHsvPaint = new Paint();
        this.mCursorPaint = new Paint();
        this.isPicture = false;
        this.isSet = false;
        this.color = 16767744;
        this.color2 = 16767744;
        this.mh = 0;
        this.mw = 0;
        this.oldtime = 0L;
        this.oldangle = 0.0f;
        this.isDown = true;
        this.wx = 0;
        this.wy = 0;
        this.timet = -1L;
        getContext();
        init();
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap;
        canvas.translate(this.mWidth / 2, this.mHeight / 2);
        if (!this.isSet) {
            this.mh = canvas.getHeight();
            this.mw = canvas.getWidth();
            this.bmpSlideLine = tools.postScale(this.bmpSlideLine, (this.mw / this.bmpSlideLine.getWidth()) / 1.25f);
            this.isSet = true;
        }
        if (this.isPicture && (bitmap = this.mBitmap) != null) {
            canvas.drawBitmap(bitmap, (-(this.mWidth - 100)) / 2, (-(this.mHeight - 100)) / 2, (Paint) null);
            this.mBlackp.setColor(Color.argb(255, 255 - Color.red(this.bigColor), 255 - Color.green(this.bigColor), 255 - Color.blue(this.bigColor)));
            canvas.drawCircle(this.wx, this.wy, 10.0f, this.mBlackp);
            return;
        }
        if (this.isRing) {
            canvas.drawCircle(0.0f, 0.0f, this.r - 80.0f, this.mbigPaint);
            int i = this.color;
            RadialGradient radialGradient = new RadialGradient(0.0f, 0.0f, this.p1, new int[]{i, i, this.color2}, (float[]) null, Shader.TileMode.CLAMP);
            Paint paint = new Paint(1);
            paint.setShader(radialGradient);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(this.p2);
            canvas.drawCircle(0.0f, 0.0f, this.p3, paint);
            Paint paint2 = new Paint(1);
            paint2.setColor(this.color);
            paint2.setStrokeWidth(20.0f);
            canvas.drawCircle(0.0f, 0.0f, this.p4, paint2);
        } else {
            Paint paint3 = new Paint(1);
            paint3.setColor(this.bigColor);
            paint3.setStrokeWidth(20.0f);
            canvas.drawCircle(0.0f, 0.0f, ((this.mWidth - 100) / 2) + 20, paint3);
            Bitmap bitmap2 = this.bmp;
            int i2 = this.mWidth;
            canvas.drawBitmap(bitmap2, (((-i2) / 2) + (i2 / 4)) - 50, ((-i2) / 2.4f) + (i2 / 10), (Paint) null);
            canvas.drawBitmap(this.mCanvasBitmap, (-(this.mWidth - 100)) / 2, (-(this.mHeight - 100)) / 2, (Paint) null);
            this.mBlackp.setColor(Color.argb(255, 255 - Color.red(this.bigColor), 255 - Color.green(this.bigColor), 255 - Color.blue(this.bigColor)));
            canvas.drawCircle(this.bmpBlackX, this.bmpBlackY, 10.0f, this.mBlackp);
        }
        super.onDraw(canvas);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX() - (this.mWidth / 2);
        float y = motionEvent.getY() - (this.mHeight / 2);
        this.wx = (int) x;
        this.wy = (int) y;
        if (this.isPicture && this.mBitmap != null) {
            int x2 = ((int) motionEvent.getX()) - 50;
            int y2 = ((int) motionEvent.getY()) - 50;
            if (x2 < 0) {
                x2 = 0;
            }
            if (y2 < 0) {
                y2 = 0;
            }
            if (x2 > this.mBitmap.getWidth() - 5) {
                x2 = this.mBitmap.getWidth() - 5;
            }
            if (y2 > this.mBitmap.getHeight() - 5) {
                y2 = this.mBitmap.getHeight() - 5;
            }
            int pixel = this.mBitmap.getPixel(x2, y2);
            if (this.myColorChangeInterface != null) {
                this.bigColor = pixel;
                if (motionEvent.getAction() == 1) {
                    this.myColorChangeInterface.colorChange(pixel, this.angle + 130.0f, true);
                } else {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    if (jCurrentTimeMillis - this.timet >= 100) {
                        this.timet = jCurrentTimeMillis;
                        this.myColorChangeInterface.colorChange(pixel, this.angle + 130.0f, false);
                    }
                }
            }
            invalidate();
            return true;
        }
        double dAtan2 = (float) Math.atan2(y, x);
        Double.isNaN(dAtan2);
        float f = (float) (dAtan2 / 6.283185307179586d);
        if (f < 0.0f) {
            f += 1.0f;
        }
        if (this.isRing) {
            this.angle = getPOSAngle(x, y);
            this.bmpSlideX = calculateX(this.bmpSlideR, getR(x, y), x);
            this.bmpSlideY = calculateX(this.bmpSlideR, getR(x, y), y);
            int iInterpCircleColor = interpCircleColor(this.mCircleColors, f);
            setSunColor(iInterpCircleColor);
            if (motionEvent.getAction() == 1) {
                this.myColorChangeInterface.colorChange(iInterpCircleColor, this.angle + 130.0f, true);
            } else {
                long jCurrentTimeMillis2 = System.currentTimeMillis();
                if (jCurrentTimeMillis2 - this.timet >= 100) {
                    this.timet = jCurrentTimeMillis2;
                    this.myColorChangeInterface.colorChange(iInterpCircleColor, this.angle + 130.0f, false);
                }
            }
        } else {
            float fSqrt = (int) Math.sqrt((x * x) + (y * y));
            float f2 = this.mCanvasBitmapR;
            if (fSqrt < f2) {
                this.bmpBlackX = calculateX(fSqrt, getR(x, y), x);
                this.bmpBlackY = calculateX(fSqrt, getR(x, y), y);
            } else {
                this.bmpBlackX = calculateX(f2, getR(x, y), x);
                this.bmpBlackY = calculateX(this.mCanvasBitmapR, getR(x, y), y);
            }
            motionEvent.getX();
            motionEvent.getY();
            int i = x >= 0.0f ? -5 : 5;
            int i2 = y >= 0.0f ? -5 : 5;
            float f3 = this.mCanvasBitmapR;
            int pixel2 = this.mCanvasBitmap.getPixel((int) (this.bmpBlackX + f3 + i), (int) (f3 + this.bmpBlackY + i2));
            if (this.myColorChangeInterface != null) {
                this.bigColor = pixel2;
                if (motionEvent.getAction() == 1) {
                    this.myColorChangeInterface.colorChange(pixel2, this.angle, true);
                } else {
                    long jCurrentTimeMillis3 = System.currentTimeMillis();
                    if (jCurrentTimeMillis3 - this.timet >= 100) {
                        this.timet = jCurrentTimeMillis3;
                        this.myColorChangeInterface.colorChange(pixel2, this.angle, false);
                    }
                }
            }
        }
        invalidate();
        return true;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(this.mWidth, this.mHeight);
    }

    private boolean inColorCircle(float f, float f2, float f3, float f4) {
        double d = 140.0f + f3;
        Double.isNaN(d);
        double d2 = f3;
        Double.isNaN(d2);
        double d3 = d * 3.141592653589793d * d2;
        double d4 = f4 - this.mysetcenter;
        Double.isNaN(d4);
        double d5 = f4;
        Double.isNaN(d5);
        double d6 = d4 * 3.141592653589793d * d5;
        double d7 = (f * f) + (f2 * f2);
        Double.isNaN(d7);
        double d8 = d7 * 3.141592653589793d;
        return d8 < d3 + 100.0d && d8 > d6 - 100.0d;
    }

    private int interpCircleColor(int[] iArr, float f) {
        if (f <= 0.0f) {
            return iArr[0];
        }
        if (f >= 1.0f) {
            return iArr[iArr.length - 1];
        }
        float length = f * (iArr.length - 1);
        int i = (int) length;
        float f2 = length - i;
        int i2 = iArr[i];
        int i3 = iArr[i + 1];
        int iAve = ave(Color.alpha(i2), Color.alpha(i3), f2);
        int iAve2 = ave(Color.red(i2), Color.red(i3), f2);
        int iAve3 = ave(Color.green(i2), Color.green(i3), f2);
        int iAve4 = ave(Color.blue(i2), Color.blue(i3), f2);
        this.alpha = iAve;
        this.red = iAve2;
        this.green = iAve3;
        this.blue = iAve4;
        return Color.argb(iAve, iAve2, iAve3, iAve4);
    }

    private int ave(int i, int i2, float f) {
        return i + Math.round(f * (i2 - i));
    }

    private void init() {
        setMinimumHeight(this.mHeight);
        setMinimumWidth(this.mWidth);
        this.bmp = BitmapFactory.decodeResource(getResources(), com.qh.onehlight.R.drawable.ic_stars);
        this.bmpSlide = BitmapFactory.decodeResource(getResources(), com.qh.onehlight.R.drawable.ic_bmpslide);
        this.bmpSlideLine = BitmapFactory.decodeResource(getResources(), com.qh.onehlight.R.drawable.ic_slideline);
        this.bmpSlidePR = this.bmpSlide.getHeight() / 2;
        this.mCircleColors = new int[]{-16711936, -16711681, -16776961, -65281, SupportMenu.CATEGORY_MASK, InputDeviceCompat.SOURCE_ANY, -16711936};
        this.mRadialGradient = new SweepGradient(0.0f, 0.0f, this.mCircleColors, (float[]) null);
        Paint paint = new Paint(1);
        this.mbigPaint = paint;
        paint.setShader(this.mRadialGradient);
        this.mbigPaint.setStyle(Paint.Style.STROKE);
        this.mbigPaint.setStrokeWidth(this.p5);
        setSunColor(-9472);
        Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(getResources(), com.qh.onehlight.R.drawable.ic_big_solorwheel);
        this.mCanvasBitmap = bitmapDecodeResource;
        int i = this.mWidth;
        this.mCanvasBitmap = Bitmap.createScaledBitmap(bitmapDecodeResource, i - 100, i - 100, true);
        this.mCanvasBitmapR = (this.mWidth - 100) / 2;
        Paint paint2 = new Paint(1);
        this.mBlackp = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.mBlackp.setColor(SupportMenu.CATEGORY_MASK);
        this.mBlackp.setStrokeWidth(4.0f);
    }

    private float getPOSAngle(float f, float f2) {
        double d = (f * f) + (f2 * f2);
        float fSqrt = (this.r / ((float) Math.sqrt(d))) * f;
        float fSqrt2 = (this.r / ((float) Math.sqrt(d))) * f2;
        float fAbs = (fSqrt > 0.0f || fSqrt2 < 0.0f) ? 0.0f : (float) Math.abs(Math.toDegrees(Math.atan(fSqrt / fSqrt2)));
        if (fSqrt <= 0.0f && fSqrt2 <= 0.0f) {
            fAbs = 180.0f - ((float) Math.abs(Math.toDegrees(Math.atan(fSqrt / fSqrt2))));
        }
        if (fSqrt >= 0.0f && fSqrt2 <= 0.0f) {
            fAbs = ((float) Math.abs(Math.toDegrees(Math.atan(fSqrt / fSqrt2)))) + 180.0f;
        }
        return (fSqrt < 0.0f || fSqrt2 < 0.0f) ? fAbs : 360.0f - ((float) Math.abs(Math.toDegrees(Math.atan(fSqrt / fSqrt2))));
    }

    private float getR(float f, float f2) {
        return (float) Math.sqrt((f * f) + (f2 * f2));
    }
}
