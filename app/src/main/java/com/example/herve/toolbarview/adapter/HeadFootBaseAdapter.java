package com.example.herve.toolbarview.adapter;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public abstract class HeadFootBaseAdapter<T extends RecyclerView.ViewHolder, V> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    protected Context mContext;
    protected String TAG = getClass().getSimpleName() + "HeadFootBaseAdapter";
    protected ArrayList<V> data = new ArrayList<>();

    private T t;
    private SparseArrayCompat<View> headerViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> footerViews = new SparseArrayCompat<>();

    private int VIE_TYPE_SIMPLE = 0;

    public HeadFootBaseAdapter(Context mContext) {
        this.mContext = mContext;

    }

    public void setData(ArrayList<V> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addHeaderView(View headerView) {
        int key = findHeaderKeyByView(headerView);
        if (key == -1) {
            headerViews.put(headerViews.size() + BASE_ITEM_TYPE_HEADER, headerView);
            notifyItemInserted(0);
        }

    }

    public void addFooterView(View footerView) {
        footerViews.put(footerViews.size() + BASE_ITEM_TYPE_FOOTER, footerView);
        notifyItemInserted(getItemCount());

    }

    public int getHeaderViewSize() {
        return headerViews.size();
    }

    public int getFooterViewSize() {
        return footerViews.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position < getHeaderViewSize()) {
            return headerViews.keyAt(position);
        }
        if (position >= getItemCount() - getFooterViewSize()) {
            return footerViews.keyAt(position - data.size() - headerViews.size());
        }
        return VIE_TYPE_SIMPLE;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (headerViews.get(viewType) != null) {
            return new HeaderViewHolder(headerViews.get(viewType));
        } else if (footerViews.get(viewType) != null) {
            return new FooterViewHolder(footerViews.get(viewType));
        } else {
            return onCreateItemViewHolder(parent, viewType);
        }

    }

    protected abstract T onCreateItemViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindItemViewHolder(T holder, final int position);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof HeaderViewHolder) {

            HeaderViewHolder headFootViewHolder = (HeaderViewHolder) holder;
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder headFootViewHolder = (FooterViewHolder) holder;
        } else {
            onBindItemViewHolder((T) holder, position - headerViews.size());
        }


    }


    public void deleteHeaderView(View view) {
        int key = findHeaderKeyByView(view);
        if (key != -1) {
            headerViews.remove(key);
        }
    }

    public void deleteFooterView(View view) {
        int key = findFooterKeyByView(view);
        if (key != -1) {
            footerViews.remove(key);
        }
    }

    private int findFooterKeyByView(View view) {
        for (int i = 0; i < footerViews.size(); i++) {
            int key = footerViews.keyAt(i);
            if (footerViews.get(key) == view) {
                return key;
            }
        }

        return -1;
    }

    private int findHeaderKeyByView(View view) {
        for (int i = 0; i < headerViews.size(); i++) {
            int key = headerViews.keyAt(i);
            if (headerViews.get(key) == view) {
                return key;
            }
        }

        return -1;
    }


    @Override
    public int getItemCount() {
        return data.size()
                + headerViews.size()
                + footerViews.size();
    }


    public int getSimpleCount() {
        return data.size();
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {


        private FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {


        private HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }




}
