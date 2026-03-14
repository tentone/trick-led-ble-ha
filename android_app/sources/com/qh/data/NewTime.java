package com.qh.data;

/* JADX INFO: loaded from: classes.dex */
public class NewTime {
    public int h;
    public int m;
    public boolean open;
    public int s;
    public boolean valid;
    public byte w;

    public NewTime() {
        this.valid = false;
        this.h = 0;
        this.m = 0;
        this.s = 0;
        this.w = (byte) 0;
        this.open = true;
    }

    public NewTime(boolean z, int i, int i2, int i3, byte b, boolean z2) {
        this.valid = false;
        this.h = 0;
        this.m = 0;
        this.s = 0;
        this.w = (byte) 0;
        this.open = true;
        this.valid = z;
        this.h = i;
        this.m = i2;
        this.s = i3;
        this.w = b;
        this.open = z2;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean z) {
        this.valid = z;
    }

    public int getH() {
        return this.h;
    }

    public void setH(int i) {
        this.h = i;
    }

    public int getM() {
        return this.m;
    }

    public void setM(int i) {
        this.m = i;
    }

    public int getS() {
        return this.s;
    }

    public void setS(int i) {
        this.s = i;
    }

    public int getW() {
        return this.w;
    }

    public void setW(byte b) {
        this.w = b;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean z) {
        this.open = z;
    }
}
