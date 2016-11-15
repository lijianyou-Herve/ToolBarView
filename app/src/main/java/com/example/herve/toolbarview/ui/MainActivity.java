package com.example.herve.toolbarview.ui;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.example.herve.toolbarview.view.MaterialItemView;
import com.example.herve.toolbarview.view.PreViewBar;
import com.example.herve.toolbarview.view.ijkplayer.widget.media.AndroidMediaController;
import com.example.herve.toolbarview.view.ijkplayer.widget.media.IjkVideoView;

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
    private AndroidMediaController mediaController;
    private PreViewItemAdapter preViewItemAdapter;

    private int selectPosition = 0;
    private Context mContext;
    private ArrayList<String> data;
    private ArrayList<MaterialItemBean> materialItemBeans;
    private final String TAG = getClass().getSimpleName();
    private String url;

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

    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            btnPlay.postDelayed(mShowProgress, 1000 - (pos % 1000));
        }
    };

    private int setProgress() {

        int position = ijk_video.getCurrentPosition();
        int duration = ijk_video.getDuration();
        if (duration > 0) {
            // use long to avoid overflow
            long pos = 1000L * position / duration;

            previewBar.startAnim((int) pos);

        }

        return position;
    }

    private void initListener() {
        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ijk_video.isPlaying()) {
                    ijk_video.stopPlayback();
                } else {
                    ijk_video.setVideoPath(url);
                    ijk_video.start();
                }

            }
        });


        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                preViewItemAdapter.removeMaterialItem(selectPosition);

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

                Log.i(TAG, "onTimelineChangeListener:totalTime= " + totalTime);
                Log.i(TAG, "onTimelineChangeListener: currentTime=" + currentTime);

            }

            @Override
            public void onMaterialItemSelectListener(MaterialItemView view, int position) {

                selectPosition = position;

            }
        });

        ijk_video.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                mp.start();
//                previewBar.startAnim(ijk_video.getCurrentPosition());
//                btnPlay.postDelayed(mShowProgress, 0);


                previewBar.startAnim(0);
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


    }

    private int totalTime = 74000;


    private int last = 0;


    private void initData() {


        url = "http://bj.bcebos.com/v1/tomato-dev/00000000-0000-0000-0000-000000000000/filmTemplate/0118.mp4";

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mediaController = new AndroidMediaController(mContext);
        ijk_video.setMediaController(mediaController);
        ijk_video.setHudView(hud_view);

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
        previewBar.setTotalTime(totalTime);
        previewBar.setAdapter(preViewItemAdapter);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
