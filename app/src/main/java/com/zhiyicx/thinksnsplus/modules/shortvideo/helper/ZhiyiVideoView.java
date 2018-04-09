package com.zhiyicx.thinksnsplus.modules.shortvideo.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;

import java.lang.reflect.Constructor;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUserActionStandard;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;
import cn.jzvd.JZVideoPlayerStandard;

import static com.umeng.socialize.bean.SHARE_MEDIA.QQ;

/**
 * @Author Jliuer
 * @Date 2018/03/29/15:32
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ZhiyiVideoView extends JZVideoPlayerStandard {

    public ImageView mShareImageView;

    public LinearLayout mShareLineLinearLayout;

    public LinearLayout mShareLinearLayout;

    public TextView mShareToQQ;
    public TextView mShareToQQZone;
    public TextView mShareToWX;
    public TextView mShareToWXZone;
    public TextView mShareToWeiBo;

    public TextView mShareTextView;

    public ZhiyiVideoView(Context context) {
        super(context);
    }

    public ZhiyiVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        mShareImageView = (ImageView) findViewById(R.id.share);
        mShareLineLinearLayout = (LinearLayout) findViewById(R.id.ll_share_line_container);
        mShareLinearLayout = (LinearLayout) findViewById(R.id.ll_share_container);

        mShareToQQ = (TextView) findViewById(R.id.share_qq);
        mShareToQQZone = (TextView) findViewById(R.id.share_qq_zone);
        mShareToWX = (TextView) findViewById(R.id.share_wx);
        mShareToWXZone = (TextView) findViewById(R.id.share_wx_zone);
        mShareToWeiBo = (TextView) findViewById(R.id.share_weibo);
        mShareTextView = (TextView) findViewById(R.id.share_text);

        mShareImageView.setOnClickListener(this);
        mShareToQQ.setOnClickListener(this);
        mShareToQQZone.setOnClickListener(this);
        mShareToWXZone.setOnClickListener(this);
        mShareToWeiBo.setOnClickListener(this);
        mShareToWX.setOnClickListener(this);

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
            AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context
                    .AUDIO_SERVICE);
            mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager
                    .STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            JZUtils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams
                    .FLAG_KEEP_SCREEN_ON);

            JZMediaManager.setDataSource(dataSourceObjects);
            JZMediaManager.setCurrentDataSource(JZUtils.getCurrentFromDataSource
                    (dataSourceObjects, currentUrlMapIndex));
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
            mShareImageView.setVisibility(GONE);
            mShareTextView.setVisibility(GONE);
            mShareTextView.setVisibility(GONE);
            mShareLineLinearLayout.setVisibility(VISIBLE);
            mShareLinearLayout.setVisibility(VISIBLE);
        } else {
            super.onAutoCompletion();
            mShareImageView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void changeUiToNormal() {
        super.changeUiToNormal();
        mShareImageView.setVisibility(GONE);
        mShareLineLinearLayout.setVisibility(GONE);
        mShareLinearLayout.setVisibility(GONE);
    }

    @Override
    public void changeUiToPreparing() {
        super.changeUiToPreparing();
        mShareImageView.setVisibility(GONE);
        mShareLineLinearLayout.setVisibility(GONE);
        mShareLinearLayout.setVisibility(GONE);
    }

    @Override
    public void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        mShareImageView.setVisibility(GONE);
        mShareLineLinearLayout.setVisibility(GONE);
        mShareLinearLayout.setVisibility(GONE);
    }

    @Override
    public void changeUiToPlayingClear() {
        super.changeUiToPlayingClear();
        mShareImageView.setVisibility(GONE);
        mShareLineLinearLayout.setVisibility(GONE);
        mShareLinearLayout.setVisibility(GONE);
    }

    @Override
    public void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        mShareImageView.setVisibility(GONE);
        mShareLineLinearLayout.setVisibility(GONE);
        mShareLinearLayout.setVisibility(GONE);
    }

    @Override
    public void changeUiToPauseClear() {
        super.changeUiToPauseClear();
        mShareImageView.setVisibility(GONE);
        mShareLineLinearLayout.setVisibility(GONE);
        mShareLinearLayout.setVisibility(GONE);
    }

    @Override
    public void changeUiToError() {
        super.changeUiToError();
        mShareImageView.setVisibility(GONE);
        mShareLineLinearLayout.setVisibility(GONE);
        mShareLinearLayout.setVisibility(GONE);
    }

    @Override
    public void changeUiToComplete() {
        super.changeUiToComplete();
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                mShareImageView.setVisibility(VISIBLE);
                mShareLineLinearLayout.setVisibility(GONE);
                mShareLinearLayout.setVisibility(GONE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                mShareImageView.setVisibility(GONE);
                mShareLineLinearLayout.setVisibility(VISIBLE);
                mShareLinearLayout.setVisibility(VISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
            default:
        }
    }


    @Override
    public void updateStartImage() {
        super.updateStartImage();
        mShareTextView.setVisibility(replayTextView.getVisibility() == INVISIBLE ? GONE : VISIBLE);
    }

    @Override
    public void onVideoSizeChanged() {
        LogUtils.i(TAG, "onVideoSizeChanged " + " [" + this.hashCode() + "] ");
        if (JZMediaManager.textureView != null) {
            if (videoRotation != 0) {
                JZMediaManager.textureView.setRotation(videoRotation);
            }
            JZMediaManager.textureView.setVideoSize(JZMediaManager.instance().currentVideoWidth,
                    JZMediaManager.instance().currentVideoHeight);
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
            Constructor<ZhiyiVideoView> constructor = (Constructor<ZhiyiVideoView>)
                    ZhiyiVideoView.this.getClass().getConstructor(Context.class);
            ZhiyiVideoView jzVideoPlayer = constructor.newInstance(getContext());
            jzVideoPlayer.setId(cn.jzvd.R.id.jz_fullscreen_id);
            jzVideoPlayer.setShareInterface(mShareInterface);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vp.addView(jzVideoPlayer, lp);
            jzVideoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            jzVideoPlayer.setUp(dataSourceObjects, currentUrlMapIndex, JZVideoPlayerStandard
                    .SCREEN_WINDOW_FULLSCREEN, objects);
            jzVideoPlayer.setState(currentState);
            jzVideoPlayer.positionInList = this.positionInList;
            jzVideoPlayer.addTextureView();
            JZVideoPlayerManager.setSecondFloor(jzVideoPlayer);
//            final Animation ra = AnimationUtils.loadAnimation(getContext(), R.anim
// .start_fullscreen);
//            jzVideoPlayer.setAnimation(ra);

            int orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            if (JZMediaManager.instance().currentVideoWidth > JZMediaManager.instance()
                    .currentVideoHeight) {
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
        builder.setPositiveButton(getResources().getString(R.string.tips_not_wifi_confirm),
                (dialog, which) -> {
                    dialog.dismiss();
                    onEvent(JZUserActionStandard.ON_CLICK_START_WIFIDIALOG);
                    startVideo();
                    WIFI_TIP_DIALOG_SHOWED = true;
                });
        builder.setNegativeButton(getResources().getString(R.string.tips_not_wifi_cancel),
                (dialog, which) -> {
                    dialog.dismiss();
                    clearFloatScreen();
                });
        builder.setOnCancelListener(dialog -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (mShareInterface == null) {
            return;
        }
        int i = v.getId();
        SHARE_MEDIA type = null;
        switch (i) {
            case R.id.share:
                mShareInterface.share(positionInList);
                break;
            case R.id.share_qq:
                type = SHARE_MEDIA.QQ;
                break;
            case R.id.share_qq_zone:
                type = SHARE_MEDIA.QZONE;
                break;
            case R.id.share_wx:
                type = SHARE_MEDIA.WEIXIN;
                break;
            case R.id.share_wx_zone:
                type = SHARE_MEDIA.WEIXIN_CIRCLE;
                break;
            case R.id.share_weibo:
                type = SHARE_MEDIA.SINA;
                break;
            default:
        }

        if (type != null) {
            mShareInterface.shareWihtType(positionInList, type);
        }

    }

    protected ShareInterface mShareInterface;

    public void setShareInterface(ShareInterface shareInterface) {
        mShareInterface = shareInterface;
    }

    public interface ShareInterface {
        void share(int position);

        void shareWihtType(int position, SHARE_MEDIA type);
    }
}
