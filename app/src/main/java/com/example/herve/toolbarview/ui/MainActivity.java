package com.example.herve.toolbarview.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.herve.toolbarview.R;
import com.example.herve.toolbarview.adapter.PreViewItemAdapter;
import com.example.herve.toolbarview.bean.MaterialItemBean;
import com.example.herve.toolbarview.common.AppConstant;
import com.example.herve.toolbarview.view.PreViewBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout activityMain;
    private PreViewBar previewBar;
    private Button btnPlay;
    private Button btn_next;
    private Button btn_add;
    private Button btn_remove;
    private PreViewItemAdapter preViewItemAdapter;

    private Context mContext;
    private ArrayList<String> data;
    private ArrayList<MaterialItemBean> materialItemBeans;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

        activityMain = (RelativeLayout) findViewById(R.id.activity_main);
        previewBar = (PreViewBar) findViewById(R.id.preview_bar);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_remove = (Button) findViewById(R.id.btn_remove);

        initData();


        initListener();

    }


    private boolean isScroll = false;

    private void initListener() {

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                preViewItemAdapter.removeMaterialItem(1);

            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                preViewItemAdapter.addMaterialItem();

            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isScroll) {
                    previewBar.setTranslateStop();
                } else {
                    previewBar.setTranslateTime(600);
                }

                isScroll = !isScroll;
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppConstant.materialItemBeans = materialItemBeans;

                Intent intent = new Intent(mContext, EmptyActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }


    private void initData() {
        data = new ArrayList<>();

        if (AppConstant.materialItemBeans == null
                || AppConstant.materialItemBeans.size() <= 0) {
            AppConstant.materialItemBeans = new ArrayList<>();

            for (int i = 0; i < 1; i++) {
                AppConstant.materialItemBeans.add(new MaterialItemBean());
            }
        }

        materialItemBeans = AppConstant.materialItemBeans;
        for (int i = 0; i < 5; i++) {
            data.add("第" + i + "个");
        }

        preViewItemAdapter = new PreViewItemAdapter(mContext);

        preViewItemAdapter.setData(data);
        preViewItemAdapter.setMaterialData(previewBar, materialItemBeans);

        previewBar.setAdapter(preViewItemAdapter);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
