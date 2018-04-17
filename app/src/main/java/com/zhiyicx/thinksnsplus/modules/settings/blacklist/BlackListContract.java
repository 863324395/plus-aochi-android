package com.zhiyicx.thinksnsplus.modules.settings.blacklist;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;

import java.util.List;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2018/4/17
 * @Contact master.jungle68@gmail.com
 */

public interface BlackListContract {
    interface View extends ITSListView<UserInfoBean, Presenter> {

    }

    interface Presenter extends ITSListPresenter<UserInfoBean> {

    }

}
