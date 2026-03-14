package com.qh.tools;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/* JADX INFO: loaded from: classes.dex */
public class MusicData {
    public static final int PLAY_MOD_ORDER = 2;
    public static final int PLAY_MOD_RANDOM = 1;
    public static final int PLAY_MOD_SIGLE = 0;

    public static Cursor getMP3MusicInfo(Context context, String str) {
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, "_data like '%.mp3'", null, "title_key");
    }
}
