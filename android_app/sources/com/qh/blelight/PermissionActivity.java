package com.qh.blelight;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.qh.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class PermissionActivity extends Activity {
    private Handler mHandler = new Handler();

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_permission);
        getWindow().setFlags(1024, 1024);
    }

    @Override // android.app.Activity
    protected void onResume() {
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            startActivity(new Intent(this, (Class<?>) MainActivity.class));
            finish();
        }
        super.onResume();
    }
}
