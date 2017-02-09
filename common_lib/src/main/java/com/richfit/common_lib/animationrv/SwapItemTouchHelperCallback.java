package com.richfit.common_lib.animationrv;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * 不允许左右滑动删除
 * Created by monday on 2016/5/23.
 */
public class SwapItemTouchHelperCallback extends ItemTouchHelper.Callback {

    /**
     * Recycleview的adapter必须实现ItemTouchHelperAdapter接口，这样在移动或者滑动删除的时候，
     * 可以通过adapter的引用可以回到接口的方法
     */
    private ItemTouchHelperAdapter mAdapter;

    public SwapItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int swipeFlag = 0;
            final int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            return makeMovementFlags(dragFlag, swipeFlag);
        }
    }

    /**
     * 通知adapter该item要删除
     * @param recyclerView
     * @param source
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if(source.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        mAdapter.onItemMove(source.getAdapterPosition(),target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
