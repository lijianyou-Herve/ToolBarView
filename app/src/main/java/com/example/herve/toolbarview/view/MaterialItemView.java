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
    private View leftView;
    private View rightView;

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


    public void setLefyRightView(View leftView, View rightView) {

        this.leftView = leftView;
        this.rightView = rightView;
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

                if (leftView != null && rightView != null) {
                    if (getX() + offsetX < leftView.getX() + leftView.getWidth()) {

                        setX(leftView.getX() + leftView.getWidth());

                    } else if (rightView.isShown() && getX() + offsetX + getWidth() > rightView.getX()) {

                        setX(rightView.getX() - getWidth());

                    } else {
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

        return super.onTouchEvent(event);
    }

    @Override
    public float getTranslationX() {
        return super.getTranslationX();
    }

    @Override
    public void setTranslationX(float translationX) {
        super.setTranslationX(translationX);
    }

    @Override
    public void setX(float x) {

        if (onScrollListener != null) {
            onScrollListener.onScrolledX(x);
        }
        super.setX(x);


    }


    private OnScrollListener onScrollListener;

    public void setScollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    interface OnScrollListener {

        void onScrolledX(float scrolledX);

    }
}
