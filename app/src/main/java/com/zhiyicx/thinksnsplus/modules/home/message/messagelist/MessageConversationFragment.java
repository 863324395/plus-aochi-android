package com.zhiyicx.thinksnsplus.modules.home.message.messagelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.widget.recycleview.BlankClickRecycleView;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBean;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.i.OnUserInfoClickListener;
import com.zhiyicx.thinksnsplus.modules.chat.ChatActivity;
import com.zhiyicx.thinksnsplus.modules.chat.ChatFragment;
import com.zhiyicx.thinksnsplus.modules.home.message.MessageAdapterV2;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2017/12/28
 * @contact email:648129313@qq.com
 */

public class MessageConversationFragment extends TSListFragment<MessageConversationContract.Presenter, MessageItemBean>
        implements MessageConversationContract.View ,MessageAdapterV2.OnSwipeItemClickListener,
        OnUserInfoClickListener, BlankClickRecycleView.BlankClickListener{

    @Inject
    MessageConversationPresenter mConversationPresenter;

    private List<MessageItemBeanV2> mMessageItemBeanList;

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_home_message_list;
    }

    @Override
    protected boolean isLoadingMoreEnable() {
        return false;
    }

    @Override
    protected boolean isNeedRefreshAnimation() {
        return false;
    }

    @Override
    protected void initData() {
        DaggerMessageConversationComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .messageConversationPresenterModule(new MessageConversationPresenterModule(this))
                .build()
                .inject(this);
        super.initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 刷新信息内容
        if (mPresenter != null) {
            mPresenter.refreshConversationReadMessage();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mPresenter != null && mMessageItemBeanList.isEmpty()) {
            mPresenter.requestNetData(DEFAULT_PAGE_MAX_ID, false);
        }
        if (mAdapter != null && ((MessageAdapterV2) mAdapter).hasItemOpend()) {
            ((MessageAdapterV2) mAdapter).closeAllItems();
        }
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        mMessageItemBeanList = new ArrayList<>();
        MessageAdapterV2 commonAdapter = new MessageAdapterV2(getActivity(), mMessageItemBeanList);
        commonAdapter.setOnSwipItemClickListener(this);
        commonAdapter.setOnUserInfoClickListener(this);
        return commonAdapter;
    }

    @Override
    public void getMessageListSuccess(List<MessageItemBeanV2> list) {
        mMessageItemBeanList.clear();
        mMessageItemBeanList.addAll(list);
        mAdapter.notifyDataSetChanged();
        hideLoading();
    }

    @Override
    public List<MessageItemBeanV2> getRealMessageList() {
        return mMessageItemBeanList;
    }

    @Override
    public void hideLoading() {
        mRefreshlayout.finishRefresh();
        mHeaderAndFooterWrapper.notifyDataSetChanged();
    }

    @Override
    public void showMessage(String message) {
        showMessageNotSticky(message);
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }
    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        onBlickClick();
    }

    /**
     * 进入聊天页
     *
     * @param messageItemBean 当前 item 内容
     * @param position         当前点击位置
     */
    private void toChatV2(MessageItemBeanV2 messageItemBean, int position) {
        if (messageItemBean == null || messageItemBean.getUserInfo() == null || messageItemBean.getUserInfo().getUser_id() == null) {
            return;
        }
        Intent to = new Intent(getActivity(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatFragment.BUNDLE_CHAT_USER, messageItemBean.getUserInfo());
        bundle.putString(ChatFragment.BUNDLE_CHAT_ID, messageItemBean.getEmKey());
        to.putExtras(bundle);
        startActivity(to);
    }

    @Override
    public void onUserInfoClick(UserInfoBean userInfoBean) {
        PersonalCenterFragment.startToPersonalCenter(getContext(), userInfoBean);
    }

    @Override
    public void onBlickClick() {
        if (((MessageAdapterV2) mAdapter).hasItemOpend()) {
            ((MessageAdapterV2) mAdapter).closeAllItems();
        }
    }

    @Override
    public void onLeftClick(int position) {
        toChatV2(mMessageItemBeanList.get(position), position);
    }

    @Override
    public void onRightClick(int position) {
        mPresenter.deleteConversation(position);
        refreshData();
    }
}
