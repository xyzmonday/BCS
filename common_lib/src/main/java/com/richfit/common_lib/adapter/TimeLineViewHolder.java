package com.richfit.common_lib.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.richfit.common_lib.R;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    private ExpandableTextView mErrorMessage;

    public TimeLineViewHolder(View itemView, int type) {
        super(itemView);

        mErrorMessage = (ExpandableTextView) itemView.findViewById(R.id.item_time_line_txt);

//        TimeLineMarker mMarker = (TimeLineMarker) itemView.findViewById(R.id.item_time_line_mark);
//
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
        mErrorMessage.setText(data, collapsedStatus, position);
//        mErrorMessage.setText(data);
    }
}