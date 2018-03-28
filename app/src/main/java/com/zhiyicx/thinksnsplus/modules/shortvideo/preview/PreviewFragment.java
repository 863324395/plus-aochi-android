package com.zhiyicx.thinksnsplus.modules.shortvideo.preview;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.tym.shortvideo.filter.helper.MagicFilterType;
import com.tym.shortvideo.filter.helper.SlideGpuFilterGroup;
import com.tym.shortvideo.media.MediaPlayerWrapper;
import com.tym.shortvideo.media.VideoInfo;
import com.tym.shortvideo.mediacodec.VideoClipper;
import com.tym.shortvideo.utils.FileUtils;
import com.tym.shortvideo.view.VideoPreviewView;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.common.utils.ToastUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.EmptySubscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2018/03/28/15:04
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class PreviewFragment extends TSFragment implements MediaPlayerWrapper.IMediaCallback,
        SlideGpuFilterGroup.OnFilterChangeListener, View.OnTouchListener {

    public static final String PATH = "path";

    @BindView(R.id.videoView)
    VideoPreviewView mVideoView;
    @BindView(R.id.tv_fps)
    TextView mTvFps;
    @BindView(R.id.btn_switch)
    Button mBtnSwitch;
    @BindView(R.id.btn_lvjing)
    Button mBtnLvjing;
    @BindView(R.id.btn_beauty)
    Button mBtnBeauty;
    @BindView(R.id.tv_confrim)
    TextView mTvConfrim;

    /**
     * 预览视频地址
     */
    private String mPath;

    /**
     * 是否第一次 Resumed
     */
    private boolean mResumed;

    private boolean isDestroy;
    private boolean isPlaying = false;

    private MagicFilterType mFilterType;

    /**
     * 视频输出地址
     */
    private String mOutputPath;

    /**
     * 替换滤镜提示
     */
    private Subscription mSubscription;

    public boolean isLoading;

    public static PreviewFragment getInstance(Bundle bundle) {
        PreviewFragment fragment = new PreviewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected boolean needCenterLoadingDialog() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        mTvFps.setVisibility(View.GONE);
        mBtnSwitch.setVisibility(View.GONE);
        mBtnLvjing.setVisibility(View.GONE);
        mFilterType = MagicFilterType.NONE;
        initListener();

    }

    private void initListener() {
        mVideoView.setOnFilterChangeListener(this);
        mVideoView.setOnTouchListener(this);

        RxView.clicks(mBtnBeauty)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> {
                    mVideoView.switchBeauty();
                    if (mBtnBeauty.isSelected()) {
                        mBtnBeauty.setSelected(false);
                    } else {
                        mBtnBeauty.setSelected(true);
                    }
                });

        RxView.clicks(mTvConfrim)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> {
                    if (isLoading()) {
                        return;
                    }
                    mVideoView.pause();
                    showCenterLoading("视频处理中");
                    isLoading = true;
                    VideoClipper clipper = new VideoClipper();
                    if (mBtnBeauty.isSelected()) {
                        clipper.showBeauty();
                    }
                    clipper.setInputVideoPath(mActivity, mPath);
                    final String fileName = "tym_" + System.currentTimeMillis() + ".mp4";
                    mOutputPath = FileUtils.getPath("tym/tym/", fileName);
                    clipper.setFilterType(mFilterType);
                    clipper.setOutputVideoPath(mOutputPath);
                    clipper.setOnVideoCutFinishListener(() -> mSubscription = Observable.empty()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new EmptySubscribe<Object>() {
                                @Override
                                public void onCompleted() {
                                    isLoading = false;
                                    ToastUtils.showToast("视频保存地址   " + mOutputPath);
                                    hideCenterLoading();
                                    FileUtils.updateMediaStore(mActivity, mOutputPath, fileName);
//                                                TrimmerActivity.go(mActivity, mOutputPath);
                                }
                            }));
                    try {
                        clipper.clipVideo(0, mVideoView.getVideoDuration() * 1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private boolean isLoading() {
        return isLoading;
    }

    @Override
    protected void initData() {
        mPath = getArguments().getString(PATH, "");
        if (TextUtils.isEmpty(mPath)) {
            throw new IllegalArgumentException("video path can not be null");
        }
        ArrayList<String> srcList = new ArrayList<>();
        srcList.add(mPath);
        mVideoView.setVideoPath(srcList);
        mVideoView.setIMediaCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ToastUtils.showToast(R.string.change_filter);
        if (mResumed) {
            mVideoView.start();
        }
        mResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        isDestroy = true;
        mVideoView.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!isLoading()) {
            super.onBackPressed();
        }
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.activity_video_preview;
    }

    @Override
    public void onVideoPrepare() {

    }

    @Override
    public void onVideoStart() {
        isPlaying = true;
    }

    @Override
    public void onVideoPause() {
        isPlaying = false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mVideoView.seekTo(0);
        mVideoView.start();
    }

    @Override
    public void onVideoChanged(VideoInfo info) {

    }

    @Override
    public void onFilterChange(MagicFilterType type) {
        this.mFilterType = type;
        mSubscription = Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscribe<Object>() {
                    @Override
                    public void onCompleted() {
                        ToastUtils.showToast("滤镜切换为---" + type);
                    }
                });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mVideoView.onTouch(event);
        return true;
    }
}
