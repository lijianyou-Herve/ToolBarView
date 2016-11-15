package com.example.herve.toolbarview.utils;


import android.util.Log;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;


public class TimeUtils {
    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    private static String TAG = "TimeUtils";


    public static String formatCropVideoHMS_TextViewShow(double time, double more_than_one_hour) {
        BigDecimal b = new BigDecimal(time);
        double ftime = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return secToHMSTime_TextViewShow(ftime, more_than_one_hour);
    }

    public static String secToHMSTime_TextViewShow(double time, double more_than_one_hour) {

        Log.i(TAG, "secToHMSTime_TextViewShow: time=" + time);
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
