package com.qh.blelight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.qh.blelight.MyMExpandableListAdapter;
import com.qh.onehlight.R;
import com.qh.tools.DBAdapter;
import com.qh.tools.DBTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class GroupManagement extends Activity {
    public DBAdapter dbAdapter;
    private ImageView dragImageView;
    private int dragOffset;
    private int dragPoint;
    private int dragPosition;
    private int dragSrcPosition;
    private RelativeLayout lin_back;
    public MyApplication mMyApplication;
    public MyMExpandableListAdapter mMyExpandableListAdapter;
    private Resources mResources;
    public Context mcontext;
    public ExpandableListView myExpandableListView;
    private RelativeLayout rel_add;
    public RelativeLayout rel_main;
    private WindowManager windowManager;
    private WindowManager.LayoutParams windowParams;
    Handler mHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.GroupManagement.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what == 2) {
                GroupManagement.this.finish();
                return false;
            }
            GroupManagement.this.mMyExpandableListAdapter.setgroupNames();
            GroupManagement.this.myExpandableListView.setAdapter(GroupManagement.this.mMyExpandableListAdapter);
            if (GroupManagement.this.isExpandedID != -1) {
                GroupManagement.this.myExpandableListView.expandGroup(GroupManagement.this.isExpandedID);
            }
            GroupManagement.this.mMyExpandableListAdapter.notifyDataSetChanged();
            return false;
        }
    });
    public int isExpandedID = -1;
    private int len = 5;
    private ArrayList<String> groupNames = new ArrayList<>();
    private ArrayList<Integer> groupIDs = new ArrayList<>();

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_management);
        this.mResources = getResources();
        this.lin_back = (RelativeLayout) findViewById(R.id.lin_back);
        this.rel_add = (RelativeLayout) findViewById(R.id.rel_add);
        this.rel_main = (RelativeLayout) findViewById(R.id.rel_main);
        if (this.mMyApplication.bgsrc.length > this.mMyApplication.typebg) {
            this.rel_main.setBackgroundResource(this.mMyApplication.bgsrc[this.mMyApplication.typebg]);
        }
        this.lin_back.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.GroupManagement.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", 1);
                GroupManagement.this.setResult(-1, intent);
                GroupManagement.this.finish();
            }
        });
        this.rel_add.setOnClickListener(new AnonymousClass3());
        DBAdapter dBAdapterInit = DBAdapter.init(this);
        this.dbAdapter = dBAdapterInit;
        dBAdapterInit.open();
        this.mcontext = this;
        this.windowManager = (WindowManager) getSystemService("window");
        this.mMyApplication = (MyApplication) getApplication();
        this.myExpandableListView = (ExpandableListView) findViewById(R.id.management_list);
        MyMExpandableListAdapter myMExpandableListAdapter = new MyMExpandableListAdapter(this.mcontext, this, this.dbAdapter, this.mMyApplication.mBluetoothLeService);
        this.mMyExpandableListAdapter = myMExpandableListAdapter;
        myMExpandableListAdapter.mHandler = this.mHandler;
        setOnTouch();
        this.myExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // from class: com.qh.blelight.GroupManagement.4
            @Override // android.widget.ExpandableListView.OnGroupExpandListener
            public void onGroupExpand(int i) {
                int groupCount = GroupManagement.this.myExpandableListView.getExpandableListAdapter().getGroupCount();
                for (int i2 = 0; i2 < groupCount; i2++) {
                    if (i != i2) {
                        GroupManagement.this.myExpandableListView.collapseGroup(i2);
                    }
                }
                GroupManagement.this.isExpandedID = i;
            }
        });
        this.mMyExpandableListAdapter.setgroupNames();
        this.myExpandableListView.setAdapter(this.mMyExpandableListAdapter);
        this.myExpandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.qh.blelight.GroupManagement.5
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                MyMExpandableListAdapter.GroupViewHolder groupViewHolder;
                boolean zBooleanValue = ((Boolean) view.getTag(R.id.ic_hook1)).booleanValue();
                int iIntValue = ((Integer) view.getTag(R.id.ic_hook2)).intValue();
                int iIntValue2 = ((Integer) view.getTag(R.id.ic_hook3)).intValue();
                if (!zBooleanValue) {
                    Log.e("", "num = " + iIntValue + " child = " + iIntValue2);
                    MyMExpandableListAdapter.MChildViewHolder mChildViewHolder = (MyMExpandableListAdapter.MChildViewHolder) view.getTag(R.id.ic_hook4);
                    if (mChildViewHolder != null) {
                        mChildViewHolder.tx_delete.setVisibility(0);
                    }
                } else if (iIntValue != 0 && (groupViewHolder = (MyMExpandableListAdapter.GroupViewHolder) view.getTag(R.id.ic_hook4)) != null) {
                    groupViewHolder.delect.setVisibility(0);
                }
                return true;
            }
        });
        this.myExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: com.qh.blelight.GroupManagement.6
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
                Log.e("", "--? " + i + " " + i2);
                MyMExpandableListAdapter.MChildViewHolder mChildViewHolder = (MyMExpandableListAdapter.MChildViewHolder) view.getTag(R.id.ic_hook4);
                if (mChildViewHolder == null) {
                    return false;
                }
                mChildViewHolder.tx_delete.setVisibility(8);
                return false;
            }
        });
    }

    /* JADX INFO: renamed from: com.qh.blelight.GroupManagement$3, reason: invalid class name */
    class AnonymousClass3 implements View.OnClickListener {
        AnonymousClass3() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            final EditText editText = new EditText(GroupManagement.this.mcontext);
            new AlertDialog.Builder(GroupManagement.this.mcontext).setTitle("" + GroupManagement.this.mcontext.getResources().getString(R.string.Name1)).setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("" + GroupManagement.this.mcontext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() { // from class: com.qh.blelight.GroupManagement.3.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    String string = editText.getText().toString();
                    if ("".equals(string)) {
                        return;
                    }
                    Log.e("", "name = " + string);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBTable.GROUP_NAME, "" + string);
                    GroupManagement.this.dbAdapter.insert(DBTable.DB_TABLE, contentValues);
                    GroupManagement.this.setListData();
                    GroupManagement.this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.GroupManagement.3.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Log.e("", "groupNames " + GroupManagement.this.groupNames.size() + " groupIDs " + GroupManagement.this.groupIDs.size());
                            GroupManagement.this.mMyExpandableListAdapter.setgroupNames();
                            GroupManagement.this.myExpandableListView.setAdapter(GroupManagement.this.mMyExpandableListAdapter);
                        }
                    }, 50L);
                }
            }).setNegativeButton("" + GroupManagement.this.mcontext.getResources().getString(R.string.cencel), (DialogInterface.OnClickListener) null).show();
        }
    }

    public void setListData() {
        this.groupNames.clear();
        this.groupIDs.clear();
        Cursor cursorQueryAllData = this.dbAdapter.queryAllData();
        while (cursorQueryAllData.moveToNext()) {
            String string = cursorQueryAllData.getString(cursorQueryAllData.getColumnIndex(DBTable.GROUP_NAME));
            int i = cursorQueryAllData.getInt(cursorQueryAllData.getColumnIndex(DBTable.ID));
            this.groupNames.add(string);
            this.groupIDs.add(Integer.valueOf(i));
            Log.e("", "-id = " + i);
        }
    }

    public Animation getFromSelfAnimation(int i, int i2) {
        TranslateAnimation translateAnimation = new TranslateAnimation(1, 0.0f, 0, i, 1, 0.0f, 0, i2);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(20L);
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

    /* JADX INFO: Access modifiers changed from: private */
    public void startDrag(Bitmap bitmap, int i) {
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
        ImageView imageView = new ImageView(this.mcontext);
        imageView.setImageBitmap(bitmap);
        this.windowManager.addView(imageView, this.windowParams);
        this.dragImageView = imageView;
    }

    public void onDrag(int i) {
        int i2 = i - this.dragPoint;
        if (this.dragImageView == null || i2 < 0) {
            return;
        }
        this.windowParams.alpha = 1.0f;
        this.windowParams.y = (i - this.dragPoint) + this.dragOffset;
        this.windowManager.updateViewLayout(this.dragImageView, this.windowParams);
    }

    public void stopDrag() {
        ImageView imageView = this.dragImageView;
        if (imageView != null) {
            this.windowManager.removeView(imageView);
            this.dragImageView = null;
        }
    }

    public int getGroupPos(int i) {
        if (this.mMyExpandableListAdapter.moveV == null) {
            return -1;
        }
        Iterator<Integer> it = this.mMyExpandableListAdapter.mViews.keySet().iterator();
        ArrayList arrayList = new ArrayList();
        while (it.hasNext()) {
            arrayList.add(it.next());
        }
        Collections.sort(arrayList);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            int iIntValue = ((Integer) arrayList.get(i2)).intValue();
            int size = iIntValue == this.isExpandedID ? this.mMyExpandableListAdapter.groupViews.get(Integer.valueOf(iIntValue)).size() * com.xiaoyu.onehlight.R.styleable.AppCompatTheme_windowFixedWidthMinor : 0;
            View view = this.mMyExpandableListAdapter.mViews.get(Integer.valueOf(iIntValue));
            if (view != null) {
                int[] iArr = {0, 0};
                view.getLocationOnScreen(iArr);
                int i3 = iArr[1];
                if (i < 340) {
                    return 0;
                }
                if (i3 + size > i - 150) {
                    return iIntValue;
                }
            } else {
                Log.e("", "mView==null");
            }
        }
        return -1;
    }

    private void setOnTouch() {
        this.myExpandableListView.setOnTouchListener(new View.OnTouchListener() { // from class: com.qh.blelight.GroupManagement.7
            private float startX = 0.0f;
            private float startY = 0.0f;

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int groupPos;
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.startX = motionEvent.getX();
                    this.startY = motionEvent.getY();
                    if (GroupManagement.this.mMyExpandableListAdapter.moveV != null) {
                        GroupManagement.this.dragPoint = ((int) motionEvent.getY()) - GroupManagement.this.mMyExpandableListAdapter.moveV.getTop();
                        GroupManagement.this.dragOffset = (int) (motionEvent.getRawY() - ((int) motionEvent.getY()));
                    }
                    GroupManagement.this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.GroupManagement.7.1
                        @Override // java.lang.Runnable
                        public void run() {
                            HashMap<Integer, View> map = GroupManagement.this.mMyExpandableListAdapter.mViews;
                            Iterator<Integer> it = map.keySet().iterator();
                            while (it.hasNext()) {
                                View view2 = map.get(it.next());
                                if (view2 != null) {
                                    view2.getLocationOnScreen(new int[]{0, 0});
                                }
                            }
                        }
                    }, 1000L);
                    GroupManagement.this.getFromSelfAnimation(0, 20);
                    GroupManagement.this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.GroupManagement.7.2
                        @Override // java.lang.Runnable
                        public void run() {
                            if (GroupManagement.this.mMyExpandableListAdapter.moveV != null) {
                                GroupManagement.this.mMyExpandableListAdapter.moveV.buildDrawingCache();
                                GroupManagement.this.startDrag(Bitmap.createBitmap(GroupManagement.this.mMyExpandableListAdapter.moveV.getDrawingCache()), (int) AnonymousClass7.this.startY);
                            }
                        }
                    }, 400L);
                } else if (action == 1) {
                    GroupManagement.this.stopDrag();
                    motionEvent.getY();
                    if (GroupManagement.this.mMyExpandableListAdapter.moveV != null) {
                        MyMExpandableListAdapter.MChildViewHolder mChildViewHolder = (MyMExpandableListAdapter.MChildViewHolder) GroupManagement.this.mMyExpandableListAdapter.moveV.getTag();
                        if (mChildViewHolder != null && (groupPos = GroupManagement.this.getGroupPos((int) motionEvent.getRawY())) != GroupManagement.this.isExpandedID) {
                            GroupManagement.this.mMyExpandableListAdapter.move(mChildViewHolder.groupPosition, groupPos, mChildViewHolder.childPosition);
                            GroupManagement.this.mMyExpandableListAdapter.notifyDataSetChanged();
                        }
                        GroupManagement.this.mMyExpandableListAdapter.moveV = null;
                    }
                } else if (action == 2) {
                    motionEvent.getX();
                    GroupManagement.this.onDrag((int) motionEvent.getY());
                    if (view.getHeight() - motionEvent.getY() < 50.0f) {
                        GroupManagement.this.myExpandableListView.scrollListBy(50);
                    }
                    if (motionEvent.getY() < 50.0f) {
                        GroupManagement.this.myExpandableListView.scrollListBy(-50);
                    }
                }
                return false;
            }
        });
    }
}
