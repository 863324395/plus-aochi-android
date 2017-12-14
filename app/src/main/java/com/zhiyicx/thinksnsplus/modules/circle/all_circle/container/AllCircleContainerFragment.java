package com.zhiyicx.thinksnsplus.modules.circle.all_circle.container;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zhiyicx.baseproject.base.TSViewPagerAdapter;
import com.zhiyicx.baseproject.base.TSViewPagerFragment;
import com.zhiyicx.baseproject.widget.TabSelectView;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.CircleTypeBean;
import com.zhiyicx.thinksnsplus.modules.circle.all_circle.CircleListFragment;
import com.zhiyicx.thinksnsplus.modules.circle.create.CreateCircleActivity;
import com.zhiyicx.thinksnsplus.modules.circle.create.types.CircleTyepsActivity;
import com.zhiyicx.thinksnsplus.modules.circle.search.container.CircleSearchContainerActivity;
import com.zhiyicx.thinksnsplus.modules.circle.search.container.CircleSearchContainerViewPagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Jliuer
 * @Date 2017/11/21/14:49
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class AllCircleContainerFragment extends TSViewPagerFragment<AllCircleContainerContract.Presenter>
        implements AllCircleContainerContract.View {

    private List<String> mTitle;
    public static final String RECOMMEND_INFO = "1";

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    protected int setRightImg() {
        return R.mipmap.ico_createcircle;
    }

    @Override
    protected int setRightLeftImg() {
        return R.mipmap.ico_search;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.all_group);
    }

    @Override
    protected List<String> initTitles() {
        if (mTitle == null) {
            mTitle = new ArrayList<>();
            mTitle.add(getString(R.string.info_recommend));
        }
        return mTitle;
    }

    @Override
    public void setCategroiesList(List<CircleTypeBean> circleTypeList) {
        for (CircleTypeBean circleTypeBean : circleTypeList) {
            if (RECOMMEND_INFO.equals(circleTypeBean.getId().intValue() + "")) {
                continue;
            }
            mTitle.add(circleTypeBean.getName());
            mFragmentList.add(CircleListFragment.newInstance(circleTypeBean.getId() + ""));
        }
        mTsvToolbar.notifyDataSetChanged(mTitle);
        tsViewPagerAdapter.bindData(mFragmentList, mTitle.toArray(new String[]{}));
    }

    @Override
    protected void setRightClick() {
        super.setRightClick();
        CreateCircleActivity.startCreateActivity(mActivity);
    }

    @Override
    protected void setRightLeftClick() {
        super.setRightLeftClick();
        CircleSearchContainerActivity.startCircelSearchActivity(mActivity, CircleSearchContainerViewPagerFragment.PAGE_CIRCLE);
    }

    @Override
    protected List<Fragment> initFragments() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<>();
            mFragmentList.add(CircleListFragment.newInstance(RECOMMEND_INFO));
        }
        return mFragmentList;
    }

    @Override
    protected void initViewPager(View rootView) {
        mTsvToolbar = (TabSelectView) rootView.findViewById(com.zhiyicx.baseproject.R.id.tsv_toolbar);
        mTsvToolbar.setRightImg(R.mipmap.sec_nav_arrow, R.color.white);
        mTsvToolbar.setLeftImg(0);
        mTsvToolbar.setDefaultTabLinehegiht(R.integer.no_line_height);
        mTsvToolbar.showDivider(false);
        mTsvToolbar.setIndicatorMatchWidth(true);
        mVpFragment = (ViewPager) rootView.findViewById(com.zhiyicx.baseproject.R.id.vp_fragment);
        mVpFragment.setOffscreenPageLimit(getOffsetPage());
        tsViewPagerAdapter = new TSViewPagerAdapter(getChildFragmentManager());
        tsViewPagerAdapter.bindData(initFragments());
        mVpFragment.setAdapter(tsViewPagerAdapter);
        mTsvToolbar.setAdjustMode(isAdjustMode());
        mTsvToolbar.initTabView(mVpFragment, initTitles());
        mTsvToolbar.setLeftClickListener(this, () -> setLeftClick());
        mTsvToolbar.setRightClickListener(this, () -> {
            Intent typeIntent = new Intent(getActivity(), CircleTyepsActivity.class);
            startActivity(typeIntent);
        });
    }

    @Override
    protected void initData() {
        mPresenter.getCategroiesList(0, 0);
    }
}
