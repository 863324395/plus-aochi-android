package com.zhiyicx.thinksnsplus.modules.chat;

import com.hyphenate.chat.EMMessage;
import com.zhiyicx.common.mvp.i.IBasePresenter;
import com.zhiyicx.common.mvp.i.IBaseView;

import java.util.List;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/1/6
 * @Contact master.jungle68@gmail.com
 */

public interface ChatContract {

    interface View extends IBaseView<Presenter> {
        void onMessageReceivedWithUserInfo(List<EMMessage> messages);
    }

    interface Presenter extends IBasePresenter {
        void dealMessages(List<EMMessage> messages);
        String getUserName(String id);
        String getGroupName(String id);
    }
}
