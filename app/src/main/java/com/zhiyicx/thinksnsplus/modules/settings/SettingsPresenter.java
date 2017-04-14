package com.zhiyicx.thinksnsplus.modules.settings;

import android.text.TextUtils;

import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.source.repository.AuthRepository;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskManager;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/1/10
 * @Contact master.jungle68@gmail.com
 */

public class SettingsPresenter extends BasePresenter<SettingsContract.Repository, SettingsContract.View> implements SettingsContract.Presenter {

    @Inject
    AuthRepository mIAuthRepository;

    @Inject
    public SettingsPresenter(SettingsContract.Repository repository, SettingsContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void getDirCacheSize() {
        Subscription getCacheDirSizeSub = mRepository.getDirCacheSize(mContext)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String size) {
                        if (TextUtils.isEmpty(size)) {
                            mRootView.setCacheDirSize(mContext.getString(R.string.cache_zero_size));//将缓存大小改为 0b
                        } else {
                            mRootView.setCacheDirSize(size);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
        addSubscrebe(getCacheDirSizeSub);
    }

    @Override
    public void cleanCache() {
        Subscription cleanCacheSub = mRepository.cleanCache(mContext)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isDelete) {
                        if (isDelete) {
                            mRootView.showSnackSuccessMessage(mContext.getString(R.string.clean_success));// 删除成功
                            mRootView.setCacheDirSize(mContext.getString(R.string.cache_zero_size));//将缓存大小改为 0b
                        } else {
                            mRootView.showSnackErrorMessage(mContext.getString(R.string.clean_failure));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.clean_failure));
                    }
                });
        addSubscrebe(cleanCacheSub);
    }

    @Override
    public boolean loginOut() {
        mIAuthRepository.clearAuthBean();
        BackgroundTaskManager.getInstance(mContext).closeBackgroundTask();// 关闭后台任务
        return true;
    }

    @Override
    public void onStart() {

    }

}
