package com.example.herve.toolbarview.view.previewbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.herve.toolbarview.R;
import com.example.herve.toolbarview.adapter.HeadFootBaseAdapter;
import com.example.herve.toolbarview.bean.MaterialItemBean;
import com.example.herve.toolbarview.view.ijkplayer.widget.media.IjkVideoView;

import java.util.ArrayList;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class PreViewBar extends RelativeLayout {

    private Context mContext;
    private final String TAG = getClass().getSimpleName();

    private IjkVideoView ijkVideoView;
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

    //左边的限制View
    private View leftView;
    //右边的限制View
    private View rightView;
    private MaterialItemView onTouchView;
    //是否是自动滑动状态
    private boolean isAutoScroll = false;
    private int halfScreenWidth = 0;

    private long seekToFrequencyTime;

    private float totalWidth = 520;
    private int preViewItemWidth = 40;
    private float totalTime = 150;//秒计时
    private float currentTime = 0;//秒计时

    private ValueAnimator valueAnimator;
    private int remainWidth;

    //素材指示器

    private int lastChangeValue = 0;

    private MediaType mMediaType;

    public enum MediaType {
        video,
        image
    }

    public PreViewBar(Context context) {
        this(context, null);
    }

    public PreViewBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreViewBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        setWillNotDraw(false);
        init();
    }


    public void setMediaType(MediaType mediaType) {
        this.mMediaType = mediaType;

        if (mMediaType == MediaType.image) {
            valueAnimator = new ValueAnimator();

        }
    }

    private void init() {

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
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.i(TAG, "onScrolled: 调整时间B=" + (int) currentTime);
                    if (mMediaType == MediaType.video) {
                        ijkVideoView.seekTo((int) (currentTime * 1000));
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                translateCurrent += dx;
                Log.i(TAG, "onScrolled: translateCurrent=" + translateCurrent);
                currentTime = translateCurrent / totalWidth * totalTime;

                Log.i(TAG, "onScrolled: totalTime=" + totalTime);
                Log.i(TAG, "onScrolled: currentTime=" + currentTime);

                if (mMediaType == MediaType.video) {
                    if (!isAutoScroll()) {
                        if (System.currentTimeMillis() - seekToFrequencyTime > 100) {
                            Log.i(TAG, "onScrolled: 调整时间A=" + (int) currentTime);
                            ijkVideoView.seekTo((int) (currentTime * 1000));
                            seekToFrequencyTime = System.currentTimeMillis();
                        }
                    }
                }

                tvTime.setText(secToHMSTime_TextViewShow((double) currentTime, totalTime / 60 / 60));
                if (onPreViewChangeListener != null) {
                    onPreViewChangeListener.onTimelineChangeListener(totalTime, currentTime);
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

            if (mMediaType == MediaType.video) {
                setProgress();

                if (isShown() && ijkVideoView.isPlaying()) {
                    postDelayed(mShowProgress, 100);
                }
                if (!ijkVideoView.isPlaying()) {
                    removeCallbacks(mShowProgress);
                }
            }


        }
    };


    private void startImageAnimation() {

        remainWidth = (int) (totalWidth - translateCurrent);
        Log.i(TAG, "onAnimationUpdate:remainWidth= " + remainWidth);

        valueAnimator.setIntValues(remainWidth);

        valueAnimator.setDuration((int) (totalTime - currentTime) * 1000);
        Log.i(TAG, "onAnimationUpdate:time= " + (int) (totalTime - currentTime) * 1000);

        valueAnimator.setInterpolator(new LinearInterpolator());

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int animationValue = (int) animation.getAnimatedValue();

                int avlue = (int) (totalWidth - remainWidth + animationValue);

                Log.i(TAG, "onAnimationUpdate:animationValue= " + animationValue);
                Log.i(TAG, "onAnimationUpdate:value= " + avlue);

                startAnim(avlue);
            }
        });

        valueAnimator.start();


    }

    private void setProgress() {
        if (mMediaType == MediaType.video) {
            int position = ijkVideoView.getCurrentPosition();
            int duration = ijkVideoView.getDuration();
            Log.i(TAG, "setProgress: position=" + position);
            Log.i(TAG, "setProgress: duration=" + duration);
            if (position > 0) {
                // use long to avoid overflow
                long pos = 1000 * position / duration;

                int animationValue = (int) ((float) pos / 1000 * totalWidth);

                startAnim(animationValue);
                Log.i(TAG, "setProgress: pos=" + pos);

            }
        }

    }

    /**
     * 启动和视频播放的交互
     */
    public void start() {
        setAutoScroll(true);
        lastChangeValue = (int) translateCurrent;

        if (mMediaType == MediaType.video) {
            postDelayed(mShowProgress, 0);
        }
        if (mMediaType == MediaType.image) {
            startImageAnimation();
        }
    }

    /**
     * 启动和视频播放的交互
     */
    public void pause() {
        setAutoScroll(false);
        if (mMediaType == MediaType.image) {
            valueAnimator.cancel();
        }
        if (mMediaType == MediaType.video) {
            ijkVideoView.pause();
            removeCallbacks(mShowProgress);
        }

    }

    /**
     * 绑定MediaPlayerControl
     */
    public void bindVideoView(IjkVideoView ijkVideoView) {
        this.ijkVideoView = ijkVideoView;
        mMediaType = MediaType.video;
        ijkVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {

                Log.i(TAG, "onCompletion: position=" + 1000);
//                startAnim(1000);
                removeCallbacks(mShowProgress);


            }
        });
        ijkVideoView.showMediaController();
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
                float time = materialAdapter.getItemTranslateX(position);

                float x = time / totalTime * totalWidth;
                materialItemView.firstSetX(x);
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
        totalWidth = dip2px(preViewItemWidth) * mAdapter.getSimpleCount();

        rvPreviewBar.setAdapter(mAdapter);
    }

    public PreViewMaterialAdapter getAdapter() {
        if (mAdapter instanceof PreViewMaterialAdapter) {
            return (PreViewMaterialAdapter) mAdapter;
        }
        return null;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 更新Item的位置
     */
    public void notifyDataSetChanged(int position) {
        for (int i = 0; i < rlMaterialRoot.getChildCount(); i++) {
            int tagPosition = (int) rlMaterialRoot.getChildAt(i).getTag();
            if (tagPosition > position) {
                rlMaterialRoot.getChildAt(i).setTag(--tagPosition);
            }
        }
    }

    /**
     * 添加素材位置
     */
    public void addMaterialItem() {
        final int position = materialAdapter.getMaterialCount() - 1;
        MaterialItemView material = materialAdapter.getItemMaterialView(rlMaterialRoot, position);

        float offX = middleLine.getX() - halfScreenWidth + middleLine.getMeasuredWidth() / 2;

        setItemAttribute(position, material);
        material.firstSetX(offX);
        material.setTransX(translateCurrent);
        onMaterialSelect(material, false);
        rlMaterialRoot.addView(material);

    }

    public void resetState() {

        if (onTouchView != null) {
            onTouchView.setNormal();
            onTouchView = null;
        }
    }

    /**
     * 设置素材位的选中状态
     */
    private void onMaterialSelect(MaterialItemView material, boolean needScroll) {
        if (onTouchView == null) {
            onTouchView = material;
            onTouchView.setSelect();
        } else if (onTouchView != material) {
            onTouchView.setNormal();
            onTouchView = material;
            onTouchView.setSelect();
        }

        /**
         * 是否需要滚动
         * */
        if (needScroll) {
            rvPreviewBar.scrollBy((int) material.getX() - halfScreenWidth + material.getWidth() / 2, (int) rvPreviewBar.getY());
        }
        if (onPreViewChangeListener != null) {
            onPreViewChangeListener.onMaterialItemSelectListener(material, material.getMediaType(), (int) material.getTag());
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

            float time = scrolledX / totalWidth * totalTime;
            Log.i(TAG, "onScrolledX: time=" + time);

            materialAdapter.setScrollListener((int) view.getTag(), time);


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
    public void removeMaterialItem(int position) {

        if (onTouchView != null) {
            rlMaterialRoot.removeView(onTouchView);
        }

        notifyDataSetChanged(position);

    }


    /**
     * 添加所有的素材指示器
     */
    private void addMaterialViews() {
        if (mAdapter instanceof PreViewMaterialAdapter) {
            materialAdapter = (PreViewMaterialAdapter) mAdapter;
            for (int i = 0; i < materialAdapter.getMaterialCount(); i++) {

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
        leftView = new View(mContext);
        rightView = new View(mContext);

        setPararmsHalfOfScreen(leftView);
        setPararmsHalfOfScreen(rightView);
        halfScreenWidth = getScreenWidth(mContext) / 2;
        mAdapter.addHeaderView(leftView);
        mAdapter.addFooterView(rightView);
    }

    /**
     * 更新位置
     */
    private void startAnim(int animationValue) {

        /**
         * 计算出当前位置
         * */
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

    /**
     * 跟随视频播放状态
     */
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

        /**
         * 取消滚动
         * 视频暂停
         * 隐藏播放按钮
         * */
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            setAutoScroll(false);
            if (mMediaType == MediaType.image) {
                valueAnimator.cancel();
            }
            if (mMediaType == MediaType.video) {
                removeCallbacks(mShowProgress);
                ijkVideoView.pause();
                ijkVideoView.hideMediaController();
            }

        }
        if (ev.getAction() == MotionEvent.ACTION_UP ||
                ev.getAction() == MotionEvent.ACTION_CANCEL) {
        }


        return super.dispatchTouchEvent(ev);
    }


    public float getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(float totalTime) {
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
    private <T extends MarginLayoutParams> void setPararmsHalfOfScreen(View view) {
        int dmw = getScreenWidth(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

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
        int getMaterialCount();

        MaterialItemBean getItem(int position);

        /**
         * 在对应的素材指示器X轴发生改变时，会回调该函数
         */
        void setScrollListener(int position, float currentTime);

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
        void onTimelineChangeListener(float totalTime, float currentTime);

        /**
         * 选中状态
         */
        void onMaterialItemSelectListener(MaterialItemView view, int mediaType, int position);

    }


    public static String secToHMSTime_TextViewShow(double time, double more_than_one_hour) {

        String timeStr = null;
        double hour = 0;
        double minute = 0;
        double second = 0;
        second = time % 60;
        minute = time / 60;
        hour = minute / 60;

        if (more_than_one_hour > 60) {
            String[] strs = String.valueOf(time).split("[.]");
            double i = Double.parseDouble(strs[0]);
            second = i % 60;
            second = second + Double.parseDouble("." + strs[1]);
            if (minute > 60) {
                minute = minute % 60;
            }
            if (hour > 0) {
                timeStr = unitFormat((int) hour) + ":" + unitFormat((int) minute) + ":" + unitFormat_seconds(second);
            } else {
                timeStr = "00:" + unitFormat((int) minute) + ":" + unitFormat_seconds(second);
            }
        } else {
            String[] strs = String.valueOf(time).split("[.]");
            double i = Double.parseDouble(strs[0]);
            second = i % 60;
            second = second + Double.parseDouble("." + strs[1]);
            // hour = minute/60;
            if (minute > 0) {
                timeStr = unitFormat((int) minute) + ":" + unitFormat_seconds(second);
            } else {
                timeStr = "00:" + unitFormat_seconds(second);
            }
        }
        return timeStr;

    }

    public static String unitFormat_seconds(double i) {
        String retStr = null;
        if (i >= 0 && i < 10) {
            retStr = "0" + Double.toString(i);
        } else {
            retStr = "" + Double.toString(i);
        }
        retStr = retStr.substring(0, 4);
        return retStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

}
