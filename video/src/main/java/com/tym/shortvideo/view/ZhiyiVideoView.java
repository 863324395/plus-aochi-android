package com.tym.shortvideo.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;
import com.tym.video.R;
import com.zhiyicx.common.utils.log.LogUtils;

import java.lang.reflect.Constructor;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUserActionStandard;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * @Author Jliuer
 * @Date 2018/03/29/15:32
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ZhiyiVideoView extends JZVideoPlayerStandard {

//    LinearLayout mBottonContainer;
//    LinearLayout mTopContainer;

    public ZhiyiVideoView(Context context) {
        super(context);
    }

    public ZhiyiVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
//        mBottonContainer = (LinearLayout) findViewById(R.id.layout_bottom);
//        mTopContainer = (LinearLayout) findViewById(R.id.layout_top);
    }

    @Override
    public int getLayoutId() {
        return R.layout.zhiyi_layout_standard;
    }

    @Override
    public void startVideo() {
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            LogUtils.d(TAG, "startVideo [" + this.hashCode() + "] ");
            initTextureView();
            addTextureView();
            AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            JZUtils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            JZMediaManager.setDataSource(dataSourceObjects);
            JZMediaManager.setCurrentDataSource(JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
            JZMediaManager.instance().positionInList = positionInList;
            onStatePreparing();
        } else {
            super.startVideo();
        }
    }

    @Override
    public void onAutoCompletion() {
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            onStateAutoComplete();
        } else {
            super.onAutoCompletion();
        }

    }

    @Override
    public void onVideoSizeChanged() {
        LogUtils.i(TAG, "onVideoSizeChanged " + " [" + this.hashCode() + "] ");
        if (JZMediaManager.textureView != null) {
            if (videoRotation != 0) {
                JZMediaManager.textureView.setRotation(videoRotation);
            }
            JZMediaManager.textureView.setVideoSize(JZMediaManager.instance().currentVideoWidth, JZMediaManager.instance().currentVideoHeight);
        }
    }

    @Override
    public void startWindowFullscreen() {
        LogUtils.i(TAG, "startWindowFullscreen " + " [" + this.hashCode() + "] ");
        hideSupportActionBar(getContext());
        //.getWindow().getDecorView();
        ViewGroup vp = (ViewGroup) (JZUtils.scanForActivity(getContext()))
                .findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(cn.jzvd.R.id.jz_fullscreen_id);
        if (old != null) {
            vp.removeView(old);
        }
        textureViewContainer.removeView(JZMediaManager.textureView);
        try {
            Constructor<ZhiyiVideoView> constructor = (Constructor<ZhiyiVideoView>) ZhiyiVideoView.this.getClass().getConstructor(Context.class);
            JZVideoPlayer jzVideoPlayer = constructor.newInstance(getContext());
            jzVideoPlayer.setId(cn.jzvd.R.id.jz_fullscreen_id);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vp.addView(jzVideoPlayer, lp);
            jzVideoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            jzVideoPlayer.setUp(dataSourceObjects, currentUrlMapIndex, JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects);
            jzVideoPlayer.setState(currentState);
            jzVideoPlayer.addTextureView();
            JZVideoPlayerManager.setSecondFloor(jzVideoPlayer);
//            final Animation ra = AnimationUtils.loadAnimation(getContext(), R.anim.start_fullscreen);
//            jzVideoPlayer.setAnimation(ra);

            int orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            if (JZMediaManager.instance().currentVideoWidth > JZMediaManager.instance().currentVideoHeight) {
                // 横屏
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            } else {
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
            JZUtils.setRequestedOrientation(getContext(), orientation);


            onStateNormal();
            jzVideoPlayer.progressBar.setSecondaryProgress(progressBar.getSecondaryProgress());
            jzVideoPlayer.startProgressTimer();
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showWifiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(getResources().getString(R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onEvent(JZUserActionStandard.ON_CLICK_START_WIFIDIALOG);
                startVideo();
                WIFI_TIP_DIALOG_SHOWED = true;
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clearFloatScreen();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
