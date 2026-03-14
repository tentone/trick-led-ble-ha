package com.qh.blelight;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.qh.WheelView.OnWheelScrollListener;
import com.qh.WheelView.WheelView;
import com.qh.WheelView.WheelViewAdapter;
import com.qh.blelight.WeekAdapter;
import com.qh.onehlight.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class ModActivity extends Activity {
    private ImageView cacheImageView;
    private TextView cacheTextView;
    public ImageView ic_hook1;
    public ImageView ic_hook10;
    public ImageView ic_hook11;
    public ImageView ic_hook12;
    public ImageView ic_hook2;
    public ImageView ic_hook3;
    public ImageView ic_hook4;
    public ImageView ic_hook5;
    public ImageView ic_hook6;
    public ImageView ic_hook7;
    public ImageView ic_hook8;
    public ImageView ic_hook9;
    public ImageView img_mod_color1;
    public ImageView img_mod_color10;
    public ImageView img_mod_color11;
    public ImageView img_mod_color12;
    public ImageView img_mod_color2;
    public ImageView img_mod_color3;
    public ImageView img_mod_color4;
    public ImageView img_mod_color5;
    public ImageView img_mod_color6;
    public ImageView img_mod_color7;
    public ImageView img_mod_color8;
    public ImageView img_mod_color9;
    private ImageView img_x;
    private LinearLayoutManager linearLayoutManager;
    private Context mContext;
    private LayoutInflater mInflator;
    public ModWheelViewAdapter mModWheelViewAdapter;
    public MyApplication mMyApplication;
    public Resources mResources;
    public WheelView myWheelView;
    public RelativeLayout rel_b;
    public RelativeLayout rel_mod1;
    public RelativeLayout rel_mod2;
    public RecyclerView rl_view;
    public SeekBar seekbar_b;
    public SeekBar seekbar_mod_br;
    public SeekBar seekbar_mod_speed;
    public SeekBar seekbar_speed;
    public TextView tv_huancai;
    public TextView tv_qicai;
    private WeekAdapter weekAdapter;
    public String[] data = {"�߲ʽ���", "��ɫ����", "��ɫ����", "��ɫ����", "��ɫ����", "��ɫ����", "��ɫ����", "��ɫ����", "���̽���", "��������", "��������", "�߲�Ƶ��", "��ɫƵ��", "��ɫƵ��", "��ɫƵ��", "��ɫƵ��", "��ɫƵ��", "��ɫƵ��", "��ɫƵ��", "�߲�����"};
    public String[] data2 = new String[256];
    public int speed = 16;
    private HashMap<Integer, TextView> mTextViews = new HashMap<>();
    private HashMap<Integer, ImageView> imgs = new HashMap<>();
    public Handler mModHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.ModActivity.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what == 0) {
                ModActivity.this.setMod(new Random().nextInt(ModActivity.this.data.length), 1, 1, 127);
            }
            Log.e("ishaveT", "ishaveT=" + ModActivity.this.mMyApplication.ishaveT);
            return false;
        }
    });
    public int[] dataNewMod = {R.mipmap.ic_type_mod1, R.mipmap.ic_type_mod2, R.mipmap.ic_type_mod3, R.mipmap.ic_type_mod4, R.mipmap.ic_type_mod5, R.mipmap.ic_type_mod6, R.mipmap.ic_type_mod7, R.mipmap.ic_type_mod8, R.mipmap.ic_type_mod9, R.mipmap.ic_type_mod10, R.mipmap.ic_type_mod11, R.mipmap.ic_type_mod12, R.mipmap.ic_type_mod13, R.mipmap.ic_type_mod14, R.mipmap.ic_type_mod15};
    ArrayList<Integer> weekList = new ArrayList<>();
    public int[] newmodcolors = {SupportMenu.CATEGORY_MASK, -16711936, -16776961, -1280, -11862022, -5373697};
    public int modid = 0;
    public int modType = 1;
    public ArrayList<View> pageview = new ArrayList<>();
    public PagerAdapter mPagerAdapter = new PagerAdapter() { // from class: com.qh.blelight.ModActivity.10
        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return ModActivity.this.pageview.size();
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(View view, int i, Object obj) {
            ((ViewPager) view).removeView(ModActivity.this.pageview.get(i));
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(View view, int i) {
            ((ViewPager) view).addView(ModActivity.this.pageview.get(i));
            return ModActivity.this.pageview.get(i);
        }
    };
    public int colorID = 0;
    public View.OnClickListener mynewModOnClickListener = new View.OnClickListener() { // from class: com.qh.blelight.ModActivity.11
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_mod_color1 /* 2131230895 */:
                    ModActivity.this.colorID = 0;
                    break;
                case R.id.img_mod_color10 /* 2131230896 */:
                    ModActivity.this.colorID = 9;
                    break;
                case R.id.img_mod_color11 /* 2131230897 */:
                    ModActivity.this.colorID = 10;
                    break;
                case R.id.img_mod_color12 /* 2131230898 */:
                    ModActivity.this.colorID = 11;
                    break;
                case R.id.img_mod_color2 /* 2131230899 */:
                    ModActivity.this.colorID = 1;
                    break;
                case R.id.img_mod_color3 /* 2131230900 */:
                    ModActivity.this.colorID = 2;
                    break;
                case R.id.img_mod_color4 /* 2131230901 */:
                    ModActivity.this.colorID = 3;
                    break;
                case R.id.img_mod_color5 /* 2131230902 */:
                    ModActivity.this.colorID = 4;
                    break;
                case R.id.img_mod_color6 /* 2131230903 */:
                    ModActivity.this.colorID = 5;
                    break;
                case R.id.img_mod_color7 /* 2131230904 */:
                    ModActivity.this.colorID = 6;
                    break;
                case R.id.img_mod_color8 /* 2131230905 */:
                    ModActivity.this.colorID = 7;
                    break;
                case R.id.img_mod_color9 /* 2131230906 */:
                    ModActivity.this.colorID = 8;
                    break;
            }
            ModActivity modActivity = ModActivity.this;
            modActivity.select(modActivity.colorID);
            ModActivity.this.mMyApplication.sendNewMod(ModActivity.this.modid, ModActivity.this.seekbar_mod_speed.getProgress(), ModActivity.this.seekbar_mod_br.getProgress(), ModActivity.this.colorID);
        }
    };
    public SeekBar.OnSeekBarChangeListener myOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: com.qh.blelight.ModActivity.12
        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            ModActivity.this.mMyApplication.sendNewMod(ModActivity.this.modid, ModActivity.this.seekbar_mod_speed.getProgress(), ModActivity.this.seekbar_mod_br.getProgress(), ModActivity.this.colorID);
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mod);
        this.mContext = getApplicationContext();
        Resources resources = getResources();
        this.mResources = resources;
        this.data = resources.getStringArray(R.array.mods);
        String string = this.mResources.getString(R.string.mod2);
        int i = 0;
        while (true) {
            String[] strArr = this.data2;
            if (i >= strArr.length) {
                break;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            int i2 = i + 1;
            sb.append(String.format("%03d", Integer.valueOf(i2)));
            strArr[i] = sb.toString();
            i = i2;
        }
        this.tv_qicai = (TextView) findViewById(R.id.tv_qicai);
        this.tv_huancai = (TextView) findViewById(R.id.tv_huancai);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rel_b);
        this.rel_b = relativeLayout;
        relativeLayout.setVisibility(8);
        this.mInflator = getLayoutInflater();
        this.myWheelView = (WheelView) findViewById(R.id.myWheelView);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar_speed);
        this.seekbar_speed = seekBar;
        seekBar.setMax(30);
        this.seekbar_b = (SeekBar) findViewById(R.id.seekbar_b);
        MyApplication myApplication = (MyApplication) getApplication();
        this.mMyApplication = myApplication;
        myApplication.ModHandler = this.mModHandler;
        MyApplication myApplication2 = this.mMyApplication;
        if (myApplication2 != null && myApplication2.mBluetoothLeService != null) {
            Iterator<String> it = this.mMyApplication.mBluetoothLeService.mDevices.keySet().iterator();
            while (it.hasNext()) {
                Log.e("", "--" + it.next());
            }
        }
        ModWheelViewAdapter modWheelViewAdapter = new ModWheelViewAdapter();
        this.mModWheelViewAdapter = modWheelViewAdapter;
        modWheelViewAdapter.setData(this.data);
        this.myWheelView.setVisibleItems(3);
        this.myWheelView.setCyclic(true);
        this.myWheelView.setViewAdapter(this.mModWheelViewAdapter);
        this.myWheelView.addScrollingListener(new OnWheelScrollListener() { // from class: com.qh.blelight.ModActivity.2
            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingStarted(WheelView wheelView) {
            }

            @Override // com.qh.WheelView.OnWheelScrollListener
            public void onScrollingFinished(WheelView wheelView) {
                if (ModActivity.this.mTextViews.containsKey(Integer.valueOf(wheelView.getCurrentItem()))) {
                    TextView textView = (TextView) ModActivity.this.mTextViews.get(Integer.valueOf(wheelView.getCurrentItem()));
                    textView.setTextColor(-11872414);
                    if (ModActivity.this.cacheTextView != null) {
                        ModActivity.this.cacheTextView.setTextColor(-1);
                    }
                    ModActivity.this.cacheTextView = textView;
                }
                if (ModActivity.this.imgs.containsKey(Integer.valueOf(wheelView.getCurrentItem()))) {
                    ImageView imageView = (ImageView) ModActivity.this.imgs.get(Integer.valueOf(wheelView.getCurrentItem()));
                    imageView.setImageResource(R.drawable.ic_mod_n);
                    if (ModActivity.this.cacheImageView != null) {
                        ModActivity.this.cacheImageView.setImageResource(R.drawable.ic_mod_u);
                    }
                    ModActivity.this.cacheImageView = imageView;
                }
                if (ModActivity.this.modType != 1) {
                    ModActivity.this.setMod(wheelView.getCurrentItem(), ModActivity.this.seekbar_speed.getProgress(), ModActivity.this.modType, ModActivity.this.seekbar_b.getProgress());
                } else {
                    ModActivity modActivity = ModActivity.this;
                    modActivity.speed = 31 - modActivity.seekbar_speed.getProgress();
                    ModActivity.this.setMod(wheelView.getCurrentItem(), ModActivity.this.speed, ModActivity.this.modType, ModActivity.this.seekbar_b.getProgress());
                }
                if (ModActivity.this.mMyApplication.mainHandler != null) {
                    ModActivity.this.mMyApplication.mainHandler.sendEmptyMessage(MainActivity.mic_msg);
                }
            }
        });
        this.seekbar_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.qh.blelight.ModActivity.3
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i3, boolean z) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
                if (ModActivity.this.modType == 1) {
                    ModActivity.this.speed = 31 - seekBar2.getProgress();
                    ModActivity modActivity = ModActivity.this;
                    modActivity.setSpeed(modActivity.speed);
                    return;
                }
                ModActivity modActivity2 = ModActivity.this;
                modActivity2.setMod(modActivity2.myWheelView.getCurrentItem(), ModActivity.this.seekbar_speed.getProgress(), ModActivity.this.modType, ModActivity.this.seekbar_b.getProgress());
            }
        });
        this.seekbar_b.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.qh.blelight.ModActivity.4
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar2, int i3, boolean z) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar2) {
                ModActivity modActivity = ModActivity.this;
                modActivity.setMod(modActivity.myWheelView.getCurrentItem(), ModActivity.this.seekbar_speed.getProgress(), ModActivity.this.modType, ModActivity.this.seekbar_b.getProgress());
            }
        });
        this.tv_qicai.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.ModActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ModActivity.this.setMod(1);
            }
        });
        this.tv_huancai.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.ModActivity.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ModActivity.this.setMod(2);
            }
        });
        this.mModHandler.sendEmptyMessage(1);
        this.ic_hook1 = (ImageView) findViewById(R.id.ic_hook1);
        this.ic_hook2 = (ImageView) findViewById(R.id.ic_hook2);
        this.ic_hook3 = (ImageView) findViewById(R.id.ic_hook3);
        this.ic_hook4 = (ImageView) findViewById(R.id.ic_hook4);
        this.ic_hook5 = (ImageView) findViewById(R.id.ic_hook5);
        this.ic_hook6 = (ImageView) findViewById(R.id.ic_hook6);
        this.ic_hook7 = (ImageView) findViewById(R.id.ic_hook7);
        this.ic_hook8 = (ImageView) findViewById(R.id.ic_hook8);
        this.ic_hook9 = (ImageView) findViewById(R.id.ic_hook9);
        this.ic_hook10 = (ImageView) findViewById(R.id.ic_hook10);
        this.ic_hook11 = (ImageView) findViewById(R.id.ic_hook11);
        this.ic_hook12 = (ImageView) findViewById(R.id.ic_hook12);
        this.rel_mod1 = (RelativeLayout) findViewById(R.id.rel_mod1);
        this.rel_mod2 = (RelativeLayout) findViewById(R.id.rel_mod2);
        this.img_x = (ImageView) findViewById(R.id.img_x);
        this.rl_view = (RecyclerView) findViewById(R.id.rl_view);
        initData();
        initView();
        initListener();
    }

    private void initListener() {
        this.weekAdapter.changePostion(7);
        this.linearLayoutManager.scrollToPositionWithOffset(5, 0);
        this.linearLayoutManager.setStackFromEnd(true);
        this.modid = 5;
        this.weekAdapter.setOnWeekClickListener(new WeekAdapter.OnWeekClickListener() { // from class: com.qh.blelight.ModActivity.7
            @Override // com.qh.blelight.WeekAdapter.OnWeekClickListener
            public void scrollMid(int i) {
                int i2 = i - 2;
                if (i2 >= 0) {
                    ModActivity.this.weekAdapter.changePostion(i);
                    ModActivity.this.linearLayoutManager.scrollToPositionWithOffset(i2, 0);
                    ModActivity.this.linearLayoutManager.setStackFromEnd(true);
                }
                ModActivity.this.modid = i2;
                Log.e("--", "pos=" + i2);
                ModActivity.this.mMyApplication.sendNewMod(ModActivity.this.modid, ModActivity.this.seekbar_mod_speed.getProgress(), ModActivity.this.seekbar_mod_br.getProgress(), ModActivity.this.colorID);
            }
        });
        this.rl_view.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.qh.blelight.ModActivity.8
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 0) {
                    int iFindFirstVisibleItemPosition = ModActivity.this.linearLayoutManager.findFirstVisibleItemPosition();
                    if (iFindFirstVisibleItemPosition >= ModActivity.this.dataNewMod.length - 1) {
                        iFindFirstVisibleItemPosition = ModActivity.this.dataNewMod.length - 1;
                    }
                    ModActivity.this.weekAdapter.changePostion(iFindFirstVisibleItemPosition + 2);
                    ModActivity.this.linearLayoutManager.scrollToPositionWithOffset(iFindFirstVisibleItemPosition, 0);
                    ModActivity.this.linearLayoutManager.setStackFromEnd(true);
                    ModActivity.this.modid = iFindFirstVisibleItemPosition;
                    Log.e("newState", "newState=" + iFindFirstVisibleItemPosition);
                    ModActivity.this.mMyApplication.sendNewMod(ModActivity.this.modid, ModActivity.this.seekbar_mod_speed.getProgress(), ModActivity.this.seekbar_mod_br.getProgress(), ModActivity.this.colorID);
                }
            }
        });
        this.img_x.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.ModActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ModActivity.this.setMod(3);
            }
        });
        this.img_mod_color1.setOnClickListener(this.mynewModOnClickListener);
        this.img_mod_color2.setOnClickListener(this.mynewModOnClickListener);
        this.img_mod_color3.setOnClickListener(this.mynewModOnClickListener);
        this.img_mod_color4.setOnClickListener(this.mynewModOnClickListener);
        this.img_mod_color5.setOnClickListener(this.mynewModOnClickListener);
        this.img_mod_color6.setOnClickListener(this.mynewModOnClickListener);
        this.img_mod_color7.setOnClickListener(this.mynewModOnClickListener);
        this.img_mod_color8.setOnClickListener(this.mynewModOnClickListener);
        this.img_mod_color9.setOnClickListener(this.mynewModOnClickListener);
        this.img_mod_color10.setOnClickListener(this.mynewModOnClickListener);
        this.img_mod_color11.setOnClickListener(this.mynewModOnClickListener);
        this.img_mod_color12.setOnClickListener(this.mynewModOnClickListener);
        this.seekbar_mod_speed.setOnSeekBarChangeListener(this.myOnSeekBarChangeListener);
        this.seekbar_mod_br.setOnSeekBarChangeListener(this.myOnSeekBarChangeListener);
    }

    private void initData() {
        int i = 0;
        this.weekList.add(0);
        this.weekList.add(0);
        while (true) {
            int[] iArr = this.dataNewMod;
            if (i < iArr.length) {
                this.weekList.add(Integer.valueOf(iArr[i]));
                i++;
            } else {
                this.weekList.add(0);
                this.weekList.add(0);
                this.weekList.add(0);
                return;
            }
        }
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, 0, false);
        this.linearLayoutManager = linearLayoutManager;
        this.rl_view.setLayoutManager(linearLayoutManager);
        WeekAdapter weekAdapter = this.weekAdapter;
        if (weekAdapter == null) {
            WeekAdapter weekAdapter2 = new WeekAdapter(this, this.weekList);
            this.weekAdapter = weekAdapter2;
            this.rl_view.setAdapter(weekAdapter2);
        } else {
            weekAdapter.notifyDataSetChanged();
        }
        this.img_mod_color1 = (ImageView) findViewById(R.id.img_mod_color1);
        this.img_mod_color2 = (ImageView) findViewById(R.id.img_mod_color2);
        this.img_mod_color3 = (ImageView) findViewById(R.id.img_mod_color3);
        this.img_mod_color4 = (ImageView) findViewById(R.id.img_mod_color4);
        this.img_mod_color5 = (ImageView) findViewById(R.id.img_mod_color5);
        this.img_mod_color6 = (ImageView) findViewById(R.id.img_mod_color6);
        this.img_mod_color7 = (ImageView) findViewById(R.id.img_mod_color7);
        this.img_mod_color8 = (ImageView) findViewById(R.id.img_mod_color8);
        this.img_mod_color9 = (ImageView) findViewById(R.id.img_mod_color9);
        this.img_mod_color10 = (ImageView) findViewById(R.id.img_mod_color10);
        this.img_mod_color11 = (ImageView) findViewById(R.id.img_mod_color11);
        this.img_mod_color12 = (ImageView) findViewById(R.id.img_mod_color12);
        setBG(this.img_mod_color1, this.newmodcolors[0]);
        setBG(this.img_mod_color2, this.newmodcolors[1]);
        setBG(this.img_mod_color3, this.newmodcolors[2]);
        setBG(this.img_mod_color4, this.newmodcolors[3]);
        setBG(this.img_mod_color5, this.newmodcolors[4]);
        setBG(this.img_mod_color6, this.newmodcolors[5]);
        this.seekbar_mod_speed = (SeekBar) findViewById(R.id.seekbar_mod_speed);
        this.seekbar_mod_br = (SeekBar) findViewById(R.id.seekbar_mod_br);
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

    public class ModWheelViewAdapter implements WheelViewAdapter {
        private String[] data;

        @Override // com.qh.WheelView.WheelViewAdapter
        public View getEmptyItem(View view, ViewGroup viewGroup) {
            return view;
        }

        @Override // com.qh.WheelView.WheelViewAdapter
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        }

        @Override // com.qh.WheelView.WheelViewAdapter
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        }

        public ModWheelViewAdapter() {
        }

        public void setData(String[] strArr) {
            if (strArr == null || strArr.length <= 0) {
                return;
            }
            this.data = new String[strArr.length];
            for (int i = 0; i < strArr.length; i++) {
                this.data[i] = strArr[i];
            }
        }

        @Override // com.qh.WheelView.WheelViewAdapter
        public int getItemsCount() {
            return this.data.length;
        }

        @Override // com.qh.WheelView.WheelViewAdapter
        public View getItem(int i, View view, ViewGroup viewGroup) {
            View viewInflate = ModActivity.this.mInflator.inflate(R.layout.wheel_item, (ViewGroup) null);
            TextView textView = (TextView) viewInflate.findViewById(R.id.tx_lable);
            textView.setText("" + this.data[i]);
            ModActivity.this.mTextViews.put(Integer.valueOf(i), textView);
            ModActivity.this.imgs.put(Integer.valueOf(i), (ImageView) viewInflate.findViewById(R.id.img_conn));
            return viewInflate;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMod(int i, int i2, int i3, int i4) {
        MyBluetoothGatt myBluetoothGatt;
        MyBluetoothGatt myBluetoothGatt2;
        if (i3 == 1) {
            if (i2 > 31) {
                i2 = 31;
            }
            if (i2 < 1) {
                i2 = 1;
            }
        }
        if (this.mMyApplication.isOpenMusicHop()) {
            this.mMyApplication.setMusicHop(false, true);
        }
        if (this.mMyApplication.MusicHandler != null) {
            this.mMyApplication.MusicHandler.sendEmptyMessage(2);
        }
        for (String str : MainActivity.ControlMACs.keySet()) {
            if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt2 = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null) {
                byte b = myBluetoothGatt2.datas[2];
            }
            if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
                if (i3 == 1) {
                    myBluetoothGatt.setMod(i, i2);
                } else {
                    myBluetoothGatt.setSpeed(i, i2, i4);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSpeed(int i) {
        MyBluetoothGatt myBluetoothGatt;
        MyBluetoothGatt myBluetoothGatt2;
        if (i > 31) {
            i = 31;
        }
        if (i < 1) {
            i = 1;
        }
        this.mMyApplication.isopenmic = false;
        if (this.mMyApplication.isOpenMusicHop()) {
            this.mMyApplication.setMusicHop(false, true);
        }
        this.mMyApplication.isopenmic = false;
        if (this.mMyApplication.AdjustHandler != null) {
            this.mMyApplication.AdjustHandler.sendEmptyMessage(5);
        }
        if (this.mMyApplication.MusicHandler != null) {
            this.mMyApplication.MusicHandler.sendEmptyMessage(2);
        }
        for (String str : MainActivity.ControlMACs.keySet()) {
            if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt2 = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null) {
                byte b = myBluetoothGatt2.datas[2];
            }
            if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.setSpeed(i);
            }
        }
    }

    private void setSpeed(int i, int i2, int i3) {
        MyBluetoothGatt myBluetoothGatt;
        MyBluetoothGatt myBluetoothGatt2;
        if (this.mMyApplication.isOpenMusicHop()) {
            this.mMyApplication.setMusicHop(false, true);
        }
        if (this.mMyApplication.MusicHandler != null) {
            this.mMyApplication.MusicHandler.sendEmptyMessage(2);
        }
        if (this.mMyApplication.AdjustHandler != null) {
            this.mMyApplication.AdjustHandler.sendEmptyMessage(5);
        }
        for (String str : MainActivity.ControlMACs.keySet()) {
            if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt2 = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null) {
                byte b = myBluetoothGatt2.datas[2];
            }
            if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
                myBluetoothGatt.setSpeed(i, i2, i3);
            }
        }
    }

    public void setMod(int i) {
        if (i == this.modType) {
            return;
        }
        if (this.mMyApplication.MusicHandler != null) {
            this.mMyApplication.MusicHandler.sendEmptyMessage(2);
        }
        if (this.mModWheelViewAdapter != null) {
            if (i == 1) {
                if (!this.mMyApplication.ishavess() && this.data.length == 23) {
                    String[] strArr = new String[20];
                    for (int i2 = 0; i2 < 20; i2++) {
                        strArr[i2] = this.data[i2];
                    }
                    this.data = strArr;
                }
                this.modType = 1;
                this.mModWheelViewAdapter.setData(this.data);
                this.tv_qicai.setBackgroundResource(R.drawable.btn_mod_n);
                this.tv_huancai.setBackgroundResource(R.drawable.btn_mod_u);
                this.seekbar_speed.setMax(31);
                this.seekbar_speed.setProgress(16);
                this.rel_b.setVisibility(8);
                this.img_x.setImageResource(R.drawable.ic_mod_type_xun);
                this.rel_mod1.setVisibility(0);
                this.rel_mod2.setVisibility(8);
            } else if (i == 2) {
                this.modType = 2;
                String string = this.mResources.getString(R.string.mod2);
                if (this.mMyApplication.ishaveColor && !this.mMyApplication.ishaveDream) {
                    this.data2 = new String[30];
                    int i3 = 0;
                    while (true) {
                        String[] strArr2 = this.data2;
                        if (i3 >= strArr2.length) {
                            break;
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append(string);
                        int i4 = i3 + 1;
                        sb.append(String.format("%03d", Integer.valueOf(i4)));
                        strArr2[i3] = sb.toString();
                        i3 = i4;
                    }
                    this.rel_b.setVisibility(8);
                } else {
                    this.data2 = new String[256];
                    int i5 = 0;
                    while (true) {
                        String[] strArr3 = this.data2;
                        if (i5 >= strArr3.length) {
                            break;
                        }
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(string);
                        int i6 = i5 + 1;
                        sb2.append(String.format("%03d", Integer.valueOf(i6)));
                        strArr3[i5] = sb2.toString();
                        i5 = i6;
                    }
                    this.rel_b.setVisibility(0);
                }
                this.mModWheelViewAdapter.setData(this.data2);
                this.tv_qicai.setBackgroundResource(R.drawable.btn_mod_u);
                this.tv_huancai.setBackgroundResource(R.drawable.btn_mod_n);
                this.seekbar_speed.setMax(255);
                this.seekbar_speed.setProgress(127);
                this.seekbar_b.setMax(230);
                this.seekbar_b.setProgress(com.xiaoyu.onehlight.R.styleable.AppCompatTheme_windowActionBar);
                this.img_x.setImageResource(R.drawable.ic_mod_type_xun);
                this.rel_mod1.setVisibility(0);
                this.rel_mod2.setVisibility(8);
            } else {
                this.img_x.setImageResource(R.drawable.ic_mod_type_x);
                this.tv_qicai.setBackgroundResource(R.drawable.btn_mod_u);
                this.tv_huancai.setBackgroundResource(R.drawable.btn_mod_u);
                this.rel_mod1.setVisibility(8);
                this.rel_mod2.setVisibility(0);
                this.modType = 3;
            }
            this.myWheelView.setVisibleItems(3);
            this.myWheelView.setCyclic(true);
            this.myWheelView.setViewAdapter(this.mModWheelViewAdapter);
            this.myWheelView.setCurrentItem(0);
        }
    }

    public void setBG(ImageView imageView, int i) {
        if (imageView != null) {
            imageView.setColorFilter(i);
        }
    }

    public void select(int i) {
        this.ic_hook1.setVisibility(8);
        this.ic_hook2.setVisibility(8);
        this.ic_hook3.setVisibility(8);
        this.ic_hook4.setVisibility(8);
        this.ic_hook5.setVisibility(8);
        this.ic_hook6.setVisibility(8);
        this.ic_hook7.setVisibility(8);
        this.ic_hook8.setVisibility(8);
        this.ic_hook9.setVisibility(8);
        this.ic_hook10.setVisibility(8);
        this.ic_hook11.setVisibility(8);
        this.ic_hook12.setVisibility(8);
        switch (i) {
            case 0:
                this.ic_hook1.setVisibility(0);
                break;
            case 1:
                this.ic_hook2.setVisibility(0);
                break;
            case 2:
                this.ic_hook3.setVisibility(0);
                break;
            case 3:
                this.ic_hook4.setVisibility(0);
                break;
            case 4:
                this.ic_hook5.setVisibility(0);
                break;
            case 5:
                this.ic_hook6.setVisibility(0);
                break;
            case 6:
                this.ic_hook7.setVisibility(0);
                break;
            case 7:
                this.ic_hook8.setVisibility(0);
                break;
            case 8:
                this.ic_hook9.setVisibility(0);
                break;
            case 9:
                this.ic_hook10.setVisibility(0);
                break;
            case 10:
                this.ic_hook11.setVisibility(0);
                break;
            case 11:
                this.ic_hook12.setVisibility(0);
                break;
        }
    }
}
