package com.qh.blelight;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.qh.data.MyChild;
import com.qh.tools.DBAdapter;
import com.qh.tools.DBLightTable;
import com.qh.tools.DBTable;
import com.xiaoyu.onehlight.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class MyMExpandableListAdapter extends BaseExpandableListAdapter {
    public DBAdapter dbAdapter;
    private Activity mActivity;
    private BluetoothLeService mBluetoothLeService;
    private Context mContext;
    public Handler mHandler;
    private LayoutInflater mInflator;
    public GroupViewHolder mShowGroupViewHolder;
    public View moveV;
    public int groupH = R.styleable.AppCompatTheme_windowFixedWidthMinor;
    public HashMap<Integer, HashMap> groupViews = new HashMap<>();
    private HashMap<Integer, View> groupItemViews = new HashMap<>();
    public HashMap<Integer, View> mViews = new HashMap<>();
    public ArrayList<Boolean> isExpandeds = new ArrayList<>();
    private ArrayList<String> groupNames = new ArrayList<>();
    private ArrayList<Integer> groupID = new ArrayList<>();
    public HashMap<String, MyChild> DBdata = new HashMap<>();
    private ArrayList<ArrayList> children = new ArrayList<>();

    public static class GroupViewHolder {
        public ImageView arrow_img;
        public TextView delect;
        public int groupPosition;
        public TextView group_name_tx;
        public ImageView reset_name_tx;
    }

    public static class MChildViewHolder {
        public int childPosition;
        public ImageView device_img;
        public int groupPosition;
        public ImageView item_resetname;
        public TextView light_name_tx;
        public TextView tx_delete;
    }

    @Override // android.widget.ExpandableListAdapter
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override // android.widget.ExpandableListAdapter
    public long getGroupId(int i) {
        return i;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public void setgroupNames() {
        this.groupID.clear();
        this.groupNames.clear();
        Cursor cursorQueryAllData = this.dbAdapter.queryAllData(DBTable.DB_TABLE);
        while (cursorQueryAllData.moveToNext()) {
            int i = cursorQueryAllData.getInt(cursorQueryAllData.getColumnIndex(DBTable.ID));
            String string = cursorQueryAllData.getString(cursorQueryAllData.getColumnIndex(DBTable.GROUP_NAME));
            Log.e("", "mac = " + i + " myGroup =  " + string);
            this.groupID.add(Integer.valueOf(i));
            this.groupNames.add(string);
        }
        Cursor cursorQueryAllData2 = this.dbAdapter.queryAllData(DBLightTable.DB_TABLE);
        while (cursorQueryAllData2.moveToNext()) {
            int i2 = cursorQueryAllData2.getInt(cursorQueryAllData2.getColumnIndex(DBLightTable.GROUP));
            Log.e("", "mac = " + cursorQueryAllData2.getString(cursorQueryAllData2.getColumnIndex(DBLightTable.ID)) + " myGroup =  " + i2 + " name = " + cursorQueryAllData2.getString(cursorQueryAllData2.getColumnIndex(DBLightTable.LIGTH_NAME)));
        }
        this.isExpandeds.clear();
        for (int i3 = 0; i3 < this.groupNames.size(); i3++) {
            if (this.isExpandeds.size() < this.groupNames.size()) {
                this.isExpandeds.add(false);
            }
        }
        this.children.clear();
        for (int i4 = 0; i4 < this.groupID.size(); i4++) {
            Cursor cursorQueryDataByGroup = this.dbAdapter.queryDataByGroup(this.groupID.get(i4).intValue());
            while (cursorQueryDataByGroup.moveToNext()) {
                MyChild myChild = new MyChild();
                myChild.mac = cursorQueryDataByGroup.getString(cursorQueryDataByGroup.getColumnIndex(DBLightTable.ID));
                myChild.myGroup = cursorQueryDataByGroup.getInt(cursorQueryDataByGroup.getColumnIndex(DBLightTable.GROUP));
                myChild.name = cursorQueryDataByGroup.getString(cursorQueryDataByGroup.getColumnIndex(DBLightTable.LIGTH_NAME));
                this.DBdata.put(myChild.mac, myChild);
                Log.e("", "mac = " + myChild.mac);
                Log.e("", "myGroup = " + myChild.myGroup);
                Log.e("", "name = " + myChild.name);
            }
        }
        BluetoothLeService bluetoothLeService = this.mBluetoothLeService;
        if (bluetoothLeService == null) {
            return;
        }
        for (String str : bluetoothLeService.mDevices.keySet()) {
            BluetoothDevice bluetoothDevice = this.mBluetoothLeService.mDevices.get(str);
            Log.e("", "addr :" + str);
            if (!this.DBdata.containsKey(str)) {
                str.substring(str.length() - 8, str.length());
                MyChild myChild2 = new MyChild();
                myChild2.mac = str;
                myChild2.myGroup = 1;
                myChild2.name = "" + bluetoothDevice.getName();
                this.DBdata.put(myChild2.mac, myChild2);
            }
        }
        for (int i5 = 0; i5 < this.groupID.size(); i5++) {
            int iIntValue = this.groupID.get(i5).intValue();
            ArrayList arrayList = new ArrayList();
            Iterator<String> it = this.DBdata.keySet().iterator();
            while (it.hasNext()) {
                MyChild myChild3 = this.DBdata.get(it.next());
                if (iIntValue == myChild3.myGroup) {
                    arrayList.add(myChild3);
                }
                Log.e("", "name = " + myChild3.name + " myGroup = " + myChild3.myGroup);
            }
            this.children.add(arrayList);
        }
    }

    MyMExpandableListAdapter(Context context, Activity activity, DBAdapter dBAdapter, BluetoothLeService bluetoothLeService) {
        this.mContext = context;
        this.mInflator = activity.getLayoutInflater();
        this.dbAdapter = dBAdapter;
        this.mBluetoothLeService = bluetoothLeService;
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getChild(int i, int i2) {
        return this.children.get(i).get(i2);
    }

    @Override // android.widget.ExpandableListAdapter
    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        MChildViewHolder mChildViewHolder;
        if (!this.groupViews.containsKey(Integer.valueOf(i))) {
            this.groupViews.put(Integer.valueOf(i), new HashMap());
        }
        HashMap map = this.groupViews.get(Integer.valueOf(i));
        View viewInflate = map.containsKey(Integer.valueOf(i2)) ? (View) map.get(Integer.valueOf(i2)) : null;
        if (viewInflate == null) {
            viewInflate = this.mInflator.inflate(com.qh.onehlight.R.layout.managelist_item, (ViewGroup) null);
            mChildViewHolder = new MChildViewHolder();
            mChildViewHolder.groupPosition = i;
            mChildViewHolder.childPosition = i2;
            mChildViewHolder.light_name_tx = (TextView) viewInflate.findViewById(com.qh.onehlight.R.id.light_name);
            mChildViewHolder.device_img = (ImageView) viewInflate.findViewById(com.qh.onehlight.R.id.light_img);
            mChildViewHolder.device_img.setOnTouchListener(new moveOnTouchListener().setViewHolder(viewInflate));
            mChildViewHolder.tx_delete = (TextView) viewInflate.findViewById(com.qh.onehlight.R.id.tx_delete);
            mChildViewHolder.tx_delete.setVisibility(8);
            mChildViewHolder.item_resetname = (ImageView) viewInflate.findViewById(com.qh.onehlight.R.id.item_resetname);
            viewInflate.setTag(mChildViewHolder);
            map.put(Integer.valueOf(i2), viewInflate);
            this.groupViews.put(Integer.valueOf(i), map);
        } else {
            mChildViewHolder = (MChildViewHolder) viewInflate.getTag();
        }
        ArrayList arrayList = this.children.get(i);
        String str = arrayList.size() > i2 ? ((MyChild) arrayList.get(i2)).name : "";
        viewInflate.setTag(com.qh.onehlight.R.id.ic_hook1, false);
        viewInflate.setTag(com.qh.onehlight.R.id.ic_hook2, Integer.valueOf(i));
        viewInflate.setTag(com.qh.onehlight.R.id.ic_hook3, Integer.valueOf(i2));
        viewInflate.setTag(com.qh.onehlight.R.id.ic_hook4, mChildViewHolder);
        mChildViewHolder.light_name_tx.setText("" + str);
        mChildViewHolder.tx_delete.setOnClickListener(new delectOnClickListener().setPos(i, i2));
        mChildViewHolder.item_resetname.setOnClickListener(new OnResetNameChildClickListener().setChildViewHolder(mChildViewHolder).setPos(i, i2));
        return viewInflate;
    }

    @Override // android.widget.ExpandableListAdapter
    public int getChildrenCount(int i) {
        ArrayList arrayList;
        if (this.children.size() <= i || (arrayList = this.children.get(i)) == null) {
            return 0;
        }
        return arrayList.size();
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getGroup(int i) {
        return this.groupNames.get(i);
    }

    @Override // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return this.groupNames.size();
    }

    @Override // android.widget.ExpandableListAdapter
    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        GroupViewHolder groupViewHolder;
        View view2 = this.groupItemViews.get(Integer.valueOf(i));
        View viewInflate = this.groupItemViews.containsKey(Integer.valueOf(i)) ? this.groupItemViews.get(Integer.valueOf(i)) : null;
        if (viewInflate == null) {
            viewInflate = this.mInflator.inflate(com.qh.onehlight.R.layout.managelist_group_item, (ViewGroup) null);
            viewInflate.setOnTouchListener(new OnGroupTouchListener().setGroupPosition(i));
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.groupPosition = i;
            groupViewHolder.groupPosition = i;
            groupViewHolder.group_name_tx = (TextView) viewInflate.findViewById(com.qh.onehlight.R.id.group_name);
            groupViewHolder.arrow_img = (ImageView) viewInflate.findViewById(com.qh.onehlight.R.id.arrow_img);
            groupViewHolder.reset_name_tx = (ImageView) viewInflate.findViewById(com.qh.onehlight.R.id.reset);
            groupViewHolder.reset_name_tx.setOnClickListener(new OnResetNameClickListener().setGroupPosition(i).setGroupViewHolder(groupViewHolder));
            groupViewHolder.delect = (TextView) viewInflate.findViewById(com.qh.onehlight.R.id.tx_delete);
            viewInflate.setTag(groupViewHolder);
            this.groupItemViews.put(Integer.valueOf(i), view2);
        } else {
            groupViewHolder = (GroupViewHolder) viewInflate.getTag();
        }
        if (i == 0) {
            groupViewHolder.reset_name_tx.setVisibility(4);
        }
        if (z) {
            groupViewHolder.arrow_img.setImageResource(com.qh.onehlight.R.drawable.arrow_open);
        } else {
            groupViewHolder.arrow_img.setImageResource(com.qh.onehlight.R.drawable.arrow_close);
        }
        if (this.isExpandeds.size() > i) {
            this.isExpandeds.set(i, Boolean.valueOf(z));
        }
        viewInflate.setTag(com.qh.onehlight.R.id.ic_hook1, true);
        viewInflate.setTag(com.qh.onehlight.R.id.ic_hook2, Integer.valueOf(i));
        viewInflate.setTag(com.qh.onehlight.R.id.ic_hook3, Integer.valueOf(i));
        viewInflate.setTag(com.qh.onehlight.R.id.ic_hook4, groupViewHolder);
        this.mViews.put(Integer.valueOf(i), viewInflate);
        String str = this.groupNames.size() > i ? this.groupNames.get(i) : "error!";
        groupViewHolder.group_name_tx.setText("" + str);
        groupViewHolder.delect.setOnClickListener(new delectGroupOnClickListener().setPos(i));
        return viewInflate;
    }

    private class delectGroupOnClickListener implements View.OnClickListener {
        private int groupPosition;

        private delectGroupOnClickListener() {
        }

        public delectGroupOnClickListener setPos(int i) {
            this.groupPosition = i;
            return this;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (MyMExpandableListAdapter.this.groupID.size() > this.groupPosition) {
                Log.e("", "id= " + MyMExpandableListAdapter.this.groupID.get(this.groupPosition));
                MyMExpandableListAdapter.this.dbAdapter.deleteOneData(DBTable.DB_TABLE, "_id=?", new String[]{"" + MyMExpandableListAdapter.this.groupID.get(this.groupPosition)});
                Log.e("", "uuuuu = " + MyMExpandableListAdapter.this.dbAdapter.deleteOneData(DBLightTable.DB_TABLE, "lightgroup=?", new String[]{"" + MyMExpandableListAdapter.this.groupID.get(this.groupPosition)}));
                MyMExpandableListAdapter.this.mHandler.sendEmptyMessage(1);
            }
        }
    }

    private class delectOnClickListener implements View.OnClickListener {
        private int childPosition;
        private int groupPosition;

        private delectOnClickListener() {
        }

        public delectOnClickListener setPos(int i, int i2) {
            this.groupPosition = i;
            this.childPosition = i2;
            return this;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ArrayList arrayList;
            MyChild myChild;
            if (MyMExpandableListAdapter.this.children.size() <= this.groupPosition || (arrayList = (ArrayList) MyMExpandableListAdapter.this.children.get(this.groupPosition)) == null) {
                return;
            }
            int size = arrayList.size();
            int i = this.childPosition;
            if (size <= i || (myChild = (MyChild) arrayList.get(i)) == null) {
                return;
            }
            Log.e("", "mMyChild.mac = " + myChild.mac + " mMyChild.myGroup = " + myChild.myGroup);
            MyMExpandableListAdapter.this.dbAdapter.deleteOneData(DBLightTable.DB_TABLE, "Mac=?", new String[]{myChild.mac});
            MyMExpandableListAdapter.this.mHandler.sendEmptyMessage(2);
        }
    }

    private class moveOnTouchListener implements View.OnTouchListener {
        private View convertView;

        private moveOnTouchListener() {
        }

        public moveOnTouchListener setViewHolder(View view) {
            this.convertView = view;
            return this;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            MyMExpandableListAdapter.this.moveV = this.convertView;
            return false;
        }
    }

    private class OnGroupTouchListener implements View.OnTouchListener {
        private int groupPosition;

        private OnGroupTouchListener() {
        }

        public OnGroupTouchListener setGroupPosition(int i) {
            this.groupPosition = i;
            return this;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.e("", "Group-onTouch " + this.groupPosition);
            Log.e("", "OnTouchListener - " + view.getHeight());
            return false;
        }
    }

    private class OnChildTouchListener implements View.OnTouchListener {
        private View convertView;
        public MChildViewHolder mChildViewHolder;
        private float x;
        private float y;

        private OnChildTouchListener() {
        }

        public OnChildTouchListener setOnChildTouchListener(View view) {
            this.convertView = view;
            return this;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            if (action == 0) {
                this.x = motionEvent.getX();
            } else if (action == 2) {
                this.mChildViewHolder = (MChildViewHolder) view.getTag();
                if (this.x - motionEvent.getX() > 30.0f) {
                    Log.e("", "<---1---L");
                    this.mChildViewHolder.tx_delete.setVisibility(8);
                }
                if (motionEvent.getX() - this.x > 30.0f) {
                    Log.e("", "L----1--->");
                    this.mChildViewHolder.tx_delete.setVisibility(0);
                }
            }
            return false;
        }
    }

    private class OnResetNameChildClickListener implements View.OnClickListener {
        private int childPosition;
        private int groupPosition;
        private MChildViewHolder mMChildViewHolder;

        private OnResetNameChildClickListener() {
        }

        public OnResetNameChildClickListener setPos(int i, int i2) {
            this.groupPosition = i;
            this.childPosition = i2;
            return this;
        }

        public OnResetNameChildClickListener setChildViewHolder(MChildViewHolder mChildViewHolder) {
            this.mMChildViewHolder = mChildViewHolder;
            return this;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            final EditText editText = new EditText(MyMExpandableListAdapter.this.mContext);
            new AlertDialog.Builder(MyMExpandableListAdapter.this.mContext).setTitle("" + MyMExpandableListAdapter.this.mContext.getResources().getString(com.qh.onehlight.R.string.Rename)).setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("" + MyMExpandableListAdapter.this.mContext.getResources().getString(com.qh.onehlight.R.string.ok), new DialogInterface.OnClickListener() { // from class: com.qh.blelight.MyMExpandableListAdapter.OnResetNameChildClickListener.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ArrayList arrayList;
                    MyChild myChild;
                    String string = editText.getText().toString();
                    if ("".equals(string) || MyMExpandableListAdapter.this.children.size() <= OnResetNameChildClickListener.this.groupPosition || (arrayList = (ArrayList) MyMExpandableListAdapter.this.children.get(OnResetNameChildClickListener.this.groupPosition)) == null || arrayList.size() <= OnResetNameChildClickListener.this.childPosition || (myChild = (MyChild) arrayList.get(OnResetNameChildClickListener.this.childPosition)) == null) {
                        return;
                    }
                    Log.e("", "mMyChild.mac = " + myChild.mac + " mMyChild.myGroup = " + myChild.myGroup);
                    Log.e("", "groupPosition = " + OnResetNameChildClickListener.this.groupPosition + " childPosition = " + OnResetNameChildClickListener.this.childPosition);
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    sb.append(myChild.mac);
                    String[] strArr = {sb.toString()};
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBLightTable.LIGTH_NAME, string);
                    int iUpDataforTable = MyMExpandableListAdapter.this.dbAdapter.upDataforTable(DBLightTable.DB_TABLE, contentValues, "Mac=?", strArr);
                    if (iUpDataforTable == 0) {
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put(DBLightTable.GROUP, (Integer) 1);
                        contentValues2.put(DBLightTable.ID, myChild.mac);
                        contentValues2.put(DBLightTable.LIGTH_NAME, string);
                        MyMExpandableListAdapter.this.dbAdapter.insert(DBLightTable.DB_TABLE, contentValues2);
                    }
                    Log.e("", "k = " + iUpDataforTable);
                    OnResetNameChildClickListener.this.mMChildViewHolder.light_name_tx.setText("" + string);
                    MyMExpandableListAdapter.this.mHandler.sendEmptyMessage(1);
                    Log.e("", "-- " + ((Object) editText.getText()));
                }
            }).setNegativeButton("" + MyMExpandableListAdapter.this.mContext.getResources().getString(com.qh.onehlight.R.string.cencel), (DialogInterface.OnClickListener) null).show();
        }
    }

    private class OnResetNameClickListener implements View.OnClickListener {
        private int groupPosition;
        private GroupViewHolder mGroupViewHolder;

        private OnResetNameClickListener() {
        }

        public OnResetNameClickListener setGroupPosition(int i) {
            this.groupPosition = i;
            return this;
        }

        public OnResetNameClickListener setGroupViewHolder(GroupViewHolder groupViewHolder) {
            this.mGroupViewHolder = groupViewHolder;
            return this;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            MyMExpandableListAdapter.this.mShowGroupViewHolder = this.mGroupViewHolder;
            final EditText editText = new EditText(MyMExpandableListAdapter.this.mContext);
            new AlertDialog.Builder(MyMExpandableListAdapter.this.mContext).setTitle("" + MyMExpandableListAdapter.this.mContext.getResources().getString(com.qh.onehlight.R.string.Rename)).setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("" + MyMExpandableListAdapter.this.mContext.getResources().getString(com.qh.onehlight.R.string.ok), new DialogInterface.OnClickListener() { // from class: com.qh.blelight.MyMExpandableListAdapter.OnResetNameClickListener.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    String string = editText.getText().toString();
                    if ("".equals(string)) {
                        return;
                    }
                    Log.e("", "-- " + ((Object) editText.getText()));
                    Log.e("", "-- " + MyMExpandableListAdapter.this.groupID.get(OnResetNameClickListener.this.groupPosition));
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("groupname", string);
                    message.setData(bundle);
                    MyMExpandableListAdapter.this.mHandler.sendMessage(message);
                    String[] strArr = {"" + MyMExpandableListAdapter.this.groupID.get(OnResetNameClickListener.this.groupPosition)};
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBTable.GROUP_NAME, string);
                    MyMExpandableListAdapter.this.dbAdapter.upDataforTable(DBTable.DB_TABLE, contentValues, "_id=?", strArr);
                }
            }).setNegativeButton("" + MyMExpandableListAdapter.this.mContext.getResources().getString(com.qh.onehlight.R.string.cencel), (DialogInterface.OnClickListener) null).show();
        }
    }

    public void move(int i, int i2, int i3) {
        if (i2 != -1 && this.children.size() > i && this.children.size() > i2) {
            ArrayList arrayList = this.children.get(i);
            ArrayList arrayList2 = this.children.get(i2);
            if (arrayList.size() > i3) {
                MyChild myChild = (MyChild) arrayList.get(i3);
                arrayList.remove(i3);
                arrayList2.add(myChild);
                this.children.set(i, arrayList);
                this.children.set(i2, arrayList2);
                Log.e("", "name--->" + myChild.mac);
                if (i == 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBLightTable.GROUP, this.groupID.get(i2));
                    contentValues.put(DBLightTable.ID, myChild.mac);
                    contentValues.put(DBLightTable.LIGTH_NAME, myChild.name);
                    long jInsert = this.dbAdapter.insert(DBLightTable.DB_TABLE, contentValues);
                    Log.e("", "kkkkk = " + jInsert);
                    if (jInsert == -1) {
                        String[] strArr = {myChild.mac};
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put(DBLightTable.GROUP, this.groupID.get(i2));
                        this.dbAdapter.upDataforTable(DBLightTable.DB_TABLE, contentValues2, "Mac=?", strArr);
                    }
                } else {
                    String[] strArr2 = {myChild.mac};
                    ContentValues contentValues3 = new ContentValues();
                    contentValues3.put(DBLightTable.GROUP, this.groupID.get(i2));
                    this.dbAdapter.upDataforTable(DBLightTable.DB_TABLE, contentValues3, "Mac=?", strArr2);
                }
                if (i2 == 0) {
                    String[] strArr3 = {myChild.mac};
                    ContentValues contentValues4 = new ContentValues();
                    contentValues4.put(DBLightTable.GROUP, (Integer) 1);
                    this.dbAdapter.upDataforTable(DBLightTable.DB_TABLE, contentValues4, "Mac=?", strArr3);
                }
                notifyDataSetChanged();
            }
        }
    }
}
