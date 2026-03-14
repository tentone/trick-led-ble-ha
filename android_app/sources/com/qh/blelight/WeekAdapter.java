package com.qh.blelight;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.qh.onehlight.R;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class WeekAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private int mClickPositon;
    private OnWeekClickListener onWeekClickListener;
    private ArrayList<Integer> weekList;

    public interface OnWeekClickListener {
        void scrollMid(int i);
    }

    public WeekAdapter(Context context, ArrayList<Integer> arrayList) {
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        this.weekList = arrayList2;
        this.context = context;
        arrayList2.clear();
        this.weekList.addAll(arrayList);
    }

    public void setOnWeekClickListener(OnWeekClickListener onWeekClickListener) {
        this.onWeekClickListener = onWeekClickListener;
    }

    public void changePostion(int i) {
        if (this.mClickPositon != i) {
            this.mClickPositon = i;
            notifyDataSetChanged();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(View.inflate(this.context, R.layout.week_rec_item, null));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
        int iIntValue = this.weekList.get(i).intValue();
        myViewHolder.itemView.setTag(Integer.valueOf(i));
        if (this.mClickPositon == i) {
            myViewHolder.img_type_mod.setPadding(11, 11, 11, 11);
            myViewHolder.img_q.setVisibility(0);
        } else {
            myViewHolder.img_type_mod.setPadding(22, 22, 22, 22);
            myViewHolder.img_q.setVisibility(4);
        }
        if (iIntValue != 0) {
            myViewHolder.img_type_mod.setImageResource(iIntValue);
            myViewHolder.img_type_mod.setVisibility(0);
        } else {
            myViewHolder.img_type_mod.setVisibility(4);
        }
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.qh.blelight.WeekAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (WeekAdapter.this.onWeekClickListener != null) {
                    WeekAdapter.this.onWeekClickListener.scrollMid(i);
                }
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.weekList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flView;
        ImageView img_q;
        ImageView img_type_mod;

        MyViewHolder(View view) {
            super(view);
            this.flView = (FrameLayout) view.findViewById(R.id.fl_view);
            this.img_type_mod = (ImageView) view.findViewById(R.id.img_type_mod);
            this.img_q = (ImageView) view.findViewById(R.id.img_q);
        }
    }
}
