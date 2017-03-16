package com.zhiyicx.thinksnsplus.modules.information.infochannel;

import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribe;
import com.zhiyicx.thinksnsplus.data.source.repository.InfoChannelRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.InfoMainRepository;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Author Jliuer
 * @Date 2017/03/15
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class InfoChannelPresenter extends BasePresenter<InfoChannelConstract.Reppsitory,
        InfoChannelConstract.View> implements InfoChannelConstract.Presenter {

    @Inject
    InfoChannelRepository mInfoChannelRepository;

    @Inject
    public InfoChannelPresenter(InfoChannelConstract.Reppsitory repository, InfoChannelConstract
            .View rootView) {
        super(repository, rootView);
    }

    @Inject
    void setupListeners() {
        mRootView.setPresenter(this);
    }

    @Override
    public void doSubscribe(String follows) {
        mInfoChannelRepository.doSubscribe(follows).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<Integer>() {
                    @Override
                    protected void onSuccess(Integer data) {

                    }

                    @Override
                    protected void onFailure(String message) {

                    }

                    @Override
                    protected void onException(Throwable throwable) {

                    }
                });
    }

}
