package com.zhiyicx.thinksnsplus.modules.chat;

import android.text.TextUtils;
import android.util.SparseArray;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.imsdk.core.ChatType;
import com.zhiyicx.imsdk.db.dao.MessageDao;
import com.zhiyicx.imsdk.entity.AuthData;
import com.zhiyicx.imsdk.entity.Conversation;
import com.zhiyicx.imsdk.entity.Message;
import com.zhiyicx.imsdk.entity.MessageStatus;
import com.zhiyicx.imsdk.manage.ChatClient;
import com.zhiyicx.imsdk.manage.ZBIMClient;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.BaseSubscribe;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.ChatItemBean;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBean;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.UserInfoBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.SystemRepository;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.zhiyicx.thinksnsplus.config.EventBusTagConfig.EVENT_IM_ONCONVERSATIONCRATED;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/1/6
 * @Contact master.jungle68@gmail.com
 */

public class ChatPresenter extends BasePresenter<ChatContract.Repository, ChatContract.View> implements ChatContract.Presenter {

    private SparseArray<UserInfoBean> mUserInfoBeanSparseArray = new SparseArray<>();// 把用户信息存入内存，方便下次使用
    @Inject
    SystemRepository mSystemRepository;
    @Inject
    UserInfoBeanGreenDaoImpl mUserInfoBeanGreenDao;

    @Inject
    public ChatPresenter(ChatContract.Repository repository, ChatContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    public void getUserInfo(long user_id) {

    }

    @Override
    public List<ChatItemBean> getHistoryMessages(int cid, long creat_time) {
        final List<ChatItemBean> data = mRepository.getChatListData(cid, creat_time);
        Collections.reverse(data);
        Subscription subscribe = Observable.just(data)
                .observeOn(Schedulers.io())
                .subscribe(chatItemBeen -> {
                    for (ChatItemBean chatItemBean : chatItemBeen) {
                        if (!chatItemBean.getLastMessage().getIs_read()) {
                            // 把消息更新为已经读
                            MessageDao.getInstance(mContext).readMessage(chatItemBean.getLastMessage().getMid());
                        }
                    }
                });
        addSubscrebe(subscribe);
        mRootView.hideLoading();
        return data;
    }

    @Override
    public List<ChatItemBean> getHistoryMessagesV2(String id, int pageSize) {
        List<ChatItemBean> data = mRepository.getChatListDataV2(mRootView.getMessItemBean(), pageSize);
        Subscription subscribe = Observable.just(data)
                .observeOn(Schedulers.io())
                .subscribe(chatItemBeen -> {
                    for (ChatItemBean chatItemBean : chatItemBeen) {
                        if (!chatItemBean.getLastMessage().getIs_read()) {
                            // 把消息更新为已经读
                            MessageDao.getInstance(mContext).readMessage(chatItemBean.getLastMessage().getMid());
                        }
                    }
                });
        addSubscrebe(subscribe);
        mRootView.hideLoading();
        return data;
    }

    /*******************************************
     * IM 相关
     *********************************************/
    /**
     * 发送文本消息
     *
     * @param text 文本内容
     * @param cid  对话 id
     */
    @Override
    public void sendTextMessage(String text, int cid) {
        // usid 暂不使用
        Message message = ChatClient.getInstance(mContext).sendTextMsg(text, cid, "");
        // 更新
        message.setUid(AppApplication.getmCurrentLoginAuth() != null ? (int) AppApplication.getMyUserIdWithdefault() : 0);
        // IM 没有连接成功
        if (!ZBIMClient.getInstance().isLogin()) {
            message.setSend_status(MessageStatus.SEND_FAIL);
        }
        // 更新
        message.setIs_read(true);
        MessageDao.getInstance(mContext).insertOrUpdateMessage(message);
        updateMessage(message);
    }

    @Override
    public void sendTextMessageV2(String content, String userId) {
        // 环信的发送消息
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, userId);
        //如果是群聊，设置chattype，默认是单聊
//        if (chatType == CHATTYPE_GROUP){
//            message.setChatType(ChatType.GroupChat);
//        }
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                // 发送成功 需要刷新页面
                updateMessageV2(message);
            }

            @Override
            public void onError(int code, String error) {
                // 这个错误也太多了 先随便写点吧_(:з」∠)_  具体的查看官方API EMError
                switch (code) {
                    case EMError.SERVER_BUSY:
                        // 服务器繁忙
                        break;
                    case EMError.NETWORK_ERROR:
                        // 网络异常
                        break;
                    case EMError.SERVER_NOT_REACHABLE:
                        // 无法访问到服务器
                        break;
                    default:
                }

            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * 消息重发
     */
    @Override
    public void reSendText(ChatItemBean chatItemBean) {
        if (ZBIMClient.getInstance().isLogin()) {
            ChatClient.getInstance(mContext).sendMessage(chatItemBean.getLastMessage());
            mRootView.refreshData();
        } else {
            ZBIMClient.getInstance().reConnect();
        }
    }

    @Override
    public void createChat(final UserInfoBean userInfoBean, final String text) {
        if (AppApplication.getmCurrentLoginAuth() == null) {
            return;
        }
        // 替换为环信的创建会话
        MessageItemBeanV2 itemBeanV2 = mRootView.getMessItemBean();
        Subscription subscription = Observable.just(itemBeanV2)
                .map(messageItemBeanV2 -> {
                    // 创建会话的 conversation 要传入用户名 ts+采用用户Id作为用户名，聊天类型 单聊
                    EMConversation conversation =
                            EMClient.getInstance().chatManager().getConversation(itemBeanV2.getEmKey(), EMConversation.EMConversationType.Chat, true);
                    if (!TextUtils.isEmpty(text)) {
                        // 发送信息的时候 如果没有会话信息，则创建一个
                    }
                    messageItemBeanV2.setConversation(conversation);
                    return messageItemBeanV2;
                })
                .subscribe(messageItemBeanV2 -> {
                    if (messageItemBeanV2.getConversation() != null) {
                        // 通知会话列表
                        EventBus.getDefault().post(messageItemBeanV2, EVENT_IM_ONCONVERSATIONCRATED);
                    } else {
                        mRootView.showSnackWarningMessage(mContext.getString(R.string.im_not_work));
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 检测 ts helper 是否是当前用户
     */
    @Override
    public String checkTShelper(long user_id) {
        return mSystemRepository.checkTShelper(user_id);
    }

    /**
     * 收到消息
     */
    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONMESSAGERECEIVED)
    private void onMessageReceived(Message message) {
        LogUtils.d(TAG, "------onMessageReceived------->" + message);
        // 丢弃非当前房间的消息
        if (message.cid != mRootView.getCurrentChatCid()) {
            return;
        }
        updateMessage(message);
        // 把消息更新为已经读
        Subscription subscribe = Observable.just(message)
                .observeOn(Schedulers.io())
                .subscribe(message1 -> MessageDao.getInstance(mContext).readMessage(message1.getMid()));
        addSubscrebe(subscribe);

    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONMESSAGEACKRECEIVED)
    private void onMessageACKReceived(Message message) {
        LogUtils.d(TAG, "------onMessageACKReceived------->" + message);
        mRootView.updateMessageStatus(message);
    }

    private void updateMessage(Message message) {
        ChatItemBean chatItemBean = new ChatItemBean();
        chatItemBean.setLastMessage(message);
        if (message.getUid() == 0) {// 如果没有 uid, 则表明是当前用户发的消息
            message.setUid(AppApplication.getmCurrentLoginAuth() != null ? (int) AppApplication.getMyUserIdWithdefault() : 0);
        }
        UserInfoBean userInfoBean = mUserInfoBeanSparseArray.get(message.getUid());
        if (userInfoBean == null) {
            userInfoBean = mUserInfoBeanGreenDao.getSingleDataFromCache((long) message.getUid());
            mUserInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(), userInfoBean);
        }
        chatItemBean.setUserInfo(userInfoBean);
        mRootView.reFreshMessage(chatItemBean);
    }

    /**
     * 更新消息 基于环信
     *
     * @param message 消息体
     */
    private void updateMessageV2(EMMessage message) {
        ChatItemBean chatItemBean = new ChatItemBean();
        chatItemBean.setMessage(message);
        // 消息的来源与当前用户不一致，则证明非当前用户
        String currentUser = String.valueOf(AppApplication.getmCurrentLoginAuth() != null ? (int) AppApplication.getMyUserIdWithdefault() : 0);
        if (!message.getFrom().equals(currentUser)){
            // 当前这个版本还没有群聊呢，要快速出版本，暂时不考虑群聊的情况，后面需要根据来源查找用户信息
            chatItemBean.setUserInfo(mRootView.getMessItemBean().getUserInfo());
        } else {
            chatItemBean.setUserInfo(mUserInfoBeanGreenDao.getSingleDataFromCache(Long.parseLong(currentUser)));
        }
        mRootView.reFreshMessage(chatItemBean);
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_AUTHSUCESSED)
    private void onAuthSuccessed(AuthData authData) {
//        mRootView.showSnackSuccessMessage("IM 聊天加载成功");
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONCONNECTED)
    private void onConnected() {
//        mRootView.showSnackSuccessMessage("IM 聊天加载成功");
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONDISCONNECT)
    private void onDisconnect(int code, String reason) {
//        mRootView.showSnackSuccessMessage("IM 聊天断开" + reason);
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONERROR)
    private void onError(Exception error) {
        LogUtils.d(" 超时   message = " + error);
//        mRootView.showSnackSuccessMessage("IM 聊天错误" + error.toString());
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONMESSAGETIMEOUT)
    private void onMessageTimeout(Message message) {
        LogUtils.d(" 超时   message = " + message);
        mRootView.updateMessageStatus(message);
    }
}
