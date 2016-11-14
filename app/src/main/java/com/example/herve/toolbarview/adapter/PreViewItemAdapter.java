package com.example.herve.toolbarview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.herve.toolbarview.R;
import com.example.herve.toolbarview.bean.MaterialItemBean;
import com.example.herve.toolbarview.view.MaterialItemView;
import com.example.herve.toolbarview.view.PreViewBar;

import java.util.ArrayList;

/**
 * Created           :Herve on 2016/11/10.
 *
 * @ Author          :Herve
 * @ e-mail          :lijianyou.herve@gmail.com
 * @ LastEdit        :2016/11/10
 * @ projectName     :ToolBarView
 * @ version
 */
public class PreViewItemAdapter extends HeadFootBaseAdapter<PreViewItemAdapter.PreViewItemViewHolder, String> implements PreViewBar.PreViewMaterialAdapter {


    private PreViewBar preViewBar;
    private ArrayList<MaterialItemBean> materialData = new ArrayList<>();


    public PreViewItemAdapter(Context mContext) {
        super(mContext);

    }

    @Override
    protected PreViewItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_preview, parent, false);


        return new PreViewItemViewHolder(itemView);
    }

    @Override
    protected void onBindItemViewHolder(PreViewItemViewHolder holder, final int position) {

        holder.tvItem.setText(data.get(position));

    }



    @Override
    public void setMaterialData(PreViewBar preViewBar, ArrayList<MaterialItemBean> materialData) {
        this.preViewBar = preViewBar;
        this.materialData = materialData;

    }

    @Override
    public MaterialItemView getItemMaterialView(ViewGroup parent, int position) {
        MaterialItemView materialItemView = (MaterialItemView) LayoutInflater.from(mContext).inflate(R.layout.item_material_layout, parent, false);
        return materialItemView;
    }

    @Override
    public float getItemTranslateX(int position) {

        return materialData.get(position).getX();
    }

    @Override
    public int getCount() {
        return materialData.size();
    }

    @Override
    public MaterialItemBean getItem(int position) {
        return materialData.get(position);
    }

    @Override
    public void setScrollListener(int position, float scrolledX) {

        Log.e(TAG, "setScrollListener: 元素位置=" + position + "位置变化=" + scrolledX);

        materialData.get(position).setX(scrolledX);

    }

    public void addMaterialItem() {
        materialData.add(new MaterialItemBean());
        preViewBar.addMaterialItem();
    }

    public void removeMaterialItem(int position) {
        preViewBar.removeMaterialItem(position);
        materialData.remove(position);
    }

    class PreViewItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItem;

        public PreViewItemViewHolder(View itemView) {
            super(itemView);
            tvItem = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }


}