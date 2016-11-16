package com.example.herve.toolbarview.bean;

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

    private String text;
    private float time;
    private String TAG = getClass().getSimpleName();

    public MaterialItemBean(String text) {
        this.text = text;
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


}
