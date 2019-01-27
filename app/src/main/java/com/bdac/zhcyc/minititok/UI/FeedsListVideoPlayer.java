package com.bdac.zhcyc.minititok.UI;

import android.content.Context;
import android.util.AttributeSet;

import com.bdac.zhcyc.minititok.R;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class FeedsListVideoPlayer extends StandardGSYVideoPlayer {
    public FeedsListVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public FeedsListVideoPlayer(Context context) {
        super(context);
    }

    public FeedsListVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.feeds_list_video_player;
    }

    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        mChangePosition = false;
        mChangeVolume = false;
        mBrightness = false;
    }



    @Override
    protected void touchSurfaceUp() {
        clickStartIcon();
    }

    @Override
    protected void touchDoubleUp() {
        //super.touchDoubleUp();
    }
}
