package com.richfit.common_lib.baseadapterrv;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.richfit.common_lib.IInterface.IFragmentState;
import com.richfit.common_lib.R;
import com.richfit.common_lib.baseadapterrv.base.ItemViewDelegate;
import com.richfit.common_lib.baseadapterrv.base.ItemViewDelegateManager;
import com.richfit.common_lib.baseadapterrv.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhy on 16/4/9.
 */
public class MultiItemTypeAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    protected Context mContext;
    protected List<T> mDatas;

    protected ItemViewDelegateManager mItemViewDelegateManager;
    protected OnItemClickListener mOnItemClickListener;
    protected IFragmentState<T> mFragmentState;


    public MultiItemTypeAdapter(Context context, List<T> datas) {
        mContext = context;
        mDatas = datas;
        mItemViewDelegateManager = new ItemViewDelegateManager();
    }

    @Override
    public int getItemViewType(int position) {
        if (!useItemViewDelegateManager()) return super.getItemViewType(position);
        return mItemViewDelegateManager.getItemViewType(mDatas.get(position), position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, layoutId);
        onViewHolderCreated(holder, holder.getConvertView());
        setListener(parent, holder, viewType);
        return holder;
    }

    public void onViewHolderCreated(ViewHolder holder, View itemView) {

    }

    public void convert(ViewHolder holder, T t) {
        mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }

    public void removeAll() {
        if (mDatas != null) {
            mDatas.clear();
            notifyDataSetChanged();
        }
    }

    public void addAll(List<T> data) {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        mDatas.clear();
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        if (mDatas != null && position >= 0 && position < mDatas.size()) {
            return mDatas.get(position);
        }
        return null;
    }


    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                int position = viewHolder.getAdapterPosition();
                mOnItemClickListener.onItemClick(v, viewHolder, position);
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(v -> {
            if (mOnItemClickListener != null) {
                int position = viewHolder.getAdapterPosition();
                return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
            }
            return false;
        });
        setItemNodeEditAndDeleteListener(viewHolder);
    }

    public void setOnItemEditAndDeleteListener(IFragmentState<T> listener) {
        mFragmentState = listener;
    }

    private void setItemNodeEditAndDeleteListener(ViewHolder holder) {
        //设置点击和删除监听
        holder.setOnClickListener(R.id.item_edit, view -> {
            if (mFragmentState != null) {
                int position = holder.getAdapterPosition();
                mFragmentState.editNode(mDatas.get(position), position);
            }
        });
        holder.setOnClickListener(R.id.item_delete, view -> {
            int position = holder.getAdapterPosition();
            showDialogForNodeDelete(mDatas.get(position), position);
        });
    }

    /**
     * 提示用户是否删除该子节点
     *
     * @param node：将要删除的子节点
     * @param position：将要删除的子节点在显示列表的位置
     */
    protected void showDialogForNodeDelete(final T node, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("警告");
        builder.setIcon(R.mipmap.icon_warning);
        builder.setMessage("您真的要删除该条数据?点击确定删除.");
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("确定", (dialog, which) -> {
            if (node != null) {
                if (mFragmentState != null) {
                    mFragmentState.deleteNode(node, position);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        convert(holder, mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        int itemCount = mDatas.size();
        return itemCount;
    }


    public List<T> getDatas() {
        return mDatas;
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public MultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
