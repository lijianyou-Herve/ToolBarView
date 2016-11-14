package com.example.herve.toolbarview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created           :Herve on 2016/11/11.
 *
 * @ Author          :Herve
 * @ e-mail          :lijianyou.herve@gmail.com
 * @ LastEdit        :2016/11/11
 * @ projectName     :ToolBarView
 * @ version
 */
public class MaterialItemView extends ImageView {

    // 计算滑动距离
    private int lastX = 0;
    private int lastY = 0;

    private Context mContext;

    /*左边*/
    private View leftLimitView;
    private View rightLimitView;
    private float transX = 0;
    private int width = 160;
    private int halfScreenWidth = 0;

    private String TAG = getClass().getSimpleName();

    public MaterialItemView(Context context) {
        this(context, null);
    }

    public MaterialItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public void setLimitViews(View leftLimitView, View rightLimitView) {
        halfScreenWidth = getScreenWidth(mContext) / 2;
        this.leftLimitView = leftLimitView;
        this.rightLimitView = rightLimitView;
    }


    /**
     * 获取屏幕的宽度
     */
    private int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * @param context
     */
    private void init(Context context) {
        mContext = context;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 检测到触摸事件后 第一时间得到相对于父控件的触摸点坐标 并赋值给x,y
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            // 触摸事件中绕不开的第一步，必然执行，将按下时的触摸点坐标赋值给 lastX 和 last Y
            case MotionEvent.ACTION_DOWN:
                setClickable(true);
                bringToFront();
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:

                int offsetX = x - lastX;

                float beforeX = getX();

                if (leftLimitView.isShown() && getX() + offsetX + width / 2 < leftLimitView.getX() + halfScreenWidth) {//超过左边界，不让继续滑动

                    Log.i(TAG, "onTouchEvent: 左边界");
                    setX(leftLimitView.getX() + halfScreenWidth - width / 2);

                } else if (rightLimitView.isShown() && getX() + offsetX + width / 2 > rightLimitView.getX()) {//超过右边界，不让继续滑动
                    Log.i(TAG, "onTouchEvent: 右边界");

                    setX(rightLimitView.getX() - width / 2);

                } else {//正常值范围
                    setX(getX() + offsetX);
                }

                float nowChangeX = getX() - beforeX;

                if (Math.abs(nowChangeX) > 2) {
                    setClickable(false);
                }

                transX += nowChangeX;

                setTransX(transX);

                break;
            case MotionEvent.ACTION_UP:

                break;

            case MotionEvent.ACTION_CANCEL:

                break;
        }

        return super.onTouchEvent(event);
    }


    public float getTransX() {
        return transX;
    }


    public void setTransX(float transX) {
        Log.i(TAG, "setTransX: transX=" + transX);
        this.transX = transX;
        if (onScrollListener != null) {
            onScrollListener.onScrolledX(this, transX);
        }
    }

    /**
     * 通过这里来监听位置的变化
     */
    public void firstSetX(float x) {
        Log.i(TAG, "setX: getMeasuredWidth=" + halfScreenWidth);
        float reallyX = halfScreenWidth - width / 2 + x;
        setTransX(x);
        super.setX(reallyX);

    }

    @Override
    public void setX(float x) {
        super.setX(x);
    }

    /**
     * X轴变化监听
     */
    private OnCustomTouchListener onScrollListener;

    /**
     * 设置X轴变化监听接口
     */
    public void setScrollListener(OnCustomTouchListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**
     * X轴变化监听接口
     */
    interface OnCustomTouchListener {

        void onScrolledX(View view, float scrolledX);

    }
}
