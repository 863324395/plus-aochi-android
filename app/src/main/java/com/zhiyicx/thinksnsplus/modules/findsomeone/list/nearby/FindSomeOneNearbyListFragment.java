package com.zhiyicx.thinksnsplus.modules.findsomeone.list.nearby;

import android.view.View;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.NearbyBean;
import com.zhy.adapter.recyclerview.CommonAdapter;

import javax.inject.Inject;

/**
 * @Describe 附近的人列表页
 * @Author Jungle68
 * @Date 2017/8/10
 * @Contact master.jungle68@gmail.com
 */
public class FindSomeOneNearbyListFragment extends TSListFragment<FindSomeOneNearbyListContract.Presenter, NearbyBean> implements FindSomeOneNearbyListContract.View {

    @Inject
    FindSomeOneNearbyListPresenter mFollowFansListPresenter;

    public static FindSomeOneNearbyListFragment initFragment() {
        FindSomeOneNearbyListFragment followFansListFragment = new FindSomeOneNearbyListFragment();
        return followFansListFragment;
    }


    @Override
    protected CommonAdapter<NearbyBean> getAdapter() {
        return new FindSomeOneNearbyListAdapter(getContext(), R.layout.item_find_some_list, mListDatas, mPresenter);
    }

    @Override
    protected boolean isNeedRefreshDataWhenComeIn() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        DaggerFindSomeOneNearbyListPresenterComponent
                .builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .findSomeOneNearbyListPresenterModule(new FindSomeOneNearbyListPresenterModule(this))
                .build().inject(this);

        super.initView(rootView);
        LogUtils.e("    5555555         ");
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_nobody;
    }

    @Override
    public void showLoading() {
        mRefreshlayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mRefreshlayout.setRefreshing(false);
    }

    @Override
    protected void requestNetData(Long maxId, boolean isLoadMore) {
        mPresenter.requestNetData(maxId, isLoadMore);
    }


    @Override
    public void upDateFollowFansState(int index) {
        refreshData(index);
    }

    @Override
    public void upDateFollowFansState() {
        refreshData();
    }

}