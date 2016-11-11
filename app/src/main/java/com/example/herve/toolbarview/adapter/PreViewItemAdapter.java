package com.example.herve.toolbarview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.herve.toolbarview.R;

/**
 * Created           :Herve on 2016/11/10.
 *
 * @ Author          :Herve
 * @ e-mail          :lijianyou.herve@gmail.com
 * @ LastEdit        :2016/11/10
 * @ projectName     :ToolBarView
 * @ version
 */
public class PreViewItemAdapter extends HeadFootBaseAdapter<PreViewItemAdapter.PreViewItemViewHolder, String> {


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
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(mContext, "点击了=" + position, Toast.LENGTH_SHORT).show();
//            }
//        });

        holder.tvItem.setText(data.get(position));


    }

    class PreViewItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItem;

        public PreViewItemViewHolder(View itemView) {
            super(itemView);
            tvItem = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }




}