package com.example.herve.toolbarview.view;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.herve.toolbarview.R;
import com.example.herve.toolbarview.adapter.HeadFootBaseAdapter;

/**
 * Created           :Herve on 2016/11/10.
 *
 * @ Author          :Herve
 * @ e-mail          :lijianyou.herve@gmail.com
 * @ LastEdit        :2016/11/10
 * @ projectName     :ToolBarView
 * @ version
 */
public class PreViewBar extends FrameLayout {

    private Context mContext;
    private final String TAG = getClass().getSimpleName();


    private TextView tvTime;
    private RelativeLayout rl_preview_bar;
    private RelativeLayout rlMaterialRoot;
    private RecyclerView rvPreviewBar;
    private MaterialItemView ivMaterial;
    private View middleLine;

    private HeadFootBaseAdapter mAdapter;
    private PreViewMaterialAdapter materialAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public PreViewBar(Context context) {
        this(context, null);
    }

    public PreViewBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreViewBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        LayoutInflater.from(mContext).inflate(R.layout.bar_preview, this);


        tvTime = (TextView) findViewById(R.id.tv_time);
        rl_preview_bar = (RelativeLayout) findViewById(R.id.rl_preview_bar);
        rvPreviewBar = (RecyclerView) findViewById(R.id.rv_preview_bar);
        middleLine = (View) findViewById(R.id.middle_line);
        ivMaterial = (MaterialItemView) findViewById(R.id.iv_material);
        rlMaterialRoot = (RelativeLayout) findViewById(R.id.rl_material_root);

        layoutManager = new LinearLayoutManager(mContext, OrientationHelper.HORIZONTAL, false);

        rvPreviewBar.setLayoutManager(layoutManager);
//
//        ivMaterial.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });

        ivMaterial.setScollListener(new MaterialItemView.OnScrollListener() {
            @Override
            public void onScrolledX(float scrolledX) {


                Log.i(TAG, "onScrolledX: 正常=scrolledX=" + scrolledX);


            }
        });


        rvPreviewBar.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                Log.i(TAG, "onScrolled: dx=" + dx);

                translateCurrent += dx;
                if (onAttach) {
                    previewImageTran = ivMaterial.getX() - dx;
                    ivMaterial.setX(previewImageTran);
                } else {
                    onAttach = true;
                    previewImageTran = rvPreviewBar.getChildAt(1).getX();
                    ivMaterial.setX(previewImageTran);
                }
                Log.i(TAG, "onScrolled: translateCurrent=" + translateCurrent);
                super.onScrolled(recyclerView, dx, dy);
            }
        });


    }

    /**
     * 添加的素材属性
     */
    //水平偏移距离
    private float previewImageTran = 0;

    //是否第一次加载完成
    private boolean onAttach = false;

    //停止时间
    private int translateAnimationTime = 0;
    //当前时间
    private int translateTime = 0;
    //水平偏移距离
    private float translateCurrent = 0;

    private Handler handler = new Handler();

    //左边的限制View
    private View leftView;
    //右边的限制View
    private View rightView;

    public void setAdapter(HeadFootBaseAdapter adapter) {
        this.mAdapter = adapter;

        addEmptyView();


        addMaterialViews();

        ivMaterial.setLefyRightView(leftView, rightView);

        rvPreviewBar.setAdapter(mAdapter);
    }

    private void addMaterialViews() {
        if (mAdapter instanceof PreViewMaterialAdapter) {
            materialAdapter = (PreViewMaterialAdapter) mAdapter;
            for (int i = 0; i < materialAdapter.getCount(); i++) {

                MaterialItemView material = materialAdapter.getItemMaterialView(rlMaterialRoot, i);

                material.setX(materialAdapter.getItemTranslateX());

                rlMaterialRoot.addView(material);

            }
        } else {

            //当前类没有继承接口
        }


    }

    //添加两侧不可见的View
    private void addEmptyView() {
        leftView = LayoutInflater.from(mContext).inflate(R.layout.transparent_view, rl_preview_bar, false);
        rightView = LayoutInflater.from(mContext).inflate(R.layout.transparent_view, rl_preview_bar, false);
        setPararmHalfOfScreen(leftView);
        setPararmHalfOfScreen(rightView);
        mAdapter.addHeaderView(leftView);
        mAdapter.addFooterView(rightView);
    }

    /**/
    private Runnable translateRunnable = new Runnable() {
        public void run() {
            setItemScrollX(1);
            translateTime += 1;
            if (translateTime < translateAnimationTime) {
                handler.postDelayed(translateRunnable, 1);
            } else {
                translateTime = 0;
            }
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: 触碰到了dispatchTouchEvent");
        setTranslateStop();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.i(TAG, "onTouchEvent: 触碰到了dispatchTouchEvent");
        setTranslateStop();
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 设置滑动时间
     */
    public void setTranslateTime(int translateAnimationTime) {
        this.translateAnimationTime = translateAnimationTime;
        handler.removeCallbacks(translateRunnable);
        handler.postDelayed(translateRunnable, 1);
    }

    /**
     * 停止滑动
     */
    public void setTranslateStop() {
        translateTime = 0;
        handler.removeCallbacks(translateRunnable);
    }


    /**
     * 设置View的滑动距离
     */
    private void setItemScrollX(int value) {
//        rvPreviewBar.setScrollX(value);
//        rvPreviewBar.smoothScrollBy(value,rvPreviewBar.getScrollX());


//        rvPreviewBar.smoothScrollToPosition(value);
//        rvPreviewBar.onScrolled(value,rvPreviewBar.getScrollX());
//        rvPreviewBar.scrollTo(-200, -100);
        rvPreviewBar.scrollBy(value, rvPreviewBar.getScrollY());
//        rvPreviewBar.smoothScrollBy(value,rvPreviewBar.getScrollY());

//        rvPreviewBar.scrollToPosition(value);
//        rvPreviewBar.scrollTo(rvPreviewBar.getScrollX(),value);
    }


    /**
     * 获取屏幕的宽度
     */
    private int getScreenWidth(Context context) {

        return context.getResources().getDisplayMetrics().widthPixels;

    }

    //设置宽度为屏幕的一半
    private <T extends ViewGroup.MarginLayoutParams> void setPararmHalfOfScreen(View view) {

        int dmw = getScreenWidth(mContext);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = dmw / 2;
        view.setLayoutParams(params);
    }

    public interface PreViewMaterialAdapter {

        MaterialItemView getItemMaterialView(ViewGroup parent, int position);

        float getItemTranslateX();

        int getCount();

    }
}
