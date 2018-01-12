package com.zhiyicx.thinksnsplus.modules.chat.select;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.common.mvp.i.IBasePresenter;
import com.zhiyicx.common.mvp.i.IBaseView;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.home.mine.friends.IBaseFriendsRepository;

import java.util.List;

/**
 * @author Catherine
 * @describe
 * @date 2018/1/11
 * @contact email:648129313@qq.com
 */

public interface SelectFriendsContract {

    interface View extends ITSListView<UserInfoBean, Presenter> {
        /**
         * 返回搜索结果
         *
         * @param userInfoBeans 用户列表
         */
        void getFriendsListByKeyResult(List<UserInfoBean> userInfoBeans);
    }

    interface Presenter extends ITSListPresenter<UserInfoBean> {
        /**
         * 根据关键字获取用户列表
         *
         * @param maxId 0
         * @param key   关键字
         */
        void getFriendsListByKey(Long maxId, String key);

        /**
         * 创建会话
         *
         * @param list 用户列表
         */
        void createConversation(List<UserInfoBean> list);
    }

    interface Repository extends IBaseFriendsRepository {

    }
}
