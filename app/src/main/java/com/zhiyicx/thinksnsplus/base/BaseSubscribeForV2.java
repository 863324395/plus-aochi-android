package com.zhiyicx.thinksnsplus.base;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.log.LogUtils;

import java.io.IOException;
import java.util.Map;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * @Describe 处理服务器数据 适用 ＲＥＳＥＴＦＵＬ　ＡＰＩ
 * @Author Jungle68
 * @Date 2017/5/18
 * @Contact master.jungle68@gmail.com
 */

public abstract class BaseSubscribeForV2<T> extends Subscriber<T> {
    private static final String TAG = "BaseSubscribeForV2";

    /**
     * onNext 执行后执行
     */
    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            try {
                if (response != null && response.errorBody() != null) {
                    //解析response content
                    String bodyString = ConvertUtils.getResponseBodyString(response);
                    // 打印返回的json结果
                    LogUtils.d(TAG, "------onError-body--------" + bodyString);
                    // api v2版本的数据， 错误信息带有 n +1 表示  详情查看 ：https://github.com/zhiyicx/thinksns-plus/blob/master/docs/ai/v2/overvie.md
                    Map<String, String[]> errorMessageMap = new Gson().fromJson(bodyString,
                            new TypeToken<Map<String, String[]>>() {
                            }.getType());
                    for (String[] value : errorMessageMap.values()) {
                        onFailure(value[0], 0); //  app 端只需要一个
                        break;
                    }
                } else {
                    handleError(e);
                }
            } catch (IOException e1) {
                handleError(e);
            } catch (JsonSyntaxException e1) {
                handleError(e);
            }
        } else {
            handleError(e);
        }

    }


    /**
     * 处理数据，按照 resutful api 要求，204 不反回数据
     *
     * @param data
     */
    @Override
    public void onNext(T data) {
        // 数据接收成功
        onSuccess(data);
    }


    /**
     * 处理错误
     *
     * @param e
     */
    private void handleError(Throwable e) {
        e.printStackTrace();
        onException(e);
    }


    /**
     * 服务器正确处理返回正确数据
     *
     * @param data 正确的数据
     */
    protected abstract void onSuccess(T data);

    /**
     * 服务器正确接收到请求，主动返回错误状态以及数据
     *
     * @param message 错误信息
     * @param code
     */
    protected abstract void onFailure(String message, int code);

    /**
     * 系统级错误，网络错误，系统内核错误等
     *
     * @param throwable
     */
    protected abstract void onException(Throwable throwable);

}