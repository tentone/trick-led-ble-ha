package com.qh.blelight;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.qh.data.ConnectionInterface;
import com.qh.data.MyChild;
import com.qh.data.SwitchInterface;
import com.qh.tools.DBAdapter;
import com.qh.tools.DBLightTable;
import com.qh.tools.DBTable;
import com.xiaoyu.onehlight.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private DBAdapter dbAdapter;
    private Activity mActivity;
    private BluetoothLeService mBluetoothLeService;
    public ConnectionInterface mConnectionInterface;
    private Context mContext;
    private Handler mHandler;
    private LayoutInflater mInflator;
    public SwitchInterface mSwitchInterface;
    public View moveV;
    public int groupH = R.styleable.AppCompatTheme_windowFixedWidthMinor;
    public int childH = 105;
    public HashMap<Integer, Boolean> operatingChildIDs = new HashMap<>();
    public HashMap<Integer, Boolean> operatingGroupIDs = new HashMap<>();
    public HashMap<Integer, Boolean> isOpenGroupIDs = new HashMap<>();
    public HashMap<String, View> linkViews = new HashMap<>();
    public HashMap<String, ChildViewHolder> itemViewByMac = new HashMap<>();
    public Hashtable<Integer, GroupViewHolder> groupItem = new Hashtable<>();
    private HashMap<Integer, HashMap> groupViews = new HashMap<>();
    private HashMap<Integer, View> groupItemViews = new HashMap<>();
    public ArrayList<Boolean> isExpandeds = new ArrayList<>();
    private ArrayList<String> groupNames = new ArrayList<>();
    private ArrayList<Integer> groupID = new ArrayList<>();
    public ArrayList<ArrayList> children = new ArrayList<>();
    public HashMap<String, MyChild> DBdata = new HashMap<>();

    public static class GroupViewHolder {
        public ImageView arrow_img;
        public int groupPosition;
        public TextView group_name_tx;
        public ImageView img_open;
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

    public void setSwitchInterface(SwitchInterface switchInterface) {
        this.mSwitchInterface = switchInterface;
    }

    public void setConnectionInterface(ConnectionInterface connectionInterface) {
        this.mConnectionInterface = connectionInterface;
    }

    public void setgroupNames(ArrayList<String> arrayList, ArrayList<Integer> arrayList2) {
        if (arrayList == null) {
            return;
        }
        this.groupID.clear();
        this.groupNames.clear();
        this.groupNames.add(this.mContext.getResources().getString(com.qh.onehlight.R.string.My_device));
        this.groupID.add(0);
        Cursor cursorQueryAllData = this.dbAdapter.queryAllData(DBTable.DB_TABLE);
        while (cursorQueryAllData.moveToNext()) {
            int i = cursorQueryAllData.getInt(cursorQueryAllData.getColumnIndex(DBTable.ID));
            String string = cursorQueryAllData.getString(cursorQueryAllData.getColumnIndex(DBTable.GROUP_NAME));
            Log.e("", "mac = " + i + " myGroup =  " + string);
            this.groupID.add(Integer.valueOf(i));
            this.groupNames.add(string);
        }
        this.isExpandeds.clear();
        for (int i2 = 0; i2 < this.groupNames.size(); i2++) {
            if (this.isExpandeds.size() < this.groupNames.size()) {
                this.isExpandeds.add(false);
            }
        }
        this.operatingChildIDs.clear();
        this.operatingGroupIDs.clear();
        this.groupViews.clear();
        this.DBdata.clear();
        this.children.clear();
        for (int i3 = 0; i3 < this.groupID.size(); i3++) {
            Cursor cursorQueryDataByGroup = this.dbAdapter.queryDataByGroup(this.groupID.get(i3).intValue());
            while (cursorQueryDataByGroup.moveToNext()) {
                MyChild myChild = new MyChild();
                myChild.mac = cursorQueryDataByGroup.getString(cursorQueryDataByGroup.getColumnIndex(DBLightTable.ID));
                myChild.myGroup = cursorQueryDataByGroup.getInt(cursorQueryDataByGroup.getColumnIndex(DBLightTable.GROUP));
                myChild.name = cursorQueryDataByGroup.getString(cursorQueryDataByGroup.getColumnIndex(DBLightTable.LIGTH_NAME));
                this.DBdata.put(myChild.mac, myChild);
                Log.e("", "mMyChild.mac - " + myChild.mac);
            }
        }
        for (String str : this.mBluetoothLeService.mDevices.keySet()) {
            BluetoothDevice bluetoothDevice = this.mBluetoothLeService.mDevices.get(str);
            Log.e("", "addr :" + str);
            if (!this.DBdata.containsKey(str)) {
                str.substring(str.length() - 8, str.length());
                MyChild myChild2 = new MyChild();
                myChild2.mac = str;
                myChild2.myGroup = 0;
                myChild2.name = "" + bluetoothDevice.getName();
                this.DBdata.put(myChild2.mac, myChild2);
            }
        }
        for (int i4 = 0; i4 < this.groupID.size(); i4++) {
            int iIntValue = this.groupID.get(i4).intValue();
            ArrayList arrayList3 = new ArrayList();
            for (String str2 : this.DBdata.keySet()) {
                MyChild myChild3 = this.DBdata.get(str2);
                if (iIntValue == myChild3.myGroup && this.mBluetoothLeService.mDevices.containsKey(str2)) {
                    arrayList3.add(myChild3);
                }
            }
            this.children.add(arrayList3);
        }
    }

    MyExpandableListAdapter(Context context, Activity activity, DBAdapter dBAdapter, Handler handler, BluetoothLeService bluetoothLeService) {
        this.mContext = context;
        this.mInflator = activity.getLayoutInflater();
        this.dbAdapter = dBAdapter;
        this.mHandler = handler;
        this.mBluetoothLeService = bluetoothLeService;
    }

    @Override // android.widget.ExpandableListAdapter
    public Object getChild(int i, int i2) {
        return this.children.get(i).get(i2);
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x0153  */
    @Override // android.widget.ExpandableListAdapter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.view.View getChildView(int r7, int r8, boolean r9, android.view.View r10, android.view.ViewGroup r11) {
        /*
            Method dump skipped, instruction units count: 506
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qh.blelight.MyExpandableListAdapter.getChildView(int, int, boolean, android.view.View, android.view.ViewGroup):android.view.View");
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
        boolean z2;
        MyBluetoothGatt myBluetoothGatt;
        View view2 = this.groupItemViews.get(Integer.valueOf(i));
        View viewInflate = this.groupItemViews.containsKey(Integer.valueOf(i)) ? this.groupItemViews.get(Integer.valueOf(i)) : null;
        if (viewInflate == null) {
            viewInflate = this.mInflator.inflate(com.qh.onehlight.R.layout.list_group_item, (ViewGroup) null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.groupPosition = i;
            groupViewHolder.group_name_tx = (TextView) viewInflate.findViewById(com.qh.onehlight.R.id.group_name);
            groupViewHolder.arrow_img = (ImageView) viewInflate.findViewById(com.qh.onehlight.R.id.arrow_img);
            groupViewHolder.img_open = (ImageView) viewInflate.findViewById(com.qh.onehlight.R.id.img_open);
            viewInflate.setOnTouchListener(new OnGroupTouchListener().setGroupPosition(i).setConvertView(viewInflate));
            groupViewHolder.arrow_img.setOnClickListener(new arrowOnClickListener().setPos(i, z));
            groupViewHolder.img_open.setOnClickListener(new myGroupOpenListener().setGroupViewHolder(groupViewHolder));
            viewInflate.setTag(groupViewHolder);
            this.groupItemViews.put(Integer.valueOf(i), view2);
        } else {
            groupViewHolder = (GroupViewHolder) viewInflate.getTag();
        }
        if (z) {
            groupViewHolder.arrow_img.setImageResource(com.qh.onehlight.R.drawable.arrow_open);
        } else {
            groupViewHolder.arrow_img.setImageResource(com.qh.onehlight.R.drawable.arrow_close);
        }
        this.isExpandeds.set(i, Boolean.valueOf(z));
        ArrayList arrayList = this.children.get(i);
        if (arrayList != null) {
            z2 = true;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                MyChild myChild = (MyChild) arrayList.get(i2);
                if (myChild != null && this.mBluetoothLeService.MyBluetoothGatts.containsKey(myChild.mac) && (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(myChild.mac)) != null && myBluetoothGatt.datas != null && myBluetoothGatt.datas.length > 10 && myBluetoothGatt.datas[2] == 35) {
                    z2 = false;
                }
            }
        } else {
            z2 = true;
        }
        if (z2) {
            groupViewHolder.img_open.setImageResource(com.qh.onehlight.R.drawable.ic_light_u);
        } else {
            groupViewHolder.img_open.setImageResource(com.qh.onehlight.R.drawable.ic_light_n);
        }
        this.isOpenGroupIDs.put(Integer.valueOf(i), Boolean.valueOf(true ^ z2));
        String str = this.groupNames.size() > i ? this.groupNames.get(i) : "error!";
        if (this.operatingGroupIDs.containsKey(Integer.valueOf(i))) {
            if (this.operatingGroupIDs.get(Integer.valueOf(i)).booleanValue()) {
                viewInflate.setBackgroundColor(1090519039);
            } else {
                viewInflate.setBackgroundColor(0);
            }
        }
        if (this.isOpenGroupIDs.containsKey(Integer.valueOf(groupViewHolder.groupPosition))) {
            if (this.isOpenGroupIDs.get(Integer.valueOf(groupViewHolder.groupPosition)).booleanValue()) {
                groupViewHolder.img_open.setImageResource(com.qh.onehlight.R.drawable.ic_light_n);
            } else {
                groupViewHolder.img_open.setImageResource(com.qh.onehlight.R.drawable.ic_light_u);
            }
        }
        this.groupItem.put(Integer.valueOf(i), groupViewHolder);
        groupViewHolder.group_name_tx.setText("" + str);
        return viewInflate;
    }

    static class ChildViewHolder {
        public int childPosition;
        public ImageView device_img;
        public int groupPosition;
        public TextView light_name_tx;
        public MyChild mMyChild;
        public ImageView open_img;
        public ProgressBar pb_conn;

        ChildViewHolder() {
        }
    }

    private class myGroupOpenListener implements View.OnClickListener {
        private GroupViewHolder mGroupViewHolder;

        private myGroupOpenListener() {
        }

        public myGroupOpenListener setGroupViewHolder(GroupViewHolder groupViewHolder) {
            this.mGroupViewHolder = groupViewHolder;
            return this;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            boolean z = true;
            if (!(MyExpandableListAdapter.this.isOpenGroupIDs.containsKey(Integer.valueOf(this.mGroupViewHolder.groupPosition)) ? MyExpandableListAdapter.this.isOpenGroupIDs.get(Integer.valueOf(this.mGroupViewHolder.groupPosition)).booleanValue() : false)) {
                this.mGroupViewHolder.img_open.setImageResource(com.qh.onehlight.R.drawable.ic_light_n);
                MyExpandableListAdapter.this.isOpenGroupIDs.put(Integer.valueOf(this.mGroupViewHolder.groupPosition), true);
            } else {
                this.mGroupViewHolder.img_open.setImageResource(com.qh.onehlight.R.drawable.ic_light_u);
                MyExpandableListAdapter.this.isOpenGroupIDs.put(Integer.valueOf(this.mGroupViewHolder.groupPosition), false);
                z = false;
            }
            ArrayList arrayList = MyExpandableListAdapter.this.children.get(this.mGroupViewHolder.groupPosition);
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    MyChild myChild = (MyChild) arrayList.get(i);
                    if (myChild != null) {
                        MyExpandableListAdapter.this.changeLightByMac(myChild.mac, z);
                        if (MyExpandableListAdapter.this.mSwitchInterface != null) {
                            MyExpandableListAdapter.this.mSwitchInterface.LightSwitch(myChild.mac, z);
                        }
                    }
                }
            }
        }
    }

    private class myOnClickListener implements View.OnClickListener {
        private boolean f;
        private ChildViewHolder mChildViewHolder;
        private int type;

        private myOnClickListener() {
            this.type = 0;
            this.f = false;
        }

        public myOnClickListener setChildViewHolder(ChildViewHolder childViewHolder, int i) {
            this.mChildViewHolder = childViewHolder;
            this.type = i;
            return this;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            GroupViewHolder groupViewHolder;
            MyBluetoothGatt myBluetoothGatt;
            MyBluetoothGatt myBluetoothGatt2;
            ChildViewHolder childViewHolder = this.mChildViewHolder;
            if (childViewHolder == null) {
                Log.e("", "------ conn ------");
                return;
            }
            int i = this.type;
            boolean z = true;
            if (i != 0) {
                if (i == 1) {
                    if (MyExpandableListAdapter.this.mBluetoothLeService.unlinkBleDevices.containsKey(this.mChildViewHolder.mMyChild.mac)) {
                        MyExpandableListAdapter.this.mBluetoothLeService.unlinkBleDevices.remove(this.mChildViewHolder.mMyChild.mac);
                    } else {
                        MyExpandableListAdapter.this.mBluetoothLeService.unlinkBleDevices.put(this.mChildViewHolder.mMyChild.mac, this.mChildViewHolder.mMyChild.mac);
                    }
                    if (MyExpandableListAdapter.this.mBluetoothLeService.MyBluetoothGatts.containsKey(this.mChildViewHolder.mMyChild.mac)) {
                        MyBluetoothGatt myBluetoothGatt3 = MyExpandableListAdapter.this.mBluetoothLeService.MyBluetoothGatts.get(this.mChildViewHolder.mMyChild.mac);
                        if (myBluetoothGatt3 != null) {
                            myBluetoothGatt3.stopLEService();
                        }
                        MyExpandableListAdapter.this.mBluetoothLeService.MyBluetoothGatts.remove(this.mChildViewHolder.mMyChild.mac);
                        return;
                    }
                    if (MyExpandableListAdapter.this.mBluetoothLeService.connBLE(this.mChildViewHolder.mMyChild.mac) == 1) {
                        Toast.makeText(MyExpandableListAdapter.this.mContext, "" + MyExpandableListAdapter.this.mContext.getString(com.qh.onehlight.R.string.limit), 0).show();
                        return;
                    }
                    return;
                }
                return;
            }
            String mac = MyExpandableListAdapter.this.getMAC(childViewHolder.groupPosition, this.mChildViewHolder.childPosition);
            if (MyExpandableListAdapter.this.mBluetoothLeService.MyBluetoothGatts.containsKey(mac) && (myBluetoothGatt2 = MyExpandableListAdapter.this.mBluetoothLeService.MyBluetoothGatts.get(mac)) != null) {
                if ((myBluetoothGatt2.datas[2] & 255) == 35) {
                    this.f = true;
                } else {
                    this.f = false;
                }
            }
            if (this.f) {
                this.mChildViewHolder.open_img.setImageResource(com.qh.onehlight.R.drawable.ic_light_u);
                this.f = false;
                ArrayList arrayList = MyExpandableListAdapter.this.children.get(this.mChildViewHolder.groupPosition);
                if (arrayList != null) {
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        MyChild myChild = (MyChild) arrayList.get(i2);
                        if ((myChild == null || !myChild.mac.equals(this.mChildViewHolder.mMyChild.mac)) && myChild != null && MyExpandableListAdapter.this.mBluetoothLeService.MyBluetoothGatts.containsKey(myChild.mac) && (myBluetoothGatt = MyExpandableListAdapter.this.mBluetoothLeService.MyBluetoothGatts.get(myChild.mac)) != null && myBluetoothGatt.datas != null && myBluetoothGatt.datas.length > 10 && myBluetoothGatt.datas[2] == 35) {
                            Log.e("", "111122" + myChild.mac);
                            z = false;
                        }
                    }
                }
                if (z && (groupViewHolder = MyExpandableListAdapter.this.groupItem.get(Integer.valueOf(this.mChildViewHolder.groupPosition))) != null) {
                    groupViewHolder.img_open.setImageResource(com.qh.onehlight.R.drawable.ic_light_u);
                    MyExpandableListAdapter.this.isOpenGroupIDs.put(Integer.valueOf(this.mChildViewHolder.groupPosition), false);
                }
            } else {
                this.mChildViewHolder.open_img.setImageResource(com.qh.onehlight.R.drawable.ic_light_n);
                this.f = true;
                GroupViewHolder groupViewHolder2 = MyExpandableListAdapter.this.groupItem.get(Integer.valueOf(this.mChildViewHolder.groupPosition));
                if (groupViewHolder2 != null) {
                    groupViewHolder2.img_open.setImageResource(com.qh.onehlight.R.drawable.ic_light_n);
                    MyExpandableListAdapter.this.isOpenGroupIDs.put(Integer.valueOf(this.mChildViewHolder.groupPosition), true);
                }
            }
            if (this.mChildViewHolder.mMyChild != null) {
                MyExpandableListAdapter.this.mSwitchInterface.LightSwitch(this.mChildViewHolder.mMyChild.mac, this.f);
            }
        }
    }

    private class OnGroupTouchListener implements View.OnTouchListener {
        private View convertView;
        private int groupPosition;
        private float x;
        private float y;

        private OnGroupTouchListener() {
        }

        public OnGroupTouchListener setGroupPosition(int i) {
            this.groupPosition = i;
            return this;
        }

        public OnGroupTouchListener setConvertView(View view) {
            this.convertView = view;
            return this;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean z;
            if (motionEvent.getAction() == 0) {
                Log.e("", "group -- " + this.groupPosition);
                if (this.convertView != null) {
                    if (!(MyExpandableListAdapter.this.operatingGroupIDs.containsKey(Integer.valueOf(this.groupPosition)) ? MyExpandableListAdapter.this.operatingGroupIDs.get(Integer.valueOf(this.groupPosition)).booleanValue() : false)) {
                        this.convertView.setBackgroundColor(1090519039);
                        MyExpandableListAdapter.this.operatingGroupIDs.put(Integer.valueOf(this.groupPosition), true);
                        z = true;
                    } else {
                        this.convertView.setBackgroundColor(0);
                        MyExpandableListAdapter.this.operatingGroupIDs.put(Integer.valueOf(this.groupPosition), false);
                        z = false;
                    }
                    ArrayList arrayList = MyExpandableListAdapter.this.children.get(this.groupPosition);
                    if (arrayList != null) {
                        Log.e("", "s = " + this.groupPosition + " mArrayList" + arrayList.size());
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (z) {
                                String mac = MyExpandableListAdapter.this.getMAC(this.groupPosition, i);
                                MainActivity.ControlMACs.put(mac, mac);
                            } else {
                                String mac2 = MyExpandableListAdapter.this.getMAC(this.groupPosition, i);
                                if (MainActivity.ControlMACs.containsKey(mac2)) {
                                    MainActivity.ControlMACs.remove(mac2);
                                }
                            }
                        }
                    }
                    HashMap map = (HashMap) MyExpandableListAdapter.this.groupViews.get(Integer.valueOf(this.groupPosition));
                    if (map != null) {
                        Log.e("", "mHashMap " + map.size());
                        Iterator it = map.keySet().iterator();
                        while (it.hasNext()) {
                            int iIntValue = ((Integer) it.next()).intValue();
                            View view2 = (View) map.get(Integer.valueOf(iIntValue));
                            if (view2 != null) {
                                if (z) {
                                    view2.setBackgroundColor(1090519039);
                                    MyExpandableListAdapter.this.operatingChildIDs.put(Integer.valueOf((this.groupPosition * 100) + iIntValue), true);
                                } else {
                                    view2.setBackgroundColor(0);
                                    MyExpandableListAdapter.this.operatingChildIDs.put(Integer.valueOf((this.groupPosition * 100) + iIntValue), false);
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    private class arrowOnClickListener implements View.OnClickListener {
        private boolean flay;
        private int pos;

        private arrowOnClickListener() {
            this.flay = false;
            this.pos = -1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public arrowOnClickListener setPos(int i, boolean z) {
            this.pos = i;
            this.flay = z;
            return this;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (this.flay) {
                Message message = new Message();
                message.what = 2;
                message.arg1 = this.pos;
                MyExpandableListAdapter.this.mHandler.sendMessage(message);
                Log.e("", "arg1 = " + message.arg1);
            } else {
                Message message2 = new Message();
                message2.what = 1;
                message2.arg1 = this.pos;
                MyExpandableListAdapter.this.mHandler.sendMessage(message2);
                Log.e("", "arg1 = " + message2.arg1);
            }
            this.flay = !this.flay;
        }
    }

    public class OnChildTouchListener implements View.OnTouchListener {
        private int childPosition;
        private View convertView;
        private int groupPosition;

        public OnChildTouchListener() {
        }

        public OnChildTouchListener setOnChildTouchListener(View view) {
            this.convertView = view;
            return this;
        }

        public OnChildTouchListener setPos(int i, int i2, boolean z) {
            this.groupPosition = i;
            this.childPosition = i2;
            return this;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                Log.e("", " c -t " + this.groupPosition + " " + this.childPosition);
                if (this.convertView != null) {
                    if (!(MyExpandableListAdapter.this.operatingChildIDs.containsKey(Integer.valueOf((this.groupPosition * 100) + this.childPosition)) ? MyExpandableListAdapter.this.operatingChildIDs.get(Integer.valueOf((this.groupPosition * 100) + this.childPosition)).booleanValue() : false)) {
                        this.convertView.setBackgroundColor(1090519039);
                        MyExpandableListAdapter.this.operatingChildIDs.put(Integer.valueOf((this.groupPosition * 100) + this.childPosition), true);
                        String mac = MyExpandableListAdapter.this.getMAC(this.groupPosition, this.childPosition);
                        MainActivity.ControlMACs.put(mac, mac);
                    } else {
                        this.convertView.setBackgroundColor(0);
                        MyExpandableListAdapter.this.operatingChildIDs.put(Integer.valueOf((this.groupPosition * 100) + this.childPosition), false);
                        String mac2 = MyExpandableListAdapter.this.getMAC(this.groupPosition, this.childPosition);
                        if (MainActivity.ControlMACs.containsKey(mac2)) {
                            MainActivity.ControlMACs.remove(mac2);
                        }
                    }
                }
            }
            return false;
        }
    }

    private class OnResetNameClickListener implements View.OnClickListener {
        private int groupPosition;

        private OnResetNameClickListener() {
        }

        public OnResetNameClickListener setGroupPosition(int i) {
            this.groupPosition = i;
            return this;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            final EditText editText = new EditText(MyExpandableListAdapter.this.mContext);
            new AlertDialog.Builder(MyExpandableListAdapter.this.mContext).setTitle("" + MyExpandableListAdapter.this.mContext.getResources().getString(com.qh.onehlight.R.string.Rename)).setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("" + MyExpandableListAdapter.this.mContext.getResources().getString(com.qh.onehlight.R.string.ok), new DialogInterface.OnClickListener() { // from class: com.qh.blelight.MyExpandableListAdapter.OnResetNameClickListener.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.e("", "-- " + ((Object) editText.getText()));
                }
            }).setNegativeButton("" + MyExpandableListAdapter.this.mContext.getResources().getString(com.qh.onehlight.R.string.cencel), (DialogInterface.OnClickListener) null).show();
        }
    }

    public void move(int i, int i2, int i3) {
        if (this.children.size() <= i || this.children.size() <= i2) {
            return;
        }
        ArrayList arrayList = this.children.get(i);
        ArrayList arrayList2 = this.children.get(i2);
        if (arrayList.size() > i3) {
            MyChild myChild = (MyChild) arrayList.get(i3);
            arrayList.remove(i3);
            arrayList2.add(myChild);
            this.children.set(i, arrayList);
            this.children.set(i2, arrayList2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getMAC(int i, int i2) {
        ArrayList arrayList;
        return (this.children.size() <= i || (arrayList = this.children.get(i)) == null || arrayList.size() <= i2) ? "" : ((MyChild) arrayList.get(i2)).mac;
    }

    public void changeLightByMac(String str, boolean z) {
        ChildViewHolder childViewHolder;
        if (!this.itemViewByMac.containsKey(str) || (childViewHolder = this.itemViewByMac.get(str)) == null) {
            return;
        }
        if (z) {
            childViewHolder.open_img.setImageResource(com.qh.onehlight.R.drawable.ic_light_n);
        } else {
            childViewHolder.open_img.setImageResource(com.qh.onehlight.R.drawable.ic_light_u);
        }
    }

    public int islinkOK(String str) {
        BluetoothLeService bluetoothLeService;
        MyBluetoothGatt myBluetoothGatt;
        if (str == null || "".equals(str) || (bluetoothLeService = this.mBluetoothLeService) == null || !bluetoothLeService.MyBluetoothGatts.containsKey(str) || (myBluetoothGatt = this.mBluetoothLeService.MyBluetoothGatts.get(str)) == null) {
            return 4;
        }
        return myBluetoothGatt.mConnectionState;
    }
}
