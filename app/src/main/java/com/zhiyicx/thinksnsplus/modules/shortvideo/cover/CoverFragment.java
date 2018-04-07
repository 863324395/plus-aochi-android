package com.zhiyicx.thinksnsplus.modules.shortvideo.cover;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.jakewharton.rxbinding.view.RxView;
import com.tym.shortvideo.interfaces.SingleCallback;
import com.tym.shortvideo.media.VideoInfo;
import com.tym.shortvideo.utils.FileUtils;
import com.tym.shortvideo.utils.TrimVideoUtil;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.thinksnsplus.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2018/04/07
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CoverFragment extends TSFragment {
    public static final String PATH = "path";
    public static final String VIDEO = "video";

    @BindView(R.id.video_loader)
    VideoView mVideoView;
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
    @BindView(R.id.layout_surface_view)
    RelativeLayout mLinearVideo;


    private ProgressDialog mProgressDialog;
    private VideoInfo mVideoInfo;
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
        mPath = getArguments().getString(PATH);
        mVideoInfo = getArguments().getParcelable(VIDEO);

        mVideoView.setVideoURI(Uri.parse(mPath));
        mVideoView.requestFocus();

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

        mVideoView.setOnPreparedListener(this::onVideoPrepared);
    }

    private void getVideoCover() {
        buildDialog(getString(R.string.covering));
        TrimVideoUtil.backgroundShootVideoThumb(mActivity, Uri.parse(mPath), (long) mSeekBar
                .getProgress(), (bitmap, integer) -> {
            mProgressDialog.dismiss();
            
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.activity_cover;
    }


    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(mActivity, "", msg);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }

    private void onVideoPrepared(MediaPlayer mp) {

        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = mLinearVideo.getWidth();
        int screenHeight = mLinearVideo.getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        mVideoView.setLayoutParams(lp);
        mSeekBar.setMax(mVideoView.getDuration());
        mVideoView.seekTo(1);

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
}
