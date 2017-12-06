package com.zhiyicx.thinksnsplus.modules.circle.create;

import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;

import javax.inject.Inject;

import rx.Subscription;

/**
 * @Author Jliuer
 * @Date 2017/11/21/16:59
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CreateCirclePresenter extends AppBasePresenter<CreateCircleContract.Repository, CreateCircleContract.View>
        implements CreateCircleContract.Presenter {

    @Inject
    public CreateCirclePresenter(CreateCircleContract.Repository repository, CreateCircleContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void createCircle(CreateCircleBean createCircleBean) {
        Subscription subscription = mRepository.createCircle(createCircleBean)
                .subscribe(new BaseSubscribeForV2<BaseJsonV2<CircleInfo>>() {
                    @Override
                    protected void onSuccess(BaseJsonV2<CircleInfo> data) {

                    }
                });
        addSubscrebe(subscription);
    }

}
