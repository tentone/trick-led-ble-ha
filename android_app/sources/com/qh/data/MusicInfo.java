package com.qh.data;

/* JADX INFO: loaded from: classes.dex */
public class MusicInfo {
    private String album;
    private String artist;
    private String audioPath;
    private String comment;
    private String display_name;
    private long playTime;
    private byte r1;
    private byte r2;
    private byte r3;
    private String songName;
    private boolean valid;
    private String year;
    private final String TAG = "TAG";
    private boolean isAssets = false;

    public String getSongName() {
        return this.songName;
    }

    public void setSongName(String str) {
        this.songName = str;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String str) {
        this.artist = str;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String str) {
        this.album = str;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String str) {
        this.year = str;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public byte getR1() {
        return this.r1;
    }

    public void setR1(byte b) {
        this.r1 = b;
    }

    public byte getR2() {
        return this.r2;
    }

    public void setR2(byte b) {
        this.r2 = b;
    }

    public byte getR3() {
        return this.r3;
    }

    public void setR3(byte b) {
        this.r3 = b;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean z) {
        this.valid = z;
    }

    public long getPlayTime() {
        return this.playTime;
    }

    public void setPlayTime(long j) {
        this.playTime = j;
    }

    public String getAudioPath() {
        return this.audioPath;
    }

    public void setAudioPath(String str) {
        this.audioPath = str;
    }

    public String getDisplay_name() {
        return this.display_name;
    }

    public void setDisplay_name(String str) {
        this.display_name = str;
    }

    public MusicInfo() {
    }

    public MusicInfo(byte[] bArr) {
        if (bArr.length != 128) {
            throw new RuntimeException("length" + bArr.length);
        }
        new String(bArr, 0, 3);
        this.valid = true;
        this.songName = new String(bArr, 3, 30).trim();
        this.artist = new String(bArr, 33, 30).trim();
        this.album = new String(bArr, 63, 30).trim();
        this.year = new String(bArr, 93, 4).trim();
        this.comment = new String(bArr, 97, 28).trim();
        this.r1 = bArr[125];
        this.r2 = bArr[126];
        this.r3 = bArr[127];
    }

    public byte[] getBytes() {
        byte[] bArr = new byte[128];
        System.arraycopy("TAG".getBytes(), 0, bArr, 0, 3);
        byte[] bytes = this.songName.getBytes();
        System.arraycopy(bytes, 0, bArr, 3, bytes.length > 30 ? 30 : bytes.length);
        byte[] bytes2 = this.artist.getBytes();
        System.arraycopy(bytes2, 0, bArr, 33, bytes2.length > 30 ? 30 : bytes2.length);
        byte[] bytes3 = this.album.getBytes();
        System.arraycopy(bytes3, 0, bArr, 63, bytes3.length <= 30 ? bytes3.length : 30);
        byte[] bytes4 = this.year.getBytes();
        System.arraycopy(bytes4, 0, bArr, 93, bytes4.length <= 4 ? bytes4.length : 4);
        byte[] bytes5 = this.comment.getBytes();
        System.arraycopy(bytes5, 0, bArr, 97, bytes5.length <= 28 ? bytes5.length : 28);
        bArr[125] = this.r1;
        bArr[126] = this.r2;
        bArr[127] = this.r3;
        return bArr;
    }
}
