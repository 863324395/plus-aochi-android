package com.zhiyicx.thinksnsplus.modules.q_a.publish.add_topic;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.data.beans.qa.QATopicBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;


/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/7/25
 * @Contact master.jungle68@gmail.com
 */

@FragmentScoped
public class AddTopicPresenter extends AppBasePresenter<AddTopicContract.Repository, AddTopicContract.View>
        implements AddTopicContract.Presenter{

    @Inject
    public AddTopicPresenter(AddTopicContract.Repository repository, AddTopicContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {

    }

    @Override
    public List<QATopicBean> requestCacheData(Long max_Id, boolean isLoadMore) {
        return null;
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<QATopicBean> data, boolean isLoadMore) {
        return false;
    }
}