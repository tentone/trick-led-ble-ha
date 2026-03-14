package com.qh.blelight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.internal.view.SupportMenu;
import com.qh.data.MusicInfo;
import com.qh.onehlight.R;
import com.qh.tools.MusicData;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* JADX INFO: loaded from: classes.dex */
public class MusicActivity<VisualizerView> extends Activity {
    public static int HEIGHT = 65;
    public static int HEIGHT_FREQUENCY = 85;
    public static final int Light_COLOR_CHANGE = 3;
    public static final int MAXCOLOR = 200;
    public static final int MUSIC_STOP = 2;
    public static final int UPDATA_UI = 1;
    private static final float VISUALIZER_HEIGHT_DIP = 100.0f;
    public static int blue = 30;
    public static int calpha = 0;
    public static int cblue = 30;
    public static int cgreen = 0;
    public static int changenum = 5;
    public static int cred = 0;
    public static int green = 30;
    public static int red = 30;
    public byte[] Bytes;
    private AudioManager audioManager;
    private Context context;
    public ImageView img_hop;
    public ImageView img_mucis_mod;
    public ImageView img_play;
    public ImageView img_play_last;
    public ImageView img_play_next;
    public LinearLayout lin_c;
    private LinearLayout lin_waveform;
    public AssetManager mAssetManager;
    private Equalizer mEqualizer;
    private ListView mListView;
    private MusicActivity<VisualizerView>.MusicInfoAdapter mMusicInfoAdapter;
    private MyApplication mMyApplication;
    public Resources mResources;
    private Visualizer mVisualizer;
    public MediaPlayer myMediaPlayer;
    public float[] myPoints;
    private File path;
    public SeekBar seekbar_play;
    public SharedPreferences settings;
    public TextView tx_album;
    public TextView tx_endtime;
    public TextView tx_music_name;
    public TextView tx_statrtime;
    String mTimerFormat = "%02d:%02d";
    public ArrayList<MusicInfo> musicInfos = new ArrayList<>();
    private int playingId = -1;
    public Visualizer.MeasurementPeakRms mMeasurementPeakRms = new Visualizer.MeasurementPeakRms();
    public int musicMod = 2;
    public boolean isVisualizer = false;
    public boolean isReplay = false;
    private Handler musicHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.MusicActivity.1
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what == 0) {
                Random random = new Random();
                int iNextInt = random.nextInt(MusicActivity.this.musicInfos.size());
                while (MusicActivity.this.playingId == iNextInt) {
                    iNextInt = random.nextInt(MusicActivity.this.musicInfos.size());
                }
                MusicActivity.this.playingId = iNextInt;
                MusicActivity.this.playMusic();
                MusicActivity.this.mHandler.sendEmptyMessage(1);
            }
            if (message.what != 2) {
                return false;
            }
            if (MusicActivity.this.myMediaPlayer.isPlaying()) {
                MusicActivity.this.myMediaPlayer.pause();
            }
            MusicActivity.this.mHandler.sendEmptyMessage(1);
            return false;
        }
    });
    private Handler mHandler = new Handler(new Handler.Callback() { // from class: com.qh.blelight.MusicActivity.4
        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                MusicActivity.this.updataUI();
            } else if (i == 2) {
                Log.e("MUSIC_STOP", "musicMod  " + MusicActivity.this.musicMod);
                if (MusicActivity.this.musicMod == 2) {
                    MusicActivity.access$008(MusicActivity.this);
                    if (MusicActivity.this.playingId >= MusicActivity.this.musicInfos.size()) {
                        MusicActivity.this.playingId = 0;
                    }
                    MusicActivity.this.playMusic();
                    MusicActivity.this.mHandler.sendEmptyMessage(1);
                }
                if (MusicActivity.this.musicMod == 1) {
                    int iNextInt = new Random().nextInt(MusicActivity.this.musicInfos.size());
                    if (iNextInt != MusicActivity.this.playingId) {
                        MusicActivity.this.playingId = iNextInt;
                    } else {
                        MusicActivity.access$008(MusicActivity.this);
                    }
                    MusicActivity.this.playMusic();
                    MusicActivity.this.mHandler.sendEmptyMessage(1);
                }
                if (MusicActivity.this.musicMod == 0) {
                    MusicActivity.this.playMusic();
                }
            } else if (i == 3) {
                MusicActivity.this.getcolorchang();
            }
            return false;
        }
    });
    private MediaPlayer.OnCompletionListener myOnCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: com.qh.blelight.MusicActivity.5
        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mediaPlayer) {
            MusicActivity.this.mHandler.sendEmptyMessage(1);
            MusicActivity.this.mHandler.sendEmptyMessage(2);
        }
    };
    private AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.qh.blelight.MusicActivity.6
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (MusicActivity.this.mMyApplication.isOpenMusicHop()) {
                MusicActivity.this.mMyApplication.isopenmic = false;
                MusicActivity.this.mMyApplication.openmic(MusicActivity.this.mMyApplication.isopenmic);
                if (MusicActivity.this.mMyApplication.AdjustHandler != null) {
                    MusicActivity.this.mMyApplication.AdjustHandler.sendEmptyMessage(5);
                }
            }
            if (MusicActivity.this.playingId == -1) {
                MusicActivity.this.playingId = i;
                MusicActivity.this.playMusic();
                MusicActivity.this.setVisualizerEnabled(true);
                MusicActivity.this.mHandler.sendEmptyMessage(1);
                return;
            }
            if (MusicActivity.this.playingId != i) {
                MusicActivity.this.playingId = i;
                MusicActivity.this.playMusic();
                MusicActivity.this.setVisualizerEnabled(true);
            } else if (MusicActivity.this.myMediaPlayer.isPlaying()) {
                MusicActivity.this.myMediaPlayer.pause();
            } else {
                MusicActivity.this.myMediaPlayer.start();
                MusicActivity.this.setVisualizerEnabled(true);
            }
            MusicActivity.this.mHandler.sendEmptyMessage(1);
            MusicActivity.this.playingId = i;
        }
    };
    private View.OnClickListener myOnClickListener = new View.OnClickListener() { // from class: com.qh.blelight.MusicActivity.10
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (MusicActivity.this.mMyApplication.isOpenMusicHop()) {
                MusicActivity.this.mMyApplication.isopenmic = false;
                MusicActivity.this.mMyApplication.openmic(MusicActivity.this.mMyApplication.isopenmic);
                if (MusicActivity.this.mMyApplication.AdjustHandler != null) {
                    MusicActivity.this.mMyApplication.AdjustHandler.sendEmptyMessage(5);
                }
            }
            int id = view.getId();
            if (id != R.id.img_hop) {
                switch (id) {
                    case R.id.img_play /* 2131230910 */:
                        if (MusicActivity.this.musicInfos != null && MusicActivity.this.musicInfos.size() != 0) {
                            if (MusicActivity.this.playingId == -1) {
                                MusicActivity.this.playingId = 0;
                                MusicActivity.this.playMusic();
                            }
                            if (MusicActivity.this.myMediaPlayer.isPlaying()) {
                                MusicActivity.this.myMediaPlayer.pause();
                            } else {
                                MusicActivity.this.myMediaPlayer.start();
                                MusicActivity.this.setVisualizerEnabled(true);
                            }
                            MusicActivity.this.mHandler.sendEmptyMessage(1);
                            break;
                        }
                        break;
                    case R.id.img_play_last /* 2131230911 */:
                        if (MusicActivity.this.musicInfos != null && MusicActivity.this.musicInfos.size() != 0) {
                            if (MusicActivity.this.playingId != 0) {
                                if (MusicActivity.this.playingId == -1) {
                                    MusicActivity.this.playingId = 0;
                                } else {
                                    MusicActivity.access$010(MusicActivity.this);
                                }
                            } else {
                                MusicActivity musicActivity = MusicActivity.this;
                                musicActivity.playingId = musicActivity.musicInfos.size() - 1;
                            }
                            MusicActivity.this.playMusic();
                            MusicActivity.this.mHandler.sendEmptyMessage(1);
                            break;
                        }
                        break;
                    case R.id.img_play_next /* 2131230912 */:
                        if (MusicActivity.this.musicInfos != null && MusicActivity.this.musicInfos.size() != 0) {
                            if (MusicActivity.this.playingId == MusicActivity.this.musicInfos.size() - 1 || MusicActivity.this.playingId == -1) {
                                MusicActivity.this.playingId = 0;
                            } else {
                                MusicActivity.access$008(MusicActivity.this);
                            }
                            MusicActivity.this.playMusic();
                            MusicActivity.this.mHandler.sendEmptyMessage(1);
                            break;
                        }
                        break;
                }
                return;
            }
            SharedPreferences.Editor editorEdit = MusicActivity.this.settings.edit();
            editorEdit.putBoolean("isOpenMusicHop", MusicActivity.this.mMyApplication.isOpenMusicHop());
            editorEdit.commit();
            if (MusicActivity.this.mMyApplication.isOpenMusicHop()) {
                MusicActivity.this.mMyApplication.isopenmic = false;
                MusicActivity.this.mMyApplication.openmic(MusicActivity.this.mMyApplication.isopenmic);
                if (MusicActivity.this.mMyApplication.AdjustHandler != null) {
                    MusicActivity.this.mMyApplication.AdjustHandler.sendEmptyMessage(5);
                }
            }
        }
    };
    boolean isfristplay = true;
    private Runnable progressBarRunnable = new Runnable() { // from class: com.qh.blelight.MusicActivity.11
        @Override // java.lang.Runnable
        public void run() {
            MusicActivity.this.seekbar_play.setProgress(MusicActivity.this.myMediaPlayer.getCurrentPosition());
            long currentPosition = MusicActivity.this.myMediaPlayer.getCurrentPosition() / 1000;
            String str = String.format(MusicActivity.this.mTimerFormat, Long.valueOf(currentPosition / 60), Long.valueOf(currentPosition % 60));
            MusicActivity.this.tx_statrtime.setText("" + str);
            if (MusicActivity.this.myMediaPlayer.isPlaying()) {
                MusicActivity.this.img_play.setBackgroundResource(R.drawable.ic_play);
                MusicActivity.this.pauseMusic();
                if (!MusicActivity.this.mMyApplication.isOpenVisualizer) {
                    MusicActivity.this.mMyApplication.mMediaRecorderDemo.startRecord();
                }
            } else {
                MusicActivity.this.img_play.setBackgroundResource(R.drawable.ic_muisc_stop);
                MusicActivity.this.mMyApplication.reSetCMD();
            }
            if (MusicActivity.this.myMediaPlayer == null || !MusicActivity.this.myMediaPlayer.isPlaying()) {
                return;
            }
            if (MusicActivity.this.isfristplay) {
                MusicActivity.this.isfristplay = false;
                MusicActivity.this.myMediaPlayer.pause();
                MusicActivity.this.myMediaPlayer.seekTo(1);
            }
            MusicActivity.this.mHandler.postDelayed(MusicActivity.this.progressBarRunnable, 1000L);
        }
    };
    public int topSize = 0;
    public int bomSize = 0;
    private ArrayList<Float> points = new ArrayList<>();
    public int oldRms = -9000;
    private boolean flay = false;
    private int num = 0;
    public int w = 180;
    public int h = 300;
    private int add = 0;
    private int minus = 0;
    private int a = 0;
    AudioManager.OnAudioFocusChangeListener listener = new AudioManager.OnAudioFocusChangeListener() { // from class: com.qh.blelight.MusicActivity.14
        @Override // android.media.AudioManager.OnAudioFocusChangeListener
        public void onAudioFocusChange(int i) {
            Log.e("--", "focusChange=" + i);
            if (MusicActivity.this.myMediaPlayer.isPlaying()) {
                MusicActivity.this.myMediaPlayer.pause();
            }
        }
    };
    private boolean ispauseMusic = false;

    /* JADX INFO: Access modifiers changed from: private */
    public int addcolor(int i, int i2) {
        int i3 = i + i2;
        if (i3 > 255) {
            i3 = 255;
        }
        if (i3 <= 0) {
            return 5;
        }
        return i3;
    }

    static /* synthetic */ int access$008(MusicActivity musicActivity) {
        int i = musicActivity.playingId;
        musicActivity.playingId = i + 1;
        return i;
    }

    static /* synthetic */ int access$010(MusicActivity musicActivity) {
        int i = musicActivity.playingId;
        musicActivity.playingId = i - 1;
        return i;
    }

    static /* synthetic */ int access$908(MusicActivity musicActivity) {
        int i = musicActivity.num;
        musicActivity.num = i + 1;
        return i;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        MusicInfo musicInfo;
        super.onCreate(bundle);
        setContentView(R.layout.activity_music);
        this.mResources = getResources();
        this.context = this;
        if (Environment.getExternalStorageState().equals("mounted")) {
            this.path = Environment.getExternalStorageDirectory();
        } else {
            Toast.makeText(this, "SD card Error !", 1).show();
        }
        this.mAssetManager = this.context.getAssets();
        this.settings = getSharedPreferences("BleLight", 0);
        if ("HUAWEI".equals(Build.MANUFACTURER)) {
            HEIGHT = 65;
            HEIGHT_FREQUENCY = 85;
            "HUAWEI P7-L09".equals(Build.MODEL);
        }
        if ("LGE".equals(Build.MANUFACTURER)) {
            HEIGHT = com.xiaoyu.onehlight.R.styleable.AppCompatTheme_windowActionBar;
            HEIGHT_FREQUENCY = 85;
        }
        this.audioManager = (AudioManager) this.context.getSystemService("audio");
        this.myMediaPlayer = new MediaPlayer();
        MyApplication myApplication = (MyApplication) getApplication();
        this.mMyApplication = myApplication;
        myApplication.MusicHandler = this.musicHandler;
        this.mListView = (ListView) findViewById(R.id.play_list);
        getMusicInfo();
        MusicActivity<VisualizerView>.MusicInfoAdapter musicInfoAdapter = new MusicInfoAdapter(this.context, this.musicInfos);
        this.mMusicInfoAdapter = musicInfoAdapter;
        this.mListView.setAdapter((ListAdapter) musicInfoAdapter);
        this.mListView.setOnItemClickListener(this.myOnItemClickListener);
        init();
        if (this.musicInfos.size() > 0 && (musicInfo = this.musicInfos.get(0)) != null) {
            String songName = musicInfo.getSongName();
            if (songName == null || "".equals(songName)) {
                String display_name = musicInfo.getDisplay_name();
                songName = (display_name == null || display_name.length() <= 4) ? "other" : display_name.substring(0, display_name.length() - 4);
            }
            this.tx_music_name.setText(songName);
            this.tx_album.setText("" + musicInfo.getAlbum());
            long duration = (long) (this.myMediaPlayer.getDuration() / 1000);
            String.format(this.mTimerFormat, Long.valueOf(duration / 60), Long.valueOf(duration % 60));
            this.tx_endtime.setText("00:00");
            this.tx_statrtime.setText("00:00");
            this.playingId = 0;
            playMusic();
            this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MusicActivity.2
                @Override // java.lang.Runnable
                public void run() {
                    if (MusicActivity.this.myMediaPlayer.isPlaying()) {
                        MusicActivity.this.myMediaPlayer.pause();
                        MusicActivity.this.myMediaPlayer.seekTo(1);
                        MusicActivity.this.isfristplay = false;
                    }
                    MusicActivity.this.myMediaPlayer.setOnCompletionListener(MusicActivity.this.myOnCompletionListener);
                }
            }, 200L);
            updataUI();
        }
        initVisualizer();
        setupEqualizeFxAndUi();
        this.mVisualizer.setEnabled(true);
        this.seekbar_play.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.qh.blelight.MusicActivity.3
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (MusicActivity.this.myMediaPlayer == null || !MusicActivity.this.myMediaPlayer.isPlaying()) {
                    return;
                }
                MusicActivity.this.myMediaPlayer.pause();
                MusicActivity.this.myMediaPlayer.seekTo(seekBar.getProgress());
                MusicActivity.this.myMediaPlayer.start();
            }
        });
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

    @Override // android.app.Activity
    protected void onResume() {
        this.ispauseMusic = false;
        if (this.isReplay) {
            this.isReplay = false;
            if (!this.myMediaPlayer.isPlaying()) {
                this.myMediaPlayer.start();
            }
        }
        MyApplication myApplication = this.mMyApplication;
        if (myApplication != null) {
            if (myApplication.isOpenMusicHop()) {
                this.img_hop.setImageResource(R.drawable.ic_hop_n);
            } else {
                this.img_hop.setImageResource(R.drawable.ic_hop_u);
            }
        }
        super.onResume();
    }

    public class MusicInfoAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        List<MusicInfo> mMusicInfos;

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        public MusicInfoAdapter(Context context, List<MusicInfo> list) {
            this.context = context;
            this.mMusicInfos = list;
            this.inflater = LayoutInflater.from(context);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mMusicInfos.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mMusicInfos.get(i);
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewGroup viewGroup2;
            MusicInfo musicInfo = this.mMusicInfos.get(i);
            Item item = new Item();
            if (view != null) {
                viewGroup2 = (ViewGroup) view;
            } else {
                viewGroup2 = (ViewGroup) this.inflater.inflate(R.layout.play_item, (ViewGroup) null);
            }
            item.list_music_name = (TextView) viewGroup2.findViewById(R.id.muics_name);
            item.list_music_artist = (TextView) viewGroup2.findViewById(R.id.music_artists);
            item.list_play_time = (TextView) viewGroup2.findViewById(R.id.music_total_time);
            item.list_music_name.setText("" + musicInfo.getSongName());
            item.list_music_artist.setText("" + musicInfo.getArtist());
            long playTime = musicInfo.getPlayTime() / 1000;
            String str = String.format(MusicActivity.this.mTimerFormat, Long.valueOf(playTime / 60), Long.valueOf(playTime % 60));
            item.list_play_time.setText("" + str);
            return viewGroup2;
        }
    }

    public class Item {
        public TextView list_music_artist;
        public TextView list_music_name;
        public TextView list_play_time;

        public Item() {
        }
    }

    public void setVisualizerEnabled(boolean z) {
        if (!this.mMyApplication.isOpenVisualizer || this.mVisualizer.getEnabled() == z) {
            return;
        }
        this.mVisualizer.setEnabled(z);
    }

    public void playMusic() {
        MediaPlayer mediaPlayer = this.myMediaPlayer;
        if (mediaPlayer == null) {
            Log.e("", "myMediaPlayer == null");
            return;
        }
        mediaPlayer.reset();
        try {
            int i = this.playingId;
            if (i != -1 && i > 2) {
                FileInputStream fileInputStream = new FileInputStream(this.musicInfos.get(this.playingId).getAudioPath());
                this.myMediaPlayer.setDataSource(fileInputStream.getFD(), 0L, fileInputStream.getChannel().size());
                this.myMediaPlayer.prepare();
                this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MusicActivity.8
                    @Override // java.lang.Runnable
                    public void run() {
                        MusicActivity.this.myMediaPlayer.start();
                    }
                }, 100L);
                fileInputStream.close();
                return;
            }
            String str = "Alan Walker - Faded.mp3";
            if (i != 0) {
                if (i == 1) {
                    str = "Alan Walker - Play.mp3";
                } else if (i == 2) {
                    str = "Ava Max - Salt.mp3";
                }
            }
            try {
                AssetFileDescriptor assetFileDescriptorOpenFd = this.mAssetManager.openFd(str);
                FileInputStream fileInputStreamCreateInputStream = assetFileDescriptorOpenFd.createInputStream();
                fileInputStreamCreateInputStream.getFD();
                fileInputStreamCreateInputStream.getChannel();
                Log.e("setDataSource", "setDataSource 8888");
                this.myMediaPlayer.setDataSource(assetFileDescriptorOpenFd.getFileDescriptor(), assetFileDescriptorOpenFd.getStartOffset(), assetFileDescriptorOpenFd.getLength());
                this.myMediaPlayer.prepare();
                this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MusicActivity.7
                    @Override // java.lang.Runnable
                    public void run() {
                        MusicActivity.this.myMediaPlayer.start();
                    }
                }, 100L);
                fileInputStreamCreateInputStream.close();
            } catch (Exception e) {
                Log.e("Exception", "Exception " + e.getMessage());
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            Log.e("777", "8888");
            try {
                FileInputStream fileInputStream2 = new FileInputStream(this.musicInfos.get(this.playingId).getAudioPath());
                this.myMediaPlayer.setDataSource(fileInputStream2.getFD(), 0L, fileInputStream2.getChannel().size());
                this.myMediaPlayer.prepare();
                this.mHandler.postDelayed(new Runnable() { // from class: com.qh.blelight.MusicActivity.9
                    @Override // java.lang.Runnable
                    public void run() {
                        MusicActivity.this.myMediaPlayer.start();
                    }
                }, 100L);
                fileInputStream2.close();
            } catch (Exception unused) {
            }
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        } catch (IllegalStateException e4) {
            e4.printStackTrace();
        } catch (SecurityException e5) {
            e5.printStackTrace();
        }
    }

    public void getMusicInfo() {
        this.musicInfos.clear();
        MusicInfo musicInfo = new MusicInfo();
        musicInfo.setSongName("Alan Walker - Faded");
        musicInfo.setArtist("Alan Walker");
        musicInfo.setAlbum("Faded");
        musicInfo.setDisplay_name("Alan Walker - Faded");
        musicInfo.setAudioPath("");
        musicInfo.setPlayTime(212000L);
        this.musicInfos.add(musicInfo);
        MusicInfo musicInfo2 = new MusicInfo();
        musicInfo2.setSongName("Alan Walker - Play");
        musicInfo2.setArtist("K-391&Alan Walker&Martin Tungevaag&Mangoo");
        musicInfo2.setAlbum("Play");
        musicInfo2.setDisplay_name("Alan Walker - Play");
        musicInfo2.setAudioPath("");
        musicInfo2.setPlayTime(168000L);
        this.musicInfos.add(musicInfo2);
        MusicInfo musicInfo3 = new MusicInfo();
        musicInfo3.setSongName("Ava Max - Salt");
        musicInfo3.setArtist("Ava Max");
        musicInfo3.setAlbum("Salt");
        musicInfo3.setDisplay_name("Ava Max - Salt");
        musicInfo3.setAudioPath("");
        musicInfo3.setPlayTime(180000L);
        this.musicInfos.add(musicInfo3);
        Cursor mP3MusicInfo = MusicData.getMP3MusicInfo(this.context, this.path.toString());
        if (mP3MusicInfo.moveToFirst()) {
            do {
                MusicInfo musicInfo4 = new MusicInfo();
                String string = mP3MusicInfo.getString(mP3MusicInfo.getColumnIndexOrThrow("_data"));
                String string2 = mP3MusicInfo.getString(mP3MusicInfo.getColumnIndexOrThrow("title"));
                String string3 = mP3MusicInfo.getString(mP3MusicInfo.getColumnIndexOrThrow("_display_name"));
                String string4 = mP3MusicInfo.getString(mP3MusicInfo.getColumnIndexOrThrow("artist"));
                String string5 = mP3MusicInfo.getString(mP3MusicInfo.getColumnIndexOrThrow("album"));
                long j = mP3MusicInfo.getLong(mP3MusicInfo.getColumnIndexOrThrow("duration"));
                if (j >= 40000) {
                    musicInfo4.setSongName(string2);
                    musicInfo4.setArtist(string4);
                    musicInfo4.setAlbum(string5);
                    musicInfo4.setDisplay_name(string3);
                    musicInfo4.setAudioPath(string);
                    musicInfo4.setPlayTime(j);
                    this.musicInfos.add(musicInfo4);
                }
            } while (mP3MusicInfo.moveToNext());
        }
    }

    public void updataUI() {
        MediaPlayer mediaPlayer = this.myMediaPlayer;
        if (mediaPlayer == null) {
            this.tx_music_name.setText("--------");
            return;
        }
        if (mediaPlayer.getDuration() >= 1961778602) {
            return;
        }
        this.seekbar_play.setMax(this.myMediaPlayer.getDuration());
        this.mHandler.postDelayed(this.progressBarRunnable, 1000L);
        long duration = this.myMediaPlayer.getDuration() / 1000;
        String str = String.format(this.mTimerFormat, Long.valueOf(duration / 60), Long.valueOf(duration % 60));
        this.tx_endtime.setText("" + str);
        if (this.playingId == -1) {
            this.playingId = 0;
        }
        if (this.musicInfos.size() == 0) {
            return;
        }
        String songName = this.musicInfos.get(this.playingId).getSongName();
        if (songName == null || "".equals(songName)) {
            String display_name = this.musicInfos.get(this.playingId).getDisplay_name();
            songName = (display_name == null || display_name.length() <= 4) ? "other" : display_name.substring(0, display_name.length() - 4);
        }
        this.tx_music_name.setText(songName);
        this.tx_album.setText("" + this.musicInfos.get(this.playingId).getAlbum());
        if (this.myMediaPlayer.isPlaying()) {
            this.img_play.setBackgroundResource(R.drawable.ic_play);
        } else {
            this.img_play.setBackgroundResource(R.drawable.ic_muisc_stop);
        }
    }

    private void init() {
        this.lin_waveform = (LinearLayout) findViewById(R.id.lin_waveform);
        this.img_play = (ImageView) findViewById(R.id.img_play);
        this.img_play_next = (ImageView) findViewById(R.id.img_play_next);
        this.img_play_last = (ImageView) findViewById(R.id.img_play_last);
        this.img_mucis_mod = (ImageView) findViewById(R.id.img_mucis_mod);
        this.img_play.setOnClickListener(this.myOnClickListener);
        this.img_play_next.setOnClickListener(this.myOnClickListener);
        this.img_play_last.setOnClickListener(this.myOnClickListener);
        ImageView imageView = (ImageView) findViewById(R.id.img_hop);
        this.img_hop = imageView;
        imageView.setOnClickListener(this.myOnClickListener);
        MyApplication myApplication = this.mMyApplication;
        if (myApplication != null) {
            if (myApplication.isOpenMusicHop()) {
                this.img_hop.setImageResource(R.drawable.ic_hop_n);
            } else {
                this.img_hop.setImageResource(R.drawable.ic_hop_u);
            }
        }
        this.tx_album = (TextView) findViewById(R.id.tx_album);
        this.tx_music_name = (TextView) findViewById(R.id.tx_music_name);
        this.tx_statrtime = (TextView) findViewById(R.id.tx_statrtime);
        this.tx_endtime = (TextView) findViewById(R.id.tx_endtime);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar_play);
        this.seekbar_play = seekBar;
        seekBar.setProgress(0);
        this.lin_c = (LinearLayout) findViewById(R.id.lin_c);
        this.img_mucis_mod.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.MusicActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MusicActivity.this.musicMod++;
                if (MusicActivity.this.musicMod >= 3) {
                    MusicActivity.this.musicMod = 0;
                }
                int i = MusicActivity.this.musicMod;
                if (i == 0) {
                    MusicActivity.this.img_mucis_mod.setImageResource(R.drawable.ic_playone);
                    Toast.makeText(MusicActivity.this.context, "" + MusicActivity.this.mResources.getString(R.string.playMod1), 0).show();
                    return;
                }
                if (i == 1) {
                    MusicActivity.this.img_mucis_mod.setImageResource(R.drawable.ic_random);
                    Toast.makeText(MusicActivity.this.context, "" + MusicActivity.this.mResources.getString(R.string.playMod2), 0).show();
                    return;
                }
                if (i != 2) {
                    return;
                }
                MusicActivity.this.img_mucis_mod.setImageResource(R.drawable.ic_playsequence);
                Toast.makeText(MusicActivity.this.context, "" + MusicActivity.this.mResources.getString(R.string.playMod3), 0).show();
            }
        });
    }

    private void initVisualizer() {
        try {
            Visualizer visualizer = new Visualizer(this.myMediaPlayer.getAudioSessionId());
            this.mVisualizer = visualizer;
            visualizer.setMeasurementMode(1);
            this.mVisualizer.setCaptureSize(512);
            this.mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() { // from class: com.qh.blelight.MusicActivity.13
                @Override // android.media.audiofx.Visualizer.OnDataCaptureListener
                public void onFftDataCapture(Visualizer visualizer2, byte[] bArr, int i) {
                }

                @Override // android.media.audiofx.Visualizer.OnDataCaptureListener
                public void onWaveFormDataCapture(Visualizer visualizer2, byte[] bArr, int i) {
                    if (!MusicActivity.this.myMediaPlayer.isPlaying() || MusicActivity.this.mMeasurementPeakRms == null || visualizer2 == null) {
                        return;
                    }
                    visualizer2.getMeasurementPeakRms(MusicActivity.this.mMeasurementPeakRms);
                    if (MusicActivity.this.mMeasurementPeakRms.mRms == -9600) {
                        if (!MusicActivity.this.flay && MusicActivity.this.num > 80) {
                            MusicActivity.this.mMyApplication.isOpenVisualizer = false;
                        }
                        MusicActivity.access$908(MusicActivity.this);
                        return;
                    }
                    MusicActivity.this.mMyApplication.isOpenVisualizer = true;
                    MusicActivity.this.flay = true;
                    if (MusicActivity.this.mMeasurementPeakRms.mRms >= -9000 && !MusicActivity.this.mMyApplication.mMediaRecorderDemo.isPlay) {
                        int i2 = MusicActivity.this.mMeasurementPeakRms.mRms - MusicActivity.this.oldRms;
                        MusicActivity musicActivity = MusicActivity.this;
                        double d = musicActivity.mMeasurementPeakRms.mRms;
                        double d2 = i2;
                        Double.isNaN(d2);
                        Double.isNaN(d);
                        int i3 = (int) (d + (d2 * 1.5d));
                        double d3 = MusicActivity.this.mMeasurementPeakRms.mRms - MusicActivity.this.oldRms;
                        Double.isNaN(d3);
                        musicActivity.setcolor(i3, d3 * 2.5d);
                        MusicActivity.this.mHandler.sendEmptyMessage(3);
                        MusicActivity musicActivity2 = MusicActivity.this;
                        musicActivity2.oldRms = musicActivity2.mMeasurementPeakRms.mRms;
                    }
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false);
        } catch (Exception unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void setcolor(int i, double d) {
        if (cblue > 5) {
            cblue = 5;
            return;
        }
        if (i > -8000 && i <= -3000) {
            double dAbs = Math.abs(i) - 3000;
            Double.isNaN(dAbs);
            double d2 = (int) (dAbs * 0.05d);
            Double.isNaN(d2);
            double d3 = d2 / 255.0d;
            int i2 = red;
            double d4 = i2;
            Double.isNaN(d4);
            int i3 = (int) (d4 * d3);
            cred = i3;
            double d5 = green;
            Double.isNaN(d5);
            cgreen = (int) (d5 * d3);
            double d6 = blue;
            Double.isNaN(d6);
            cblue = (int) (d3 * d6);
            double d7 = d / 255.0d;
            double d8 = i2;
            Double.isNaN(d8);
            cred = addcolor(i2, i3 + ((int) (d8 * d7)));
            int i4 = green;
            int i5 = cgreen;
            double d9 = i4;
            Double.isNaN(d9);
            cgreen = addcolor(i4, i5 + ((int) (d9 * d7)));
            int i6 = blue;
            int i7 = cblue;
            double d10 = i6;
            Double.isNaN(d10);
            cblue = addcolor(i6, i7 + ((int) (d7 * d10)));
        }
        if (i > -3000 && i <= -2000) {
            double dAbs2 = Math.abs(i) - 1500;
            Double.isNaN(dAbs2);
            double d11 = ((int) (dAbs2 * 0.0667d)) + 100;
            Double.isNaN(d11);
            double d12 = d11 / 255.0d;
            int i8 = red;
            double d13 = i8;
            Double.isNaN(d13);
            int i9 = (int) (d13 * d12);
            cred = i9;
            double d14 = green;
            Double.isNaN(d14);
            cgreen = (int) (d14 * d12);
            double d15 = blue;
            Double.isNaN(d15);
            cblue = (int) (d12 * d15);
            double d16 = (1.25d * d) / 255.0d;
            double d17 = i8;
            Double.isNaN(d17);
            cred = addcolor(i8, i9 + ((int) (d17 * d16)));
            int i10 = green;
            int i11 = cgreen;
            double d18 = i10;
            Double.isNaN(d18);
            cgreen = addcolor(i10, i11 + ((int) (d18 * d16)));
            int i12 = blue;
            int i13 = cblue;
            double d19 = i12;
            Double.isNaN(d19);
            cblue = addcolor(i12, i13 + ((int) (d16 * d19)));
        }
        if (i > -2000 && i <= -1500) {
            double dAbs3 = Math.abs(i) - 1500;
            Double.isNaN(dAbs3);
            double d20 = ((int) (dAbs3 * 0.0667d)) + 100;
            Double.isNaN(d20);
            double d21 = d20 / 255.0d;
            int i14 = red;
            double d22 = i14;
            Double.isNaN(d22);
            int i15 = (int) (d22 * d21);
            cred = i15;
            double d23 = green;
            Double.isNaN(d23);
            cgreen = (int) (d23 * d21);
            double d24 = blue;
            Double.isNaN(d24);
            cblue = (int) (d21 * d24);
            double d25 = d / 255.0d;
            double d26 = i14;
            Double.isNaN(d26);
            cred = addcolor(i14, i15 + ((int) (d26 * d25)));
            int i16 = green;
            int i17 = cgreen;
            double d27 = i16;
            Double.isNaN(d27);
            cgreen = addcolor(i16, i17 + ((int) (d27 * d25)));
            int i18 = blue;
            int i19 = cblue;
            double d28 = i18;
            Double.isNaN(d28);
            cblue = addcolor(i18, i19 + ((int) (d25 * d28)));
        }
        if (i > -1500 && i <= -880) {
            double dAbs4 = Math.abs(i) - 880;
            Double.isNaN(dAbs4);
            double d29 = ((int) (dAbs4 * 0.07d)) + 50;
            Double.isNaN(d29);
            double d30 = d29 / 255.0d;
            int i20 = red;
            double d31 = i20;
            Double.isNaN(d31);
            int i21 = (int) (d31 * d30);
            cred = i21;
            double d32 = green;
            Double.isNaN(d32);
            cgreen = (int) (d32 * d30);
            double d33 = blue;
            Double.isNaN(d33);
            cblue = (int) (d30 * d33);
            double d34 = (1.3d * d) / 255.0d;
            double d35 = i20;
            Double.isNaN(d35);
            cred = addcolor(i20, i21 + ((int) (d35 * d34)));
            int i22 = green;
            int i23 = cgreen;
            double d36 = i22;
            Double.isNaN(d36);
            cgreen = addcolor(i22, i23 + ((int) (d36 * d34)));
            int i24 = blue;
            int i25 = cblue;
            double d37 = i24;
            Double.isNaN(d37);
            cblue = addcolor(i24, i25 + ((int) (d34 * d37)));
        }
        if (i > -880 && i <= -800) {
            double dAbs5 = Math.abs(i) - 800;
            Double.isNaN(dAbs5);
            double d38 = ((int) (dAbs5 * 0.9325d)) + com.xiaoyu.onehlight.R.styleable.AppCompatTheme_windowFixedWidthMajor;
            Double.isNaN(d38);
            double d39 = d38 / 255.0d;
            int i26 = red;
            double d40 = i26;
            Double.isNaN(d40);
            int i27 = (int) (d40 * d39);
            cred = i27;
            double d41 = green;
            Double.isNaN(d41);
            cgreen = (int) (d41 * d39);
            double d42 = blue;
            Double.isNaN(d42);
            cblue = (int) (d39 * d42);
            int i28 = i26 + 2;
            double d43 = (1.5d * d) / 255.0d;
            double d44 = i26;
            Double.isNaN(d44);
            cred = addcolor(i28, i27 + ((int) (d44 * d43)));
            int i29 = green;
            int i30 = i29 + 2;
            int i31 = cgreen;
            double d45 = i29;
            Double.isNaN(d45);
            cgreen = addcolor(i30, i31 + ((int) (d45 * d43)));
            int i32 = blue;
            int i33 = i32 + 2;
            int i34 = cblue;
            double d46 = i32;
            Double.isNaN(d46);
            cblue = addcolor(i33, i34 + ((int) (d43 * d46)));
        }
        if (i > -800 && i <= -700) {
            double dAbs6 = ((Math.abs(i) - 700) * 2) + 100;
            Double.isNaN(dAbs6);
            double d47 = dAbs6 / 255.0d;
            int i35 = red;
            double d48 = i35;
            Double.isNaN(d48);
            int i36 = (int) (d48 * d47);
            cred = i36;
            double d49 = green;
            Double.isNaN(d49);
            cgreen = (int) (d49 * d47);
            double d50 = blue;
            Double.isNaN(d50);
            cblue = (int) (d47 * d50);
            int i37 = i35 + 5;
            double d51 = (2.0d * d) / 255.0d;
            double d52 = i35;
            Double.isNaN(d52);
            cred = addcolor(i37, i36 + ((int) (d52 * d51)));
            int i38 = green;
            int i39 = i38 + 5;
            int i40 = cgreen;
            double d53 = i38;
            Double.isNaN(d53);
            cgreen = addcolor(i39, i40 + ((int) (d53 * d51)));
            int i41 = blue;
            int i42 = i41 + 5;
            int i43 = cblue;
            double d54 = i41;
            Double.isNaN(d54);
            cblue = addcolor(i42, i43 + ((int) (d51 * d54)));
        }
        if (i > -700) {
            int i44 = red;
            double d55 = i44;
            Double.isNaN(d55);
            int i45 = (int) (d55 * 2.53d);
            cred = i45;
            double d56 = green;
            Double.isNaN(d56);
            cgreen = (int) (d56 * 2.53d);
            double d57 = blue;
            Double.isNaN(d57);
            cblue = (int) (d57 * 2.53d);
            int i46 = i44 + 10;
            double d58 = d / 255.0d;
            double d59 = i44;
            Double.isNaN(d59);
            cred = addcolor(i46, i45 + ((int) (d59 * d58)));
            int i47 = green;
            int i48 = i47 + 10;
            int i49 = cgreen;
            double d60 = i47;
            Double.isNaN(d60);
            cgreen = addcolor(i48, i49 + ((int) (d60 * d58)));
            int i50 = blue;
            int i51 = i50 + 10;
            int i52 = cblue;
            double d61 = i50;
            Double.isNaN(d61);
            cblue = addcolor(i51, i52 + ((int) (d58 * d61)));
        }
    }

    private void setupEqualizeFxAndUi() {
        Equalizer equalizer = new Equalizer(0, this.myMediaPlayer.getAudioSessionId());
        this.mEqualizer = equalizer;
        equalizer.setEnabled(true);
        short numberOfBands = this.mEqualizer.getNumberOfBands();
        short s = this.mEqualizer.getBandLevelRange()[0];
        short s2 = this.mEqualizer.getBandLevelRange()[1];
        for (short s3 = 0; s3 < numberOfBands; s3 = (short) (s3 + 1)) {
            this.mEqualizer.getCenterFreq(s3);
            this.mEqualizer.setBandLevel(s3, (short) -15);
        }
    }

    class VisualizerView extends View {
        private byte[] mBytes;
        private Paint mPaint;
        private float[] mPoints;
        private Rect mRect;

        private void init() {
            this.mBytes = null;
            this.mPaint.setStrokeWidth(10.0f);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setColor(SupportMenu.CATEGORY_MASK);
        }

        public VisualizerView(Context context) {
            super(context);
            this.mRect = new Rect();
            this.mPaint = new Paint();
            init();
        }

        public void updateVisualizer(byte[] bArr) {
            this.mBytes = bArr;
            invalidate();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int i;
            int i2;
            super.onDraw(canvas);
            MusicActivity.this.w = getWidth();
            MusicActivity.this.h = getHeight();
            byte[] bArr = this.mBytes;
            if (bArr == null) {
                return;
            }
            float[] fArr = this.mPoints;
            if (fArr == null || fArr.length < bArr.length * 4) {
                this.mPoints = new float[bArr.length * 4];
            }
            this.mRect.set(0, 0, getWidth(), getHeight());
            MusicActivity.this.points.clear();
            int i3 = 0;
            while (true) {
                if (i3 >= this.mBytes.length - 1) {
                    break;
                }
                int i4 = i3 * 4;
                this.mPoints[i4] = (this.mRect.width() * i3) / (this.mBytes.length - 1);
                this.mPoints[i4 + 1] = (this.mRect.height() / 2) + ((((byte) (this.mBytes[i3] + 128)) * (this.mRect.height() / 2)) / 128);
                i3++;
                this.mPoints[i4 + 2] = (this.mRect.width() * i3) / (this.mBytes.length - 1);
                int i5 = i4 + 3;
                this.mPoints[i5] = (this.mRect.height() / 2) + ((((byte) (this.mBytes[i3] + 128)) * (this.mRect.height() / 2)) / 128);
                MusicActivity.this.points.add(Float.valueOf(this.mPoints[i5]));
            }
            float fFloatValue = 0.0f;
            int i6 = 1;
            int i7 = 0;
            int i8 = 0;
            int i9 = 0;
            int i10 = 0;
            int i11 = 0;
            int i12 = 0;
            float fFloatValue2 = 200.0f;
            for (i = 1; i6 < MusicActivity.this.points.size() - i; i = 1) {
                int i13 = i6 + 1;
                if (((Float) MusicActivity.this.points.get(i6)).floatValue() > ((Float) MusicActivity.this.points.get(i13)).floatValue() && ((Float) MusicActivity.this.points.get(i6)).floatValue() > ((Float) MusicActivity.this.points.get(i6 - 1)).floatValue()) {
                    i10++;
                    if (((Float) MusicActivity.this.points.get(i6)).floatValue() > fFloatValue) {
                        fFloatValue = ((Float) MusicActivity.this.points.get(i6)).floatValue();
                    }
                    if (((Float) MusicActivity.this.points.get(i6)).floatValue() < MusicActivity.HEIGHT) {
                        i12++;
                    }
                    if (((Float) MusicActivity.this.points.get(i6)).floatValue() > MusicActivity.HEIGHT) {
                        int i14 = (((Float) MusicActivity.this.points.get(i6)).floatValue() > (MusicActivity.HEIGHT + 20) ? 1 : (((Float) MusicActivity.this.points.get(i6)).floatValue() == (MusicActivity.HEIGHT + 20) ? 0 : -1));
                    }
                    if (((Float) MusicActivity.this.points.get(i6)).floatValue() > MusicActivity.HEIGHT + 20 && ((Float) MusicActivity.this.points.get(i6)).floatValue() <= MusicActivity.HEIGHT + 40) {
                        i11++;
                    }
                    if (((Float) MusicActivity.this.points.get(i6)).floatValue() > MusicActivity.HEIGHT + 40 && ((Float) MusicActivity.this.points.get(i6)).floatValue() <= MusicActivity.HEIGHT + 60) {
                        i9++;
                    }
                    if (((Float) MusicActivity.this.points.get(i6)).floatValue() > MusicActivity.HEIGHT + 60 && ((Float) MusicActivity.this.points.get(i6)).floatValue() <= MusicActivity.HEIGHT + 80) {
                        i8++;
                    }
                    if (((Float) MusicActivity.this.points.get(i6)).floatValue() > MusicActivity.HEIGHT + 80) {
                        i7++;
                    }
                }
                if (((Float) MusicActivity.this.points.get(i6)).floatValue() < ((Float) MusicActivity.this.points.get(i13)).floatValue() && ((Float) MusicActivity.this.points.get(i6)).floatValue() < ((Float) MusicActivity.this.points.get(i6 - 1)).floatValue() && ((Float) MusicActivity.this.points.get(i6)).floatValue() < fFloatValue2) {
                    fFloatValue2 = ((Float) MusicActivity.this.points.get(i6)).floatValue();
                }
                i6 = i13;
            }
            if (i7 > 0) {
                MusicActivity.this.oldRms = 900 - (i7 * 100);
                MusicActivity musicActivity = MusicActivity.this;
                musicActivity.oldRms = -musicActivity.oldRms;
            } else if (i8 > 0) {
                MusicActivity.this.oldRms = 3300 - (i8 * 80);
                MusicActivity musicActivity2 = MusicActivity.this;
                musicActivity2.oldRms = -musicActivity2.oldRms;
            } else if (i9 > 0) {
                MusicActivity.this.oldRms = 6000 - (i9 * 80);
                MusicActivity musicActivity3 = MusicActivity.this;
                musicActivity3.oldRms = -musicActivity3.oldRms;
            } else if (i11 > 0) {
                MusicActivity.this.oldRms = 8000 - (i11 * 80);
                MusicActivity musicActivity4 = MusicActivity.this;
                musicActivity4.oldRms = -musicActivity4.oldRms;
            }
            MusicActivity.calpha = 0;
            if (i10 > 80 && i7 > 1) {
                MusicActivity.calpha = 5;
            }
            if (i10 > 80 && i7 > 3) {
                MusicActivity.calpha = 8;
            }
            if (i10 > 80 && i7 > 5) {
                MusicActivity.calpha = 13;
            }
            canvas.drawLine(20.0f, MusicActivity.this.h - 1, 20.0f, 150 - (i12 * 5), this.mPaint);
            canvas.drawLine(50.0f, MusicActivity.this.h - 1, 50.0f, 150 - (i11 * 5), this.mPaint);
            canvas.drawLine(90.0f, MusicActivity.this.h - 1, 90.0f, 150 - (i8 * 5), this.mPaint);
            canvas.drawLine(20.0f, (i8 * 3) + 20, 20.0f, 20.0f, this.mPaint);
            canvas.drawLine(50.0f, (i11 * 3) + 20, 50.0f, 20.0f, this.mPaint);
            canvas.drawLine(90.0f, (i12 * 3) + 20, 90.0f, 20.0f, this.mPaint);
            canvas.drawLines(this.mPoints, this.mPaint);
            if (i10 >= MusicActivity.HEIGHT_FREQUENCY) {
                double d = MusicActivity.red;
                Double.isNaN(d);
                MusicActivity.cred = (int) (d * 1.5d);
                double d2 = MusicActivity.green;
                Double.isNaN(d2);
                MusicActivity.cgreen = (int) (d2 * 1.5d);
                double d3 = MusicActivity.blue;
                Double.isNaN(d3);
                MusicActivity.cblue = (int) (d3 * 1.5d);
                MusicActivity.cred = MusicActivity.this.addcolor(MusicActivity.cred, 0);
                MusicActivity.cgreen = MusicActivity.this.addcolor(MusicActivity.cgreen, 0);
                MusicActivity.cblue = MusicActivity.this.addcolor(MusicActivity.cblue, 0);
            } else if (i10 >= MusicActivity.HEIGHT_FREQUENCY - 30) {
                double d4 = MusicActivity.red;
                Double.isNaN(d4);
                MusicActivity.cred = (int) (d4 * 1.3d);
                double d5 = MusicActivity.green;
                Double.isNaN(d5);
                MusicActivity.cgreen = (int) (d5 * 1.3d);
                double d6 = MusicActivity.blue;
                Double.isNaN(d6);
                MusicActivity.cblue = (int) (d6 * 1.3d);
                MusicActivity.cred = MusicActivity.this.addcolor(MusicActivity.cred, 0);
                MusicActivity.cgreen = MusicActivity.this.addcolor(MusicActivity.cgreen, 0);
                MusicActivity.cblue = MusicActivity.this.addcolor(MusicActivity.cblue, 0);
            } else if (i10 >= MusicActivity.HEIGHT_FREQUENCY - 50) {
                double d7 = MusicActivity.red;
                Double.isNaN(d7);
                MusicActivity.cred = (int) (d7 * 1.1d);
                double d8 = MusicActivity.green;
                Double.isNaN(d8);
                MusicActivity.cgreen = (int) (d8 * 1.1d);
                double d9 = MusicActivity.blue;
                Double.isNaN(d9);
                MusicActivity.cblue = (int) (d9 * 1.1d);
                MusicActivity.cred = MusicActivity.this.addcolor(MusicActivity.cred, 0);
                MusicActivity.cgreen = MusicActivity.this.addcolor(MusicActivity.cgreen, 0);
                MusicActivity.cblue = MusicActivity.this.addcolor(MusicActivity.cblue, 0);
            } else if (i10 >= MusicActivity.HEIGHT_FREQUENCY - 80) {
                MusicActivity.cred = MusicActivity.this.addcolor(MusicActivity.red, 0);
                MusicActivity.cgreen = MusicActivity.this.addcolor(MusicActivity.green, 0);
                MusicActivity.cblue = MusicActivity.this.addcolor(MusicActivity.blue, 0);
            } else if (i10 < 20) {
                double d10 = MusicActivity.red;
                Double.isNaN(d10);
                MusicActivity.cred = (int) (d10 * 0.5d);
                double d11 = MusicActivity.green;
                Double.isNaN(d11);
                MusicActivity.cgreen = (int) (d11 * 0.5d);
                double d12 = MusicActivity.blue;
                Double.isNaN(d12);
                MusicActivity.cblue = (int) (d12 * 0.5d);
                MusicActivity.cred = MusicActivity.this.addcolor(MusicActivity.cred, 0);
                MusicActivity.cgreen = MusicActivity.this.addcolor(MusicActivity.cgreen, 0);
                MusicActivity.cblue = MusicActivity.this.addcolor(MusicActivity.cblue, 0);
            } else if (i10 < 10) {
                double d13 = MusicActivity.red;
                Double.isNaN(d13);
                MusicActivity.cred = (int) (d13 * 0.2d);
                double d14 = MusicActivity.green;
                Double.isNaN(d14);
                MusicActivity.cgreen = (int) (d14 * 0.2d);
                double d15 = MusicActivity.blue;
                Double.isNaN(d15);
                MusicActivity.cblue = (int) (d15 * 0.2d);
                MusicActivity.cred = MusicActivity.this.addcolor(MusicActivity.cred, 0);
                MusicActivity.cgreen = MusicActivity.this.addcolor(MusicActivity.cgreen, 0);
                MusicActivity.cblue = MusicActivity.this.addcolor(MusicActivity.cblue, 0);
            }
            if (fFloatValue >= 255.0f) {
                MusicActivity.cred = MusicActivity.this.addcolor(MusicActivity.cred, 15);
                MusicActivity.cgreen = MusicActivity.this.addcolor(MusicActivity.cgreen, 15);
                MusicActivity.cblue = MusicActivity.this.addcolor(MusicActivity.cblue, 15);
                return;
            }
            if (fFloatValue >= 200.0f) {
                MusicActivity.cred = MusicActivity.this.addcolor(MusicActivity.cred, 8);
                MusicActivity.cgreen = MusicActivity.this.addcolor(MusicActivity.cgreen, 8);
                MusicActivity.cblue = MusicActivity.this.addcolor(MusicActivity.cblue, 8);
                return;
            }
            if (fFloatValue >= 180.0f) {
                MusicActivity.cred = MusicActivity.this.addcolor(MusicActivity.cred, 3);
                MusicActivity.cgreen = MusicActivity.this.addcolor(MusicActivity.cgreen, 3);
                MusicActivity.cblue = MusicActivity.this.addcolor(MusicActivity.cblue, 3);
                return;
            }
            if (fFloatValue < 150.0f) {
                if (MusicActivity.cred > MusicActivity.cgreen && MusicActivity.cred > MusicActivity.cblue) {
                    MusicActivity.cred = 30;
                    MusicActivity.cgreen = 5;
                    MusicActivity.cblue = 0;
                }
                if (MusicActivity.cgreen <= MusicActivity.cred || MusicActivity.cgreen <= MusicActivity.cblue) {
                    i2 = 0;
                } else {
                    MusicActivity.cred = 5;
                    MusicActivity.cgreen = 30;
                    i2 = 0;
                    MusicActivity.cblue = 0;
                }
                if (MusicActivity.cblue > MusicActivity.cred && MusicActivity.cblue > MusicActivity.cgreen) {
                    MusicActivity.cred = 5;
                    MusicActivity.cgreen = i2;
                    MusicActivity.cblue = 30;
                }
                MusicActivity.calpha = i2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getcolorchang() {
        this.lin_c.setBackgroundColor(Color.argb(255, cred, cgreen, cblue));
        Log.e("--", "cblue=" + cblue);
        setMusicColor(cblue);
    }

    private void addColor() {
        int iFloor = (int) Math.floor(Math.random() * 6.0d);
        this.a = iFloor;
        if (iFloor < 0) {
            iFloor = 0;
        }
        this.a = iFloor;
        if (iFloor > 5) {
            iFloor = 5;
        }
        this.a = iFloor;
        if (iFloor == 0) {
            red = 200;
            blue = 0;
            int i = green + changenum;
            green = i;
            if (i >= 200) {
                green = 200;
                this.a = 1;
                return;
            }
            return;
        }
        if (iFloor == 1) {
            green = 200;
            int i2 = red - changenum;
            red = i2;
            blue = 0;
            if (i2 <= 0) {
                red = 0;
                this.a = 2;
                return;
            }
            return;
        }
        if (iFloor == 2) {
            green = 200;
            int i3 = blue + changenum;
            blue = i3;
            red = 0;
            if (i3 >= 200) {
                blue = 200;
                this.a = 3;
                return;
            }
            return;
        }
        if (iFloor == 3) {
            blue = 200;
            int i4 = green - changenum;
            green = i4;
            red = 0;
            if (i4 <= 0) {
                green = 0;
                this.a = 4;
                return;
            }
            return;
        }
        if (iFloor == 4) {
            blue = 200;
            int i5 = red + changenum;
            red = i5;
            green = 0;
            if (i5 >= 200) {
                red = 200;
                this.a = 5;
                return;
            }
            return;
        }
        if (iFloor != 5) {
            if (iFloor == 6) {
                int i6 = changenum;
                red = 100 - i6;
                blue = 100 - i6;
                green = 100 - i6;
                return;
            }
            return;
        }
        red = 200;
        int i7 = blue - changenum;
        blue = i7;
        green = 0;
        if (i7 <= 0) {
            blue = 0;
            this.a = 0;
        }
    }

    private void setMusicColor(int i) {
        MyBluetoothGatt myBluetoothGatt;
        MyBluetoothGatt myBluetoothGatt2;
        if (this.mMyApplication.isOpenMusicHop()) {
            for (String str : MainActivity.ControlMACs.keySet()) {
                if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt2 = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null) {
                    byte b = myBluetoothGatt2.datas[2];
                }
                if (this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.containsKey(str) && (myBluetoothGatt = this.mMyApplication.mBluetoothLeService.MyBluetoothGatts.get(str)) != null && myBluetoothGatt.mConnectionState == 2) {
                    myBluetoothGatt.isopenmyMIC = false;
                    myBluetoothGatt.setMusicColor(i);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pauseMusic() {
        if (this.ispauseMusic) {
            return;
        }
        this.ispauseMusic = true;
        this.audioManager.requestAudioFocus(this.listener, 3, 2);
    }
}
