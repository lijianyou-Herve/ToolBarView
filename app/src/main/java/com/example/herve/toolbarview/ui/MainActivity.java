package com.example.herve.toolbarview.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.example.herve.toolbarview.R;
import com.example.herve.toolbarview.adapter.PreViewItemAdapter;
import com.example.herve.toolbarview.bean.MaterialItemBean;
import com.example.herve.toolbarview.common.AppConstant;
import com.example.herve.toolbarview.view.ijkplayer.widget.media.IjkVideoView;
import com.example.herve.toolbarview.view.previewbar.MaterialItemView;
import com.example.herve.toolbarview.view.previewbar.PreViewBar;
import com.example.herve.toolbarview.view.previewbar.PreViewBarMediaControl;

import java.util.ArrayList;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout activityMain;
    private PreViewBar previewBar;
    private Button btnPlay;
    private Button btn_next;
    private Button btn_add;
    private Button btn_remove;
    private Button btn_video;
    private IjkVideoView ijk_video;
    private TableLayout hud_view;
    private PreViewBarMediaControl mediaController;
    private PreViewItemAdapter preViewItemAdapter;

    private int selectPosition = -1;
    private Context mContext;
    private ArrayList<String> data;
    private ArrayList<MaterialItemBean> materialItemBeans;
    private final String TAG = getClass().getSimpleName();
    private String url;

    private boolean canPlaying = false;

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
        btn_video = (Button) findViewById(R.id.btn_video);
        hud_view = (TableLayout) findViewById(R.id.hud_view);
        ijk_video = (IjkVideoView) findViewById(R.id.ijk_video);

        initData();

        initListener();

    }


    private void initListener() {
        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ijk_video.isPlaying()) {
                    ijk_video.pause();
                } else {
                    canPlaying = true;
                    previewBar.start();
                    ijk_video.start();
                }

            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                preViewItemAdapter.removeMaterialItem(selectPosition);
                selectPosition = -1;
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

        previewBar.setOnPreViewChangeListener(new PreViewBar.OnPreViewChangeListener() {
            @Override
            public void onTimelineChangeListener(double totalTime, double currentTime) {


            }

            @Override
            public void onMaterialItemSelectListener(MaterialItemView view, int position) {

                selectPosition = position;

            }
        });

        ijk_video.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {

                if (canPlaying) {
                    mp.start();
                }
            }
        });

        ijk_video.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {

                Log.i(TAG, "onInfo: i-" + i);
                Log.i(TAG, "onInfo: i1=" + i1);

                return false;
            }
        });

        mediaController.setOnStateListener(new PreViewBarMediaControl.OnStateListener() {
            @Override
            public void onStateListener(boolean isPlaying) {

                if (isPlaying) {
                    previewBar.start();
                } else {
                    previewBar.pause();
                }
            }
        });

    }

    private float totalTime = 34;

    private void initData() {

        url = Environment.getExternalStorageDirectory() + "/BJX" + "/20160918_104437.mp4";

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mediaController = new PreViewBarMediaControl(mContext);
        ijk_video.setBackgroundColor(Color.BLACK);
        ijk_video.changeAspectRaito(0);
        ijk_video.setRender(2);
        ijk_video.setMediaPlayerType(1);

        ijk_video.setMediaController(mediaController);
        ijk_video.setHudView(hud_view);
        ijk_video.setVideoPath(url);
        previewBar.bindVideoView(ijk_video);
        data = new ArrayList<>();

        if (AppConstant.materialItemBeans == null
                || AppConstant.materialItemBeans.size() <= 0) {
            AppConstant.materialItemBeans = new ArrayList<>();

        }

        materialItemBeans = AppConstant.materialItemBeans;
        for (int i = 0; i < 10; i++) {
            data.add("第" + i + "个");
        }

        preViewItemAdapter = new PreViewItemAdapter(mContext);

        preViewItemAdapter.setData(data);
        preViewItemAdapter.setMaterialData(previewBar, materialItemBeans);
        previewBar.setTotalTime(totalTime);
        previewBar.setAdapter(preViewItemAdapter);


    }


    @Override
    protected void onDestroy() {
        try {
            ijk_video.stopPlayback();
            ijk_video.release(true);
            ijk_video.stopBackgroundPlay();
            IjkMediaPlayer.native_profileEnd();
        } catch (Exception e) {
        }
        super.onDestroy();

    }

}
