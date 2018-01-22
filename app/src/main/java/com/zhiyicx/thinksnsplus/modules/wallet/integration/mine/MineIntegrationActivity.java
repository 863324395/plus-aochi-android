package com.zhiyicx.thinksnsplus.modules.wallet.integration.mine;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;

/**
 * @Describe 我的积分
 * @Author Jungle68
 * @Date 2018/1/18
 * @Contact master.jungle68@gmail.com
 */
public class MineIntegrationActivity extends TSActivity<MineIntegrationPresenter, MineIntegrationFragment> {


    @Override
    protected void componentInject() {
        DaggerMineIntegrationComponent
                .builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .mineIntegrationPresenterModule(new MineIntegrationPresenterModule(mContanierFragment))
                .build()
                .inject(this);

    }

    @Override
    protected MineIntegrationFragment getFragment() {
        return MineIntegrationFragment.Companion.newInstance();
    }

}
