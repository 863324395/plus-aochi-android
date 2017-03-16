package com.zhiyicx.thinksnsplus.modules.information.infochannel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.InfoTypeBean;
import com.zhiyicx.thinksnsplus.modules.information.infosearch.SearchActivity;
import com.zhiyicx.thinksnsplus.widget.pager_recyclerview.itemtouch.DefaultItemTouchHelpCallback;
import com.zhiyicx.thinksnsplus.widget.pager_recyclerview.itemtouch.DefaultItemTouchHelper;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.zhiyicx.thinksnsplus.modules.information.infomain.list.InfoListFragment
        .BUNDLE_INFO_TYPE;

/**
 * @Author Jliuer
 * @Date 2017/03/06
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class InfoChannelFragment extends TSFragment<InfoChannelConstract.Presenter>
        implements InfoChannelConstract.View {

    @BindView(R.id.fragment_channel_editor)
    TextView mFragmentChannelEditor;
    @BindView(R.id.fragment_channel_complete)
    TextView mFragmentChannelComplete;
    @BindView(R.id.fragment_channel_content_subscribed)
    RecyclerView mFragmentChannelContentSubscribed;
    @BindView(R.id.fragment_channel_content_unsubscribe)
    RecyclerView mFragmentChannelContentUnsubscribe;
    @BindView(R.id.info_prompt)
    TextView mInfoPrompt;

    private List<InfoTypeBean.MyCatesBean> mMyCatesBeen;
    private List<InfoTypeBean.MoreCatesBean> mMoreCatesBeen;
    private CommonAdapter mSubscribeAdapter;
    private CommonAdapter mUnSubscribeAdapter;
    private boolean isEditor;
    private InfoTypeBean mInfoTypeBean;
    private DefaultItemTouchHelper mItemTouchHelper;

    public static InfoChannelFragment newInstance(Bundle params) {
        InfoChannelFragment fragment = new InfoChannelFragment();
        fragment.setArguments(params);
        return fragment;
    }

    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener =
            new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {

                @Override
                public void onSwiped(int adapterPosition) {

                }

                @Override
                public boolean onMove(int srcPosition, int targetPosition) {
                    if (mMyCatesBeen != null
                            && targetPosition != mMyCatesBeen.size() - 1
                            && !isEditor) {
                        // 更换数据源中的数据Item的位置
                        Collections.swap(mMyCatesBeen, srcPosition, targetPosition);
                        // 更新UI中的Item的位置，主要是给用户看到交互效果
                        mSubscribeAdapter.notifyItemMoved(srcPosition, targetPosition);
                        return true;
                    }
                    return false;
                }
            };

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_info_channel;
    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.information);
    }

    @Override
    protected int setRightImg() {
        return R.mipmap.ico_search;
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected void setRightClick() {
        mPresenter.doSubscribe(getFollows(mMyCatesBeen));
//        startActivity(new Intent(getActivity(), SearchActivity.class));
    }

    @Override
    protected void initView(View rootView) {

        mFragmentChannelContentSubscribed.setLayoutManager(new GridLayoutManager(getActivity
                (), 4));
        mFragmentChannelContentUnsubscribe.setLayoutManager(new GridLayoutManager(getActivity(),
                4));

        mItemTouchHelper = new DefaultItemTouchHelper(
                onItemTouchCallbackListener);
        mItemTouchHelper.attachToRecyclerView(mFragmentChannelContentSubscribed);
        mItemTouchHelper.setDragEnable(true);
        mItemTouchHelper.setSwipeEnable(true);
    }

    @Override
    protected void initData() {
        mInfoTypeBean = getArguments().getParcelable(BUNDLE_INFO_TYPE);
        mMyCatesBeen = mInfoTypeBean.getMy_cates();
        mMoreCatesBeen = mInfoTypeBean.getMore_cates();

        mSubscribeAdapter = new CommonAdapter<InfoTypeBean.MyCatesBean>(getActivity(), R.layout
                .item_info_channel, mMyCatesBeen) {
            @Override
            protected void convert(ViewHolder holder, InfoTypeBean.MyCatesBean data
                    , final int position) {
                ImageView delete = holder.getView(R.id.item_info_channel_deal);
                if (isEditor) {
                    delete.setVisibility(View.VISIBLE);
                } else {
                    delete.setVisibility(View.GONE);
                }
                holder.setText(R.id.item_info_channel, data.getName());
            }
        };
        mSubscribeAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                InfoTypeBean.MyCatesBean bean = mMyCatesBeen.get(position);
                mSubscribeAdapter.removeItem(position);
                mUnSubscribeAdapter.addItem(new InfoTypeBean.MoreCatesBean(bean.getId(),
                        bean.getName()));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int
                    position) {
                return false;
            }
        });

        mFragmentChannelContentSubscribed.setAdapter(mSubscribeAdapter);
        mUnSubscribeAdapter = new CommonAdapter<InfoTypeBean.MoreCatesBean>(getActivity(),
                R.layout.item_info_channel, mMoreCatesBeen) {
            @Override
            protected void convert(ViewHolder holder, InfoTypeBean.MoreCatesBean data,
                                   int position) {
                holder.setText(R.id.item_info_channel, data.getName());
            }
        };

        mFragmentChannelContentUnsubscribe.setAdapter(mUnSubscribeAdapter);
        mUnSubscribeAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                InfoTypeBean.MoreCatesBean bean = mMoreCatesBeen.get(position);
                mSubscribeAdapter.addItem(new InfoTypeBean.MyCatesBean(bean.getId(),
                        bean.getName()), 0);
                mUnSubscribeAdapter.removeItem(position);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int
                    position) {
                return false;
            }
        });
    }

    @OnClick({R.id.fragment_channel_editor, R.id.fragment_channel_complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_channel_editor:
                mItemTouchHelper.setDragEnable(isEditor);
                if (isEditor) {
                    mInfoPrompt.setText(R.string.info_sort);
                    mFragmentChannelEditor.setText(getText(R.string.info_editor));
                } else {
                    mInfoPrompt.setText(R.string.info_delete);
                    mFragmentChannelEditor.setText(getText(R.string.complete));
                }
                isEditor = !isEditor;
                mSubscribeAdapter.notifyDataSetChanged();
                break;
            case R.id.fragment_channel_complete:

                break;
        }
    }

    @Override
    public void setPresenter(InfoChannelConstract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(String message) {

    }

    private String getFollows(List<InfoTypeBean.MyCatesBean> bean) {
        StringBuilder ids=new StringBuilder();
        for (InfoTypeBean.MyCatesBean data:bean){
            ids.append(data.getId());
            ids.append(",");
        }
        return ids.toString();
    }
}
