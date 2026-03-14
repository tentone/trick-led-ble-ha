package com.qh.blelight;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import com.qh.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class FristActivity extends Activity {
    private Handler mHandler = new Handler();
    private SharedPreferences setting;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_frist);
        getWindow().setFlags(1024, 1024);
        this.setting = getSharedPreferences("BleLight", 0);
        this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.FristActivity.1
            @Override // java.lang.Runnable
            public void run() {
                FristActivity.this.startActivity(new Intent(FristActivity.this, (Class<?>) MainActivity.class));
                FristActivity.this.finish();
            }
        }, 1800L);
    }
}
