package com.zhiyicx.thinksnsplus.modules.follow_fans;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.mvp.i.IBasePresenter;
import com.zhiyicx.common.mvp.i.IBaseView;
import com.zhiyicx.thinksnsplus.data.beans.FollowFansBean;
import com.zhiyicx.thinksnsplus.data.beans.FollowFansItemBean;

import java.util.List;

import rx.Observable;

/**
 * @author LiuChao
 * @describe
 * @date 2017/2/13
 * @contact email:450127106@qq.com
 */

public interface FollowFansListContract {
    interface View extends ITSListView<FollowFansBean, Presenter> {

    }

    interface Presenter extends ITSListPresenter<FollowFansBean> {
        /**
         * 重写获取网络数据的方法，添加方法参数
         *
         * @param maxId
         * @param isLoadMore
         * @param userId     用户id
         * @param isFollowed true ：是关注  false：还是粉丝
         */
        void requestNetData(int maxId, boolean isLoadMore, int userId, boolean isFollowed);

        List<FollowFansBean> requestCacheData(int maxId, boolean isLoadMore, int userId, boolean isFollowed);

        void followUser(long userId);

        void cancleFollowUser(long userId);
    }

    interface Repository {

        Observable<BaseJson<List<FollowFansBean>>> getFollowListFromNet(long userId, int maxId);

        Observable<BaseJson<List<FollowFansBean>>> getFansListFromNet(long userId, int maxId);

        Observable<BaseJson> followUser(long userId);

        Observable<BaseJson> cancleFollowUser(long userId);

    }

}
