package com.zhiyicx.thinksnsplus.modules.q_a.mine.question;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/28
 * @contact email:648129313@qq.com
 */
@FragmentScoped
public class MyPublishQuestionPresenter extends AppBasePresenter<MyPublishQuestionContract.Repository, MyPublishQuestionContract.View>
        implements MyPublishQuestionContract.Presenter{

    @Inject
    public MyPublishQuestionPresenter(MyPublishQuestionContract.Repository repository, MyPublishQuestionContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        mRepository.getUserQAQustion(mRootView.getMyQuestionType(),maxId).subscribe(new BaseSubscribeForV2<List<QAListInfoBean>>() {
            @Override
            protected void onSuccess(List<QAListInfoBean> data) {
                mRootView.onNetResponseSuccess(data, isLoadMore);
            }

            @Override
            protected void onFailure(String message, int code) {
                super.onFailure(message, code);
            }

            @Override
            protected void onException(Throwable throwable) {
                super.onException(throwable);
                mRootView.onResponseError(throwable, isLoadMore);
            }
        });
    }

    @Override
    public List<QAListInfoBean> requestCacheData(Long max_Id, boolean isLoadMore) {
        return null;
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<QAListInfoBean> data, boolean isLoadMore) {
        return false;
    }
}