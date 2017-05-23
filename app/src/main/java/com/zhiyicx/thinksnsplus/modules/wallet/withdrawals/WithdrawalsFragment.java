package com.zhiyicx.thinksnsplus.modules.wallet.withdrawals;

import android.content.Intent;
import android.view.View;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.wallet.withdrawals.detail.WithdrawalsDetailActivity;

/**
 * @Author Jliuer
 * @Date 2017/05/23/10:24
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class WithdrawalsFragment extends TSFragment<WithDrawalsConstract.Presenter> implements WithDrawalsConstract.View {

    public static WithdrawalsFragment newInstance() {
        return new WithdrawalsFragment();
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.withdraw);
    }

    @Override
    protected String setRightTitle() {
        mToolbarRight.setTextColor(getColor(R.color.themeColor));
        return getString(R.string.withdraw_details);
    }

    @Override
    protected void setRightClick() {
        startActivity(new Intent(getActivity(), WithdrawalsDetailActivity.class));
    }

    @Override
    protected void initView(View rootView) {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_withdrawals;
    }
}