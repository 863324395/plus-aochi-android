package com.zhiyicx.thinksnsplus.modules.home.message.messagecomment;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.BaseListPresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribe;
import com.zhiyicx.thinksnsplus.data.beans.CommentedBean;
import com.zhiyicx.thinksnsplus.data.source.local.CommentedBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.CommentRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/8
 * @Contact master.jungle68@gmail.com
 */
@FragmentScoped
public class MessageCommentPresenter extends BaseListPresenter<MessageCommentContract.Repository, MessageCommentContract.View,CommentedBean> implements MessageCommentContract.Presenter {
    @Inject
    CommentRepository mCommentRepository;
    @Inject
    CommentedBeanGreenDaoImpl mCommentedBeanGreenDao;

    @Inject
    public MessageCommentPresenter(MessageCommentContract.Repository repository, MessageCommentContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void requestNetData(Long maxId, final boolean isLoadMore) {
        Subscription commentSub = mRepository.getMyComments(maxId.intValue())
                .subscribe(new BaseSubscribe<List<CommentedBean>>() {
                    @Override
                    protected void onSuccess(List<CommentedBean> data) {
                        mRootView.onNetResponseSuccess(data, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        mRootView.showMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        mRootView.onResponseError(throwable, isLoadMore);
                    }
                });
        addSubscrebe(commentSub);
    }

    @Override
    public List<CommentedBean> requestCacheData(Long maxId, boolean isLoadMore) {
        if (isLoadMore) {
            return new ArrayList<>();
        }
        return mCommentedBeanGreenDao.getMultiDataFromCache();
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<CommentedBean> data, boolean isLoadMore) {
        if (!isLoadMore) {
            mCommentedBeanGreenDao.clearTable();
        }
        mCommentedBeanGreenDao.saveMultiData(data);
        return true;
    }

    @Override
    public void sendComment(int mCurrentPostion, long replyToUserId, String commentContent) {
        CommentedBean currentCommentBean = mRootView.getListDatas().get(mCurrentPostion);
        String path = CommentRepository.getCommentPath(currentCommentBean.getSource_id(), currentCommentBean.getComponent(), currentCommentBean.getSource_table());
        Subscription commentSub = mCommentRepository.sendComment(commentContent, replyToUserId, Long.parseLong(AppApplication.getmCurrentLoginAuth().getUser_id() + "" + System.currentTimeMillis()), path)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mRootView.showSnackLoadingMessage(mContext.getString(R.string.comment_ing));
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<Object>() {
                    @Override
                    protected void onSuccess(Object data) {

                        mRootView.showSnackSuccessMessage(mContext.getString(R.string.comment_success));
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.comment_fail));
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        mRootView.showSnackErrorMessage(mContext.getString(R.string.comment_fail));
                    }
                });
        addSubscrebe(commentSub);

    }
}
