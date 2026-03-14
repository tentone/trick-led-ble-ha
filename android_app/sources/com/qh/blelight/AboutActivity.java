package com.qh.blelight;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.qh.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class AboutActivity extends Activity {
    private RelativeLayout lin_back;
    private MyApplication mMyApplication;
    private RelativeLayout rel_main;
    private TextView tv_version;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_about);
        this.lin_back = (RelativeLayout) findViewById(R.id.lin_back);
        this.mMyApplication = (MyApplication) getApplication();
        this.rel_main = (RelativeLayout) findViewById(R.id.rel_main);
        this.lin_back.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.AboutActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AboutActivity.this.finish();
            }
        });
        TextView textView = (TextView) findViewById(R.id.tv_version);
        this.tv_version = textView;
        textView.setText(getVersion());
        if (this.mMyApplication.bgsrc.length > this.mMyApplication.typebg) {
            this.rel_main.setBackgroundResource(this.mMyApplication.bgsrc[this.mMyApplication.typebg]);
        }
    }

    public String getVersion() {
        try {
            return getString(R.string.Version) + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return getString(R.string.not_find_version);
        }
    }
}
