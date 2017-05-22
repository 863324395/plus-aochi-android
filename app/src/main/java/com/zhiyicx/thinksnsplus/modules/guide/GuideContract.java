package com.zhiyicx.thinksnsplus.modules.guide;

import android.content.Context;

import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.mvp.i.IBasePresenter;
import com.zhiyicx.common.mvp.i.IBaseView;
import com.zhiyicx.thinksnsplus.data.beans.LaunchAdvertBean;

import java.util.List;

import rx.Observable;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/10
 * @Contact master.jungle68@gmail.com
 */

public interface GuideContract {
    /**
     * 对于经常使用的关于 UI 的方法可以定义到 BaseView 中,如显示隐藏进度条,和显示文字消息
     */
    interface View extends IBaseView<Presenter> {
        /**
         * 跳转
         *
         * @param tClass
         */
        void startActivity(Class tClass);
    }

    /**
     * Model 层定义接口,外部只需关心 model 返回的数据,无需关心内部细节,及是否使用缓存
     */
    interface Repository {
        /**
         * 获取缓存大小
         */
        Observable<String> getDirCacheSize(Context context);

        Observable<BaseJson<List<LaunchAdvertBean>>> getLaunchAdverts();

    }

    interface Presenter extends IBasePresenter {

        void checkLogin();

        void getLaunchAdverts();
    }

}
