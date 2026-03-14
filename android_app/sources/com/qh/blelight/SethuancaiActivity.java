package com.qh.blelight;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.qh.WheelView.OnWheelScrollListener;
import com.qh.WheelView.WheelView;
import com.qh.WheelView.WheelViewAdapter;
import com.qh.onehlight.R;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class SethuancaiActivity extends Activity {
    private Button btn_send;
    private EditText et_input;
    private RelativeLayout lin_back;
    private LayoutInflater mInflator;
    private MyApplication mMyApplication;
    private Resources mResources;
    private Context mcontext;
    private MyWheelViewAdapter myWheelViewAdapter;
    private RelativeLayout rel_main;
    private Spinner spinner_huancaitype;
    private Spinner spinner_rgb;
    private Spinner spinner_xunhuan;
    private WheelView timing_wheelview;
    private TextView tv_shuancaitype;
    private TextView tv_srgb;
    private ArrayList mDataArrayList = new ArrayList();
    private ArrayList<String> listhuancaitype = new ArrayList<>();
    private ArrayList<String> listhuancairgb = new ArrayList<>();
    private ArrayList<String> listhuancainum = new ArrayList<>();
    public int[] huancaitypes = {0, 1, 2, 3, 4, 5, 6, 7, 9, 17, 11};
    private String selectMac = "";
    private String deviceName = "";
    public int lighttype = 0;
    public int lightnum = -1;
    public int order = 0;
    public int sysmod = 0;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.activity_sethuancai);
        this.mcontext = this;
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.lin_back);
        this.lin_back = relativeLayout;
        relativeLayout.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.SethuancaiActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SethuancaiActivity.this.finish();
            }
        });
        this.mResources = getResources();
        this.mMyApplication = (MyApplication) getApplication();
        init();
    }

    public void init() {
        this.tv_srgb = (TextView) findViewById(R.id.tv_srgb);
        this.tv_shuancaitype = (TextView) findViewById(R.id.tv_shuancaitype);
        this.mInflator = getLayoutInflater();
        this.timing_wheelview = (WheelView) findViewById(R.id.timing_wheelview);
        setData();
        this.rel_main = (RelativeLayout) findViewById(R.id.rel_main);
        if (this.mMyApplication.bgsrc.length > this.mMyApplication.typebg) {
            this.rel_main.setBackgroundResource(this.mMyApplication.bgsrc[this.mMyApplication.typebg]);
        }
        this.et_input = (EditText) findViewById(R.id.et_input);
        this.spinner_huancaitype = (Spinner) findViewById(R.id.spinner_huancaitype);
        for (String str : getResources().getStringArray(R.array.huancaitype)) {
            this.listhuancaitype.add(str);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.listhuancaitype);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_huancaitype.setAdapter((SpinnerAdapter) arrayAdapter);
        this.btn_send = (Button) findViewById(R.id.btn_send);
        this.spinner_rgb = (Spinner) findViewById(R.id.spinner_rgb);
        this.spinner_xunhuan = (Spinner) findViewById(R.id.spinner_xunhuan);
        for (String str2 : getResources().getStringArray(R.array.xuanhuan)) {
            this.listhuancainum.add(str2);
        }
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.listhuancainum);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_xunhuan.setAdapter((SpinnerAdapter) arrayAdapter2);
        for (String str3 : getResources().getStringArray(R.array.rgb)) {
            this.listhuancairgb.add(str3);
        }
        ArrayAdapter arrayAdapter3 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.listhuancairgb);
        arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_rgb.setAdapter((SpinnerAdapter) arrayAdapter3);
        this.btn_send.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.SethuancaiActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    int i = Integer.parseInt(SethuancaiActivity.this.et_input.getEditableText().toString());
                    if (i > 1024) {
                        Toast.makeText(SethuancaiActivity.this.mcontext, "" + SethuancaiActivity.this.getResources().getString(R.string.numerror), 0).show();
                        return;
                    }
                    int i2 = SethuancaiActivity.this.huancaitypes.length > SethuancaiActivity.this.spinner_huancaitype.getSelectedItemPosition() ? SethuancaiActivity.this.huancaitypes[SethuancaiActivity.this.spinner_huancaitype.getSelectedItemPosition()] : 0;
                    int selectedItemPosition = SethuancaiActivity.this.spinner_rgb.getSelectedItemPosition();
                    int selectedItemPosition2 = SethuancaiActivity.this.spinner_xunhuan.getSelectedItemPosition();
                    Log.e("--", "type=" + i2 + " rgb=" + selectedItemPosition + " xunhuan=" + selectedItemPosition2);
                    StringBuilder sb = new StringBuilder();
                    sb.append("n=");
                    sb.append(i);
                    Log.e("--", sb.toString());
                    byte[] bArr = {27, (byte) i2, (byte) (i / 256), (byte) (i & 255), (byte) selectedItemPosition, (byte) selectedItemPosition2, 0, -16};
                    if (SethuancaiActivity.this.deviceName.contains("Dream&") || SethuancaiActivity.this.deviceName.contains("Flash&")) {
                        bArr[1] = 11;
                        bArr[2] = 0;
                    }
                    SethuancaiActivity.this.mMyApplication.setData(SethuancaiActivity.this.selectMac, bArr);
                    Toast.makeText(SethuancaiActivity.this.mcontext, "" + SethuancaiActivity.this.getResources().getString(R.string.send_ok), 0).show();
                    new Handler().postDelayed(new Runnable() { // from class: com.qh.blelight.SethuancaiActivity.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            SethuancaiActivity.this.mMyApplication.readhuancai(SethuancaiActivity.this.selectMac);
                        }
                    }, 500L);
                } catch (NumberFormatException unused) {
                    Toast.makeText(SethuancaiActivity.this.mcontext, "" + SethuancaiActivity.this.getResources().getString(R.string.numerror), 0).show();
                }
            }
        });
        this.timing_wheelview.addScrollingListener(new OnWheelScrollListener() { // from class: com.qh.blelight.SethuancaiActivity.3
            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingStarted(WheelView wheelView) {
            }

            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingFinished(WheelView wheelView) {
                int currentItem = wheelView.getCurrentItem() - 1;
                if (SethuancaiActivity.this.mDataArrayList.size() > currentItem) {
                    if (currentItem < 0) {
                        SethuancaiActivity.this.selectMac = "";
                        return;
                    }
                    MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) SethuancaiActivity.this.mDataArrayList.get(currentItem);
                    if (myBluetoothGatt != null) {
                        SethuancaiActivity.this.selectMac = myBluetoothGatt.mAddr;
                        SethuancaiActivity.this.deviceName = myBluetoothGatt.mLEdevice.getName();
                        SethuancaiActivity.this.lighttype = myBluetoothGatt.huancaidata[1];
                        SethuancaiActivity.this.lightnum = ((myBluetoothGatt.huancaidata[2] & 255) * 256) | (myBluetoothGatt.huancaidata[3] & 255);
                        SethuancaiActivity.this.order = myBluetoothGatt.huancaidata[4];
                        SethuancaiActivity.this.sysmod = myBluetoothGatt.huancaidata[5];
                        if (SethuancaiActivity.this.lighttype == 9) {
                            SethuancaiActivity.this.lighttype = 8;
                        }
                        if (SethuancaiActivity.this.lighttype == 17) {
                            SethuancaiActivity.this.lighttype = 9;
                        }
                        if (SethuancaiActivity.this.lighttype == 11) {
                            SethuancaiActivity.this.lighttype = 10;
                        }
                        if (SethuancaiActivity.this.huancaitypes.length > SethuancaiActivity.this.lighttype) {
                            SethuancaiActivity.this.spinner_huancaitype.setSelection(SethuancaiActivity.this.lighttype);
                        }
                        SethuancaiActivity.this.spinner_rgb.setSelection(SethuancaiActivity.this.order);
                        SethuancaiActivity.this.spinner_xunhuan.setSelection(SethuancaiActivity.this.sysmod);
                        if (SethuancaiActivity.this.lightnum != -1) {
                            SethuancaiActivity.this.et_input.setText("" + SethuancaiActivity.this.lightnum);
                        }
                        if (myBluetoothGatt.mLEdevice.getName().contains("Dream&") || myBluetoothGatt.mLEdevice.getName().contains("Flash&")) {
                            SethuancaiActivity.this.spinner_rgb.setVisibility(8);
                            SethuancaiActivity.this.spinner_huancaitype.setVisibility(8);
                            SethuancaiActivity.this.tv_srgb.setVisibility(0);
                            SethuancaiActivity.this.tv_shuancaitype.setVisibility(0);
                            return;
                        }
                        SethuancaiActivity.this.spinner_rgb.setVisibility(0);
                        SethuancaiActivity.this.spinner_huancaitype.setVisibility(0);
                        SethuancaiActivity.this.tv_srgb.setVisibility(8);
                        SethuancaiActivity.this.tv_shuancaitype.setVisibility(8);
                    }
                }
            }
        });
    }

    private class MyWheelViewAdapter implements WheelViewAdapter {
        public ArrayList<String> arraylistName;
        public ArrayList<MyBluetoothGatt> data;

        @Override // com.qh.WheelView.WheelViewAdapter
        public View getEmptyItem(View view, ViewGroup viewGroup) {
            return null;
        }

        @Override // com.qh.WheelView.WheelViewAdapter
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        }

        @Override // com.qh.WheelView.WheelViewAdapter
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        }

        private MyWheelViewAdapter() {
            this.arraylistName = new ArrayList<>();
            this.data = new ArrayList<>();
        }

        public void setData(ArrayList arrayList) {
            this.arraylistName.clear();
            this.arraylistName.add("" + SethuancaiActivity.this.mResources.getString(R.string.huandong));
            for (int i = 0; i < arrayList.size(); i++) {
                MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) arrayList.get(i);
                if (myBluetoothGatt != null) {
                    if (!SethuancaiActivity.this.mMyApplication.mMyExpandableListAdapter.DBdata.containsKey(myBluetoothGatt.mAddr)) {
                        if (SethuancaiActivity.this.mMyApplication.mBluetoothLeService.mDevices.containsKey(myBluetoothGatt.mAddr)) {
                            BluetoothDevice bluetoothDevice = SethuancaiActivity.this.mMyApplication.mBluetoothLeService.mDevices.get(myBluetoothGatt.mAddr);
                            this.arraylistName.add("" + bluetoothDevice.getName());
                        }
                    } else {
                        this.arraylistName.add(SethuancaiActivity.this.mMyApplication.mMyExpandableListAdapter.DBdata.get(myBluetoothGatt.mAddr).name);
                    }
                }
            }
            this.data.addAll(arrayList);
        }

        @Override // com.qh.WheelView.WheelViewAdapter
        public int getItemsCount() {
            return this.arraylistName.size();
        }

        @Override // com.qh.WheelView.WheelViewAdapter
        public View getItem(int i, View view, ViewGroup viewGroup) {
            View viewInflate = SethuancaiActivity.this.mInflator.inflate(R.layout.wheel_item, (ViewGroup) null);
            TextView textView = (TextView) viewInflate.findViewById(R.id.tx_lable);
            ((ImageView) viewInflate.findViewById(R.id.img_conn)).setVisibility(8);
            textView.setText("" + this.arraylistName.get(i));
            return viewInflate;
        }
    }

    private void setData() {
        this.myWheelViewAdapter = new MyWheelViewAdapter();
        this.mDataArrayList.clear();
        Iterator<String> it = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.keySet().iterator();
        while (it.hasNext()) {
            MyBluetoothGatt myBluetoothGatt = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(it.next());
            if (myBluetoothGatt.mLEdevice.getName().contains("Dream") || myBluetoothGatt.mLEdevice.getName().contains("Flash")) {
                this.mDataArrayList.add(myBluetoothGatt);
            }
        }
        this.myWheelViewAdapter.setData(this.mDataArrayList);
        this.timing_wheelview.setViewAdapter(this.myWheelViewAdapter);
        this.timing_wheelview.setVisibleItems(1);
    }
}
