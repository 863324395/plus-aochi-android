package com.zhiyicx.thinksnsplus.dagger;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author LiuChao
 * @describe
 * @date 2016/12/30
 * @contact email:450127106@qq.com
 */
@Singleton
@Component(modules = {GreenDaoModule.class})
public interface GreenDaoComponent {
}
