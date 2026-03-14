package com.qh.WheelView;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public class AdapterWheel extends AbstractWheelTextAdapter {
    private WheelAdapter adapter;

    public AdapterWheel(Context context, WheelAdapter wheelAdapter) {
        super(context);
        this.adapter = wheelAdapter;
    }

    public WheelAdapter getAdapter() {
        return this.adapter;
    }

    @Override // com.qh.WheelView.WheelViewAdapter
    public int getItemsCount() {
        return this.adapter.getItemsCount();
    }

    @Override // com.qh.WheelView.AbstractWheelTextAdapter
    protected CharSequence getItemText(int i) {
        return this.adapter.getItem(i);
    }
}
