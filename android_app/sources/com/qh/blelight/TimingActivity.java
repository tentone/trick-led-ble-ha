package com.qh.blelight;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.qh.WheelView.OnWheelScrollListener;
import com.qh.WheelView.WheelView;
import com.qh.WheelView.WheelViewAdapter;
import com.qh.data.TimeData;
import com.qh.onehlight.R;
import com.qh.tools.DBAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class TimingActivity extends Activity {
    public static final int EVERYDAY = 254;
    public static final int FRI = 32;
    public static final int MON = 2;
    public static final int SAT = 64;
    public static final int SUN = 128;
    public static final int THU = 16;
    public static final int TUE = 4;
    public static final int WED = 8;
    public DBAdapter dbAdapter;
    public Context mContext;
    private LayoutInflater mInflator;
    public Resources mResources;
    public TimeAdapter mTimeAdapter;
    public MyApplication myApplication;
    private MyWheelViewAdapter myWheelViewAdapter;
    public byte[] timeData;
    private ListView time_list;
    private WheelView timing_wheelview;
    public String mTimerFormat = "%02d:%02d";
    private String selectMac = "";
    private ArrayList mDataArrayList = new ArrayList();
    public Hashtable<Integer, TimeData> TimeDatas = new Hashtable<>();
    public Handler timingHander = new Handler(new Handler.Callback() { // from class: com.qh.blelight.TimingActivity.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            MyBluetoothGatt myBluetoothGatt;
            if (message == null) {
                return false;
            }
            Log.e("999 ", "arg0.what=" + message.what);
            if (message.what == 3) {
                String string = message.getData().getString("deviceAddr", "");
                if (!"".equals(string) && !"null".equals(string)) {
                    if (!string.equals(TimingActivity.this.selectMac)) {
                        TimingActivity.this.time_list.setVisibility(4);
                        TimingActivity.this.setData();
                        if (TimingActivity.this.timing_wheelview != null) {
                            TimingActivity.this.timing_wheelview.setCurrentItem(0);
                        }
                    } else {
                        if (TimingActivity.this.myApplication != null && TimingActivity.this.myApplication.mBluetoothLeService != null && TimingActivity.this.myApplication.mBluetoothLeService.MyBluetoothGatts != null && TimingActivity.this.myApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(TimingActivity.this.selectMac) && (myBluetoothGatt = TimingActivity.this.myApplication.mBluetoothLeService.MyBluetoothGatts.get(TimingActivity.this.selectMac)) != null) {
                            TimingActivity.this.selectMac = myBluetoothGatt.mAddr;
                            Log.e("", "" + myBluetoothGatt.timedata.length);
                            TimingActivity.this.timeData = myBluetoothGatt.timedata;
                        }
                        TimingActivity.this.setData();
                        if (TimingActivity.this.mTimeAdapter != null) {
                            TimingActivity.this.mTimeAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Log.e("000 ", "arg0.what=" + message.what);
                    return false;
                }
            }
            if (message.what == 4) {
                TimingActivity.this.selectMac = "";
                TimingActivity.this.time_list.setVisibility(4);
                TimingActivity.this.setData();
                if (TimingActivity.this.timing_wheelview != null) {
                    TimingActivity.this.timing_wheelview.setCurrentItem(0);
                }
                if (TimingActivity.this.mTimeAdapter != null) {
                    TimingActivity.this.mTimeAdapter.notifyDataSetChanged();
                }
            }
            return false;
        }
    });

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_timing);
        this.mInflator = getLayoutInflater();
        this.mResources = getResources();
        this.myApplication = (MyApplication) getApplication();
        this.mContext = getApplicationContext();
        DBAdapter dBAdapterInit = DBAdapter.init(this);
        this.dbAdapter = dBAdapterInit;
        dBAdapterInit.open();
        this.mTimeAdapter = new TimeAdapter();
        this.timing_wheelview = (WheelView) findViewById(R.id.timing_wheelview);
        ListView listView = (ListView) findViewById(R.id.time_list);
        this.time_list = listView;
        listView.setAdapter((ListAdapter) this.mTimeAdapter);
        this.time_list.setVisibility(4);
        setData();
        this.timing_wheelview.setVisibleItems(1);
        this.time_list.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.qh.blelight.TimingActivity.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if ("".equals(TimingActivity.this.selectMac) || TimingActivity.this.selectMac == null) {
                    Toast.makeText(TimingActivity.this.mContext, TimingActivity.this.mResources.getString(R.string.select_device), 0).show();
                    return;
                }
                Intent intent = new Intent(TimingActivity.this, (Class<?>) PopActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putInt("index", i);
                bundle2.putString("MAC", TimingActivity.this.selectMac);
                if (TimingActivity.this.TimeDatas.containsKey(Integer.valueOf(i))) {
                    TimeData timeData = TimingActivity.this.TimeDatas.get(Integer.valueOf(i));
                    if (timeData != null) {
                        bundle2.putInt("day", timeData.day);
                        bundle2.putInt("hour", timeData.hour);
                        bundle2.putInt("minite", timeData.minite);
                        bundle2.putBoolean("isNO", timeData.isNO);
                        bundle2.putBoolean("isWork", timeData.isWork);
                    }
                } else {
                    bundle2.putInt("day", 0);
                    bundle2.putInt("hour", 0);
                    bundle2.putInt("minite", 0);
                    bundle2.putBoolean("isNO", false);
                    bundle2.putBoolean("isWork", false);
                }
                intent.putExtras(bundle2);
                TimingActivity.this.startActivityForResult(intent, 100);
                Log.e("", "selectMac = " + TimingActivity.this.selectMac);
            }
        });
        this.timing_wheelview.addScrollingListener(new OnWheelScrollListener() { // from class: com.qh.blelight.TimingActivity.3
            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingStarted(WheelView wheelView) {
            }

            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingFinished(WheelView wheelView) {
                int currentItem = wheelView.getCurrentItem() - 1;
                if (TimingActivity.this.mDataArrayList.size() > currentItem) {
                    if (currentItem < 0) {
                        TimingActivity.this.selectMac = "";
                        TimingActivity.this.time_list.setVisibility(4);
                    } else {
                        TimingActivity.this.time_list.setVisibility(0);
                        MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) TimingActivity.this.mDataArrayList.get(currentItem);
                        if (myBluetoothGatt != null) {
                            TimingActivity.this.selectMac = myBluetoothGatt.mAddr;
                            Log.e("", " " + myBluetoothGatt.timedata.length);
                            TimingActivity.this.timeData = myBluetoothGatt.timedata;
                            myBluetoothGatt.setDate();
                        }
                        TimingActivity.this.setTimedata();
                        TimingActivity.this.mTimeAdapter.notifyDataSetChanged();
                    }
                }
                if ("".equals(TimingActivity.this.selectMac)) {
                    TimingActivity.this.TimeDatas.clear();
                    TimingActivity.this.mTimeAdapter.notifyDataSetChanged();
                }
            }
        });
        this.myApplication.TimingHandler = this.timingHander;
    }

    @Override // android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        MyBluetoothGatt myBluetoothGatt;
        Log.e("", "requestCode " + i);
        if (this.myApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(this.selectMac) && (myBluetoothGatt = this.myApplication.mBluetoothLeService.MyBluetoothGatts.get(this.selectMac)) != null) {
            this.selectMac = myBluetoothGatt.mAddr;
            Log.e("", "" + myBluetoothGatt.timedata.length);
            this.timeData = myBluetoothGatt.timedata;
        }
        setData();
        this.mTimeAdapter.notifyDataSetChanged();
        super.onActivityResult(i, i2, intent);
    }

    @Override // android.app.Activity
    protected void onResume() {
        setData();
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setData() {
        this.myWheelViewAdapter = new MyWheelViewAdapter();
        this.mDataArrayList.clear();
        MyApplication myApplication = this.myApplication;
        if (myApplication == null || myApplication.mBluetoothLeService == null || this.myApplication.mBluetoothLeService.MyBluetoothGatts == null) {
            return;
        }
        try {
            Iterator<String> it = this.myApplication.mBluetoothLeService.MyBluetoothGatts.keySet().iterator();
            while (it.hasNext()) {
                this.mDataArrayList.add(this.myApplication.mBluetoothLeService.MyBluetoothGatts.get(it.next()));
            }
            this.TimeDatas.clear();
            setTimedata();
            this.myWheelViewAdapter.setData(this.mDataArrayList);
            this.timing_wheelview.setViewAdapter(this.myWheelViewAdapter);
        } catch (Exception unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTimedata() {
        if (this.timeData != null) {
            for (int i = 0; i < 6; i++) {
                TimeData timeData = new TimeData();
                int i2 = i * 14;
                timeData.day = this.timeData[i2 + 8] & 255;
                timeData.hour = this.timeData[i2 + 5];
                timeData.minite = this.timeData[i2 + 6];
                if ((this.timeData[i2 + 1] & 255) == 240) {
                    timeData.isWork = true;
                }
                if ((this.timeData[i2 + 14] & 255) == 240) {
                    timeData.isNO = true;
                }
                this.TimeDatas.put(Integer.valueOf(i), timeData);
            }
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (4 == i) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(268435456);
            intent.addCategory("android.intent.category.HOME");
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    private class TimeAdapter extends BaseAdapter {
        private LayoutInflater mInflator;
        private ArrayList mArrayList = new ArrayList();
        private HashMap<Integer, View> mItemHashMap = new HashMap<>();

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        public TimeAdapter() {
            this.mInflator = TimingActivity.this.getLayoutInflater();
            for (int i = 0; i < 6; i++) {
                this.mArrayList.add(Integer.valueOf(i));
            }
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mArrayList.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            if (this.mItemHashMap.containsKey(Integer.valueOf(i))) {
                return this.mItemHashMap.get(Integer.valueOf(i));
            }
            return null;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View viewInflate = this.mInflator.inflate(R.layout.time_item, (ViewGroup) null);
            TimeViewHolder timeViewHolder = (TimeViewHolder) viewInflate.getTag();
            if (timeViewHolder == null) {
                timeViewHolder = TimingActivity.this.new TimeViewHolder();
                timeViewHolder.tx_timing = (TextView) viewInflate.findViewById(R.id.tx_timing);
                timeViewHolder.operation_switch = (TextView) viewInflate.findViewById(R.id.operation_switch);
                timeViewHolder.tx_day = (TextView) viewInflate.findViewById(R.id.tx_day);
                timeViewHolder.time_open = (ImageView) viewInflate.findViewById(R.id.time_open);
                timeViewHolder.img1 = (ImageView) viewInflate.findViewById(R.id.img1);
            }
            MyOnClickListener myOnClickListener = TimingActivity.this.new MyOnClickListener(i, timeViewHolder);
            timeViewHolder.time_open.setOnClickListener(myOnClickListener);
            timeViewHolder.tx_timing.setText("00:00");
            if (TimingActivity.this.TimeDatas.containsKey(Integer.valueOf(i))) {
                TimeData timeData = TimingActivity.this.TimeDatas.get(Integer.valueOf(i));
                if (timeData != null) {
                    String str = String.format(TimingActivity.this.mTimerFormat, Integer.valueOf(timeData.hour), Integer.valueOf(timeData.minite));
                    timeViewHolder.tx_timing.setText("" + str);
                    timeViewHolder.tx_day.setText("" + TimingActivity.this.getDay(timeData.day));
                    if (timeData.isWork) {
                        timeViewHolder.time_open.setImageResource(R.drawable.ic_open);
                        timeViewHolder.time_open.setOnClickListener(myOnClickListener.setIsWork(true));
                    } else {
                        timeViewHolder.time_open.setImageResource(R.drawable.ic_close);
                    }
                    if (timeData.isNO) {
                        timeViewHolder.operation_switch.setText(TimingActivity.this.mResources.getString(R.string.NO));
                        timeViewHolder.img1.setImageResource(R.drawable.ic_light_n);
                    } else {
                        timeViewHolder.operation_switch.setText(TimingActivity.this.mResources.getString(R.string.OFF));
                        timeViewHolder.img1.setImageResource(R.drawable.ic_light_u);
                    }
                }
            } else {
                String str2 = String.format(TimingActivity.this.mTimerFormat, 0, 0);
                timeViewHolder.tx_timing.setText("" + str2);
                timeViewHolder.tx_day.setText("");
                timeViewHolder.time_open.setImageResource(R.drawable.ic_close);
                timeViewHolder.operation_switch.setText(TimingActivity.this.mResources.getString(R.string.OFF));
            }
            this.mItemHashMap.put(Integer.valueOf(i), viewInflate);
            viewInflate.setTag(timeViewHolder);
            return viewInflate;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getDay(int i) {
        String str = "";
        if ((i & 2) == 2) {
            str = "" + this.mResources.getString(R.string.MON);
        }
        if ((i & 4) == 4) {
            str = str + " " + this.mResources.getString(R.string.TUE);
        }
        if ((i & 8) == 8) {
            str = str + " " + this.mResources.getString(R.string.WED);
        }
        if ((i & 16) == 16) {
            str = str + " " + this.mResources.getString(R.string.THU);
        }
        if ((i & 32) == 32) {
            str = str + " " + this.mResources.getString(R.string.FRI);
        }
        if ((i & 64) == 64) {
            str = str + " " + this.mResources.getString(R.string.SAT);
        }
        if ((i & 128) == 128) {
            str = str + " " + this.mResources.getString(R.string.SUN);
        }
        return i == 254 ? this.mResources.getString(R.string.EVERYDAY) : str;
    }

    public class TimeViewHolder {
        ImageView img1;
        TextView operation_switch;
        ImageView time_open;
        TextView tx_day;
        TextView tx_timing;

        public TimeViewHolder() {
        }
    }

    private class MyOnClickListener implements View.OnClickListener {
        private boolean isWork = false;
        private TimeViewHolder mTimeViewHolder;
        private int position;

        public MyOnClickListener(int i, TimeViewHolder timeViewHolder) {
            this.position = i;
            this.mTimeViewHolder = timeViewHolder;
        }

        public MyOnClickListener setIsWork(boolean z) {
            this.isWork = z;
            return this;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            final MyBluetoothGatt myBluetoothGatt;
            if (TimingActivity.this.myApplication == null || TimingActivity.this.myApplication.mBluetoothLeService == null || TimingActivity.this.myApplication.mBluetoothLeService.MyBluetoothGatts == null) {
                return;
            }
            if (TimingActivity.this.myApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(TimingActivity.this.selectMac) && (myBluetoothGatt = TimingActivity.this.myApplication.mBluetoothLeService.MyBluetoothGatts.get(TimingActivity.this.selectMac)) != null) {
                if (this.isWork) {
                    myBluetoothGatt.timedata[(this.position * 14) + 1] = 15;
                    this.mTimeViewHolder.time_open.setImageResource(R.drawable.ic_close);
                    this.isWork = false;
                } else {
                    myBluetoothGatt.timedata[(this.position * 14) + 1] = -16;
                    this.mTimeViewHolder.time_open.setImageResource(R.drawable.ic_open);
                    this.isWork = true;
                }
                TimeData timeData = TimingActivity.this.TimeDatas.get(Integer.valueOf(this.position));
                timeData.isWork = this.isWork;
                if (myBluetoothGatt.isTriones && myBluetoothGatt.isLong) {
                    TimingActivity.this.timingHander.postDelayed(new Runnable() { // from class: com.qh.blelight.TimingActivity.MyOnClickListener.1
                        @Override // java.lang.Runnable
                        public void run() {
                            myBluetoothGatt.setDayData();
                        }
                    }, 100L);
                } else {
                    myBluetoothGatt.setNewTime(this.position, this.isWork, timeData.hour, timeData.minite, 0, (byte) (timeData.day & 255), timeData.isNO);
                    myBluetoothGatt.sendNewTime(this.position, this.isWork);
                }
                TimingActivity.this.timingHander.postDelayed(new Runnable() { // from class: com.qh.blelight.TimingActivity.MyOnClickListener.2
                    @Override // java.lang.Runnable
                    public void run() {
                        myBluetoothGatt.setDate();
                    }
                }, 4000L);
            }
            Log.e("", "position = " + this.position + " isWork = " + this.isWork);
        }
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
            this.arraylistName.add("" + TimingActivity.this.mResources.getString(R.string.huandong));
            for (int i = 0; i < arrayList.size(); i++) {
                MyBluetoothGatt myBluetoothGatt = (MyBluetoothGatt) arrayList.get(i);
                if (myBluetoothGatt != null) {
                    if (TimingActivity.this.myApplication.mMyExpandableListAdapter.DBdata.containsKey(myBluetoothGatt.mAddr)) {
                        this.arraylistName.add(TimingActivity.this.myApplication.mMyExpandableListAdapter.DBdata.get(myBluetoothGatt.mAddr).name);
                    } else if (TimingActivity.this.myApplication.mBluetoothLeService.mDevices.containsKey(myBluetoothGatt.mAddr)) {
                        BluetoothDevice bluetoothDevice = TimingActivity.this.myApplication.mBluetoothLeService.mDevices.get(myBluetoothGatt.mAddr);
                        this.arraylistName.add("" + bluetoothDevice.getName());
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
            View viewInflate = TimingActivity.this.mInflator.inflate(R.layout.wheel_item, (ViewGroup) null);
            TextView textView = (TextView) viewInflate.findViewById(R.id.tx_lable);
            ((ImageView) viewInflate.findViewById(R.id.img_conn)).setVisibility(8);
            textView.setText("" + this.arraylistName.get(i));
            return viewInflate;
        }
    }
}
