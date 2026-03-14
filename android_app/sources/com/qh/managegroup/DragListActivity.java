package com.qh.managegroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.qh.blelight.MyApplication;
import com.qh.data.ItemInfo;
import com.qh.managegroup.DragListView;
import com.qh.onehlight.R;
import com.qh.tools.DBAdapter;
import com.qh.tools.DBLightTable;
import com.qh.tools.DBTable;
import java.util.ArrayList;
import java.util.Hashtable;

/* JADX INFO: loaded from: classes.dex */
public class DragListActivity extends Activity {
    public DBAdapter dbAdapter;
    private RelativeLayout lin_back;
    public MyApplication mMyApplication;
    public Resources mResources;
    public Context mcontext;
    private RelativeLayout rel_add;
    private RelativeLayout rel_main;
    private DragListAdapter adapter = null;
    private ArrayList<MyListData> data = new ArrayList<>();
    public Handler mHandler = new Handler(new Handler.Callback() { // from class: com.qh.managegroup.DragListActivity.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what != 1) {
                return false;
            }
            DragListActivity.this.initData();
            DragListActivity.this.adapter.notifyDataSetChanged();
            return false;
        }
    });

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.managegroup_main);
        Log.e("", "11");
        this.mResources = getResources();
        this.mcontext = this;
        this.mMyApplication = (MyApplication) getApplication();
        DBAdapter dBAdapterInit = DBAdapter.init(this);
        this.dbAdapter = dBAdapterInit;
        dBAdapterInit.open();
        init();
        initData();
        if (this.mMyApplication.bgsrc.length > this.mMyApplication.typebg) {
            this.rel_main.setBackgroundResource(this.mMyApplication.bgsrc[this.mMyApplication.typebg]);
        }
        DragListView dragListView = (DragListView) findViewById(R.id.other_drag_list);
        DragListAdapter dragListAdapter = new DragListAdapter(this, this.data);
        this.adapter = dragListAdapter;
        dragListView.setAdapter((ListAdapter) dragListAdapter);
        dragListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // from class: com.qh.managegroup.DragListActivity.2
            @Override // android.widget.AdapterView.OnItemLongClickListener
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                MyListData myListData;
                Log.e("long", "position = " + i);
                ItemInfo itemInfo = DragListActivity.this.adapter.ItemInfos.get(Integer.valueOf(i));
                if (i == 0 || itemInfo == null || itemInfo.tx_delete == null || (myListData = (MyListData) itemInfo.light_img.getTag()) == null || myListData.groupId == 0) {
                    return true;
                }
                itemInfo.tx_delete.setVisibility(0);
                return true;
            }
        });
        dragListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.qh.managegroup.DragListActivity.3
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Log.e("", "onItemClick = " + i);
                ItemInfo itemInfo = DragListActivity.this.adapter.ItemInfos.get(Integer.valueOf(i));
                if (i == 0 || itemInfo == null || itemInfo.tx_delete == null) {
                    return;
                }
                itemInfo.tx_delete.setVisibility(8);
            }
        });
        dragListView.setDragListChange(new DragListView.DragListChange() { // from class: com.qh.managegroup.DragListActivity.4
            @Override // com.qh.managegroup.DragListView.DragListChange
            public void change(MyListData myListData, MyListData myListData2) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBLightTable.GROUP, Integer.valueOf(myListData2.groupId));
                contentValues.put(DBLightTable.ID, myListData.addr);
                contentValues.put(DBLightTable.LIGTH_NAME, myListData.name);
                long jInsert = DragListActivity.this.dbAdapter.insert(DBLightTable.DB_TABLE, contentValues);
                Log.e("", "kkkkk = " + jInsert);
                if (jInsert == -1) {
                    String[] strArr = {myListData.addr};
                    ContentValues contentValues2 = new ContentValues();
                    contentValues2.put(DBLightTable.GROUP, Integer.valueOf(myListData2.groupId));
                    DragListActivity.this.dbAdapter.upDataforTable(DBLightTable.DB_TABLE, contentValues2, "Mac=?", strArr);
                }
                DragListActivity.this.mHandler.sendEmptyMessage(1);
            }

            @Override // com.qh.managegroup.DragListView.DragListChange
            public void delect(MyListData myListData) {
                if (myListData == null) {
                    return;
                }
                Log.e("", "" + myListData.isGroup);
                Log.e("", "" + myListData.groupId);
                Log.e("", "" + myListData.name);
                if (myListData.isGroup) {
                    DragListActivity.this.dbAdapter.deleteOneData(DBTable.DB_TABLE, "_id=?", new String[]{"" + myListData.groupId});
                    DragListActivity.this.dbAdapter.deleteOneData(DBLightTable.DB_TABLE, "lightgroup=?", new String[]{"" + myListData.groupId});
                } else {
                    DragListActivity.this.dbAdapter.deleteOneData(DBLightTable.DB_TABLE, "Mac=?", new String[]{myListData.addr});
                }
                DragListActivity.this.mHandler.sendEmptyMessage(1);
            }

            @Override // com.qh.managegroup.DragListView.DragListChange
            public void resetname(final MyListData myListData) {
                if (myListData == null) {
                    return;
                }
                final EditText editText = new EditText(DragListActivity.this.mcontext);
                editText.setText("" + myListData.name);
                editText.selectAll();
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                new AlertDialog.Builder(DragListActivity.this.mcontext).setTitle("" + DragListActivity.this.mcontext.getResources().getString(R.string.Rename)).setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("" + DragListActivity.this.mcontext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() { // from class: com.qh.managegroup.DragListActivity.4.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String string = editText.getText().toString();
                        if ("".equals(string)) {
                            return;
                        }
                        if (DragListActivity.this.mResources.getString(R.string.My_device).equals(string)) {
                            Toast.makeText(DragListActivity.this.mcontext, "" + DragListActivity.this.mResources.getString(R.string.error1), 1).show();
                            return;
                        }
                        Cursor cursorQueryAllData = DragListActivity.this.dbAdapter.queryAllData(DBTable.DB_TABLE);
                        while (cursorQueryAllData.moveToNext()) {
                            if (string.equals(cursorQueryAllData.getString(cursorQueryAllData.getColumnIndex(DBTable.GROUP_NAME)))) {
                                Toast.makeText(DragListActivity.this.mcontext, "" + DragListActivity.this.mResources.getString(R.string.error1), 1).show();
                                return;
                            }
                        }
                        Cursor cursorQueryAllData2 = DragListActivity.this.dbAdapter.queryAllData(DBLightTable.DB_TABLE);
                        while (cursorQueryAllData2.moveToNext()) {
                            cursorQueryAllData2.getString(cursorQueryAllData2.getColumnIndex(DBLightTable.LIGTH_NAME));
                            if (string.equals(cursorQueryAllData2)) {
                                Toast.makeText(DragListActivity.this.mcontext, "" + DragListActivity.this.mResources.getString(R.string.error1), 1).show();
                                return;
                            }
                        }
                        if (myListData.isGroup) {
                            String[] strArr = {"" + myListData.groupId};
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DBTable.GROUP_NAME, string);
                            DragListActivity.this.dbAdapter.upDataforTable(DBTable.DB_TABLE, contentValues, "_id=?", strArr);
                        } else {
                            String[] strArr2 = {"" + myListData.addr};
                            ContentValues contentValues2 = new ContentValues();
                            contentValues2.put(DBLightTable.LIGTH_NAME, string);
                            if (DragListActivity.this.dbAdapter.upDataforTable(DBLightTable.DB_TABLE, contentValues2, "Mac=?", strArr2) == 0) {
                                ContentValues contentValues3 = new ContentValues();
                                contentValues3.put(DBLightTable.GROUP, (Integer) 0);
                                contentValues3.put(DBLightTable.ID, myListData.addr);
                                contentValues3.put(DBLightTable.LIGTH_NAME, string);
                                DragListActivity.this.dbAdapter.insert(DBLightTable.DB_TABLE, contentValues3);
                            }
                        }
                        DragListActivity.this.mHandler.sendEmptyMessage(1);
                    }
                }).setNegativeButton("" + DragListActivity.this.mcontext.getResources().getString(R.string.cencel), (DialogInterface.OnClickListener) null).show();
                DragListActivity.this.mHandler.postDelayed(new Runnable() { // from class: com.qh.managegroup.DragListActivity.4.2
                    @Override // java.lang.Runnable
                    public void run() {
                        ((InputMethodManager) editText.getContext().getSystemService("input_method")).showSoftInput(editText, 0);
                    }
                }, 1000L);
            }
        });
    }

    private void init() {
        this.lin_back = (RelativeLayout) findViewById(R.id.lin_back);
        this.rel_add = (RelativeLayout) findViewById(R.id.rel_add);
        this.rel_main = (RelativeLayout) findViewById(R.id.rel_main);
        this.lin_back.setOnClickListener(new View.OnClickListener() { // from class: com.qh.managegroup.DragListActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", 1);
                DragListActivity.this.setResult(-1, intent);
                DragListActivity.this.finish();
            }
        });
        this.rel_add.setOnClickListener(new View.OnClickListener() { // from class: com.qh.managegroup.DragListActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                final EditText editText = new EditText(DragListActivity.this.mcontext);
                new AlertDialog.Builder(DragListActivity.this.mcontext).setTitle("" + DragListActivity.this.mcontext.getResources().getString(R.string.Name1)).setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton("" + DragListActivity.this.mcontext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() { // from class: com.qh.managegroup.DragListActivity.6.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String string = editText.getText().toString();
                        if ("".equals(string)) {
                            return;
                        }
                        if (DragListActivity.this.mResources.getString(R.string.My_device).equals(string)) {
                            Toast.makeText(DragListActivity.this.mcontext, "" + DragListActivity.this.mResources.getString(R.string.error1), 1).show();
                            return;
                        }
                        Cursor cursorQueryAllData = DragListActivity.this.dbAdapter.queryAllData(DBTable.DB_TABLE);
                        while (cursorQueryAllData.moveToNext()) {
                            if (string.equals(cursorQueryAllData.getString(cursorQueryAllData.getColumnIndex(DBTable.GROUP_NAME)))) {
                                Toast.makeText(DragListActivity.this.mcontext, "" + DragListActivity.this.mResources.getString(R.string.error1), 1).show();
                                return;
                            }
                        }
                        Cursor cursorQueryAllData2 = DragListActivity.this.dbAdapter.queryAllData(DBLightTable.DB_TABLE);
                        while (cursorQueryAllData2.moveToNext()) {
                            cursorQueryAllData2.getString(cursorQueryAllData2.getColumnIndex(DBLightTable.LIGTH_NAME));
                            if (string.equals(cursorQueryAllData2)) {
                                Toast.makeText(DragListActivity.this.mcontext, "" + DragListActivity.this.mResources.getString(R.string.error1), 1).show();
                                return;
                            }
                        }
                        Log.e("", "name = " + string);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBTable.GROUP_NAME, "" + string);
                        DragListActivity.this.dbAdapter.insert(DBTable.DB_TABLE, contentValues);
                        DragListActivity.this.initData();
                        DragListActivity.this.adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("" + DragListActivity.this.mcontext.getResources().getString(R.string.cencel), (DialogInterface.OnClickListener) null).show();
            }
        });
    }

    public void initData() {
        new Hashtable();
        this.data.clear();
        MyListData myListData = new MyListData();
        myListData.name = this.mResources.getString(R.string.My_device);
        myListData.isGroup = true;
        myListData.groupId = 0;
        myListData.addr = "0";
        this.data.add(myListData);
        Hashtable hashtable = new Hashtable();
        Cursor cursorQueryDataByGroup = this.dbAdapter.queryDataByGroup(0);
        while (cursorQueryDataByGroup.moveToNext()) {
            int i = cursorQueryDataByGroup.getInt(cursorQueryDataByGroup.getColumnIndex(DBLightTable.GROUP));
            String string = cursorQueryDataByGroup.getString(cursorQueryDataByGroup.getColumnIndex(DBLightTable.ID));
            String string2 = cursorQueryDataByGroup.getString(cursorQueryDataByGroup.getColumnIndex(DBLightTable.LIGTH_NAME));
            Log.e("", "mac = " + string + " myGroup =  " + i + " name = " + string2);
            MyListData myListData2 = new MyListData();
            myListData2.isGroup = false;
            myListData2.addr = string;
            myListData2.name = string2;
            myListData2.groupId = i;
            hashtable.put(string, myListData2);
        }
        for (String str : this.mMyApplication.mBluetoothLeService.mDevices.keySet()) {
            if (hashtable.containsKey(str)) {
                this.data.add((MyListData) hashtable.get(str));
            } else if (this.dbAdapter.queryDataByMAC(str).getCount() == 0) {
                BluetoothDevice bluetoothDevice = this.mMyApplication.mBluetoothLeService.mDevices.get(str);
                MyListData myListData3 = new MyListData();
                myListData3.isGroup = false;
                myListData3.addr = bluetoothDevice.getAddress();
                myListData3.name = bluetoothDevice.getName();
                myListData3.groupId = 0;
                this.data.add(myListData3);
            }
        }
        Cursor cursorQueryAllData = this.dbAdapter.queryAllData(DBTable.DB_TABLE);
        while (cursorQueryAllData.moveToNext()) {
            int i2 = cursorQueryAllData.getInt(cursorQueryAllData.getColumnIndex(DBTable.ID));
            String string3 = cursorQueryAllData.getString(cursorQueryAllData.getColumnIndex(DBTable.GROUP_NAME));
            Log.e("", "mac = " + i2 + " myGroup =  " + string3);
            MyListData myListData4 = new MyListData();
            myListData4.name = string3;
            myListData4.isGroup = true;
            myListData4.groupId = i2;
            if (i2 != 0) {
                this.data.add(myListData4);
                Cursor cursorQueryDataByGroup2 = this.dbAdapter.queryDataByGroup(i2);
                while (cursorQueryDataByGroup2.moveToNext()) {
                    int i3 = cursorQueryDataByGroup2.getInt(cursorQueryDataByGroup2.getColumnIndex(DBLightTable.GROUP));
                    String string4 = cursorQueryDataByGroup2.getString(cursorQueryDataByGroup2.getColumnIndex(DBLightTable.ID));
                    String string5 = cursorQueryDataByGroup2.getString(cursorQueryDataByGroup2.getColumnIndex(DBLightTable.LIGTH_NAME));
                    MyListData myListData5 = new MyListData();
                    myListData5.isGroup = false;
                    myListData5.addr = string4;
                    myListData5.name = string5;
                    myListData5.groupId = i3;
                    if (this.mMyApplication.mBluetoothLeService.mDevices.containsKey(string4)) {
                        this.data.add(myListData5);
                    }
                }
            }
        }
    }
}
