package com.zhiyicx.thinksnsplus.modules.q_a.publish.detail;

import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribe;
import com.zhiyicx.thinksnsplus.data.source.repository.UpLoadRepository;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Author Jliuer
 * @Date 2017/07/26/16:10
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class PublishContentPresenter extends AppBasePresenter<PublishContentConstact.Repository,PublishContentConstact.View>
        implements PublishContentConstact.Presenter {

    @Inject
    UpLoadRepository mUpLoadRepository;

    @Inject
    public PublishContentPresenter(PublishContentConstact.Repository repository, PublishContentConstact.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void uploadPic(String filePath, String mimeType, boolean isPic, int photoWidth, int photoHeight) {
        mUpLoadRepository.upLoadSingleFileV2(filePath, mimeType, true, photoWidth, photoHeight)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<Integer>() {
                    @Override
                    protected void onSuccess(Integer data) {
                        mRootView.uploadPicSuccess(data);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackErrorMessage("图片上传失败");
                        mRootView.uploadPicFailed();
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mRootView.showSnackErrorMessage("图片解析错误");
                        mRootView.uploadPicFailed();
                    }
                });
    }
}
