package com.example.herve.toolbarview.view.previewbar;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.herve.toolbarview.R;
import com.example.herve.toolbarview.adapter.HeadFootBaseAdapter;
import com.example.herve.toolbarview.bean.MaterialItemBean;
import com.example.herve.toolbarview.listener.ItemChangeListener;
import com.example.herve.toolbarview.utils.DensityUtil;
import com.example.herve.toolbarview.utils.TimeUtils;

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


    private MediaController.MediaPlayerControl mediaPlayerControl;
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

    //当前自动滑动位置
    private float translateX = 0;


    //左边的限制View
    private View leftView;
    //右边的限制View
    private View rightView;
    private View onTouchView;
    //是否是自动滑动状态
    private boolean isAutoScroll = false;
    private int halfScreenWidth = 0;


    private float totalWidth = 0;
    private int preViewItemWidth = 52;
    private long totalTime = 1500;//毫秒计时
    private long currentTime = 0;//毫秒计时

    //素材指示器

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
                currentTime = (long) (translateCurrent / totalWidth * totalTime);

                Log.i(TAG, "onScrolled: totalTime=" + totalTime);
                Log.i(TAG, "onScrolled: currentTime=" + currentTime);
                Log.i(TAG, "onScrolled: currentTime=" + TimeUtils.secToHMSTime_TextViewShow(currentTime / 1000d, 50));

                if (!isAutoScroll()) {
                    Log.i(TAG, "onScrolled: 调整时间=" + (int) currentTime);
                    mediaPlayerControl.seekTo((int) currentTime);
                }

                tvTime.setText(TimeUtils.secToHMSTime_TextViewShow(currentTime / 1000d, 50));
                if (onPreViewChangeListener != null) {
                    onPreViewChangeListener.onTimelineChangeListener(totalTime / 1000d, currentTime / 1000);
                }
                setMaterialItemChange(dx);
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        Log.i(TAG, "init: totalWidth" + totalWidth);

    }

    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            setProgress();
            postDelayed(mShowProgress, 100);

        }
    };

    private void setProgress() {

        int position = mediaPlayerControl.getCurrentPosition();
        int duration = mediaPlayerControl.getDuration();
        if (duration > 0) {
            // use long to avoid overflow
            long pos = 1000L * position / duration;
            startAnim((int) pos);
        }

    }

    /**
     * 启动和视频播放的交互
     */
    public void start() {
        setAutoScroll(true);
        postDelayed(mShowProgress, 0);
    }

    /**
     * 启动和视频播放的交互
     */
    public void pause() {
        setAutoScroll(false);
        removeCallbacks(mShowProgress);

    }
    /**
     * 绑定MediaPlayerControl
     */
    public void setMediaPlayerControl(MediaController.MediaPlayerControl mediaPlayerControl) {
        this.mediaPlayerControl = mediaPlayerControl;
    }

    /**
     * 有滑动之后，更新素材的位置
     */
    private void setMaterialItemChange(int dx) {
        if (onAttach) {//后续滑动
            for (int position = 0; position < rlMaterialRoot.getChildCount(); position++) {
                MaterialItemView materialItemView = (MaterialItemView) rlMaterialRoot.getChildAt(position);
                Log.i(TAG, "setMaterialItemChange: position=" + position);
                Log.i(TAG, "setMaterialItemChange: 位置刷新=" + (materialItemView.getX() - dx));
                materialItemView.setX(materialItemView.getX() - dx);
            }
        } else {//第一次进入
            onAttach = true;

            for (int position = 0; position < rlMaterialRoot.getChildCount(); position++) {
                MaterialItemView materialItemView = (MaterialItemView) rlMaterialRoot.getChildAt(position);
                materialItemView.firstSetX(materialAdapter.getItemTranslateX(position));
            }
        }
    }


    /**
     * 设置Adapter
     */
    public void setAdapter(HeadFootBaseAdapter adapter) {
        this.mAdapter = adapter;

        /**
         * 添加限制布局
         * */
        addLimitView();
        /**
         * 添加素材位
         * */
        addMaterialViews();

        /**
         * 计算出所以素材的占用空间
         * */
        totalWidth = DensityUtil.dip2px(mContext, preViewItemWidth) * mAdapter.getSimpleCount();

        rvPreviewBar.setAdapter(mAdapter);
    }

    /**
     * 更新Item的位置
     */
    public void notifyDataSetChanged() {
        for (int i = 0; i < rlMaterialRoot.getChildCount(); i++) {
            rlMaterialRoot.getChildAt(i).setTag(i);
        }
    }

    /**
     * 添加素材位置
     */
    @Override
    public void addMaterialItem() {
        final int position = materialAdapter.getCount() - 1;
        MaterialItemView material = materialAdapter.getItemMaterialView(rlMaterialRoot, position);
        float offX = middleLine.getX() - halfScreenWidth;
        setItemAttribute(position, material);

        material.firstSetX(offX);
        material.setTransX(translateCurrent);

        onMaterialSelect(material, false);

        rlMaterialRoot.addView(material);
        notifyDataSetChanged();

    }

    /**
     * 设置素材位的选中状态
     */
    private void onMaterialSelect(MaterialItemView material, boolean needScroll) {
        if (onTouchView == null) {
            onTouchView = material;
            onTouchView.setBackgroundColor(Color.BLUE);

        } else if (onTouchView != material) {
            onTouchView.setBackgroundColor(Color.BLACK);
            onTouchView = material;
            onTouchView.setBackgroundColor(Color.BLUE);
        }

        /**
         * 是否需要滚动
         * */
        if (needScroll) {
            rvPreviewBar.scrollBy((int) material.getX() - halfScreenWidth + material.getWidth() / 2, (int) rvPreviewBar.getY());
        }
        if (onPreViewChangeListener != null) {
            onPreViewChangeListener.onMaterialItemSelectListener(material, (int) material.getTag());
        }
    }


    /**
     * item的点击监听
     */
    private OnClickListener itemClick = new OnClickListener() {
        @Override
        public void onClick(View view) {

            onMaterialSelect((MaterialItemView) view, true);

        }
    };

    /**
     * 自定的Touch事件
     */
    private MaterialItemView.OnCustomTouchListener onCustomTouchListener = new MaterialItemView.OnCustomTouchListener() {
        @Override
        public void onScrolledX(View view, float scrolledX) {

            materialAdapter.setScrollListener((int) view.getTag(), scrolledX);

        }
    };

    /**
     * 统一设置素材的属性
     */
    private void setItemAttribute(int position, MaterialItemView material) {

        material.setLimitViews(leftView, rightView);
        material.setTag(position);
        material.setOnClickListener(itemClick);
        material.setScrollListener(onCustomTouchListener);
    }

    /**
     * 移除一个素材位
     */
    @Override
    public void removeMaterialItem(int position) {
        if (onTouchView == null && rlMaterialRoot.getChildCount() > 0) {
            rlMaterialRoot.removeViewAt(0);
        } else {
            rlMaterialRoot.removeView(onTouchView);
        }
        notifyDataSetChanged();

    }

    /**
     * 添加所有的素材指示器
     */
    private void addMaterialViews() {
        if (mAdapter instanceof PreViewMaterialAdapter) {
            materialAdapter = (PreViewMaterialAdapter) mAdapter;
            for (int i = 0; i < materialAdapter.getCount(); i++) {

                MaterialItemView material = materialAdapter.getItemMaterialView(rlMaterialRoot, i);
                setItemAttribute(i, material);

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
        setPararmsHalfOfScreen(leftView);
        setPararmsHalfOfScreen(rightView);
        halfScreenWidth = getScreenWidth(mContext) / 2;
        mAdapter.addHeaderView(leftView);
        mAdapter.addFooterView(rightView);
    }

    private int lastChangeValue = 0;

    /**
     * 更新位置
     */
    private void startAnim(int currentPosition) {

        /**
         * 计算出当前位置
         * */
        int animationValue = (int) ((float) currentPosition / 1000 * totalWidth);
        Log.i(TAG, "onAnimationUpdate: animationValue=" + animationValue);
        Log.i(TAG, "onAnimationUpdate: lastChangeValue=" + lastChangeValue);

        /**
         * 需要平移的位置=当前位置-之前的位置
         * */
        int animationX = animationValue - lastChangeValue;
        /**
         * 保留目前的位置
         * */
        lastChangeValue = animationValue;

        Log.i(TAG, "onAnimationUpdate: animationX= " + animationX);
        /**
         * 调用刷新
         * */
        rvPreviewBar.scrollBy(animationX, (int) rvPreviewBar.getY());


    }


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

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            setAutoScroll(false);
            removeCallbacks(mShowProgress);
            mediaPlayerControl.pause();
        }
        if (ev.getAction() == MotionEvent.ACTION_UP ||
                ev.getAction() == MotionEvent.ACTION_CANCEL) {
        }


        return super.dispatchTouchEvent(ev);
    }


    public float getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    /**
     * 设置View的滑动距离
     */
    private void setItemScrollX(int value) {
        rvPreviewBar.scrollBy(value, rvPreviewBar.getScrollY());
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
    private <T extends ViewGroup.MarginLayoutParams> void setPararmsHalfOfScreen(View view) {
        int dmw = getScreenWidth(mContext);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = dmw / 2;
        view.setLayoutParams(params);
    }

    /**
     * 素材位的适配器接口，这样可以方便使用和解耦。
     */

    public interface PreViewMaterialAdapter {

        void setMaterialData(PreViewBar preViewBar, ArrayList<MaterialItemBean> materialData);

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


    /**
     * 预览滑块的变化状态的监听
     */
    private OnPreViewChangeListener onPreViewChangeListener;

    /**
     * 注册监听
     */
    public void setOnPreViewChangeListener(OnPreViewChangeListener onPreViewChangeListener) {
        this.onPreViewChangeListener = onPreViewChangeListener;
    }

    /**
     * 预览滑块的变化状态
     */
    public interface OnPreViewChangeListener {

        /**
         * 时间变化
         */
        void onTimelineChangeListener(double totalTime, double currentTime);

        /**
         * 选中状态
         */
        void onMaterialItemSelectListener(MaterialItemView view, int position);

    }


}
