package com.qh.blelight;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.qh.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class ChangeBgActivity extends Activity {
    private ImageView img_1;
    private ImageView img_2;
    private ImageView img_3;
    private ImageView img_4;
    private ImageView img_5;
    private RelativeLayout lin_back;
    private MyApplication mMyApplication;
    public View.OnClickListener myOnClickListener = new View.OnClickListener() { // from class: com.qh.blelight.ChangeBgActivity.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_1 /* 2131230859 */:
                    ChangeBgActivity.this.mMyApplication.setBG(1);
                    break;
                case R.id.img_2 /* 2131230860 */:
                    ChangeBgActivity.this.mMyApplication.setBG(2);
                    break;
                case R.id.img_3 /* 2131230861 */:
                    ChangeBgActivity.this.mMyApplication.setBG(3);
                    break;
                case R.id.img_4 /* 2131230862 */:
                    ChangeBgActivity.this.mMyApplication.setBG(4);
                    break;
                case R.id.img_5 /* 2131230863 */:
                    ChangeBgActivity.this.mMyApplication.setBG(0);
                    break;
            }
            ChangeBgActivity.this.finish();
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_change);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.lin_back);
        this.lin_back = relativeLayout;
        relativeLayout.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.ChangeBgActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ChangeBgActivity.this.finish();
            }
        });
        this.mMyApplication = (MyApplication) getApplication();
        this.img_1 = (ImageView) findViewById(R.id.img_1);
        this.img_2 = (ImageView) findViewById(R.id.img_2);
        this.img_3 = (ImageView) findViewById(R.id.img_3);
        this.img_4 = (ImageView) findViewById(R.id.img_4);
        this.img_5 = (ImageView) findViewById(R.id.img_5);
        this.img_1.setOnClickListener(this.myOnClickListener);
        this.img_2.setOnClickListener(this.myOnClickListener);
        this.img_3.setOnClickListener(this.myOnClickListener);
        this.img_4.setOnClickListener(this.myOnClickListener);
        this.img_5.setOnClickListener(this.myOnClickListener);
    }
}
