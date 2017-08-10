package com.zhiyicx.thinksnsplus.modules.wallet.sticktop;

import com.zhiyicx.thinksnsplus.data.source.repository.StickTopRepsotory;

import dagger.Module;
import dagger.Provides;

/**
 * @Author Jliuer
 * @Date 2017/05/23/13:44
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Module
public class StickTopPresenterModule {

    StickTopContract.View mView;

    public StickTopPresenterModule(StickTopContract.View view) {
        this.mView = view;
    }

    @Provides
    StickTopContract.View provideStickTopContractView() {
        return mView;
    }

    @Provides
    StickTopContract.Repository provideStickTopContractRepository(StickTopRepsotory stickTopRepsotory) {
        return stickTopRepsotory;
    }
}
