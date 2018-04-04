package com.zhiyicx.thinksnsplus.modules.shortvideo.preview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.tym.shortvideo.filter.helper.MagicFilterType;
import com.tym.shortvideo.filter.helper.SlideGpuFilterGroup;
import com.tym.shortvideo.interfaces.SingleCallback;
import com.tym.shortvideo.media.MediaPlayerWrapper;
import com.tym.shortvideo.media.VideoInfo;
import com.tym.shortvideo.mediacodec.VideoClipper;
import com.tym.shortvideo.recodrender.ParamsManager;
import com.tym.shortvideo.recordcore.VideoListManager;
import com.tym.shortvideo.recordcore.multimedia.VideoCombineManager;
import com.tym.shortvideo.recordcore.multimedia.VideoCombiner;
import com.tym.shortvideo.utils.FileUtils;
import com.tym.shortvideo.utils.TrimVideoUtil;
import com.tym.shortvideo.view.VideoPreviewView;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.ToastUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.EmptySubscribe;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    @BindView(R.id.tv_toolbar_right)
    TextView mToolbarRight;
    @BindView(R.id.tv_toolbar_left)
    TextView mToolbarLeft;
    @BindView(R.id.tv_toolbar_center)
    TextView mToolbarCenter;
    @BindView(R.id.rl_toolbar)
    RelativeLayout mToolBar;

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
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected boolean setStatusbarGrey() {
        return false;
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
        mFilterType = MagicFilterType.NONE;
        mVideoView.setOnTouchListener(this);
        mToolbarCenter.setText(R.string.filter);
        mToolbarLeft.setText(R.string.cancel);
        mToolbarRight.setText(R.string.complete);
        initListener();
    }

    private void initListener() {
        RxView.clicks(mToolbarRight)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> combineVideo());

        RxView.clicks(mToolbarLeft)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> mActivity.finish());

        mVideoView.setOnFilterChangeListener(this);
    }

    @Override
    protected void setRightClick() {
        super.setRightClick();
        if (isLoading()) {
            return;
        }
        combineVideo();
    }

    private boolean isLoading() {
        return isLoading;
    }

    @Override
    protected void initData() {
        ArrayList<String> srcList = getArguments().getStringArrayList(PATH);
        if (srcList == null || srcList.isEmpty()) {
            throw new IllegalArgumentException("video path can not be null");
        }
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
    public void onDestroyView() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        isDestroy = true;
        mVideoView.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onBackPressed() {
        if (isLoading()) {
            super.onBackPressed();
        } else {
            mActivity.finish();
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

    /**
     * 合并视频
     */
    private void combineVideo() {

        mVideoView.pause();
        showCenterLoading("视频处理中");
        isLoading = true;

        final String fileName = "CainCamera_" + System.currentTimeMillis() + ".mp4";
        final String path = ParamsManager.AlbumPath
                + fileName;
        final File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        VideoCombineManager.getInstance()
                .startVideoCombiner(VideoListManager.getInstance().getSubVideoPathList(),
                        path, new VideoCombiner.VideoCombineListener() {
                            @Override
                            public void onCombineStart() {
                                LogUtils.d(TAG, "开始合并");

                            }

                            @Override
                            public void onCombineProcessing(final int current, final int sum) {
                                LogUtils.d(TAG, "当前视频： " + current + ", 合并视频总数： " + sum);

                            }

                            @Override
                            public void onCombineFinished(final boolean success) {
                                if (success) {
                                    LogUtils.d(TAG, "合并成功");
                                } else {
                                    LogUtils.d(TAG, "合并失败");
                                }
                                VideoListManager.getInstance().removeAllSubVideo();
                                clipVideo(path);

                            }
                        });
    }

    private void clipVideo(String path) {

        VideoClipper clipper = new VideoClipper();
        clipper.showBeauty();
        clipper.setInputVideoPath(mActivity, path);
        final String fileName = "tym_preview.mp4";
        mOutputPath = FileUtils.getPath("tym/tym/", fileName);
        clipper.setFilterType(mFilterType);
        clipper.setOutputVideoPath(mOutputPath);
        clipper.setOnVideoCutFinishListener(() -> mSubscription = Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscribe<Object>() {
                    @Override
                    public void onCompleted() {
                        FileUtils.updateMediaStore(mActivity, mOutputPath, (s, uri) -> {

                            isLoading = false;
                            hideCenterLoading();



                            VideoInfo videoInfo = new VideoInfo();
                            videoInfo.setPath(mOutputPath);
                            videoInfo.setCreateTime(System.currentTimeMillis()+"");
                            videoInfo.setWidth(mVideoView.getVideoWidth());
                            videoInfo.setHeight(mVideoView.getVideoHeight());
                            videoInfo.setDuration(mVideoView.getVideoDuration());

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.RGB_565;
                            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(mActivity.getContentResolver(), videoInfo.storeId, MediaStore.Images.Thumbnails.MINI_KIND,
                                    options);
                            videoInfo.setCover(com.zhiyicx.common.utils.FileUtils.saveBitmapToFile(mActivity, bitmap, "video_cover"));

                            SendDynamicDataBean sendDynamicDataBean = new SendDynamicDataBean();
                            sendDynamicDataBean.setDynamicBelong(SendDynamicDataBean.NORMAL_DYNAMIC);
                            List<ImageBean> pic = new ArrayList<>();
                            ImageBean imageBean = new ImageBean();
                            imageBean.setImgUrl(videoInfo.getCover());
                            pic.add(imageBean);
                            sendDynamicDataBean.setDynamicPrePhotos(pic);
                            sendDynamicDataBean.setDynamicType(SendDynamicDataBean.VIDEO_TEXT_DYNAMIC);
                            sendDynamicDataBean.setVideoInfo(videoInfo);
                            SendDynamicActivity.startToSendDynamicActivity(getContext(), sendDynamicDataBean);
                            mActivity.finish();
                        });
                    }
                }));
        try {
            clipper.clipVideo(0, mVideoView.getVideoDuration() * 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
