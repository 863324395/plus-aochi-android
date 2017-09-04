package com.zhiyicx.thinksnsplus.modules.q_a.mine.answer;

import com.zhiyicx.thinksnsplus.data.source.repository.MyAnswerRepository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/28
 * @contact email:648129313@qq.com
 */
@Module
public class MyAnswerPresenterModule {
    private MyAnswerContract.View mView;

    public MyAnswerPresenterModule(MyAnswerContract.View mView) {
        this.mView = mView;
    }

    @Provides
    public MyAnswerContract.View provideMyAnswerContractView() {
        return mView;
    }

    @Provides
    public MyAnswerContract.Repository provideMyAnswerContractRepository(MyAnswerRepository repository){
        return repository;
    }
}
