package com.zhiyicx.thinksnsplus.modules.guide;

import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.thinksnsplus.data.source.repository.AuthRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.SystemRepository;
import com.zhiyicx.thinksnsplus.modules.home.HomeActivity;
import com.zhiyicx.thinksnsplus.modules.login.LoginActivity;

import javax.inject.Inject;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/10
 * @Contact master.jungle68@gmail.com
 */

public class GuidePresenter extends BasePresenter<GuideContract.Repository, GuideContract.View> implements GuideContract.Presenter {

    @Inject
    AuthRepository mIAuthRepository;
    @Inject
    SystemRepository mSystemRepository;

    @Inject
    public GuidePresenter(GuideContract.Repository repository, GuideContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void checkLogin() {
        // 系统扩展配置信息处理
        mSystemRepository.getComponentStatusFromServer();
        mSystemRepository.getComponentConfigFromServer(ApiConfig.APP_PATH_GET_COMPONENT_CONFIGS_IM);

        if (mIAuthRepository.isLogin()) {
            // TODO: 2017/2/10 刷新 Token 时间，过期前一天刷新
//        mIAuthRepository.refreshToken();
            // IM login
            mRootView.startActivity(HomeActivity.class);
        } else {
            mRootView.startActivity(LoginActivity.class);
        }
    }
}

