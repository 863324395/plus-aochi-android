package com.zhiyicx.thinksnsplus.modules.home;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.imsdk.entity.ChatRoomContainer;
import com.zhiyicx.imsdk.entity.Conversation;
import com.zhiyicx.imsdk.entity.Message;
import com.zhiyicx.imsdk.manage.ChatClient;
import com.zhiyicx.imsdk.manage.listener.ImMsgReceveListener;
import com.zhiyicx.imsdk.manage.listener.ImStatusListener;
import com.zhiyicx.imsdk.manage.listener.ImTimeoutListener;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;

import org.simple.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;


/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/13
 * @Contact master.jungle68@gmail.com
 */
@FragmentScoped
class HomePresenter extends BasePresenter<HomeContract.Repository, HomeContract.View> implements HomeContract.Presenter, ImMsgReceveListener, ImStatusListener, ImTimeoutListener {


    @Inject
    public HomePresenter(HomeContract.Repository repository, HomeContract.View rootView) {
        super(repository, rootView);
        initIM();
    }

    private void initIM() {
        ChatClient.getInstance(mContext).setImMsgReceveListener(this);
        ChatClient.getInstance(mContext).setImStatusListener(this);
        ChatClient.getInstance(mContext).setImTimeoutListener(this);
    }

    /*******************************************
     * 聊天相关回调
     *********************************************/

    @Override
    public void onMessageReceived(Message message) {
        EventBus.getDefault().post(message, EventBusTagConfig.EVENT_IM_ONMESSAGERECEIVED);
    }

    @Override
    public void onMessageACKReceived(Message message) {
        EventBus.getDefault().post(message, EventBusTagConfig.EVENT_IM_ONMESSAGEACKRECEIVED);
    }

    @Override
    public void onConversationJoinACKReceived(ChatRoomContainer chatRoomContainer) {

    }

    @Override
    public void onConversationLeaveACKReceived(ChatRoomContainer chatRoomContainer) {

    }

    @Override
    public void onConversationMCACKReceived(List<Conversation> conversations) {

    }

    @Override
    public void synchronousInitiaMessage(int limit) {

    }

    @Override
    public void onConnected() {
        EventBus.getDefault().post(null,EventBusTagConfig.EVENT_IM_ONCONNECTED);
    }

    @Override
    public void onDisconnect(int code, String reason) {
        EventBus.getDefault().post(reason,EventBusTagConfig.EVENT_IM_ONDISCONNECT);
    }

    @Override
    public void onError(Exception error) {
        EventBus.getDefault().post(error,EventBusTagConfig.EVENT_IM_ONERROR);
    }

    @Override
    public void onMessageTimeout(Message message) {
        EventBus.getDefault().post(message,EventBusTagConfig.EVENT_IM_ONMESSAGETIMEOUT);
    }

    @Override
    public void onConversationJoinTimeout(int roomId) {

    }

    @Override
    public void onConversationLeaveTimeout(int roomId) {

    }

    @Override
    public void onConversationMcTimeout(List<Integer> roomIds) {

    }
}