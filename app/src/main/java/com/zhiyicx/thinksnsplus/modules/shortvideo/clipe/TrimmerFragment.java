package com.zhiyicx.thinksnsplus.modules.shortvideo.clipe;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.tym.shortvideo.interfaces.TrimVideoListener;
import com.tym.shortvideo.media.VideoInfo;
import com.tym.shortvideo.recordcore.VideoListManager;
import com.tym.shortvideo.utils.FileUtils;
import com.tym.shortvideo.utils.TrimVideoUtil;
import com.tym.shortvideo.view.VideoTrimmerView;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.preview.PreviewActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2018/03/28/18:49
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TrimmerFragment extends TSFragment implements TrimVideoListener {

    public static final String PATH = "path";
    public static final String VIDEO = "video";

    @BindView(R.id.trimmer_view)
    VideoTrimmerView mVideoTrimmerView;
    @BindView(R.id.tv_toolbar_right)
    TextView mToolbarRight;
    @BindView(R.id.tv_toolbar_left)
    TextView mToolbarLeft;
    @BindView(R.id.tv_toolbar_center)
    TextView mToolbarCenter;
    @BindView(R.id.rl_toolbar)
    RelativeLayout mToolBar;


    private ProgressDialog mProgressDialog;
    private VideoInfo mVideoInfo;

    @Override
    public void onPause() {
        super.onPause();
        mVideoTrimmerView.onPause();
        mVideoTrimmerView.setRestoreState(true);
    }

    @Override
    public void onDestroyView() {
        mVideoTrimmerView.destroy();
        super.onDestroyView();
    }

    public static TrimmerFragment newInstance(Bundle bundle) {
        TrimmerFragment fragment = new TrimmerFragment();
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
        String path = getArguments().getString(PATH);
        mVideoInfo = getArguments().getParcelable(VIDEO);
        mVideoTrimmerView.setMaxDuration(TrimVideoUtil.VIDEO_MAX_DURATION);
        mVideoTrimmerView.setOnTrimVideoListener(this);
        mVideoTrimmerView.setVideoURI(Uri.parse(path));

        mToolbarCenter.setText(R.string.clip_speed);
        mToolbarLeft.setText(R.string.cancel);
        mToolbarRight.setText(R.string.complete);
        initListener();
    }

    private void initListener() {
        RxView.clicks(mToolbarRight)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> mVideoTrimmerView.onSaveClicked());

        RxView.clicks(mToolbarLeft)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> mActivity.finish());
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.activity_time;
    }

    @Override
    public void onStartTrim() {
        buildDialog(getResources().getString(R.string.trimming)).show();
    }

    @Override
    public void onFinishTrim(String url) {
        mProgressDialog.dismiss();
        FileUtils.updateMediaStore(mActivity, url, (path, uri) -> {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(path);
            VideoListManager.getInstance().addSubVideo(path,mVideoTrimmerView.getDuration());
            PreviewActivity.startPreviewActivity(mActivity, arrayList);
            mActivity.finish();
        });
    }

    @Override
    public void onCancel() {
        mVideoTrimmerView.destroy();
        mActivity.finish();
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(mActivity, "", msg);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }
}
