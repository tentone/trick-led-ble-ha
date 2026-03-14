package com.qh.blelight;

import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qh.WheelView.OnWheelScrollListener;
import com.qh.WheelView.WheelView;
import com.qh.WheelView.WheelViewAdapter;
import com.qh.onehlight.R;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class PopActivity extends MyActivity {
    public static final int EVERYDAY = 254;
    public static final byte EVERY_DAY = 127;
    public static final int FRI = 32;
    public static final String[] HOUR = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    public static final String[] MINUTE = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
    public static final int MON = 2;
    public static final int SAT = 64;
    public static final int SUN = 128;
    public static final int THU = 16;
    public static final int TUE = 4;
    public static final int WED = 8;
    public TextView Cencel;
    public TextView OK;
    private MyWheelViewAdapter hMyWheelViewAdapter;
    private WheelView hour_wheelview;
    public ImageView img_open;
    public ImageView img_time_open;
    private LayoutInflater mInflator;
    public MyApplication mMyApplication;
    public MyBluetoothGatt mMyBluetoothGatt;
    private MyWheelViewAdapter mMyWheelViewAdapter;
    public Resources mResources;
    private WheelView minute_wheelview;
    public String msg;
    public RelativeLayout rel11;
    public TextView showTX;
    public TextView tx_fri;
    public TextView tx_mon;
    private TextView tx_msg;
    public TextView tx_sat;
    public TextView tx_sun;
    public TextView tx_thu;
    public TextView tx_tue;
    public TextView tx_wed;
    public Handler mHandler = new Handler();
    public HashMap<Integer, Boolean> mSelectDays = new HashMap<>();
    private int dayData = 0;
    private int hourData = 0;
    private int miniteData = 0;
    private boolean isNO = false;
    private boolean isWork = false;
    public String mac = "";
    public int index = 0;
    public Handler popHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.PopActivity.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what != 4) {
                return false;
            }
            if (!PopActivity.this.mac.equals(message.getData().getString("deviceAddr"))) {
                return false;
            }
            PopActivity.this.finish();
            return false;
        }
    });
    private View.OnClickListener myOnClickListener = new View.OnClickListener() { // from class: com.qh.blelight.PopActivity.8
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            boolean zBooleanValue = PopActivity.this.mSelectDays.containsKey(Integer.valueOf(view.getId())) ? PopActivity.this.mSelectDays.get(Integer.valueOf(view.getId())).booleanValue() : false;
            switch (view.getId()) {
                case R.id.tx_fri /* 2131231116 */:
                    PopActivity popActivity = PopActivity.this;
                    popActivity.showTX = popActivity.tx_fri;
                    break;
                case R.id.tx_mon /* 2131231120 */:
                    PopActivity popActivity2 = PopActivity.this;
                    popActivity2.showTX = popActivity2.tx_mon;
                    break;
                case R.id.tx_sat /* 2131231124 */:
                    PopActivity popActivity3 = PopActivity.this;
                    popActivity3.showTX = popActivity3.tx_sat;
                    break;
                case R.id.tx_sun /* 2131231126 */:
                    PopActivity popActivity4 = PopActivity.this;
                    popActivity4.showTX = popActivity4.tx_sun;
                    break;
                case R.id.tx_thu /* 2131231127 */:
                    PopActivity popActivity5 = PopActivity.this;
                    popActivity5.showTX = popActivity5.tx_thu;
                    break;
                case R.id.tx_tue /* 2131231129 */:
                    PopActivity popActivity6 = PopActivity.this;
                    popActivity6.showTX = popActivity6.tx_tue;
                    break;
                case R.id.tx_wed /* 2131231130 */:
                    PopActivity popActivity7 = PopActivity.this;
                    popActivity7.showTX = popActivity7.tx_wed;
                    break;
            }
            if (zBooleanValue) {
                PopActivity.this.showTX.setBackgroundResource(R.drawable.btn_bg2);
                PopActivity.this.mSelectDays.put(Integer.valueOf(PopActivity.this.showTX.getId()), false);
            } else {
                PopActivity.this.showTX.setBackgroundResource(R.drawable.btn_bg1);
                PopActivity.this.mSelectDays.put(Integer.valueOf(PopActivity.this.showTX.getId()), true);
            }
        }
    };

    @Override // com.qh.blelight.MyActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        Bundle extras;
        super.onCreate(bundle);
        setContentView(R.layout.pop_activity);
        this.mResources = getResources();
        this.mInflator = getLayoutInflater();
        MyApplication myApplication = (MyApplication) getApplication();
        this.mMyApplication = myApplication;
        myApplication.popHandler = this.popHandler;
        Intent intent = getIntent();
        if (intent != null && (extras = intent.getExtras()) != null) {
            this.mac = extras.getString("MAC", "");
            this.index = extras.getInt("index", -1);
            this.dayData = extras.getInt("day", 0);
            this.hourData = extras.getInt("hour", 0);
            this.miniteData = extras.getInt("minite", 0);
            this.isNO = extras.getBoolean("isNO", false);
            this.isWork = extras.getBoolean("isWork", false);
            Log.e("", "mac :" + this.mac + " index = " + this.index);
            if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(this.mac)) {
                this.mMyBluetoothGatt = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(this.mac);
            }
            Calendar calendar = Calendar.getInstance();
            int i = calendar.get(11);
            int i2 = calendar.get(12);
            if (this.hourData <= 0 && this.miniteData <= 0) {
                this.hourData = i;
                this.miniteData = i2;
            }
            Log.e("", "dayData :" + this.dayData + " hourData = " + this.hourData + " miniteData" + this.miniteData);
            StringBuilder sb = new StringBuilder();
            sb.append("isNO :");
            sb.append(this.isNO);
            sb.append(" isWork = ");
            sb.append(this.isWork);
            Log.e("", sb.toString());
            Log.e("", "isNO :" + this.isNO + " isWork = " + this.isWork);
        }
        this.hour_wheelview = (WheelView) findViewById(R.id.hour_wheelview);
        this.minute_wheelview = (WheelView) findViewById(R.id.minute_wheelview);
        this.hMyWheelViewAdapter = new MyWheelViewAdapter();
        this.mMyWheelViewAdapter = new MyWheelViewAdapter();
        this.hMyWheelViewAdapter.setData(HOUR);
        this.mMyWheelViewAdapter.setData(MINUTE);
        this.hour_wheelview.setViewAdapter(this.hMyWheelViewAdapter);
        this.minute_wheelview.setViewAdapter(this.mMyWheelViewAdapter);
        this.hour_wheelview.setVisibleItems(1);
        this.minute_wheelview.setVisibleItems(1);
        this.minute_wheelview.setCyclic(true);
        this.hour_wheelview.setCyclic(true);
        init();
        setDayView();
        setTime();
        setNoAndWork();
        this.hour_wheelview.addScrollingListener(new OnWheelScrollListener() { // from class: com.qh.blelight.PopActivity.2
            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingStarted(WheelView wheelView) {
            }

            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingFinished(WheelView wheelView) {
                PopActivity.this.hourData = wheelView.getCurrentItem();
            }
        });
        this.minute_wheelview.addScrollingListener(new OnWheelScrollListener() { // from class: com.qh.blelight.PopActivity.3
            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingStarted(WheelView wheelView) {
            }

            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingFinished(WheelView wheelView) {
                PopActivity.this.miniteData = wheelView.getCurrentItem();
            }
        });
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        this.tx_mon = (TextView) findViewById(R.id.tx_mon);
        this.tx_wed = (TextView) findViewById(R.id.tx_wed);
        this.tx_thu = (TextView) findViewById(R.id.tx_thu);
        this.tx_tue = (TextView) findViewById(R.id.tx_tue);
        this.tx_fri = (TextView) findViewById(R.id.tx_fri);
        this.tx_sat = (TextView) findViewById(R.id.tx_sat);
        this.tx_sun = (TextView) findViewById(R.id.tx_sun);
        this.img_open = (ImageView) findViewById(R.id.img_open);
        this.img_time_open = (ImageView) findViewById(R.id.img_time_open);
        this.tx_mon.setOnClickListener(this.myOnClickListener);
        this.tx_wed.setOnClickListener(this.myOnClickListener);
        this.tx_thu.setOnClickListener(this.myOnClickListener);
        this.tx_tue.setOnClickListener(this.myOnClickListener);
        this.tx_fri.setOnClickListener(this.myOnClickListener);
        this.tx_sat.setOnClickListener(this.myOnClickListener);
        this.tx_sun.setOnClickListener(this.myOnClickListener);
        this.img_open.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.PopActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                HashMap<Integer, Boolean> map = PopActivity.this.mSelectDays;
                Integer numValueOf = Integer.valueOf(R.id.img_open);
                if (map.containsKey(numValueOf)) {
                    PopActivity popActivity = PopActivity.this;
                    popActivity.isNO = popActivity.mSelectDays.get(numValueOf).booleanValue();
                }
                PopActivity.this.isNO = !r3.isNO;
                PopActivity.this.mSelectDays.put(numValueOf, Boolean.valueOf(PopActivity.this.isNO));
                if (PopActivity.this.isNO) {
                    PopActivity.this.img_open.setImageResource(R.drawable.ic_open);
                } else {
                    PopActivity.this.img_open.setImageResource(R.drawable.ic_close);
                }
            }
        });
        this.img_time_open.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.PopActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                HashMap<Integer, Boolean> map = PopActivity.this.mSelectDays;
                Integer numValueOf = Integer.valueOf(R.id.img_time_open);
                if (map.containsKey(numValueOf)) {
                    PopActivity popActivity = PopActivity.this;
                    popActivity.isWork = popActivity.mSelectDays.get(numValueOf).booleanValue();
                }
                PopActivity.this.isWork = !r3.isWork;
                PopActivity.this.mSelectDays.put(numValueOf, Boolean.valueOf(PopActivity.this.isWork));
                if (PopActivity.this.isWork) {
                    PopActivity.this.img_time_open.setImageResource(R.drawable.ic_open);
                } else {
                    PopActivity.this.img_time_open.setImageResource(R.drawable.ic_close);
                }
            }
        });
        this.OK = (TextView) findViewById(R.id.OK);
        this.Cencel = (TextView) findViewById(R.id.Cancel);
        this.OK.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.PopActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.e("", "dayData :" + PopActivity.this.getDayData() + " hourData = " + PopActivity.this.hourData + " miniteData" + PopActivity.this.miniteData);
                StringBuilder sb = new StringBuilder();
                sb.append("isNO :");
                sb.append(PopActivity.this.isNO);
                sb.append(" isWork = ");
                sb.append(PopActivity.this.isWork);
                Log.e("", sb.toString());
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int i = calendar.get(1) % 100;
                int i2 = calendar.get(2) + 1;
                int i3 = calendar.get(5);
                if (PopActivity.this.mMyBluetoothGatt != null) {
                    PopActivity.this.mMyBluetoothGatt.timedata[(PopActivity.this.index * 14) + 2] = (byte) (i & 255);
                    PopActivity.this.mMyBluetoothGatt.timedata[(PopActivity.this.index * 14) + 3] = (byte) (i2 & 255);
                    PopActivity.this.mMyBluetoothGatt.timedata[(PopActivity.this.index * 14) + 4] = (byte) (i3 & 255);
                    PopActivity.this.mMyBluetoothGatt.timedata[(PopActivity.this.index * 14) + 8] = (byte) (PopActivity.this.getDayData() & 255);
                    PopActivity.this.mMyBluetoothGatt.timedata[(PopActivity.this.index * 14) + 5] = (byte) (PopActivity.this.hourData & 255);
                    PopActivity.this.mMyBluetoothGatt.timedata[(PopActivity.this.index * 14) + 6] = (byte) (PopActivity.this.miniteData & 255);
                    if (PopActivity.this.isWork) {
                        PopActivity.this.mMyBluetoothGatt.timedata[(PopActivity.this.index * 14) + 1] = -16;
                    } else {
                        PopActivity.this.mMyBluetoothGatt.timedata[(PopActivity.this.index * 14) + 1] = 15;
                    }
                    if (PopActivity.this.isNO) {
                        PopActivity.this.mMyBluetoothGatt.timedata[(PopActivity.this.index * 14) + 14] = -16;
                    } else {
                        PopActivity.this.mMyBluetoothGatt.timedata[(PopActivity.this.index * 14) + 14] = 15;
                    }
                    Log.e("22", "11miniteData = " + PopActivity.this.miniteData);
                    if (PopActivity.this.mMyBluetoothGatt.isTriones) {
                        Log.e("--", "88 setNewTime 1");
                        if (PopActivity.this.mMyBluetoothGatt.isLong) {
                            PopActivity.this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.PopActivity.6.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    PopActivity.this.mMyBluetoothGatt.setDayData();
                                }
                            }, 100L);
                        } else {
                            Log.e("--", "88 setNewTime2");
                            PopActivity.this.mMyBluetoothGatt.setNewTime(PopActivity.this.index, PopActivity.this.isWork, (byte) (PopActivity.this.hourData & 255), (byte) (PopActivity.this.miniteData & 255), 0, (byte) (PopActivity.this.getDayData() & 255), PopActivity.this.isNO);
                            PopActivity.this.mMyBluetoothGatt.sendNewTime(PopActivity.this.index);
                        }
                    } else {
                        Log.e("--", "88 setNewTime 3");
                        PopActivity.this.mMyBluetoothGatt.setNewTime(PopActivity.this.index, PopActivity.this.isWork, (byte) (PopActivity.this.hourData & 255), (byte) (PopActivity.this.miniteData & 255), 0, (byte) (PopActivity.this.getDayData() & 255), PopActivity.this.isNO);
                        PopActivity.this.mMyBluetoothGatt.sendNewTime(PopActivity.this.index);
                    }
                    PopActivity.this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.PopActivity.6.2
                        @Override // java.lang.Runnable
                        public void run() {
                            PopActivity.this.mMyBluetoothGatt.setDate();
                        }
                    }, 4000L);
                } else {
                    Log.e("", "mMyBluetoothGatt==null");
                }
                Intent intent = new Intent();
                intent.putExtra("result", "ok");
                PopActivity.this.setResult(-1, intent);
                PopActivity.this.finish();
            }
        });
        this.Cencel.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.PopActivity.7
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", "Cencel");
                PopActivity.this.setResult(-1, intent);
                PopActivity.this.finish();
            }
        });
    }

    private void setDayView() {
        int i = this.dayData & 2;
        Integer numValueOf = Integer.valueOf(R.id.tx_mon);
        if (i == 2) {
            this.tx_mon.setBackgroundResource(R.drawable.btn_bg1);
            this.mSelectDays.put(numValueOf, true);
        } else {
            this.tx_mon.setBackgroundResource(R.drawable.btn_bg2);
            this.mSelectDays.put(numValueOf, false);
        }
        if ((this.dayData & 4) == 4) {
            this.tx_tue.setBackgroundResource(R.drawable.btn_bg1);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_tue), true);
        } else {
            this.tx_tue.setBackgroundResource(R.drawable.btn_bg2);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_tue), false);
        }
        if ((this.dayData & 8) == 8) {
            this.tx_wed.setBackgroundResource(R.drawable.btn_bg1);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_wed), true);
        } else {
            this.tx_wed.setBackgroundResource(R.drawable.btn_bg2);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_wed), false);
        }
        if ((this.dayData & 16) == 16) {
            this.tx_thu.setBackgroundResource(R.drawable.btn_bg1);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_thu), true);
        } else {
            this.tx_thu.setBackgroundResource(R.drawable.btn_bg2);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_thu), false);
        }
        if ((this.dayData & 32) == 32) {
            this.tx_fri.setBackgroundResource(R.drawable.btn_bg1);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_fri), true);
        } else {
            this.tx_fri.setBackgroundResource(R.drawable.btn_bg2);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_fri), false);
        }
        if ((this.dayData & 64) == 64) {
            this.tx_sat.setBackgroundResource(R.drawable.btn_bg1);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_sat), true);
        } else {
            this.tx_sat.setBackgroundResource(R.drawable.btn_bg2);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_sat), false);
        }
        if ((this.dayData & 128) == 128) {
            this.tx_sun.setBackgroundResource(R.drawable.btn_bg1);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_sun), true);
        } else {
            this.tx_sun.setBackgroundResource(R.drawable.btn_bg2);
            this.mSelectDays.put(Integer.valueOf(R.id.tx_sun), false);
        }
    }

    private void setTime() {
        this.hour_wheelview.setCurrentItem(this.hourData);
        this.minute_wheelview.setCurrentItem(this.miniteData);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDayData() {
        HashMap<Integer, Boolean> map = this.mSelectDays;
        Integer numValueOf = Integer.valueOf(R.id.tx_mon);
        int i = (map.containsKey(numValueOf) && this.mSelectDays.get(numValueOf).booleanValue()) ? 2 : 0;
        if (this.mSelectDays.containsKey(Integer.valueOf(R.id.tx_tue)) && this.mSelectDays.get(Integer.valueOf(R.id.tx_tue)).booleanValue()) {
            i += 4;
        }
        if (this.mSelectDays.containsKey(Integer.valueOf(R.id.tx_wed)) && this.mSelectDays.get(Integer.valueOf(R.id.tx_wed)).booleanValue()) {
            i += 8;
        }
        if (this.mSelectDays.containsKey(Integer.valueOf(R.id.tx_thu)) && this.mSelectDays.get(Integer.valueOf(R.id.tx_thu)).booleanValue()) {
            i += 16;
        }
        if (this.mSelectDays.containsKey(Integer.valueOf(R.id.tx_fri)) && this.mSelectDays.get(Integer.valueOf(R.id.tx_fri)).booleanValue()) {
            i += 32;
        }
        if (this.mSelectDays.containsKey(Integer.valueOf(R.id.tx_sat)) && this.mSelectDays.get(Integer.valueOf(R.id.tx_sat)).booleanValue()) {
            i += 64;
        }
        return (this.mSelectDays.containsKey(Integer.valueOf(R.id.tx_sun)) && this.mSelectDays.get(Integer.valueOf(R.id.tx_sun)).booleanValue()) ? i + 128 : i;
    }

    private void setNoAndWork() {
        if (this.isNO) {
            this.img_open.setImageResource(R.drawable.ic_open);
        } else {
            this.img_open.setImageResource(R.drawable.ic_close);
        }
        this.mSelectDays.put(Integer.valueOf(R.id.img_open), Boolean.valueOf(this.isNO));
        if (this.isWork) {
            this.img_time_open.setImageResource(R.drawable.ic_open);
        } else {
            this.img_time_open.setImageResource(R.drawable.ic_close);
        }
        this.mSelectDays.put(Integer.valueOf(R.id.img_time_open), Boolean.valueOf(this.isWork));
    }

    private class MyWheelViewAdapter implements WheelViewAdapter {
        public String[] arraylistName;

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
        }

        public void setData(String[] strArr) {
            this.arraylistName = strArr;
        }

        @Override // com.qh.WheelView.WheelViewAdapter
        public int getItemsCount() {
            return this.arraylistName.length;
        }

        @Override // com.qh.WheelView.WheelViewAdapter
        public View getItem(int i, View view, ViewGroup viewGroup) {
            View viewInflate = PopActivity.this.mInflator.inflate(R.layout.time_wheel_item, (ViewGroup) null);
            TextView textView = (TextView) viewInflate.findViewById(R.id.tx_lable1);
            textView.setText("" + this.arraylistName[i]);
            textView.setTextColor(-1);
            return viewInflate;
        }
    }
}
