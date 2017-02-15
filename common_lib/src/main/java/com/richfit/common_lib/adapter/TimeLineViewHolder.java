package com.richfit.common_lib.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.TextView;

import com.richfit.common_lib.R;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    private TextView mErrorMessage;

    public TimeLineViewHolder(View itemView, int type) {
        super(itemView);

        mErrorMessage = (TextView) itemView.findViewById(R.id.expandable_text);

//        TimeLineMarker mMarker = (TimeLineMarker) itemView.findViewById(R.id.item_time_line_mark);
//        if (type == ItemType.ATOM) {
//            mMarker.setBeginLine(null);
//            mMarker.setEndLine(null);
//        } else if (type == ItemType.START) {
//            mMarker.setBeginLine(null);
//        } else if (type == ItemType.END) {
//            mMarker.setEndLine(null);
//        }

    }

    public void setData(String data, SparseBooleanArray collapsedStatus,int position) {
//        mErrorMessage.setText(data, collapsedStatus, position);
        mErrorMessage.setText(data);
    }
}