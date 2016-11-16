package com.example.herve.toolbarview.view.previewbar;

import android.content.Context;
import android.util.AttributeSet;

import com.example.herve.toolbarview.view.ijkplayer.widget.media.AndroidMediaController;

/**
 * Created           :Herve on 2016/11/15.
 *
 * @ Author          :Herve
 * @ e-mail          :lijianyou.herve@gmail.com
 * @ LastEdit        :2016/11/15
 * @ projectName     :ToolBarView
 * @ version
 */
public class PreViewBarMediaControl extends AndroidMediaController {


    public PreViewBarMediaControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreViewBarMediaControl(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public PreViewBarMediaControl(Context context) {
        super(context);
    }

    @Override
    public void doPauseResume() {
        if (mPlayer.isPlaying()) {
            if (onStateListener != null) {
                onStateListener.onStateListener(false);
            }
        } else {
            if (onStateListener != null) {
                onStateListener.onStateListener(true);
            }
        }
        super.doPauseResume();

    }


    private OnStateListener onStateListener;

    public void setOnStateListener(OnStateListener onStateListener) {
        this.onStateListener = onStateListener;
    }

    public interface OnStateListener {

        void onStateListener(boolean isPlaying);
    }
}
