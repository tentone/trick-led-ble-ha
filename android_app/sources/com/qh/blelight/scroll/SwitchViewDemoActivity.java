package com.qh.blelight.scroll;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.qh.blelight.MainActivity;
import com.qh.onehlight.R;

/* JADX INFO: loaded from: classes.dex */
public class SwitchViewDemoActivity extends Activity implements OnViewChangeListener, View.OnClickListener {
    private Button btn_over;
    private FrameLayout help_f1;
    private FrameLayout help_f2;
    private FrameLayout help_f3;
    private FrameLayout help_f4;
    private FrameLayout help_f5;
    private FrameLayout help_f6;
    private FrameLayout help_f7;
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;
    private ImageView img5;
    private ImageView img6;
    private ImageView img7;
    private int mCurSel;
    private ImageView[] mImageViews;
    private Resources mResources;
    private MyScrollLayout mScrollLayout;
    private int mViewCount;
    private SharedPreferences setting;

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main);
        this.mResources = getResources();
        this.setting = getSharedPreferences("BleLight", 0);
        init();
        Log.v("@@@@@@", "this is in  SwitchViewDemoActivity onClick()");
    }

    private void init() {
        this.btn_over = (Button) findViewById(R.id.btn_over);
        this.mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);
        int childCount = this.mScrollLayout.getChildCount();
        this.mViewCount = childCount;
        this.mImageViews = new ImageView[childCount];
        for (int i = 0; i < this.mViewCount; i++) {
            this.mImageViews[i] = (ImageView) linearLayout.getChildAt(i);
            this.mImageViews[i].setEnabled(true);
            this.mImageViews[i].setOnClickListener(this);
            this.mImageViews[i].setTag(Integer.valueOf(i));
        }
        this.mCurSel = 0;
        this.mImageViews[0].setEnabled(false);
        this.mScrollLayout.SetOnViewChangeListener(this);
        this.help_f1 = (FrameLayout) findViewById(R.id.help_f1);
        this.help_f2 = (FrameLayout) findViewById(R.id.help_f2);
        this.help_f3 = (FrameLayout) findViewById(R.id.help_f3);
        this.help_f4 = (FrameLayout) findViewById(R.id.help_f4);
        this.help_f5 = (FrameLayout) findViewById(R.id.help_f5);
        this.help_f6 = (FrameLayout) findViewById(R.id.help_f6);
        this.help_f7 = (FrameLayout) findViewById(R.id.help_f7);
        this.img1 = (ImageView) findViewById(R.id.img_help1);
        this.img2 = (ImageView) findViewById(R.id.img_help2);
        this.img3 = (ImageView) findViewById(R.id.img_help3);
        this.img4 = (ImageView) findViewById(R.id.img_help4);
        this.img5 = (ImageView) findViewById(R.id.img_help5);
        this.img6 = (ImageView) findViewById(R.id.img_help6);
        this.img7 = (ImageView) findViewById(R.id.img_help7);
        if ("cn".equals(this.mResources.getString(R.string.language))) {
            this.img1.setImageResource(R.drawable.ic_help_cn1);
            this.img2.setImageResource(R.drawable.ic_help_cn2);
            this.img3.setImageResource(R.drawable.ic_help_cn3);
            this.img4.setImageResource(R.drawable.ic_help_cn4);
            this.img5.setImageResource(R.drawable.ic_help_cn5);
            this.img6.setImageResource(R.drawable.ic_help_cn6);
            this.img7.setImageResource(R.drawable.ic_help_cn7);
        }
        this.btn_over.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.scroll.SwitchViewDemoActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!SwitchViewDemoActivity.this.setting.getBoolean("isfrist", false)) {
                    SharedPreferences.Editor editorEdit = SwitchViewDemoActivity.this.setting.edit();
                    editorEdit.putBoolean("isfrist", true);
                    editorEdit.commit();
                    SwitchViewDemoActivity.this.startActivity(new Intent(SwitchViewDemoActivity.this, (Class<?>) MainActivity.class));
                }
                SwitchViewDemoActivity.this.finish();
            }
        });
    }

    private void setCurPoint(int i) {
        int i2;
        if (i < 0 || i > this.mViewCount - 1 || (i2 = this.mCurSel) == i) {
            return;
        }
        this.mImageViews[i2].setEnabled(true);
        this.mImageViews[i].setEnabled(false);
        this.mCurSel = i;
    }

    @Override // com.qh.blelight.scroll.OnViewChangeListener
    public void OnViewChange(int i) {
        setCurPoint(i);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int iIntValue = ((Integer) view.getTag()).intValue();
        setCurPoint(iIntValue);
        this.mScrollLayout.snapToScreen(iIntValue);
    }
}
