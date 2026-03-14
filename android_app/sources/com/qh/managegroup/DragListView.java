package com.qh.managegroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import com.qh.data.ItemInfo;
import com.qh.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class DragListView extends ListView {
    private static final int ANIMATION_DURATION = 200;
    public static final int MSG_DRAG_MOVE = 4098;
    public static final int MSG_DRAG_STOP = 4097;
    private static final int step = 1;
    private boolean bHasGetSapcing;
    public View cache;
    private int current_Step;
    private int downScrollBounce;
    private ImageView dragImageView;
    private ViewGroup dragItemView;
    private int dragOffset;
    private int dragPoint;
    private int dragPosition;
    private int holdPosition;
    private boolean isDragItemMoving;
    private boolean isLock;
    private boolean isMoving;
    private boolean isNormal;
    private boolean isSameDragDirection;
    private boolean isScroll;
    private int lastFlag;
    private int lastPosition;
    private int mCurFirstVisiblePosition;
    private int mCurLastVisiblePosition;
    private ItemInfo mDragItemInfo;
    public DragListChange mDragListChange;
    private int mFirstVisiblePosition;
    Handler mHandler;
    private int mItemVerticalSpacing;
    private int mLastVisiblePosition;
    private int scaledTouchSlop;
    private int startPosition;
    private int turnDownPosition;
    private int turnUpPosition;
    private int upScrollBounce;
    private WindowManager windowManager;
    private WindowManager.LayoutParams windowParams;

    public interface DragListChange {
        void change(MyListData myListData, MyListData myListData2);

        void delect(MyListData myListData);

        void resetname(MyListData myListData);
    }

    public void setDragListChange(DragListChange dragListChange) {
        DragListAdapter dragListAdapter = (DragListAdapter) getAdapter();
        if (dragListAdapter != null) {
            dragListAdapter.setDragListChange(dragListChange);
        }
        this.mDragListChange = dragListChange;
    }

    public void setLock(boolean z) {
        this.isLock = z;
    }

    public DragListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.dragItemView = null;
        this.isMoving = false;
        this.isDragItemMoving = false;
        this.mItemVerticalSpacing = 0;
        this.bHasGetSapcing = false;
        this.mHandler = new Handler() { // from class: com.qh.managegroup.DragListView.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 4097) {
                    DragListView.this.stopDrag();
                    DragListView.this.onDrop(message.arg1);
                } else {
                    if (i != 4098) {
                        return;
                    }
                    DragListView.this.onDrag(message.arg1);
                }
            }
        };
        this.isSameDragDirection = true;
        this.lastFlag = -1;
        this.isNormal = true;
        this.isScroll = false;
        setLayerType(2, null);
        this.scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mDragItemInfo = new ItemInfo();
        init();
    }

    private void init() {
        this.windowManager = (WindowManager) getContext().getSystemService("window");
    }

    private void getSpacing() {
        this.bHasGetSapcing = true;
        this.upScrollBounce = getHeight() / 3;
        this.downScrollBounce = (getHeight() * 2) / 3;
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        ViewGroup viewGroup2 = (ViewGroup) getChildAt(1);
        if (viewGroup != null) {
            viewGroup.getLocationOnScreen(iArr);
            if (viewGroup2 != null) {
                viewGroup2.getLocationOnScreen(iArr2);
                this.mItemVerticalSpacing = Math.abs(iArr2[1] - iArr[1]);
            }
        }
    }

    @Override // android.widget.AbsListView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && !this.isLock && !this.isMoving && !this.isDragItemMoving) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            int iPointToPosition = pointToPosition(x, y);
            this.dragPosition = iPointToPosition;
            this.startPosition = iPointToPosition;
            this.lastPosition = iPointToPosition;
            if (iPointToPosition == -1) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            if (!this.bHasGetSapcing) {
                getSpacing();
            }
            ViewGroup viewGroup = (ViewGroup) getChildAt(this.dragPosition - getFirstVisiblePosition());
            DragListAdapter dragListAdapter = (DragListAdapter) getAdapter();
            this.mDragItemInfo.obj = dragListAdapter.getItem(this.dragPosition - getFirstVisiblePosition());
            this.dragPoint = y - viewGroup.getTop();
            this.dragOffset = (int) (motionEvent.getRawY() - y);
            View viewFindViewById = viewGroup.findViewById(R.id.light_img);
            if (viewFindViewById != null && x > viewFindViewById.getLeft() - 20) {
                this.cache = viewFindViewById;
                this.dragItemView = viewGroup;
                viewGroup.destroyDrawingCache();
                viewGroup.setDrawingCacheEnabled(true);
                viewGroup.setBackgroundColor(1431655765);
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(viewGroup.getDrawingCache(true));
                hideDropItem();
                dragListAdapter.setInvisiblePosition(this.startPosition);
                dragListAdapter.notifyDataSetChanged();
                startDrag(bitmapCreateBitmap, y);
                this.isMoving = false;
                dragListAdapter.copyList();
            }
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public Animation getScaleAnimation() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 0.0f, 0.0f, 0.0f, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }

    private void hideDropItem() {
        ((DragListAdapter) getAdapter()).showDropItem(false);
    }

    @Override // android.widget.AbsListView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.dragImageView != null && this.dragPosition != -1 && !this.isLock) {
            int action = motionEvent.getAction();
            if (action == 1) {
                Log.e("", "dragPosition = " + this.dragPosition);
                DragListAdapter dragListAdapter = (DragListAdapter) getAdapter();
                MyListData myListData = (MyListData) this.cache.getTag();
                int size = this.dragPosition - 1;
                if (size < 0) {
                    size = 0;
                }
                if (size > dragListAdapter.arrayTitles.size()) {
                    size = dragListAdapter.arrayTitles.size() - 1;
                }
                int y = (int) motionEvent.getY();
                stopDrag();
                onDrop(y);
                MyListData myListData2 = dragListAdapter.arrayTitles.get(size);
                DragListChange dragListChange = this.mDragListChange;
                if (dragListChange != null) {
                    dragListChange.change(myListData, myListData2);
                }
            } else if (action == 2) {
                int y2 = (int) motionEvent.getY();
                onDrag(y2);
                testAnimation(y2);
            }
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    private void onChangeCopy(int i, int i2) {
        DragListAdapter dragListAdapter = (DragListAdapter) getAdapter();
        if (i != i2) {
            dragListAdapter.exchangeCopy(i, i2);
            Log.i("wanggang", "onChange");
        }
    }

    private void testAnimation(int i) {
        int i2;
        Animation toSelfAnimation;
        DragListAdapter dragListAdapter = (DragListAdapter) getAdapter();
        int iPointToPosition = pointToPosition(0, i);
        if (iPointToPosition == -1 || iPointToPosition == this.lastPosition) {
            return;
        }
        this.mFirstVisiblePosition = getFirstVisiblePosition();
        this.dragPosition = iPointToPosition;
        onChangeCopy(this.lastPosition, iPointToPosition);
        int i3 = iPointToPosition - this.lastPosition;
        int iAbs = Math.abs(i3);
        for (int i4 = 1; i4 <= iAbs; i4++) {
            if (i3 > 0) {
                if (this.lastFlag == -1) {
                    this.lastFlag = 0;
                    this.isSameDragDirection = true;
                }
                if (this.lastFlag == 1) {
                    this.turnUpPosition = iPointToPosition;
                    this.lastFlag = 0;
                    this.isSameDragDirection = !this.isSameDragDirection;
                }
                boolean z = this.isSameDragDirection;
                if (z) {
                    this.holdPosition = this.lastPosition + 1;
                } else if (this.startPosition < iPointToPosition) {
                    this.holdPosition = this.lastPosition + 1;
                    this.isSameDragDirection = !z;
                } else {
                    this.holdPosition = this.lastPosition;
                }
                i2 = -this.mItemVerticalSpacing;
                this.lastPosition++;
            } else {
                if (this.lastFlag == -1) {
                    this.lastFlag = 1;
                    this.isSameDragDirection = true;
                }
                if (this.lastFlag == 0) {
                    this.turnDownPosition = iPointToPosition;
                    this.lastFlag = 1;
                    this.isSameDragDirection = !this.isSameDragDirection;
                }
                boolean z2 = this.isSameDragDirection;
                if (z2) {
                    this.holdPosition = this.lastPosition - 1;
                } else if (this.startPosition > iPointToPosition) {
                    this.holdPosition = this.lastPosition - 1;
                    this.isSameDragDirection = !z2;
                } else {
                    this.holdPosition = this.lastPosition;
                }
                i2 = this.mItemVerticalSpacing;
                this.lastPosition--;
            }
            Log.i("wanggang", "getFirstVisiblePosition() = " + getFirstVisiblePosition());
            Log.i("wanggang", "getLastVisiblePosition() = " + getLastVisiblePosition());
            dragListAdapter.setHeight(this.mItemVerticalSpacing);
            dragListAdapter.setIsSameDragDirection(this.isSameDragDirection);
            dragListAdapter.setLastFlag(this.lastFlag);
            ViewGroup viewGroup = (ViewGroup) getChildAt(this.holdPosition - getFirstVisiblePosition());
            if (this.isSameDragDirection) {
                toSelfAnimation = getFromSelfAnimation(0, i2);
            } else {
                toSelfAnimation = getToSelfAnimation(0, -i2);
            }
            viewGroup.startAnimation(toSelfAnimation);
        }
    }

    private void onDrop(int i, int i2) {
        DragListAdapter dragListAdapter = (DragListAdapter) getAdapter();
        dragListAdapter.setInvisiblePosition(-1);
        dragListAdapter.showDropItem(true);
        dragListAdapter.notifyDataSetChanged();
    }

    private void startDrag(Bitmap bitmap, int i) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.windowParams = layoutParams;
        layoutParams.gravity = 48;
        this.windowParams.x = 0;
        this.windowParams.y = (i - this.dragPoint) + this.dragOffset;
        this.windowParams.width = -2;
        this.windowParams.height = -2;
        this.windowParams.flags = 408;
        this.windowParams.windowAnimations = 0;
        this.windowParams.alpha = 0.8f;
        this.windowParams.format = -3;
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bitmap);
        this.windowManager.addView(imageView, this.windowParams);
        this.dragImageView = imageView;
    }

    public void onDrag(int i) {
        int i2 = i - this.dragPoint;
        if (this.dragImageView != null && i2 >= 0) {
            this.windowParams.alpha = 1.0f;
            this.windowParams.y = (i - this.dragPoint) + this.dragOffset;
            this.windowManager.updateViewLayout(this.dragImageView, this.windowParams);
        }
        doScroller(i);
    }

    public void doScroller(int i) {
        int i2 = this.upScrollBounce;
        if (i < i2) {
            this.current_Step = ((i2 - i) / 10) + 1;
        } else {
            int i3 = this.downScrollBounce;
            if (i > i3) {
                this.current_Step = (-((i - i3) + 1)) / 10;
            } else {
                this.isScroll = false;
                this.current_Step = 0;
            }
        }
        setSelectionFromTop(this.dragPosition, getChildAt(this.dragPosition - getFirstVisiblePosition()).getTop() + this.current_Step);
    }

    public void stopDrag() {
        this.isMoving = false;
        ImageView imageView = this.dragImageView;
        if (imageView != null) {
            this.windowManager.removeView(imageView);
            this.dragImageView = null;
        }
        this.isSameDragDirection = true;
        this.lastFlag = -1;
        DragListAdapter dragListAdapter = (DragListAdapter) getAdapter();
        dragListAdapter.setLastFlag(this.lastFlag);
        dragListAdapter.pastList();
    }

    public void onDrop(int i) {
        onDrop(0, i);
    }

    public Animation getFromSelfAnimation(int i, int i2) {
        TranslateAnimation translateAnimation = new TranslateAnimation(1, 0.0f, 0, i, 1, 0.0f, 0, i2);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(200L);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        return translateAnimation;
    }

    public Animation getToSelfAnimation(int i, int i2) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, i, 1, 0.0f, 0, i2, 1, 0.0f);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(200L);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        return translateAnimation;
    }

    public Animation getAbsMoveAnimation(int i, int i2) {
        TranslateAnimation translateAnimation = new TranslateAnimation(1, 0.0f, 0, i, 1, 0.0f, 0, i2);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(200L);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        return translateAnimation;
    }

    public Animation getAnimation(int i, int i2) {
        TranslateAnimation translateAnimation = new TranslateAnimation(1, 0.0f, 0, 0.0f, 0, i, 0, i2);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(200L);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        return translateAnimation;
    }

    public Animation getAbsMoveAnimation2(int i, int i2) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, i, 1, 0.0f, 0, i2, 1, 0.0f);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(200L);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        return translateAnimation;
    }
}
