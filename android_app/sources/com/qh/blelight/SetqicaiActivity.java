package com.qh.blelight;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.qh.WheelView.OnWheelScrollListener;
import com.qh.WheelView.WheelView;
import com.qh.WheelView.WheelViewAdapter;
import com.qh.blelight.BluetoothLeService;
import com.qh.onehlight.R;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class SetqicaiActivity extends Activity {
    private Button btn_send;
    private Context context;
    private ImageView img_open;
    private RelativeLayout lin_back;
    private LayoutInflater mInflator;
    private MyApplication mMyApplication;
    private Resources mResources;
    private MyWheelViewAdapter myWheelViewAdapter;
    private RelativeLayout rel_4;
    private Spinner spinner_qicairgb;
    private Spinner spinner_qicaitype;
    private WheelView timing_wheelview;
    private ArrayList<String> listqicaitype = new ArrayList<>();
    private ArrayList<String> listqicairgb = new ArrayList<>();
    private boolean isopenpwd = false;
    private int type = 0;
    private int rgbn = 0;
    public BluetoothLeService.Qicaidata mQicaidata = new BluetoothLeService.Qicaidata() { // from class: com.qh.blelight.SetqicaiActivity.1
        @Override // com.qh.blelight.BluetoothLeService.Qicaidata
        public void readqicai(String str, int i, int i2, boolean z) {
            Log.e("readqicai", "addr=" + str + " lighttype=" + i2 + " rgb=" + i);
            if (SetqicaiActivity.this.listqicaitype.size() <= SetqicaiActivity.this.type || i2 < 0) {
                SetqicaiActivity.this.type = 0;
            } else {
                SetqicaiActivity.this.type = i2;
            }
            if (SetqicaiActivity.this.listqicairgb.size() <= i || i < 0) {
                SetqicaiActivity.this.rgbn = 0;
            } else {
                SetqicaiActivity.this.rgbn = i;
            }
            SetqicaiActivity.this.isopenpwd = z;
            SetqicaiActivity.this.uiHandler.sendEmptyMessage(0);
        }
    };
    public Handler uiHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.SetqicaiActivity.2
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what != 0) {
                return false;
            }
            SetqicaiActivity.this.upui();
            return false;
        }
    });
    private String selectMac = "";
    private ArrayList mDataArrayList = new ArrayList();

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.activity_sethuancai2);
        init();
        setListener();
        this.mMyApplication.mBluetoothLeService.mQicaidata = this.mQicaidata;
    }

    private void init() {
        this.context = this;
        this.mMyApplication = (MyApplication) getApplication();
        this.mResources = getResources();
        this.spinner_qicaitype = (Spinner) findViewById(R.id.spinner_qicaitype);
        this.spinner_qicairgb = (Spinner) findViewById(R.id.spinner_qicairgb);
        this.img_open = (ImageView) findViewById(R.id.img_open);
        this.btn_send = (Button) findViewById(R.id.btn_send);
        this.timing_wheelview = (WheelView) findViewById(R.id.timing_wheelview);
        this.mInflator = getLayoutInflater();
        this.lin_back = (RelativeLayout) findViewById(R.id.lin_back);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel_4);
        this.rel_4 = relativeLayout;
        relativeLayout.setVisibility(8);
        setData();
        for (String str : getResources().getStringArray(R.array.qicaiset2)) {
            this.listqicaitype.add(str);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.listqicaitype);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_qicaitype.setAdapter((SpinnerAdapter) arrayAdapter);
        for (String str2 : getResources().getStringArray(R.array.rgb2)) {
            this.listqicairgb.add(str2);
        }
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, this.listqicairgb);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_qicairgb.setAdapter((SpinnerAdapter) arrayAdapter2);
    }

    private void setListener() {
        this.lin_back.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.SetqicaiActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SetqicaiActivity.this.finish();
            }
        });
        this.timing_wheelview.addScrollingListener(new OnWheelScrollListener() { // from class: com.qh.blelight.SetqicaiActivity.4
            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingStarted(WheelView wheelView) {
            }

            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingFinished(WheelView wheelView) {
                int currentItem = wheelView.getCurrentItem() - 1;
                if (SetqicaiActivity.this.mDataArrayList.size() > currentItem) {
                    if (currentItem < 0) {
                        SetqicaiActivity.this.selectMac = "";
                        return;
                    }
                    MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) SetqicaiActivity.this.mDataArrayList.get(currentItem);
                    if (myBluetoothGatt != null) {
                        SetqicaiActivity.this.selectMac = myBluetoothGatt.mAddr;
                        myBluetoothGatt.readqicai();
                    }
                }
            }
        });
        this.img_open.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.SetqicaiActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SetqicaiActivity.this.isopenpwd = !r2.isopenpwd;
                if (SetqicaiActivity.this.isopenpwd) {
                    SetqicaiActivity.this.img_open.setImageResource(R.drawable.ic_open);
                } else {
                    SetqicaiActivity.this.img_open.setImageResource(R.drawable.ic_close);
                }
            }
        });
        this.btn_send.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.SetqicaiActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.e("btn_send", "spinner_qicaitype = " + SetqicaiActivity.this.spinner_qicaitype.getSelectedItemPosition());
                Log.e("btn_send", "spinner_qicairgb = " + SetqicaiActivity.this.spinner_qicairgb.getSelectedItemPosition());
                Log.e("btn_send", "isopen = " + SetqicaiActivity.this.isopenpwd);
                if (SetqicaiActivity.this.mMyApplication.setqicaidata(SetqicaiActivity.this.selectMac, SetqicaiActivity.this.spinner_qicairgb.getSelectedItemPosition(), SetqicaiActivity.this.spinner_qicaitype.getSelectedItemPosition(), SetqicaiActivity.this.isopenpwd)) {
                    Toast.makeText(SetqicaiActivity.this.context, SetqicaiActivity.this.mResources.getString(R.string.setok), 1).show();
                } else {
                    Toast.makeText(SetqicaiActivity.this.context, SetqicaiActivity.this.mResources.getString(R.string.setfalse), 1).show();
                }
            }
        });
    }

    public void upui() {
        BluetoothDevice bluetoothDevice;
        this.rel_4.setVisibility(8);
        if (this.isopenpwd) {
            this.img_open.setImageResource(R.drawable.ic_open);
        } else {
            this.img_open.setImageResource(R.drawable.ic_close);
        }
        if (this.mMyApplication.mBluetoothLeService.mDevices.containsKey(this.selectMac) && (bluetoothDevice = this.mMyApplication.mBluetoothLeService.mDevices.get(this.selectMac)) != null && bluetoothDevice.getName().contains("Triones:")) {
            this.rel_4.setVisibility(0);
        }
        this.spinner_qicaitype.setSelection(this.type);
        this.spinner_qicairgb.setSelection(this.rgbn);
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
            this.arraylistName.add("" + SetqicaiActivity.this.mResources.getString(R.string.huandong));
            for (int i = 0; i < arrayList.size(); i++) {
                MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) arrayList.get(i);
                if (myBluetoothGatt != null) {
                    if (!SetqicaiActivity.this.mMyApplication.mMyExpandableListAdapter.DBdata.containsKey(myBluetoothGatt.mAddr)) {
                        if (SetqicaiActivity.this.mMyApplication.mBluetoothLeService.mDevices.containsKey(myBluetoothGatt.mAddr)) {
                            BluetoothDevice bluetoothDevice = SetqicaiActivity.this.mMyApplication.mBluetoothLeService.mDevices.get(myBluetoothGatt.mAddr);
                            this.arraylistName.add("" + bluetoothDevice.getName());
                        }
                    } else {
                        this.arraylistName.add(SetqicaiActivity.this.mMyApplication.mMyExpandableListAdapter.DBdata.get(myBluetoothGatt.mAddr).name);
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
            View viewInflate = SetqicaiActivity.this.mInflator.inflate(R.layout.wheel_item, (ViewGroup) null);
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
            String name = myBluetoothGatt.mLEdevice.getName();
            if (name.contains("Triones:") || name.contains("Triones#")) {
                this.mDataArrayList.add(myBluetoothGatt);
            }
        }
        this.myWheelViewAdapter.setData(this.mDataArrayList);
        this.timing_wheelview.setViewAdapter(this.myWheelViewAdapter);
        this.timing_wheelview.setVisibleItems(1);
    }
}
