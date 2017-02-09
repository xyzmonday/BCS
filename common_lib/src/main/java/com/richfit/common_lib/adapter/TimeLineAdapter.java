package com.richfit.common_lib.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.richfit.common_lib.R;

import java.util.List;

/**
 * Created by monday
 * on 15/8/23.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {
    private List<String> mDataSet;
    private final SparseBooleanArray mCollapsedStatus;

    public TimeLineAdapter(List<String> models) {
        mDataSet = models;
        mCollapsedStatus = new SparseBooleanArray();
    }

    @Override
    public int getItemViewType(int position) {
        final int size = mDataSet.size() - 1;
        if (size == 0)
            return ItemType.ATOM;
        else if (position == 0)
            return ItemType.START;
        else if (position == size)
            return ItemType.END;
        else return ItemType.NORMAL;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_show_message, viewGroup, false);
        return new TimeLineViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder viewHolder, int position) {
        viewHolder.setData(mDataSet.get(position), mCollapsedStatus, position);

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
