package com.qh.managegroup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.qh.data.ItemInfo;
import com.qh.managegroup.DragListView;
import com.qh.onehlight.R;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class DragListAdapter extends BaseAdapter {
    private static final String TAG = "DragListAdapter";
    public ArrayList<MyListData> arrayTitles;
    private Context context;
    private int height;
    public boolean isHidden;
    public DragListView.DragListChange mDragListChange;
    public Hashtable<Integer, ItemInfo> ItemInfos = new Hashtable<>();
    private int invisilePosition = -1;
    private boolean isChanged = true;
    private boolean ShowItem = false;
    private ArrayList<MyListData> mCopyList = new ArrayList<>();
    private boolean isSameDragDirection = true;
    private int lastFlag = -1;
    private int dragPosition = -1;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public DragListAdapter(Context context, ArrayList<MyListData> arrayList) {
        this.context = context;
        this.arrayTitles = arrayList;
    }

    public void showDropItem(boolean z) {
        this.ShowItem = z;
    }

    public void setInvisiblePosition(int i) {
        this.invisilePosition = i;
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewInflate;
        MyListData myListData = this.arrayTitles.get(i);
        if (myListData.isGroup) {
            viewInflate = LayoutInflater.from(this.context).inflate(R.layout.managelist_group_item, (ViewGroup) null);
        } else {
            viewInflate = LayoutInflater.from(this.context).inflate(R.layout.managelist_item, (ViewGroup) null);
        }
        ItemInfo itemInfo = new ItemInfo();
        if (myListData.isGroup) {
            itemInfo.name = (TextView) viewInflate.findViewById(R.id.group_name);
            itemInfo.reset = (ImageView) viewInflate.findViewById(R.id.reset);
            if (i == 0) {
                itemInfo.reset.setVisibility(8);
            }
            itemInfo.light_img = (ImageView) viewInflate.findViewById(R.id.arrow_img);
            itemInfo.light_img.setTag(myListData);
        } else {
            itemInfo.name = (TextView) viewInflate.findViewById(R.id.light_name);
            itemInfo.light_img = (ImageView) viewInflate.findViewById(R.id.light_img);
            itemInfo.light_img.setTag(myListData);
            itemInfo.reset = (ImageView) viewInflate.findViewById(R.id.item_resetname);
        }
        itemInfo.tx_delete = (TextView) viewInflate.findViewById(R.id.tx_delete);
        itemInfo.tx_delete.setOnClickListener(new DelectClickListener().setData(myListData));
        itemInfo.reset.setOnClickListener(new resetClickListener().setData(myListData));
        itemInfo.name.setText(myListData.name);
        if (this.isChanged) {
            Log.i("wanggang", "position == " + i);
            Log.i("wanggang", "holdPosition == " + this.invisilePosition);
            if (i == this.invisilePosition && !this.ShowItem) {
                itemInfo.name.setVisibility(4);
                itemInfo.light_img.setVisibility(4);
            }
            int i2 = this.lastFlag;
            if (i2 != -1) {
                if (i2 == 1) {
                    if (i > this.invisilePosition) {
                        viewInflate.startAnimation(getFromSelfAnimation(0, -this.height));
                    }
                } else if (i2 == 0 && i < this.invisilePosition) {
                    viewInflate.startAnimation(getFromSelfAnimation(0, this.height));
                }
            }
        }
        this.ItemInfos.put(Integer.valueOf(i), itemInfo);
        viewInflate.setTag(myListData);
        return viewInflate;
    }

    public void exchange(int i, int i2) {
        MyListData myListData = (MyListData) getItem(i);
        System.out.println(i + "========" + i2);
        StringBuilder sb = new StringBuilder();
        sb.append("startPostion ==== ");
        sb.append(i);
        Log.d("ON", sb.toString());
        Log.d("ON", "endPosition ==== " + i2);
        if (i < i2) {
            this.arrayTitles.add(i2 + 1, myListData);
            this.arrayTitles.remove(i);
        } else {
            this.arrayTitles.add(i2, myListData);
            this.arrayTitles.remove(i + 1);
        }
        this.isChanged = true;
    }

    public void exchangeCopy(int i, int i2) {
        System.out.println(i + "--" + i2);
        MyListData myListData = (MyListData) getCopyItem(i);
        System.out.println(i + "========" + i2);
        StringBuilder sb = new StringBuilder();
        sb.append("startPostion ==== ");
        sb.append(i);
        Log.d("ON", sb.toString());
        Log.d("ON", "endPosition ==== " + i2);
        if (i < i2) {
            this.mCopyList.add(i2 + 1, myListData);
            this.mCopyList.remove(i);
        } else {
            this.mCopyList.add(i2, myListData);
            this.mCopyList.remove(i + 1);
        }
        this.isChanged = true;
    }

    public Object getCopyItem(int i) {
        return this.mCopyList.get(i);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.arrayTitles.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.arrayTitles.get(i);
    }

    public void addDragItem(int i, MyListData myListData) {
        Log.i(TAG, "start" + i);
        this.arrayTitles.get(i);
        this.arrayTitles.remove(i);
        this.arrayTitles.add(i, myListData);
    }

    public void copyList() {
        this.mCopyList.clear();
        Iterator<MyListData> it = this.arrayTitles.iterator();
        while (it.hasNext()) {
            this.mCopyList.add(it.next());
        }
    }

    public void pastList() {
        this.arrayTitles.clear();
        Iterator<MyListData> it = this.mCopyList.iterator();
        while (it.hasNext()) {
            this.arrayTitles.add(it.next());
        }
    }

    public void setIsSameDragDirection(boolean z) {
        this.isSameDragDirection = z;
    }

    public void setLastFlag(int i) {
        this.lastFlag = i;
    }

    public void setHeight(int i) {
        this.height = i;
    }

    public void setCurrentDragPosition(int i) {
        this.dragPosition = i;
    }

    public Animation getFromSelfAnimation(int i, int i2) {
        TranslateAnimation translateAnimation = new TranslateAnimation(1, 0.0f, 0, i, 1, 0.0f, 0, i2);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(100L);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        return translateAnimation;
    }

    public Animation getToSelfAnimation(int i, int i2) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, i, 1, 0.0f, 0, i2, 1, 0.0f);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(100L);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        return translateAnimation;
    }

    public void setDragListChange(DragListView.DragListChange dragListChange) {
        this.mDragListChange = dragListChange;
    }

    private class DelectClickListener implements View.OnClickListener {
        private MyListData data;

        private DelectClickListener() {
        }

        public DelectClickListener setData(MyListData myListData) {
            this.data = myListData;
            return this;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (this.data != null || DragListAdapter.this.mDragListChange == null) {
                DragListAdapter.this.mDragListChange.delect(this.data);
            }
        }
    }

    private class resetClickListener implements View.OnClickListener {
        private MyListData data;

        private resetClickListener() {
        }

        public resetClickListener setData(MyListData myListData) {
            this.data = myListData;
            return this;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (this.data != null || DragListAdapter.this.mDragListChange == null) {
                DragListAdapter.this.mDragListChange.resetname(this.data);
            }
        }
    }
}
