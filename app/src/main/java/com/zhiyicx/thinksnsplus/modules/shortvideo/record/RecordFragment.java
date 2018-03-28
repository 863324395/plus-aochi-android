package com.zhiyicx.thinksnsplus.modules.shortvideo.record;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.tym.shortvideo.filter.helper.MagicFilterFactory;
import com.tym.shortvideo.filter.helper.type.GLType;
import com.tym.shortvideo.filter.helper.type.TextureRotationUtils;
import com.tym.shortvideo.recodrender.CaptureFrameCallback;
import com.tym.shortvideo.recodrender.ColorFilterManager;
import com.tym.shortvideo.recodrender.DrawerManager;
import com.tym.shortvideo.recodrender.ParamsManager;
import com.tym.shortvideo.recodrender.RecordManager;
import com.tym.shortvideo.recodrender.RenderStateChangedListener;
import com.tym.shortvideo.recordcore.CountDownManager;
import com.tym.shortvideo.recordcore.VideoListManager;
import com.tym.shortvideo.recordcore.multimedia.MediaEncoder;
import com.tym.shortvideo.recordcore.multimedia.VideoCombineManager;
import com.tym.shortvideo.recordcore.multimedia.VideoCombiner;
import com.tym.shortvideo.utils.CameraUtils;
import com.tym.shortvideo.utils.FileUtils;
import com.tym.shortvideo.utils.StringUtils;
import com.tym.shortvideo.view.AspectFrameLayout;
import com.tym.shortvideo.view.AsyncRecyclerview;
import com.tym.shortvideo.view.CainSurfaceView;
import com.tym.shortvideo.view.ProgressView;
import com.tym.shortvideo.view.ShutterButton;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.common.utils.ToastUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.home.HomeActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.adapter.EffectFilterAdapter;
import com.zhiyicx.thinksnsplus.modules.shortvideo.preview.PreviewActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2018/03/28/10:52
 * @Email Jliuer@aliyun.com
 * @Description 录制
 */
public class RecordFragment extends TSFragment implements SurfaceHolder.Callback, CainSurfaceView.OnClickListener, CainSurfaceView.OnTouchScroller, RenderStateChangedListener,
        CaptureFrameCallback, ShutterButton.GestureListener {

    @BindView(R.id.layout_aspect)
    AspectFrameLayout mLayoutAspect;
    @BindView(R.id.tv_fps)
    TextView mTvFps;
    @BindView(R.id.btn_switch)
    Button mBtnSwitch;
    @BindView(R.id.btn_lvjing)
    Button mBtnLvjing;
    @BindView(R.id.btn_beauty)
    Button mBtnBeauty;
    @BindView(R.id.btn_take)
    ShutterButton mBtnTake;
    @BindView(R.id.btn_record_delete)
    Button mBtnRecordDelete;
    @BindView(R.id.btn_local)
    Button mBtnLocal;
    @BindView(R.id.btn_record_done)
    Button mBtnRecordDone;
    @BindView(R.id.effect_list)
    AsyncRecyclerview mEffectList;
    @BindView(R.id.tym_test)
    ProgressView mTymTest;
    @BindView(R.id.tv_countdown)
    TextView mTvCountdown;

    private CainSurfaceView mCameraSurfaceView;

    // 状态标志
    private boolean mOnPreviewing = false;
    private boolean mOnRecording = false;

    // 显示滤镜选择器
    private boolean isShowingEffect = false;
    private LinearLayoutManager mEffectManager;

    // 是否需要滚动
    private boolean mEffectNeedToMove = false;

    // 当前长宽比值
    private float mCurrentRatio;

    private int mColorIndex = 0;

    // 主线程Handler
    private Handler mMainHandler;

    // 对焦大小
    private static final int sFocusSize = 100;

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected int getstatusbarAndToolbarHeight() {
        if (setUseSatusbar()) {
            return 0;
        }
        return super.getstatusbarAndToolbarHeight();
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected void initView(View rootView) {

        mBtnLvjing.setVisibility(View.GONE);
        String phoneName = Build.MODEL;
        if (phoneName.toLowerCase().contains("bullhead")
                || phoneName.toLowerCase().contains("nexus 5x")) {
            TextureRotationUtils.setBackReverse(true);
            ParamsManager.mBackReverse = true;
        }
    }

    @Override
    protected void initData() {
        // 创建渲染线程
        DrawerManager.getInstance().createRenderThread(mActivity);
        // 添加渲染状态回调监听
        DrawerManager.getInstance().addRenderStateChangedListener(this);
        // 设置拍照回调
        DrawerManager.getInstance().setCaptureFrameCallback(this);
        mMainHandler = new Handler(mActivity.getMainLooper());

        mCurrentRatio = CameraUtils.getCurrentRatio();

        mLayoutAspect.setAspectRatio(mCurrentRatio);
        mCameraSurfaceView = new CainSurfaceView(mActivity);
        mCameraSurfaceView.getHolder().addCallback(this);
        mCameraSurfaceView.addClickListener(this);
        mLayoutAspect.addView(mCameraSurfaceView);
        mLayoutAspect.requestLayout();

        adjustBottomView();

        initEffectListView();
        DrawerManager.getInstance().setBeautifyLevel(0);
        setRecordType(2);

        initListener();
    }

    private void initListener() {
        RxView.clicks(mBtnSwitch)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> switchCamera());

        RxView.clicks(mBtnTake)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> takePicture());

        RxView.clicks(mBtnBeauty)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> DrawerManager.getInstance().setBeautifyLevel(100));

        RxView.clicks(mBtnLocal)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid ->
                        startActivity(new Intent(mActivity, HomeActivity.class))
                );

        RxView.clicks(mBtnRecordDelete)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> deleteRecordedVideo(false));

        RxView.clicks(mBtnRecordDone)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> previewRecordVideo());
    }

    @Override
    public void onResume() {
        super.onResume();
        registerHomeReceiver();
        DrawerManager.getInstance().startPreview();
        if (isShowingEffect) {
            scrollToCurrentEffect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegisterHomeReceiver();
        DrawerManager.getInstance().stopPreview();
    }

    @Override
    public void onBackPressed() {
        if (isShowingEffect) {
            isShowingEffect = false;
            mEffectList.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        DrawerManager.getInstance().destoryTrhead();
        // 在停止时需要释放上下文，防止内存泄漏
        ParamsManager.context = null;
        super.onDestroy();
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.activity_camera;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        DrawerManager.getInstance().surfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        DrawerManager.getInstance().surfacrChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        DrawerManager.getInstance().surfaceDestroyed();
    }

    @Override
    public void onPreviewing(boolean previewing) {
        mOnPreviewing = previewing;
        mBtnTake.setEnableOpenned(mOnPreviewing);
    }

    @Override
    public void onFrameCallback(ByteBuffer buffer, int width, int height) {

    }

    @Override
    public void swipeBack() {
        mColorIndex++;
        if (mColorIndex >= ColorFilterManager.getInstance().getColorFilterCount()) {
            mColorIndex = 0;
        }
        DrawerManager.getInstance()
                .changeFilterType(ColorFilterManager.getInstance().getColorFilterType(mColorIndex));
        scrollToCurrentEffect();
        LogUtils.d("changeFilter", "index = " + mColorIndex + ", filter name = "
                + ColorFilterManager.getInstance().getColorFilterName(mColorIndex));
    }

    @Override
    public void swipeFrontal() {
        mColorIndex--;
        if (mColorIndex < 0) {
            int count = ColorFilterManager.getInstance().getColorFilterCount();
            mColorIndex = count > 0 ? count - 1 : 0;
        }
        DrawerManager.getInstance()
                .changeFilterType(ColorFilterManager.getInstance().getColorFilterType(mColorIndex));

        scrollToCurrentEffect();
        LogUtils.d("changeFilter", "index = " + mColorIndex + ", filter name = "
                + ColorFilterManager.getInstance().getColorFilterName(mColorIndex));
    }

    @Override
    public void swipeUpper(boolean startInLeft) {

    }

    @Override
    public void swipeDown(boolean startInLeft) {

    }

    @Override
    public void onClick(float x, float y) {
        surfaceViewClick(x, y);
    }

    @Override
    public void doubleClick(float x, float y) {

    }

    @Override
    public void onStartRecord() {
        // 初始化录制线程
        RecordManager.getInstance().initThread();
        // 设置输出路径
        String path = ParamsManager.VideoPath
                + "CainCamera_" + System.currentTimeMillis() + ".mp4";
        RecordManager.getInstance().setOutputPath(path);
        // 是否允许录音，只有录制视频才有音频
        RecordManager.getInstance().setEnableAudioRecording(ParamsManager.canRecordingAudio
                && ParamsManager.sMGLType == GLType.VIDEO);
        // 是否允许高清录制
        RecordManager.getInstance().enableHighDefinition(true);
        // 初始化录制器
        RecordManager.getInstance().initRecorder(RecordManager.RECORD_WIDTH,
                RecordManager.RECORD_HEIGHT, mEncoderListener);

        // 隐藏删除按钮
        if (ParamsManager.sMGLType == GLType.VIDEO) {
            mTymTest.setVisibility(View.GONE);
            mBtnRecordDelete.setVisibility(View.GONE);
        }
        // 初始化倒计时
        CountDownManager.getInstance().initCountDownTimer();
        CountDownManager.getInstance().setCountDownListener(mCountDownListener);
        mBtnTake.setProgressMax((int) CountDownManager.getInstance().getMaxMilliSeconds());
        mTymTest.setProgressMax((int) CountDownManager.getInstance().getMaxMilliSeconds());
        mTymTest.setProgressMin((int) CountDownManager.getInstance().getMinMilliSeconds());
        // 添加分割线
        mBtnTake.addSplitView();
        mTymTest.setDeleteMode(false);
        mTymTest.addSplitView();
        mBtnLocal.setVisibility(View.GONE);
    }

    @Override
    public void onStopRecord() {

    }

    @Override
    public void onProgressOver() {

    }

    private void registerHomeReceiver() {
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mActivity.registerReceiver(mHomePressReceiver, homeFilter);
    }

    private void unRegisterHomeReceiver() {
        mActivity.unregisterReceiver(mHomePressReceiver);
    }

    /**
     * 切换相机
     */
    private void switchCamera() {
        if (mCameraSurfaceView != null) {
            DrawerManager.getInstance().switchCamera();
        }
    }

    /**
     * 显示滤镜
     */
    private void showFilters() {
        isShowingEffect = true;
        if (mEffectList != null) {
            mEffectList.setVisibility(View.VISIBLE);
            scrollToCurrentEffect();
        }
    }

    /**
     * 调整底部视图
     */
    private void adjustBottomView() {
        if (CameraUtils.getCurrentRatio() < CameraUtils.Ratio_4_3) {
            mBtnRecordDelete.setBackgroundResource(R.mipmap.preview_video_delete_white);
            mBtnRecordDone.setBackgroundResource(R.mipmap.preview_video_done_white);
        } else {
            mBtnRecordDelete.setBackgroundResource(R.mipmap.preview_video_delete_black);
            mBtnRecordDone.setBackgroundResource(R.mipmap.preview_video_done_black);
        }
    }

    /**
     * 初始化滤镜显示
     */
    private void initEffectListView() {
        // 初始化滤镜图片
        mEffectList.setVisibility(View.GONE);
        mEffectManager = new LinearLayoutManager(mActivity);
        mEffectManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mEffectList.setLayoutManager(mEffectManager);

        EffectFilterAdapter adapter = new EffectFilterAdapter(mActivity, R.layout.item_effect_view,
                MagicFilterFactory.getInstance().getFilterTypes());

        mEffectList.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                mColorIndex = position;
                DrawerManager.getInstance().changeFilterType(
                        ColorFilterManager.getInstance().getColorFilterType(position));
                LogUtils.d("changeFilter", "index = " + mColorIndex + ", filter name = "
                        + ColorFilterManager.getInstance().getColorFilterName(mColorIndex));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    public void setRecordType(int currentIndex) {
        if (currentIndex == 0) {
            ParamsManager.sMGLType = GLType.GIF;
            // TODO GIF录制后面再做处理
            mBtnTake.setIsRecorder(true);
        } else if (currentIndex == 1) {
            ParamsManager.sMGLType = GLType.PICTURE;
            // 拍照状态
            mBtnTake.setIsRecorder(false);
        } else if (currentIndex == 2) {
            ParamsManager.sMGLType = GLType.VIDEO;
            // 录制视频状态
            mBtnTake.setIsRecorder(true);
        }

        // 显示时间
        if (currentIndex == 2) {
            mTvCountdown.setVisibility(View.VISIBLE);
        } else {
            mTvCountdown.setVisibility(View.GONE);
        }
    }

    /**
     * 滚动到当前位置
     */
    private void scrollToCurrentEffect() {
        if (isShowingEffect) {
            int firstItem = mEffectManager.findFirstVisibleItemPosition();
            int lastItem = mEffectManager.findLastVisibleItemPosition();
            if (mColorIndex <= firstItem) {
                mEffectList.scrollToPosition(mColorIndex);
            } else if (mColorIndex <= lastItem) {
                int top = mEffectList.getChildAt(mColorIndex - firstItem).getTop();
                mEffectList.scrollBy(0, top);
            } else {
                mEffectList.scrollToPosition(mColorIndex);
                mEffectNeedToMove = true;
            }
        }
    }

    /**
     * 点击SurfaceView
     *
     * @param x x轴坐标
     * @param y y轴坐标
     */
    private void surfaceViewClick(float x, float y) {
        if (isShowingEffect) {
            isShowingEffect = false;
            mEffectList.setVisibility(View.GONE);
        }
        // 设置聚焦区域
        DrawerManager.getInstance().setFocusAres(CameraUtils.getFocusArea((int) x, (int) y,
                mCameraSurfaceView.getWidth(), mCameraSurfaceView.getHeight(), sFocusSize));
    }

    /**
     * 拍照
     */
    private void takePicture() {
        if (!mOnPreviewing) {
            return;
        }
        DrawerManager.getInstance().takePicture();
    }

    /**
     * 等待录制完成再预览录制视频
     */
    private void previewRecordVideo() {
        if (mOnRecording) {
            mNeedToWaitStop = true;
            // 停止录制
            DrawerManager.getInstance().stopRecording();
        } else {
            // 销毁录制线程
            RecordManager.getInstance().destoryThread();
            mNeedToWaitStop = false;
            combineVideo();
            // 隐藏删除和预览按钮
            mBtnRecordDone.setVisibility(View.GONE);
            mBtnRecordDelete.setVisibility(View.GONE);
        }
    }

    /**
     * 合并视频
     */
    private void combineVideo() {
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
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showToast("开始合并");
                                    }
                                });

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
                                // 更更新媒体库
                                FileUtils.updateMediaStore(mActivity, path, fileName);
                                // 跳转至视频编辑页面
                                PreviewActivity.startPreviewActivity(mActivity, path);
                            }
                        });
    }

    /**
     * 删除录制的视频
     */
    synchronized private void deleteRecordedVideo(boolean clearAll) {
        // 处于删除模式，则删除文件
        if (mBtnTake.isDeleteMode()) {

            // 删除视频，判断是否清除所有
            if (clearAll) {
                // 清除所有分割线
                mBtnTake.cleanSplitView();
                mTymTest.cleanSplitView();
                VideoListManager.getInstance().removeAllSubVideo();
            } else {
                // 删除分割线
                mBtnTake.deleteSplitView();
                mTymTest.deleteSplitView();
                VideoListManager.getInstance().removeLastSubVideo();
            }
            // 重置计时器记录走过的时长
            CountDownManager.getInstance().resetDuration();
            // 重置最后一秒点击标志
            CountDownManager.getInstance().resetLastSecondStop();
            // 更新进度
            mBtnTake.setProgress(CountDownManager.getInstance().getVisibleDuration());
            mTymTest.setProgress(CountDownManager.getInstance().getVisibleDuration());
            // 更新时间
            mTvCountdown.setText(CountDownManager.getInstance().getVisibleDurationString());
            // 如果此时没有了视频，则隐藏删除按钮
            if (VideoListManager.getInstance().getSubVideoList().size() <= 0) {
                mBtnRecordDelete.setVisibility(View.GONE);
                mBtnRecordDone.setVisibility(View.GONE);
                // 复位状态
                mNeedToWaitStop = false;
                mOnRecording = false;
            }

            if (mTymTest.getSplitList().size() == 0) {
                mBtnLocal.setVisibility(View.VISIBLE);
            }

        } else { // 没有进入删除模式则进入删除模式
            mBtnTake.setDeleteMode(true);
            mTymTest.setDeleteMode(true);
        }
    }

    /**
     * 监听点击home键
     */
    private BroadcastReceiver mHomePressReceiver = new BroadcastReceiver() {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (TextUtils.isEmpty(reason)) {
                    return;
                }
                // 当点击了home键时需要停止预览，防止后台一直持有相机
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    // 停止录制
                    if (mOnRecording) {
                        // 停止录制
                        RecordManager.getInstance().stopRecording();
                        // 取消倒计时
                        CountDownManager.getInstance().cancelTimerWithoutSaving();
                        // 重置进入条
                        mBtnTake.setProgress((int) CountDownManager.getInstance().getVisibleDuration());
                        mTymTest.setProgress((int) CountDownManager.getInstance().getVisibleDuration());
                        // 删除分割线
                        mBtnTake.deleteSplitView();
                        mTymTest.deleteSplitView();
                        // 关闭按钮
                        mBtnTake.closeButton();
                        // 更新时间
                        mTvCountdown.setText(CountDownManager.getInstance().getVisibleDurationString());
                    }
                    if (mOnPreviewing) {
                        DrawerManager.getInstance().stopPreview();
                    }
                }
            }
        }
    };

    // MediaEncoder准备好的数量
    private int mPreparedCount = 0;

    // 开始MediaEncoder的数量
    private int mStartedCount = 0;

    // 释放MediaEncoder的数量
    private int mReleaseCount = 0;

    // 是否需要等待录制完成再跳转
    private boolean mNeedToWaitStop;
    /**
     * 录制监听器
     */
    private MediaEncoder.MediaEncoderListener
            mEncoderListener = new MediaEncoder.MediaEncoderListener() {

        @Override
        public void onPrepared(MediaEncoder encoder) {
            mPreparedCount++;
            // 没有录音权限、不允许音频录制、允许录制音频并且准备好两个MediaEncoder，就可以开始录制了
            if (!ParamsManager.canRecordingAudio
                    || (ParamsManager.canRecordingAudio && mPreparedCount == 2)
                    || ParamsManager.sMGLType == GLType.GIF) { // 录制GIF，没有音频
                // 准备完成，开始录制
                DrawerManager.getInstance().startRecording();

                // 重置
                mPreparedCount = 0;
            }
        }

        @Override
        public void onStarted(MediaEncoder encoder) {
            mStartedCount++;
            // 没有录音权限、不允许音频录制、允许录制音频并且开始了两个MediaEncoder，就处于录制状态了
            if (!ParamsManager.canRecordingAudio
                    || (ParamsManager.canRecordingAudio && mStartedCount == 2)
                    || ParamsManager.sMGLType == GLType.GIF) { // 录制GIF，没有音频
                mOnRecording = true;

                // 重置状态
                mStartedCount = 0;

                // 编码器已经进入录制状态，则快门按钮可用
                mBtnTake.setEnableEncoder(true);

                // 开始倒计时
                CountDownManager.getInstance().startTimer();
            }
        }

        @Override
        public void onStopped(MediaEncoder encoder) {
        }

        @Override
        public void onReleased(MediaEncoder encoder) { // 复用器释放完成
            mReleaseCount++;
            // 没有录音权限、不允许音频录制、允许录制音频并且释放了两个MediaEncoder，就完全释放掉了
            if (!ParamsManager.canRecordingAudio
                    || (ParamsManager.canRecordingAudio && mReleaseCount == 2)
                    || ParamsManager.sMGLType == GLType.GIF) { // 录制GIF，没有音频
                // 录制完成跳转预览页面
                String outputPath = RecordManager.getInstance().getOutputPath();
                // 添加分段视频，存在时长为0的情况，也就是取消倒计时但不保存时长的情况
                if (CountDownManager.getInstance().getRealDuration() > 0) {
                    VideoListManager.getInstance().addSubVideo(outputPath,
                            (int) CountDownManager.getInstance().getRealDuration());
                } else {    // 移除多余的视频
                    FileUtils.deleteFile(outputPath);
                }
                // 重置当前走过的时长
                CountDownManager.getInstance().resetDuration();

                // 处于非录制状态
                mOnRecording = false;

                // 显示删除按钮
                if (ParamsManager.sMGLType == GLType.VIDEO) {
                    mActivity.runOnUiThread(() -> {
                        mBtnRecordDone.setVisibility(View.VISIBLE);
                        mBtnRecordDelete.setVisibility(View.VISIBLE);
                    });
                }

                // 处于录制状态点击了预览按钮，则需要等待完成再跳转， 或者是处于录制GIF状态
                if (mNeedToWaitStop || ParamsManager.sMGLType == GLType.GIF) {
                    mActivity.runOnUiThread(() -> {
                        // 重置按钮状态
                        // 开始预览
                        previewRecordVideo();
                    });
                }
                // 重置释放状态
                mReleaseCount = 0;

                // 编码器已经完全释放，则快门按钮可用
                mBtnTake.setEnableEncoder(true);

            }

        }
    };

    private CountDownManager.CountDownListener
            mCountDownListener = new CountDownManager.CountDownListener() {
        @Override
        public void onProgressChanged(long duration) {
            // 设置进度
            mBtnTake.setProgress(duration);
            mTymTest.setProgress(duration);
            // 设置时间
            mTvCountdown.setText(StringUtils.generateMillisTime((int) duration));
        }
    };
}