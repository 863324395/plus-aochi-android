package com.zhiyicx.thinksnsplus.modules.home.message.messagereview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.baseproject.widget.popwindow.TipActionPopupWindow;
import com.zhiyicx.common.utils.recycleviewdecoration.CustomLinearDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.UnhandlePinnedBean;
import com.zhiyicx.thinksnsplus.modules.home.message.messagereview.adapter.TopCircleJoinRequestItem;
import com.zhiyicx.thinksnsplus.modules.home.message.messagereview.adapter.TopDyanmicCommentItem;
import com.zhiyicx.thinksnsplus.modules.home.message.messagereview.adapter.TopNewsCommentItem;
import com.zhiyicx.thinksnsplus.modules.home.message.messagereview.adapter.TopPostCommentItem;
import com.zhiyicx.thinksnsplus.modules.home.message.messagereview.adapter.TopPostItem;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;

import static com.zhiyicx.thinksnsplus.config.NotificationConfig.TOP_CIRCLE_MEMBER;
import static com.zhiyicx.thinksnsplus.config.NotificationConfig.TOP_DYNAMIC_COMMENT;
import static com.zhiyicx.thinksnsplus.config.NotificationConfig.TOP_NEWS_COMMENT;
import static com.zhiyicx.thinksnsplus.config.NotificationConfig.TOP_POST;
import static com.zhiyicx.thinksnsplus.config.NotificationConfig.TOP_POST_COMMENT;

/**
 * @Author Jliuer
 * @Date 2017/7/5/20:55
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MessageReviewFragment extends TSListFragment<MessageReviewContract.Presenter,
        BaseListBean> implements MessageReviewContract.View {

    public static final String BUNDLE_PINNED_DATA = "bundle_pinned_data";
    private String[] mTopTypes;
    private String mTopType;

    private TipActionPopupWindow mActionPopupWindow;

    @BindView(R.id.v_shadow)
    View mVshadow;
    private UnhandlePinnedBean mUnhandlePinnedBean;

    public static MessageReviewFragment newInstance(Bundle args) {
        MessageReviewFragment fragment = new MessageReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getType() {
        return mTopType;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTopTypes = getResources().getStringArray(R.array.top_type);
        if (getArguments() != null) {
            mUnhandlePinnedBean = getArguments().getParcelable(BUNDLE_PINNED_DATA);
        }
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new CustomLinearDecoration(0, getResources().getDimensionPixelSize(R.dimen
                .divider_line), 0, 0, ContextCompat.getDrawable(getContext(), R.drawable
                .shape_recyclerview_grey_divider));
    }

    @Override
    protected boolean isNeedRefreshDataWhenComeIn() {
        return true;
    }

    @Override
    protected boolean isNeedRefreshAnimation() {
        return true;
    }

    @Override
    protected MultiItemTypeAdapter<BaseListBean> getAdapter() {
        MultiItemTypeAdapter multiItemTypeAdapter = new MultiItemTypeAdapter(getContext(), mListDatas);
        TopDyanmicCommentItem dyanmicCommentItem = new TopDyanmicCommentItem(getActivity(), mPresenter);
        TopNewsCommentItem newsCommentItem = new TopNewsCommentItem(getActivity(), mPresenter);
        TopPostCommentItem postCommentItem = new TopPostCommentItem(getActivity(), mPresenter);
        TopCircleJoinRequestItem topCircleJoinRequestItem = new TopCircleJoinRequestItem(getActivity(), mPresenter);
        TopPostItem topPostItem = new TopPostItem(getActivity(), mPresenter);
        multiItemTypeAdapter.addItemViewDelegate(dyanmicCommentItem);
        multiItemTypeAdapter.addItemViewDelegate(newsCommentItem);
        multiItemTypeAdapter.addItemViewDelegate(postCommentItem);
        multiItemTypeAdapter.addItemViewDelegate(topPostItem);
        multiItemTypeAdapter.addItemViewDelegate(topCircleJoinRequestItem);
        return multiItemTypeAdapter;
    }

    @Override
    protected String setCenterTitle() {
        mToolbarCenter.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mToolbarCenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ico_detail_arrowdown, 0);
        return getString(R.string.review);
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        if (mUnhandlePinnedBean != null) {
            if (mUnhandlePinnedBean.getFeeds() != null && mUnhandlePinnedBean.getFeeds().getCount() > 0) {
                chooseType(getString(R.string.stick_type_dynamic_commnet), 0);
            } else if (mUnhandlePinnedBean.getNewsComments() != null && mUnhandlePinnedBean.getNewsComments().getCount() > 0) {
                chooseType(getString(R.string.stick_type_news_commnet), 1);
            } else if (mUnhandlePinnedBean.getGroupComments() != null && mUnhandlePinnedBean.getGroupComments().getCount() > 0) {
                chooseType(getString(R.string.stick_type_group_commnet), 0);
            } else if (mUnhandlePinnedBean.getGroupPosts() != null && mUnhandlePinnedBean.getGroupPosts().getCount() > 0) {
                chooseType(getString(R.string.stick_type_group), 0);
            } else {
                chooseType(getString(R.string.stick_type_dynamic_commnet), 0);
            }

        } else {
            chooseType(getString(R.string.stick_type_dynamic_commnet), 0);
        }
    }

    @Override
    protected Long getMaxId(@NotNull List<BaseListBean> data) {
        return (long) mListDatas.size();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    /**
     * 选择置顶类型
     *
     * @param title    类型标题
     * @param position 类型位置
     */
    private void chooseType(String title, int position) {
        mToolbarCenter.setText(title);
        mTopType = mTopTypes[position];
        if (mPresenter != null) {
            mRefreshlayout.autoRefresh();
        }
        if (mActionPopupWindow != null) {
            mActionPopupWindow.hide();
        }
    }

    @Override
    public Long getSourceId() {
        return null;
    }

    @Override
    protected void setCenterClick() {
        initTopPopWindow();
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_review_list;
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_nothing;
    }

    @Override
    public void refuseTip() {
        switch (getType()) {
            case TOP_DYNAMIC_COMMENT:
                if (mUnhandlePinnedBean.getFeeds() != null && mUnhandlePinnedBean.getFeeds().getCount() > 0) {
                    mUnhandlePinnedBean.getFeeds().setCount(mUnhandlePinnedBean.getFeeds().getCount() - 1);
                }
                break;
            case TOP_NEWS_COMMENT:
                if (mUnhandlePinnedBean.getNewsComments() != null && mUnhandlePinnedBean.getNewsComments().getCount() > 0) {
                    mUnhandlePinnedBean.getNewsComments().setCount(mUnhandlePinnedBean.getNewsComments().getCount() - 1);
                }
                break;
            case TOP_POST_COMMENT:
                if (mUnhandlePinnedBean.getGroupComments() != null && mUnhandlePinnedBean.getGroupComments().getCount() > 0) {
                    mUnhandlePinnedBean.getGroupComments().setCount(mUnhandlePinnedBean.getGroupComments().getCount() - 1);
                }
                break;
            case TOP_POST:
                if (mUnhandlePinnedBean.getGroupPosts() != null && mUnhandlePinnedBean.getGroupPosts().getCount() > 0) {
                    mUnhandlePinnedBean.getGroupPosts().setCount(mUnhandlePinnedBean.getGroupPosts().getCount() - 1);
                }
                break;
            default:
        }
    }

    private void initTopPopWindow() {
        mActionPopupWindow = TipActionPopupWindow.builder()
                .with(getActivity())
                .isFocus(true)
                .isOutsideTouch(true)
                .parentView(mDriver)
                .animationStyle(ActionPopupWindow.NO_ANIMATION)
                .item1Str(getString(R.string.stick_type_dynamic_commnet))
                .showTip1(mUnhandlePinnedBean.getFeeds() != null && mUnhandlePinnedBean.getFeeds().getCount() > 0)
                .item1Color(mTopType.equals(mTopTypes[0]) ? getColor(R.color.themeColor) : 0)
                .item2Str(getString(R.string.stick_type_news_commnet))
                .showTip2(mUnhandlePinnedBean.getNewsComments() != null && mUnhandlePinnedBean.getNewsComments().getCount() > 0)
                .item2Color(mTopType.equals(mTopTypes[1]) ? getColor(R.color.themeColor) : 0)
                .item3Str(getString(R.string.stick_type_group_commnet))
                .showTip3(mUnhandlePinnedBean.getGroupComments() != null && mUnhandlePinnedBean.getGroupComments().getCount() > 0)
                .item3Color(mTopType.equals(mTopTypes[2]) ? getColor(R.color.themeColor) : 0)
                .item4Str(getString(R.string.stick_type_group))
                .showTip4(mUnhandlePinnedBean.getGroupPosts() != null && mUnhandlePinnedBean.getGroupPosts().getCount() > 0)
                .item4Color(mTopType.equals(mTopTypes[3]) ? getColor(R.color.themeColor) : 0)
                .item5Str(getString(R.string.stick_type_group_join))
                .item5Color(mTopType.equals(mTopTypes[4]) ? getColor(R.color.themeColor) : 0)
                .item1ClickListener(() -> {
                    chooseType(getString(R.string.stick_type_dynamic_commnet), 0);
                })
                .item2ClickListener(() -> {
                    chooseType(getString(R.string.stick_type_news_commnet), 1);
                })
                .item3ClickListener(() -> {
                    chooseType(getString(R.string.stick_type_group_commnet), 2);
                })
                .item4ClickListener(() -> {
                    chooseType(getString(R.string.stick_type_group), 3);
                })
                .item5ClickListener(() -> {
                    chooseType(getString(R.string.stick_type_group_join), 4);
                })
                .dismissListener(new ActionPopupWindow.ActionPopupWindowShowOrDismissListener() {
                    @Override
                    public void onShow() {
                        mToolbarCenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ico_detail_arrowup, 0);
                        mVshadow.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onDismiss() {
                        mToolbarCenter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ico_detail_arrowdown, 0);
                        mVshadow.setVisibility(View.GONE);
                    }
                })
                .build();
        mActionPopupWindow.showTop();

    }
}
