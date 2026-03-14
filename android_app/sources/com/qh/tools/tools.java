package com.qh.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.AudioManager;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class tools {
    private static String strRingtoneFolder = "/system/media/audio/ringtones";

    public static void recoverRound(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMode(0);
    }

    public static AlphaAnimation getAnimation(float f, float f2, long j, int i) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f2);
        alphaAnimation.setDuration(j);
        alphaAnimation.setRepeatCount(i);
        alphaAnimation.setRepeatMode(2);
        alphaAnimation.setFillAfter(true);
        return alphaAnimation;
    }

    public static Bitmap createCircleImage(Bitmap bitmap, int i) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        float f = i / 2;
        canvas.drawCircle(f, f, f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return bitmapCreateBitmap;
    }

    public static void doVibrator(boolean z, Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        if (vibrator.hasVibrator()) {
            if (!z) {
                vibrator.cancel();
            } else {
                vibrator.vibrate(new long[]{10, 200}, -1);
            }
        }
    }

    public static byte[] BitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] BitmapToByte(Bitmap bitmap, int i) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, i, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public static Bitmap ByteToBitmap(byte[] bArr) {
        return BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
    }

    public static Bitmap compressImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        int i = 100;
        while (byteArrayOutputStream.toByteArray().length / 1024 > 100) {
            byteArrayOutputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, i, byteArrayOutputStream);
            i -= 10;
            if (i <= 0) {
                break;
            }
        }
        return BitmapFactory.decodeStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), null, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x005e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.Bitmap comp(android.graphics.Bitmap r8) {
        /*
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream
            r0.<init>()
            android.graphics.Bitmap$CompressFormat r1 = android.graphics.Bitmap.CompressFormat.JPEG
            r2 = 100
            r8.compress(r1, r2, r0)
            byte[] r1 = r0.toByteArray()
            int r1 = r1.length
            int r1 = r1 / 1024
            r2 = 400(0x190, float:5.6E-43)
            if (r1 <= r2) goto L21
            r0.reset()
            android.graphics.Bitmap$CompressFormat r1 = android.graphics.Bitmap.CompressFormat.JPEG
            r2 = 50
            r8.compress(r1, r2, r0)
        L21:
            java.io.ByteArrayInputStream r8 = new java.io.ByteArrayInputStream
            byte[] r1 = r0.toByteArray()
            r8.<init>(r1)
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options
            r1.<init>()
            r2 = 1
            r1.inJustDecodeBounds = r2
            r3 = 0
            android.graphics.BitmapFactory.decodeStream(r8, r3, r1)
            r8 = 0
            r1.inJustDecodeBounds = r8
            int r8 = r1.outWidth
            int r4 = r1.outHeight
            r5 = 1145569280(0x44480000, float:800.0)
            r6 = 1139802112(0x43f00000, float:480.0)
            if (r8 <= r4) goto L4e
            float r7 = (float) r8
            int r7 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1))
            if (r7 <= 0) goto L4e
            int r8 = r1.outWidth
            float r8 = (float) r8
            float r8 = r8 / r6
        L4c:
            int r8 = (int) r8
            goto L5b
        L4e:
            if (r8 >= r4) goto L5a
            float r8 = (float) r4
            int r8 = (r8 > r5 ? 1 : (r8 == r5 ? 0 : -1))
            if (r8 <= 0) goto L5a
            int r8 = r1.outHeight
            float r8 = (float) r8
            float r8 = r8 / r5
            goto L4c
        L5a:
            r8 = 1
        L5b:
            if (r8 > 0) goto L5e
            goto L5f
        L5e:
            r2 = r8
        L5f:
            r1.inSampleSize = r2
            java.io.ByteArrayInputStream r8 = new java.io.ByteArrayInputStream
            byte[] r0 = r0.toByteArray()
            r8.<init>(r0)
            android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeStream(r8, r3, r1)
            android.graphics.Bitmap r8 = compressImage(r8)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qh.tools.tools.comp(android.graphics.Bitmap):android.graphics.Bitmap");
    }

    public static Bitmap comp(Bitmap bitmap, int i) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        if (byteArrayOutputStream.toByteArray().length / 1024 > 400) {
            byteArrayOutputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(byteArrayInputStream, null, options);
        options.inJustDecodeBounds = false;
        int i2 = options.outWidth;
        int i3 = options.outHeight;
        options.inSampleSize = i;
        return compressImage(BitmapFactory.decodeStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), null, options));
    }

    public static File[] getAllRings(File file) {
        if (file == null) {
            return null;
        }
        return file.listFiles();
    }

    public static Bitmap postScale(Bitmap bitmap, float f) {
        if (bitmap == null) {
            return null;
        }
        if (bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(f, f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static InputStream getfilesFromAssets(Context context, String str) {
        return context.getClass().getClassLoader().getResourceAsStream("assets/" + str);
    }

    public static boolean isHarmonyOs() {
        try {
            Class<?> cls = Class.forName("com.huawei.system.BuildEx");
            return "Harmony".equalsIgnoreCase(cls.getMethod("getOsBrand", new Class[0]).invoke(cls, new Object[0]).toString());
        } catch (Throwable unused) {
            return false;
        }
    }

    public static String getHarmonyVersion() {
        return getProp("hw_sc.build.platform.version", "");
    }

    private static String getProp(String str, String str2) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            String str3 = (String) cls.getDeclaredMethod("get", String.class).invoke(cls, str);
            return TextUtils.isEmpty(str3) ? str2 : str3;
        } catch (Throwable th) {
            th.printStackTrace();
            return str2;
        }
    }
}
