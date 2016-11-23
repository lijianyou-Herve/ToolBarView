package com.example.herve.toolbarview.view.previewbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.herve.toolbarview.R;


public class MaterialItemView extends LinearLayout {
    /**
     * 绘画类工具
     */
    private int mediaType ;//0 是图片，1是文字
    private Paint paint;
    private RectF rectF;
    private Path draPath;
    private int margin = 0;

    private int lineSize = 1;

    /**
     * 素材状态颜色
     */
    private int materialNormalColor = 0;
    private int materialSelectColor = 0;
    private int width = 60;
    private int height = 60;
    /**
     * 已选中的颜色
     */
    private int materialColor = 0;
    // 计算滑动距离
    private int lastX = 0;
    private int lastY = 0;
    private Context mContext;

    /*左边*/
    private View leftLimitView;
    private View rightLimitView;
    /**
     * 平移的位置
     */
    private float transX = 0;
    /**
     * 屏幕的一半
     */
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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialItem);
        materialSelectColor = typedArray.getColor(R.styleable.MaterialItem_item_select_color, Color.RED);
        materialNormalColor = typedArray.getColor(R.styleable.MaterialItem_item_normal_color, Color.DKGRAY);
        materialColor = materialNormalColor;
        typedArray.recycle();
        mContext = context;
        init();
    }

    public void setLimitViews(View leftLimitView, View rightLimitView) {
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
     * 设置素材指示器选中和正常状态
     * 的颜色
     */
    public void setMaterialColor(int materialNormalColor, int materialSelectColor) {
        this.materialNormalColor = materialNormalColor;
        this.materialSelectColor = materialSelectColor;
        materialColor = materialNormalColor;
        paint.setColor(materialNormalColor);
        postInvalidate();
    }

    /**
     * 选中状态
     */
    public void setSelect() {
        paint.setColor(materialSelectColor);
        materialColor = materialSelectColor;

        postInvalidate();

    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * 正常状态
     */
    public void setNormal() {
        paint.setColor(materialNormalColor);
        materialColor = materialNormalColor;
        postInvalidate();
    }

    /**
     * 初始化数值
     */
    private void init() {

        paint = new Paint();
        paint.setColor(materialNormalColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        margin = dip2px(10);
        rectF = new RectF();
        draPath = new Path();
        halfScreenWidth = getScreenWidth(mContext) / 2;
//        width = dip2px(width);
        /**
         * 在第一个位置添加一个透明的View使内容居中抵消绘制的三角形高度*/
        View view = new View(mContext);
        view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, margin));
        addView(view, 0);

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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

                    setX(rightLimitView.getX() - getMeasuredWidth() / 2);

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
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        measure(w, h);
        width = getMeasuredWidth();

        Log.i(TAG, "setX: width=" + width);
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


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i(TAG, "onDraw: 绘画次数");
        paint.setColor(materialColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        /**
         * 画圆角矩形
         * */
        rectF.left = lineSize;
        rectF.right = getWidth() - lineSize;
        rectF.top = margin + lineSize;
        rectF.bottom = getHeight() - lineSize;
        canvas.drawRoundRect(rectF, 6, 6, paint);


        Log.i(TAG, "onDraw: getWidth()=" + getWidth());
        /**
         * 画三角形
         * */
        draPath.moveTo(getWidth() / 2 - margin / 3 * 2, margin);// 此点为多边形的起点
        draPath.lineTo(getWidth() / 2, 0);
        draPath.lineTo(getWidth() / 2 + margin / 3 * 2, margin);
        draPath.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(draPath, paint);
        /**
         * 画圆角矩形(空心)
         * */
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(lineSize);
        rectF.left = lineSize;
        rectF.right = getWidth() - lineSize;
        rectF.top = margin + lineSize;
        rectF.bottom = getHeight() - lineSize;
        canvas.drawRoundRect(rectF, 6, 6, paint);


    }
}
