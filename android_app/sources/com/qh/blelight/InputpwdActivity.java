package com.qh.blelight;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.qh.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class InputpwdActivity extends MyActivity {
    public TextView Cancel;
    public TextView OK;
    public String addr = "";
    public Context context;
    public EditText et_pwd;
    public Resources mResources;
    public MyApplication myApplication;

    @Override // com.qh.blelight.MyActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pop_inputpwd);
        setTintColor(0);
        this.addr = getIntent().getStringExtra("addr");
        this.myApplication = (MyApplication) getApplication();
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
        this.et_pwd = (EditText) findViewById(R.id.et_pwd);
        this.OK = (TextView) findViewById(R.id.OK);
        this.Cancel = (TextView) findViewById(R.id.Cancel);
        this.OK.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.InputpwdActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.e("addr", InputpwdActivity.this.addr);
                String string = InputpwdActivity.this.et_pwd.getText().toString();
                if (string.length() != 4) {
                    Toast.makeText(InputpwdActivity.this.context, InputpwdActivity.this.mResources.getString(R.string.inputpwd), 1).show();
                } else {
                    InputpwdActivity.this.myApplication.checkpwd(InputpwdActivity.this.addr, string);
                    InputpwdActivity.this.finish();
                }
            }
        });
        this.Cancel.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.InputpwdActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                InputpwdActivity.this.myApplication.disconn(InputpwdActivity.this.addr);
                InputpwdActivity.this.finish();
            }
        });
    }
}
