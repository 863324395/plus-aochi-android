package com.zhiyicx.thinksnsplus.modules.shortvideo.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;

/**
 * @Author Jliuer
 * @Date 2018/03/29/11:12
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class VideoDetailActivity extends TSActivity<VideoDetailPresenter, VideoDetailFragment> {

    @Override
    protected VideoDetailFragment getFragment() {
        return VideoDetailFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {
        DaggerVideoDetailComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .videoDetailPresenterModule(new VideoDetailPresenterModule(mContanierFragment))
                .build().inject(this);
    }

    public static void startVideoDetailActivity(Context context,String path ,long id) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(VideoDetailFragment.SOURCEID, id);
        bundle.putString("path",path);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
