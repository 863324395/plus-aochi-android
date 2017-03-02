package com.zhiyicx.thinksnsplus.modules.dynamic.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.GlideCircleTransform;
import com.zhiyicx.baseproject.widget.DynamicDetailMenuView;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBean;
import com.zhiyicx.thinksnsplus.data.beans.FollowFansBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.adapter.DynamicDetailItemForContent;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.adapter.DynamicDetailItemForDig;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhiyicx.baseproject.widget.DynamicDetailMenuView.ITEM_POSITION_0;
import static com.zhiyicx.baseproject.widget.DynamicDetailMenuView.ITEM_POSITION_3;

/**
 * @author LiuChao
 * @describe
 * @date 2017/2/27
 * @contact email:450127106@qq.com
 */

public class DynamicDetailFragment extends TSListFragment<DynamicDetailContract.Presenter, DynamicBean> implements DynamicDetailContract.View {
    public static final String DYNAMIC_DETAIL_DATA = "dynamic_detail_data";

    @BindView(R.id.dd_dynamic_tool)
    DynamicDetailMenuView mDdDynamicTool;

    private List<DynamicBean> mDatas = new ArrayList<>();

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_dynamic_detail;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        // 初始化底部工具栏数据
        mDdDynamicTool.setImageNormalResourceIds(new int[]{R.mipmap.home_ico_good_normal
                , R.mipmap.home_ico_comment_normal, R.mipmap.detail_ico_share_normal
                , R.mipmap.detail_ico_good_uncollect
        });
        mDdDynamicTool.setImageCheckedResourceIds(new int[]{R.mipmap.home_ico_good_high
                , R.mipmap.home_ico_comment_normal, R.mipmap.detail_ico_share_normal
                , R.mipmap.detail_ico_collect
        });
        mDdDynamicTool.setButtonText(new int[]{R.string.dynamic_like, R.string.comment
                , R.string.share, R.string.favorite});
    }

    @Override
    protected int setRightImg() {
        return R.mipmap.detail_ico_follow;
    }

    @Override
    protected void initData() {
        // 处理上个页面传过来的动态数据
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(DYNAMIC_DETAIL_DATA)) {
            DynamicBean dynamicBean = bundle.getParcelable(DYNAMIC_DETAIL_DATA);
            setToolBarUser(dynamicBean);// 设置标题用户

            DynamicBean dynamicContent = new DynamicBean();
            dynamicContent.setFeed(dynamicBean.getFeed());
            DynamicBean dynamicDig = new DynamicBean();
            dynamicDig.setFeed(dynamicBean.getFeed());
            dynamicDig.setTool(dynamicBean.getTool());
            mDatas.add(dynamicContent);
            mDatas.add(dynamicDig);
            refreshData();
        }
    }

    //不显示分割线
    @Override
    protected float getItemDecorationSpacing() {
        return 0;
    }

    @Override
    protected MultiItemTypeAdapter<DynamicBean> getAdapter() {
        MultiItemTypeAdapter<DynamicBean> adapter = new MultiItemTypeAdapter<>(getContext(), mDatas);
        adapter.addItemViewDelegate(new DynamicDetailItemForContent());
        adapter.addItemViewDelegate(new DynamicDetailItemForDig());
        return adapter;
    }

    @Override
    public void setPresenter(DynamicDetailContract.Presenter presenter) {
        this.mPresenter = presenter;
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

    public static DynamicDetailFragment initFragment(Bundle bundle) {
        DynamicDetailFragment dynamicDetailFragment = new DynamicDetailFragment();
        dynamicDetailFragment.setArguments(bundle);
        return dynamicDetailFragment;
    }

    /**
     * 设置toolBar上面的用户头像
     */
    private void setToolBarUser(DynamicBean dynamicBean) {
        mToolbarCenter.setVisibility(View.VISIBLE);
        UserInfoBean userInfoBean = dynamicBean.getUserInfoBean();
        mToolbarCenter.setText(userInfoBean.getName());
        int headIconWidth = getResources().getDimensionPixelSize(R.dimen.headpic_for_assist);
        Glide.with(getContext())
                .load(String.format(ApiConfig.IMAGE_PATH, 20, 10))
                .bitmapTransform(new GlideCircleTransform(getContext()))
                .override(headIconWidth, headIconWidth)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        resource.setBounds(0, 0, resource.getMinimumWidth(), resource.getMinimumHeight());
                        mToolbarCenter.setCompoundDrawables(resource, null, null, null);
                    }
                });
    }

    /**
     * 设置toolBar上面的关注状态
     */
    private void setToolBarRightFollowState(int state) {
        mToolbarRight.setVisibility(View.VISIBLE);
        switch (state) {
            case FollowFansBean.UNFOLLOWED_STATE:
                mToolbarRight.setCompoundDrawables(null, null, UIUtils.getCompoundDrawables(getContext(), R.mipmap.detail_ico_follow), null);
                break;
            case FollowFansBean.IFOLLOWED_STATE:
                mToolbarRight.setCompoundDrawables(null, null, UIUtils.getCompoundDrawables(getContext(), R.mipmap.detail_ico_followed), null);
                break;
            case FollowFansBean.FOLLOWED_EACHOTHER_STATE:
                mToolbarRight.setCompoundDrawables(null, null, UIUtils.getCompoundDrawables(getContext(), R.mipmap.detail_ico_followed_eachother), null);
                break;
            default:
        }
    }

    @Override
    public void setLike(boolean isLike) {
        mDdDynamicTool.setItemIsChecked(isLike, ITEM_POSITION_0);
    }

    @Override
    public void setCollect(boolean isCollect) {
        mDdDynamicTool.setItemIsChecked(isCollect, ITEM_POSITION_3);
    }

    @Override
    public void setDigHeadIcon(List<UserInfoBean> userInfoBeanList) {

    }
}
