package com.example.herve.toolbarview.bean;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created           :Herve on 2016/11/11.
 *
 * @ Author          :Herve
 * @ e-mail          :lijianyou.herve@gmail.com
 * @ LastEdit        :2016/11/11
 * @ projectName     :ToolBarView
 * @ version
 */
public class MaterialItemBean {

    private int type;//0是图片 1是文字
    private String text;
    private float time;
    private boolean isSelected;
    private Matrix matrix;
    private Rect realBounds;


    private String TAG = getClass().getSimpleName();

    public MaterialItemBean() {
    }

    public Rect getRealBounds() {
        return realBounds;
    }

    public void setRealBounds(Rect realBounds) {
        this.realBounds = realBounds;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix mMatrix) {
        this.matrix = mMatrix;
    }

    public String getText() {
        return text;
    }

    public float getTime() {
        Log.i(TAG, "getTime: time=" + time);

        return time;
    }

    public void setTime(float time) {
        Log.i(TAG, "setTime: time=" + time);
        this.time = time;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
