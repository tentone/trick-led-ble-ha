package com.qh.blelight;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.qh.onehlight.R;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class UnlawfulActivity extends Activity {
    private TextView OK;
    private ListView error_list;
    private LayoutInflater mInflator;
    public MyApplication mMyApplication;
    public Resources mResources;
    private ArrayList<BluetoothDevice> errors = new ArrayList<>();
    private Handler msgHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.UnlawfulActivity.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what != 1) {
                return false;
            }
            UnlawfulActivity.this.updata();
            return false;
        }
    });
    private BaseAdapter myAdapter = new BaseAdapter() { // from class: com.qh.blelight.UnlawfulActivity.3
        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) UnlawfulActivity.this.errors.get(i);
            if (view != null) {
                return view;
            }
            View viewInflate = UnlawfulActivity.this.mInflator.inflate(R.layout.error_item, (ViewGroup) null);
            ((TextView) viewInflate.findViewById(R.id.tv_name)).setText("" + bluetoothDevice.getName());
            return viewInflate;
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return Integer.valueOf(i);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return UnlawfulActivity.this.errors.size();
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.pop_unlawful);
        this.mResources = getResources();
        this.mInflator = getLayoutInflater();
        MyApplication myApplication = (MyApplication) getApplication();
        this.mMyApplication = myApplication;
        myApplication.errorHandler = this.msgHandler;
        init();
    }

    private void init() {
        ListView listView = (ListView) findViewById(R.id.error_list);
        this.error_list = listView;
        listView.setAdapter((ListAdapter) this.myAdapter);
        updata();
        TextView textView = (TextView) findViewById(R.id.OK);
        this.OK = textView;
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.UnlawfulActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                UnlawfulActivity.this.mMyApplication.isshow = false;
                UnlawfulActivity.this.finish();
            }
        });
    }

    public void updata() {
        this.errors.clear();
        Iterator<String> it = this.mMyApplication.errorDevices.keySet().iterator();
        while (it.hasNext()) {
            BluetoothDevice bluetoothDevice = this.mMyApplication.errorDevices.get(it.next());
            if (bluetoothDevice != null) {
                this.errors.add(bluetoothDevice);
            }
        }
        this.myAdapter.notifyDataSetChanged();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.mMyApplication.isshow = false;
        super.onDestroy();
    }
}
