package com.zhiyicx.thinksnsplus.modules.dynamic;

import android.content.Context;

import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.mvp.i.IBasePresenter;
import com.zhiyicx.common.mvp.i.IBaseView;
import com.zhiyicx.thinksnsplus.data.beans.AuthBean;
import com.zhiyicx.thinksnsplus.modules.login.LoginContract;

import java.util.HashMap;

import rx.Observable;

/**
 * @author LiuChao
 * @describe
 * @date 2017/2/20
 * @contact email:450127106@qq.com
 */

public interface SendDynamicContract {
    //对于经常使用的关于UI的方法可以定义到BaseView中,如显示隐藏进度条,和显示文字消息
    interface View extends IBaseView<SendDynamicContract.Presenter> {

    }

    //Model层定义接口,外部只需关心model返回的数据,无需关心内部细节,及是否使用缓存
    interface Repository {
        Observable<BaseJson<Object>> sendDynamic(HashMap<String, Object> params);
    }

    interface Presenter extends IBasePresenter {
        void sendDynamic(HashMap<String, Object> params);
    }
}
