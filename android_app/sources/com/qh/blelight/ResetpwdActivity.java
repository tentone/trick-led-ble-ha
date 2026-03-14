package com.qh.blelight;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.qh.blelight.BluetoothLeService;
import com.qh.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class ResetpwdActivity extends MyActivity {
    public TextView Cancel;
    public TextView OK;
    public Context context;
    public EditText et_pwd1;
    public EditText et_pwd2;
    public Resources mResources;
    public MyApplication myApplication;
    public String addr = "";
    public BluetoothLeService.Resetpwd mresetpwd = new BluetoothLeService.Resetpwd() { // from class: com.qh.blelight.ResetpwdActivity.1
        @Override // com.qh.blelight.BluetoothLeService.Resetpwd
        public void resetpwd(String str, int i, String str2) {
            ResetpwdActivity.this.uiHandler.sendEmptyMessage(i);
        }
    };
    public Handler uiHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.ResetpwdActivity.2
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                ResetpwdActivity resetpwdActivity = ResetpwdActivity.this;
                resetpwdActivity.showmsg(resetpwdActivity.mResources.getString(R.string.setfalse));
                return false;
            }
            if (i == 1) {
                ResetpwdActivity resetpwdActivity2 = ResetpwdActivity.this;
                resetpwdActivity2.showmsg(resetpwdActivity2.mResources.getString(R.string.setok));
                return false;
            }
            if (i != 2) {
                return false;
            }
            ResetpwdActivity resetpwdActivity3 = ResetpwdActivity.this;
            resetpwdActivity3.showmsg(resetpwdActivity3.mResources.getString(R.string.setfalse2));
            return false;
        }
    });

    @Override // com.qh.blelight.MyActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pop_resetpwd);
        setTintColor(0);
        this.addr = getIntent().getStringExtra("addr");
        MyApplication myApplication = (MyApplication) getApplication();
        this.myApplication = myApplication;
        myApplication.mBluetoothLeService.mresetpwd = this.mresetpwd;
        this.context = getApplicationContext();
        this.mResources = getResources();
        init();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (4 == i) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    private void init() {
        this.et_pwd1 = (EditText) findViewById(R.id.et_pwd1);
        this.et_pwd2 = (EditText) findViewById(R.id.et_pwd2);
        this.OK = (TextView) findViewById(R.id.OK);
        this.Cancel = (TextView) findViewById(R.id.Cancel);
        this.OK.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.ResetpwdActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.e("addr", ResetpwdActivity.this.addr);
                String string = ResetpwdActivity.this.et_pwd1.getText().toString();
                if (string.length() != 4) {
                    Toast.makeText(ResetpwdActivity.this.context, ResetpwdActivity.this.mResources.getString(R.string.inputpwd), 1).show();
                } else {
                    if (!string.equals(ResetpwdActivity.this.et_pwd2.getText().toString())) {
                        Toast.makeText(ResetpwdActivity.this.context, ResetpwdActivity.this.mResources.getString(R.string.dissimilarity), 1).show();
                        return;
                    }
                    Log.e("pwd1", string);
                    ResetpwdActivity.this.myApplication.resetpwd(ResetpwdActivity.this.addr, string);
                    ResetpwdActivity.this.finish();
                }
            }
        });
        this.Cancel.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.ResetpwdActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ResetpwdActivity.this.finish();
            }
        });
    }

    public void showmsg(String str) {
        Toast.makeText(this.context, str, 0).show();
    }
}
