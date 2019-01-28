package com.bdac.zhcyc.minititok.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

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
    protected void setViewShowState(View view, int visibility) {
        if (view == null) return;
        if (view == mStartButton) {
//            if (visibility == VISIBLE) {
//                AlphaAnimation appearAnimation = new AlphaAnimation(0, 1);
//                appearAnimation.setDuration(250);
//                view.startAnimation(appearAnimation);
//                view.setVisibility(VISIBLE);
//            } else if (visibility == INVISIBLE) {
//                AlphaAnimation disappearAnimation = new AlphaAnimation(1, 0);
//                view.startAnimation(disappearAnimation);
//                disappearAnimation.setDuration(250);
//                disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        view.setVisibility(INVISIBLE);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//                });
//            } else {
//                view.setVisibility(visibility);
//            }
            view.setVisibility(INVISIBLE);
        } else {
            super.setViewShowState(view, visibility);
        }
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
