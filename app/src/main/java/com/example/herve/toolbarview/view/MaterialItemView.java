package com.example.herve.toolbarview.view;

import android.content.Context;
import android.util.AttributeSet;
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

        this.leftLimitView = leftLimitView;
        this.rightLimitView = rightLimitView;
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
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:

                int offsetX = x - lastX;

                if (leftLimitView != null && rightLimitView != null) {
                    if (leftLimitView.isShown() && getX() + offsetX + getWidth() / 2 < leftLimitView.getX() + leftLimitView.getWidth()) {//超过左边界，不让继续滑动

                        setX(leftLimitView.getX() + leftLimitView.getWidth() - getWidth() / 2);

                    } else if (rightLimitView.isShown() && getX() + offsetX + getWidth() / 2 > rightLimitView.getX()) {//超过右边界，不让继续滑动

                        setX(rightLimitView.getX() - getWidth() + getWidth() / 2);

                    } else {//正常值范围
                        setX(getX() + offsetX);
                    }
                } else {

                    setX(getX() + offsetX);
                }

                break;
            case MotionEvent.ACTION_UP:

                break;

            case MotionEvent.ACTION_CANCEL:

                break;
        }

        return true;
    }


    /**
     * 通过这里来监听位置的变化
     */
    @Override
    public void setX(float x) {
        super.setX(x);
        if (onScrollListener != null) {
            onScrollListener.onScrolledX(x);
        }
    }

    /**
     * X轴变化监听
     */
    private OnScrollListener onScrollListener;

    /**
     * 设置X轴变化监听接口
     */
    public void setScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**
     * X轴变化监听接口
     */
    interface OnScrollListener {

        void onScrolledX(float scrolledX);

    }
}
