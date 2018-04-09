package com.zhiyicx.thinksnsplus.modules.shortvideo.cover;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.zhiyicx.common.utils.ToastUtils;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.thinksnsplus.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2018/04/07
 * @Email Jliuer@aliyun.com
 * @Description 选择封面 && 动态发布页预览
 */
public class CoverFragment extends TSFragment implements MediaPlayerWrapper.IMediaCallback {
    public static final String PATH = "path";
    public static final String PREVIEW = "preview";
    public static final int REQUEST_COVER_CODE = 1000;
    public static final int REQUEST_DELETE_CODE = 2000;

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
    private boolean isPre;
    private boolean mResumed;

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
    protected int setLeftImg() {
        return R.mipmap.topbar_back_white;
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
                .subscribe(aVoid -> {
                    if (isPre) {
                        mActivity.setResult(Activity.RESULT_OK);
                        mActivity.finish();
                    } else {
                        getVideoCover();
                    }
                });

        RxView.clicks(mToolbarLeft)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> {
                    mActivity.setResult(Activity.RESULT_CANCELED);
                    mActivity.finish();
                });

        mVideoView.setIMediaCallback(this);

    }

    private void getVideoCover() {
        buildDialog(getString(R.string.covering));
        TrimVideoUtil.backgroundShootVideoThumb(mActivity, Uri.parse(mPath), (long) mSeekBar
                .getProgress(), (bitmap, integer) -> {
            String cover = com.zhiyicx.common.utils.FileUtils.saveBitmapToFile(mActivity,
                    bitmap,
                    "video_cover.jpg");
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
        isPre = getArguments().getBoolean(PREVIEW);
        mVideoView.setVideoPath(srcList);
        mSeekBar.setVisibility(isPre ? View.GONE : View.VISIBLE);

        if (isPre) {
            mToolbarCenter.setText(R.string.preview);
            mToolbarLeft.setText("");
            mToolbarLeft.setCompoundDrawables(UIUtils.getCompoundDrawables(getContext(), setLeftImg()), null, null, null);
            mToolbarRight.setText(R.string.delete);
            mToolbarRight.setTextColor(ContextCompat.getColorStateList(getContext(), com.zhiyicx.baseproject.R.color.selector_text_color));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mResumed && isPre) {
            mVideoView.start();
        }
        mResumed = true;
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
        if (isPre) {
            mVideoView.start();
        }
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
        if (isPre) {
            mVideoView.seekTo(0);
            mVideoView.start();
        }
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
