package com.zhiyicx.thinksnsplus.modules.shortvideo.detail;

import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2018/03/29/11:49
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class VideoDetailPresenter extends AppBasePresenter<VideoDetailContract.View> implements VideoDetailContract.Presenter{

    @Inject
    public VideoDetailPresenter(VideoDetailContract.View rootView) {
        super(rootView);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        List<DynamicCommentBean> data=new ArrayList<>();
        DynamicCommentBean video=new DynamicCommentBean();
        video.setComment_content("tym");
//        data.add(video);
        data.add(new DynamicCommentBean());
        data.add(new DynamicCommentBean());
        data.add(new DynamicCommentBean());
        data.add(new DynamicCommentBean());
        data.add(new DynamicCommentBean());
        data.add(new DynamicCommentBean());
        data.add(new DynamicCommentBean());
        data.add(new DynamicCommentBean());
        data.add(new DynamicCommentBean());
        data.add(new DynamicCommentBean());
        mRootView.onNetResponseSuccess(data,false);
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {

    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<DynamicCommentBean> data, boolean isLoadMore) {
        return false;
    }
}
