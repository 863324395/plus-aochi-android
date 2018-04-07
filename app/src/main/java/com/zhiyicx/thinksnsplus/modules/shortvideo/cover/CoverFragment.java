package com.zhiyicx.thinksnsplus.modules.shortvideo.cover;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.tym.shortvideo.media.MediaPlayerWrapper;
import com.tym.shortvideo.media.VideoInfo;
import com.tym.shortvideo.utils.TrimVideoUtil;
import com.tym.shortvideo.view.VideoPreviewView;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.thinksnsplus.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2018/04/07
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CoverFragment extends TSFragment implements MediaPlayerWrapper.IMediaCallback {
    public static final String PATH = "path";
    public static final String VIDEO = "video";
    public static final int REQUESTCODE = 1000;

    @BindView(R.id.videoView)
    VideoPreviewView mVideoView;
    @BindView(R.id.sk_cover)
    SeekBar mSeekBar;
    @BindView(R.id.tv_toolbar_right)
    TextView mToolbarRight;
    @BindView(R.id.tv_toolbar_left)
    TextView mToolbarLeft;
    @BindView(R.id.tv_toolbar_center)
    TextView mToolbarCenter;
    @BindView(R.id.rl_toolbar)
    RelativeLayout mToolBar;


    private ProgressDialog mProgressDialog;
    private String mPath;

    public static CoverFragment newInstance(Bundle bundle) {
        CoverFragment fragment = new CoverFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void initView(View rootView) {

        mToolbarCenter.setText(R.string.clip_cover);
        mToolbarLeft.setText(R.string.cancel);
        mToolbarRight.setText(R.string.complete);
        initListener();
    }

    private void initListener() {
        RxView.clicks(mToolbarRight)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> getVideoCover());

        RxView.clicks(mToolbarLeft)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> mActivity.finish());

        mVideoView.setIMediaCallback(this);

    }

    private void getVideoCover() {
        buildDialog(getString(R.string.covering));
        TrimVideoUtil.backgroundShootVideoThumb(mActivity, Uri.parse(mPath), (long) mSeekBar
                .getProgress(), (bitmap, integer) -> {
            String cover = com.zhiyicx.common.utils.FileUtils.saveBitmapToFile(mActivity,
                    bitmap,
                    "video_cover");
            mProgressDialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putString(PATH, cover);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        });
    }

    @Override
    protected void initData() {
        ArrayList<String> srcList = getArguments().getStringArrayList(PATH);
        if (srcList == null || srcList.isEmpty()) {
            throw new IllegalArgumentException("video path can not be null");
        }
        mVideoView.setVideoPath(srcList);

    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.activity_cover;
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    public void onDestroyView() {
        mVideoView.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onVideoPrepare() {
        mSeekBar.setMax(mVideoView.getVideoDuration());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mVideoView.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onVideoStart() {

    }

    @Override
    public void onVideoPause() {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onVideoChanged(VideoInfo info) {
        mPath = info.getPath();
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(mActivity, "", msg);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }
}
