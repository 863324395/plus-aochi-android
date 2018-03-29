package com.zhiyicx.thinksnsplus.modules.shortvideo.detail;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.thinksnsplus.base.AppComponent;
import com.zhiyicx.thinksnsplus.base.InjectComponent;

import dagger.Component;

/**
 * @Author Jliuer
 * @Date 2018/03/29/11:51
 * @Email Jliuer@aliyun.com
 * @Description
 */
@FragmentScoped
@Component(dependencies = AppComponent.class,modules = VideoDetailPresenterModule.class)
public interface VideoDetailComponent extends InjectComponent<VideoDetailActivity> {
}
