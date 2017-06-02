package com.zhiyicx.thinksnsplus.modules.feedback;

import com.zhiyicx.thinksnsplus.data.source.repository.FeedBackRepository;

import dagger.Module;
import dagger.Provides;

/**
 * @Author Jliuer
 * @Date 2017/06/02/17:24
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class FeedBackPresenterModule {

    FeedBackContract.View mView;

    public FeedBackPresenterModule(FeedBackContract.View view) {
        mView = view;
    }

    @Provides
    FeedBackContract.View provideFeedBackContractView() {
        return mView;
    }

    @Provides
    FeedBackContract.Repository provideFeedBackContractRepository(FeedBackRepository feedBackRepository) {
        return feedBackRepository;
    }
}
