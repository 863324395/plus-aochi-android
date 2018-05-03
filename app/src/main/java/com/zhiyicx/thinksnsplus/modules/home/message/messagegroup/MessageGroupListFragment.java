package com.zhiyicx.thinksnsplus.modules.home.message.messagegroup;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.em.manager.eventbus.TSEMConnectionEvent;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.em.manager.util.TSEMessageUtils;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.GlideCircleTransform;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.HanziToPinyin;
import com.zhiyicx.common.utils.ToastUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.common.utils.recycleviewdecoration.CustomLinearDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.CircleMembers;
import com.zhiyicx.thinksnsplus.modules.chat.ChatActivity;
import com.zhiyicx.thinksnsplus.widget.TSSearchView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @Author Jliuer
 * @Date 2018/05/03/15:37
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MessageGroupListFragment extends TSListFragment<MessageGroupContract.Presenter, ChatGroupBean>
        implements MessageGroupContract.View {

    @BindView(R.id.searchView)
    TSSearchView mSearchView;

    private String mSearchContent;
    private boolean isSearch;
    private List<ChatGroupBean> cache;

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mSearchView.setVisibility(View.VISIBLE);
    }

    @Override
    protected float getItemDecorationSpacing() {
        return 0;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.chat_group);
    }

    @Override
    protected boolean isLoadingMoreEnable() {
        return false;
    }

    @Override
    protected boolean isNeedRefreshDataWhenComeIn() {
        return true;
    }

    @Override
    protected void initData() {
        super.initData();
        initListener();

        // 刷新信息内容
        getGroupListData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mSearchView.getText())) {
            mSearchView.setText("");
        }
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<ChatGroupBean> data, boolean isLoadMore) {
        super.onNetResponseSuccess(data, isLoadMore);
        cache = new ArrayList<>(data);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        CommonAdapter adapter = new CommonAdapter<ChatGroupBean>(getActivity(), R.layout.item_group_list, mListDatas) {
            @Override
            protected void convert(ViewHolder holder, ChatGroupBean chatGroupBean, int position) {
                holder.setText(R.id.tv_group_name, chatGroupBean.getName());
                Glide.with(mContext)
                        .load(TextUtils.isEmpty(chatGroupBean.getGroup_face()) ? R.mipmap.ico_ts_assistant : chatGroupBean
                                .getGroup_face())
                        .error(R.mipmap.ico_ts_assistant)
                        .placeholder(R.mipmap.ico_ts_assistant)
                        .transform(new GlideCircleTransform(mContext))
                        .into(holder.getImageViwe(R.id.uv_group_head));
            }
        };
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                ChatGroupBean groupBean = mListDatas.get(position);
                mPresenter.checkGroupExist(groupBean.getId());
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        return adapter;
    }

    @Override
    public void checkGroupExist(String id, EMGroup data) {
        if (data != null) {
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(id, EMConversation.EMConversationType.GroupChat, true);
            ChatActivity.startChatActivity(mActivity, conversation.conversationId(), EaseConstant.CHATTYPE_GROUP);
            mActivity.finish();
        } else {
            ToastUtils.showLongToast("error");
        }

    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_message_grouplist;
    }

    @Override
    public String getsearchKeyWord() {
        return mSearchContent;
    }

    @Subscriber(mode = ThreadMode.MAIN)
    public void onTSEMConnectionEventBus(TSEMConnectionEvent event) {
        LogUtils.d("onTSEMConnectionEventBus");
        switch (event.getType()) {
            case TSEMConstants.TS_CONNECTION_USER_LOGIN_OTHER_DIVERS:
                break;
            case TSEMConstants.TS_CONNECTION_USER_REMOVED:
                break;
            case TSEMConstants.TS_CONNECTION_CONNECTED:
                hideStickyMessage();
                getGroupListData();
                break;
            case TSEMConstants.TS_CONNECTION_DISCONNECTED:
                showStickyMessage(getString(R.string.chat_unconnected));
                break;
            default:
        }
    }

    private void initListener() {

        mSearchView.setOnSearchClickListener(new TSSearchView.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mPresenter == null) {
                    return;
                }
                filterData(s.toString().trim());
            }
        });
    }

    private void filterData(CharSequence filterStr) {
        if (mListDatas.isEmpty()) {
            return;
        }
        if (TextUtils.isEmpty(filterStr)) {
            isSearch = false;
            mListDatas.clear();
            mListDatas.addAll(cache);
        } else {
            isSearch = true;
            List<ChatGroupBean> result = new ArrayList<>();
            for (ChatGroupBean sortModel : mListDatas) {
                String name = sortModel.getName();
                boolean isContent = !TextUtils.isEmpty(name) && name.toLowerCase().contains(filterStr.toString().toLowerCase());
                boolean isPinyinContent = HanziToPinyin.getInstance().getSpellStr(name).startsWith(HanziToPinyin.getInstance().getSpellStr(filterStr
                        .toString()));
                if (isContent || isPinyinContent) {
                    result.add(sortModel);
                }
            }
            mListDatas.clear();
            mListDatas.addAll(result);
        }
        refreshData();
    }

    private void getGroupListData() {
        if (mPresenter != null) {
            mRefreshlayout.autoRefresh(0);
            mPresenter.requestNetData(DEFAULT_PAGE_MAX_ID, false);
        }
    }
}
