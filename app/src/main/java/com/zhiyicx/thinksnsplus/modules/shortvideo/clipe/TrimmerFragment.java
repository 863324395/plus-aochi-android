package com.zhiyicx.thinksnsplus.modules.shortvideo.clipe;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tym.shortvideo.interfaces.TrimVideoListener;
import com.tym.shortvideo.utils.TrimVideoUtil;
import com.tym.shortvideo.view.VideoTrimmerView;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.thinksnsplus.R;

import butterknife.BindView;

/**
 * @Author Jliuer
 * @Date 2018/03/28/18:49
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TrimmerFragment extends TSFragment implements TrimVideoListener {

    public static final String PATH = "path";

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
        String path = getArguments().getString(PATH);
        mVideoTrimmerView.setMaxDuration(TrimVideoUtil.VIDEO_MAX_DURATION);
        mVideoTrimmerView.setOnTrimVideoListener(this);
        mVideoTrimmerView.setVideoURI(Uri.parse(path));

        mToolbarCenter.setText(R.string.clip_speed);
        mToolbarLeft.setText(R.string.cancel);
        mToolbarRight.setText(R.string.complete);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.activity_time;
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoTrimmerView.onPause();
        mVideoTrimmerView.setRestoreState(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mVideoTrimmerView.destroy();
    }

    @Override
    public void onStartTrim() {
        buildDialog(getResources().getString(R.string.trimming)).show();
    }

    @Override
    public void onFinishTrim(String url) {
        mProgressDialog.dismiss();
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
