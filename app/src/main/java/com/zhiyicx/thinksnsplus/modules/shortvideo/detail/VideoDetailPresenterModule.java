package com.zhiyicx.thinksnsplus.modules.shortvideo.detail;

import dagger.Module;
import dagger.Provides;

/**
 * @Author Jliuer
 * @Date 2018/03/29/11:50
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class VideoDetailPresenterModule {
    VideoDetailContract.View mView;

    public VideoDetailPresenterModule(VideoDetailContract.View view) {
        mView = view;
    }

    @Provides
    VideoDetailContract.View provideVideoDetailContractView() {
        return mView;
    }
}
