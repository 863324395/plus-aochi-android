package com.zhiyicx.thinksnsplus.modules.information.publish;

import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribe;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.InfoPublishBean;
import com.zhiyicx.thinksnsplus.data.source.repository.UpLoadRepository;

import javax.inject.Inject;
/**
 * @Author Jliuer
 * @Date 2017/08/07/9:59
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class PublishInfoPresenter extends AppBasePresenter<PublishInfoContract.Repository, PublishInfoContract.View>
        implements PublishInfoContract.Presenter {

    @Inject
    UpLoadRepository mUpLoadRepository;

    @Inject
    public PublishInfoPresenter(PublishInfoContract.Repository repository, PublishInfoContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void uploadPic(String filePath, String mimeType, boolean isPic, int photoWidth, int photoHeight) {
        mUpLoadRepository.upLoadSingleFileV2(filePath, mimeType, true, photoWidth, photoHeight)
                .subscribe(new BaseSubscribe<Integer>() {
                    @Override
                    protected void onSuccess(Integer data) {
                        mRootView.uploadPicSuccess(data);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackErrorMessage("图片错误");
                        mRootView.uploadPicFailed();
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mRootView.showSnackErrorMessage("图片错误");
                        mRootView.uploadPicFailed();
                    }
                });

    }

    @Override
    public void publishInfo(InfoPublishBean infoPublishBean) {
        mRepository.publishInfo(infoPublishBean).subscribe(new BaseSubscribeForV2<BaseJsonV2<Object>>() {
            @Override
            protected void onSuccess(BaseJsonV2<Object> data) {

            }
        });
    }
}