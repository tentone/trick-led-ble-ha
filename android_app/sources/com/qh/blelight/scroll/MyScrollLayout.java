package com.qh.blelight.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/* JADX INFO: loaded from: classes.dex */
public class MyScrollLayout extends ViewGroup {
    private static final int SNAP_VELOCITY = 600;
    private static final String TAG = "ScrollLayout";
    private int mCurScreen;
    private int mDefaultScreen;
    private float mLastMotionX;
    private OnViewChangeListener mOnViewChangeListener;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    public MyScrollLayout(Context context) {
        super(context);
        this.mDefaultScreen = 0;
        init(context);
    }

    public MyScrollLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDefaultScreen = 0;
        init(context);
    }

    public MyScrollLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDefaultScreen = 0;
        init(context);
    }

    private void init(Context context) {
        this.mCurScreen = this.mDefaultScreen;
        this.mScroller = new Scroller(context);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            int childCount = getChildCount();
            int i5 = 0;
            for (int i6 = 0; i6 < childCount; i6++) {
                View childAt = getChildAt(i6);
                if (childAt.getVisibility() != 8) {
                    int measuredWidth = childAt.getMeasuredWidth() + i5;
                    childAt.layout(i5, 0, measuredWidth, childAt.getMeasuredHeight());
                    i5 = measuredWidth;
                }
            }
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = View.MeasureSpec.getSize(i);
        View.MeasureSpec.getMode(i);
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            getChildAt(i3).measure(i, i2);
        }
        scrollTo(this.mCurScreen * size, 0);
    }

    public void snapToDestination() {
        int width = getWidth();
        snapToScreen((getScrollX() + (width / 2)) / width);
    }

    public void snapToScreen(int i) {
        int iMax = Math.max(0, Math.min(i, getChildCount() - 1));
        if (getScrollX() != getWidth() * iMax) {
            int width = (getWidth() * iMax) - getScrollX();
            this.mScroller.startScroll(getScrollX(), 0, width, 0, Math.abs(width) * 2);
            this.mCurScreen = iMax;
            invalidate();
            OnViewChangeListener onViewChangeListener = this.mOnViewChangeListener;
            if (onViewChangeListener != null) {
                onViewChangeListener.OnViewChange(this.mCurScreen);
            }
        }
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int i;
        int action = motionEvent.getAction();
        float x = motionEvent.getX();
        motionEvent.getY();
        if (action != 0) {
            int xVelocity = 0;
            if (action == 1) {
                VelocityTracker velocityTracker = this.mVelocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.addMovement(motionEvent);
                    this.mVelocityTracker.computeCurrentVelocity(1000);
                    xVelocity = (int) this.mVelocityTracker.getXVelocity();
                }
                if (xVelocity > SNAP_VELOCITY && (i = this.mCurScreen) > 0) {
                    snapToScreen(i - 1);
                } else if (xVelocity < -600 && this.mCurScreen < getChildCount() - 1) {
                    Log.e(TAG, "snap right");
                    snapToScreen(this.mCurScreen + 1);
                } else {
                    snapToDestination();
                }
                VelocityTracker velocityTracker2 = this.mVelocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.mVelocityTracker = null;
                }
            } else if (action == 2) {
                int i2 = (int) (this.mLastMotionX - x);
                if (IsCanMove(i2)) {
                    VelocityTracker velocityTracker3 = this.mVelocityTracker;
                    if (velocityTracker3 != null) {
                        velocityTracker3.addMovement(motionEvent);
                    }
                    this.mLastMotionX = x;
                    scrollBy(i2, 0);
                }
            }
        } else {
            Log.i("", "onTouchEvent  ACTION_DOWN");
            if (this.mVelocityTracker == null) {
                VelocityTracker velocityTrackerObtain = VelocityTracker.obtain();
                this.mVelocityTracker = velocityTrackerObtain;
                velocityTrackerObtain.addMovement(motionEvent);
            }
            if (!this.mScroller.isFinished()) {
                this.mScroller.abortAnimation();
            }
            this.mLastMotionX = x;
        }
        return true;
    }

    private boolean IsCanMove(int i) {
        if (getScrollX() > 0 || i >= 0) {
            return getScrollX() < (getChildCount() - 1) * getWidth() || i <= 0;
        }
        return false;
    }

    public void SetOnViewChangeListener(OnViewChangeListener onViewChangeListener) {
        this.mOnViewChangeListener = onViewChangeListener;
    }
}
