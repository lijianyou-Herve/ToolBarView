package com.example.herve.toolbarview.view;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.herve.toolbarview.R;
import com.example.herve.toolbarview.adapter.HeadFootBaseAdapter;
import com.example.herve.toolbarview.bean.MaterialItemBean;
import com.example.herve.toolbarview.listener.ItemChangeListener;

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
public class PreViewBar extends RelativeLayout implements ItemChangeListener {

    private Context mContext;
    private final String TAG = getClass().getSimpleName();


    private TextView tvTime;
    private RelativeLayout rl_preview_bar;
    private RelativeLayout rlMaterialRoot;
    private RecyclerView rvPreviewBar;
    private View middleLine;

    private HeadFootBaseAdapter mAdapter;
    private PreViewMaterialAdapter materialAdapter;
    private RecyclerView.LayoutManager layoutManager;

    /**
     * 添加的素材属性
     */

    //是否第一次加载完成
    private boolean onAttach = false;

    private float translateCurrent = 0;
    //停止时间
    private int translateAnimationTime = 0;
    //当前时间
    private int translateTime = 0;

    private Handler handler = new Handler();

    //左边的限制View
    private View leftView;
    //右边的限制View
    private View rightView;
    private View onTouchView;
    //是否是自动滑动状态
    private boolean isAutoScroll = false;
    private int halfScreenWidth = 0;


    //素材指示器
    ArrayList<MaterialItemView> materialItemViews;

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

        LayoutInflater.from(mContext).inflate(R.layout.bar_preview, this, true);


        tvTime = (TextView) findViewById(R.id.tv_time);
        rl_preview_bar = (RelativeLayout) findViewById(R.id.rl_preview_bar);
        rvPreviewBar = (RecyclerView) findViewById(R.id.rv_preview_bar);
        middleLine = (View) findViewById(R.id.middle_line);
        rlMaterialRoot = (RelativeLayout) findViewById(R.id.rl_material_root);

        layoutManager = new LinearLayoutManager(mContext, OrientationHelper.HORIZONTAL, false);

        rvPreviewBar.setLayoutManager(layoutManager);

        rvPreviewBar.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                translateCurrent += dx;
                Log.i(TAG, "onScrolled: translateCurrent=" + translateCurrent);

                setMaterialItemChange(dx);
                super.onScrolled(recyclerView, dx, dy);
            }
        });


    }

    private void setMaterialItemChange(int dx) {
        if (onAttach) {

            for (int position = 0; position < materialItemViews.size(); position++) {

                MaterialItemView materialItemView = materialItemViews.get(position);

                Log.i(TAG, "setMaterialItemChange: leftView=" + leftView.getX() + halfScreenWidth);
                materialItemView.setX(materialItemView.getX() - dx);

            }

        } else {
            onAttach = true;
            if (materialItemViews != null) {
                for (int position = 0; position < materialItemViews.size(); position++) {

                    MaterialItemView materialItemView = materialItemViews.get(position);

                    materialItemView.firstSetX(materialAdapter.getItemTranslateX(position));

                }
            }
        }
    }


    /**
     * 设置Adapter
     */
    public void setAdapter(HeadFootBaseAdapter adapter) {
        this.mAdapter = adapter;

        addLimitView();

        addMaterialViews();


        rvPreviewBar.setAdapter(mAdapter);
    }

    @Override
    public void addMaterialItem() {
        final int position = materialAdapter.getCount() - 1;
        MaterialItemView material = materialAdapter.getItemMaterialView(rlMaterialRoot, position);
        material.setLimitViews(leftView, rightView);

        float offX = middleLine.getX() - halfScreenWidth;

        material.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTouchView == null) {
                    onTouchView = view;
                    onTouchView.setBackgroundColor(Color.BLUE);
                    rvPreviewBar.scrollBy((int) view.getX() - halfScreenWidth + view.getWidth() / 2, (int) rvPreviewBar.getY());

                } else if (onTouchView != view) {
                    onTouchView.setBackgroundColor(Color.BLACK);
                    onTouchView = view;
                    onTouchView.setBackgroundColor(Color.BLUE);
                    rvPreviewBar.scrollBy((int) view.getX() - halfScreenWidth + view.getWidth() / 2, (int) rvPreviewBar.getY());
                }
            }
        });

        material.setScrollListener(new MaterialItemView.OnCustomTouchListener() {
            @Override
            public void onScrolledX(View view, float scrolledX) {

                materialAdapter.setScrollListener(position, scrolledX);

            }
        });
        material.firstSetX(offX);
        material.setTransX(translateCurrent);
        materialItemViews.add(material);
        rlMaterialRoot.addView(material);


    }

    @Override
    public void removeMaterialItem(int position) {

        materialItemViews.remove(position);
        rlMaterialRoot.removeViewAt(position);

    }

    /**
     * 添加所有的素材指示器
     */
    private void addMaterialViews() {
        if (mAdapter instanceof PreViewMaterialAdapter) {
            materialAdapter = (PreViewMaterialAdapter) mAdapter;
            materialItemViews = new ArrayList<>();
            for (int i = 0; i < materialAdapter.getCount(); i++) {

                MaterialItemView material = materialAdapter.getItemMaterialView(rlMaterialRoot, i);
                material.setLimitViews(leftView, rightView);
                Log.e(TAG, "addMaterialViews: 元素位置=" + i + "位置变化=" + materialAdapter.getItemTranslateX(i));

                final int finalI = i;

                material.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (onTouchView == null) {
                            onTouchView = view;
                            onTouchView.setBackgroundColor(Color.BLUE);
                            rvPreviewBar.scrollBy((int) view.getX() - halfScreenWidth + view.getWidth() / 2, (int) rvPreviewBar.getY());

                        } else if (onTouchView != view) {
                            onTouchView.setBackgroundColor(Color.BLACK);
                            onTouchView = view;
                            onTouchView.setBackgroundColor(Color.BLUE);

                            rvPreviewBar.scrollBy((int) view.getX() - halfScreenWidth + view.getWidth() / 2, (int) rvPreviewBar.getY());
                        }
                    }
                });

                material.setScrollListener(new MaterialItemView.OnCustomTouchListener() {
                    @Override
                    public void onScrolledX(View view, float scrolledX) {

                        materialAdapter.setScrollListener(finalI, scrolledX);

                    }
                });

                materialItemViews.add(material);
                rlMaterialRoot.addView(material);
            }
        } else {
            //当前类没有继承接口
        }
    }

    /**
     * 添加两侧不可见的View
     */
    private void addLimitView() {
        leftView = LayoutInflater.from(mContext).inflate(R.layout.transparent_view, rl_preview_bar, false);
        rightView = LayoutInflater.from(mContext).inflate(R.layout.transparent_view, rl_preview_bar, false);
        setPararmHalfOfScreen(leftView);
        setPararmHalfOfScreen(rightView);
        halfScreenWidth = getScreenWidth(mContext) / 2;
        mAdapter.addHeaderView(leftView);
        mAdapter.addFooterView(rightView);
    }

    /**/
    private Runnable translateRunnable = new Runnable() {
        public void run() {
            isAutoScroll = true;
            setItemScrollX(1);
            translateTime += 1;
            if (translateTime < translateAnimationTime) {
                handler.postDelayed(translateRunnable, 1);
            } else {
                translateTime = 0;
                setAutoScroll(false);
            }
        }
    };

    private void setAutoScroll(boolean autoScroll) {
        isAutoScroll = autoScroll;
    }

    public boolean isAutoScroll() {
        return isAutoScroll;
    }

    /**
     * 触碰就立即关闭自动滑动
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (isAutoScroll()) {
            setTranslateStop();
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 设置滑动时间
     */
    public void setTranslateTime(int translateAnimationTime) {
        this.translateAnimationTime = translateAnimationTime;
        setAutoScroll(false);
        handler.postDelayed(translateRunnable, 1);
    }

    /**
     * 停止滑动
     */
    public void setTranslateStop() {
        translateTime = 0;
        handler.removeCallbacks(translateRunnable);
        setAutoScroll(false);
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

    /**
     * 设置宽度为屏幕的一半
     */
    private <T extends ViewGroup.MarginLayoutParams> void setPararmHalfOfScreen(View view) {
        int dmw = getScreenWidth(mContext);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = dmw / 2;
        view.setLayoutParams(params);
    }

    /**
     * 素材位的适配器接口，这样可以方便使用和解耦。
     */

    public interface PreViewMaterialAdapter {

        void setMaterialData(PreViewBar preViewBar,ArrayList<MaterialItemBean> materialData);

        /**
         * 填充对应的素材指示器
         */
        MaterialItemView getItemMaterialView(ViewGroup parent, int position);

        /**
         * 获得素材指示器的初始X轴位置
         */
        float getItemTranslateX(int position);

        /**
         * 素材指示器的个数
         */
        int getCount();

        MaterialItemBean getItem(int position);

        /**
         * 在对应的素材指示器X轴发生改变时，会回调该函数
         */
        void setScrollListener(int position, float scrolledX);

    }


}
