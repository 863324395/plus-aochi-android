package com.zhiyicx.thinksnsplus.data.source.repository;

import android.app.Application;

import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.common.net.UpLoadFile;
import com.zhiyicx.thinksnsplus.data.beans.PurChasesBean;
import com.zhiyicx.thinksnsplus.data.source.remote.CommonClient;
import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;

import java.util.HashMap;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/4/14
 * @Contact master.jungle68@gmail.com
 */

public class CommentRepository implements ICommentRepository {
    protected CommonClient mCommonClient;


    @Inject
    public CommentRepository(ServiceManager serviceManager, Application context) {
        mCommonClient = serviceManager.getCommonClient();
    }


    @Override
    public Observable<BaseJson<Object>> sendComment(String comment_content, long reply_to_user_id, long comment_mark, String path) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("body", comment_content);
        params.put("reply_user", reply_to_user_id);
//        params.put("comment_mark", comment_mark);
        return mCommonClient.handleBackGroundTaskPost(path, UpLoadFile.upLoadFileAndParams(null, params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> sendCommentV2(String comment_content, long reply_to_user_id, long comment_mark, String path) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("body", comment_content);
//        params.put("reply_to_user_id", reply_to_user_id);
        params.put("comment_mark", comment_mark);
        return mCommonClient.handleBackGroundTaskPostV2(path, UpLoadFile.upLoadFileAndParams(null, params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static String getCommentPath(long source_id, String component_type) {
        String path = null;
        if (component_type == null) {
            return path;
        }
        switch (component_type) {
            case ApiConfig.APP_LIKE_FEED:
                path = String.format(ApiConfig.APP_PATH_DYNAMIC_SEND_COMMENT_V2, source_id);

                break;
            case ApiConfig.APP_LIKE_MUSIC:
                path = String.format(ApiConfig.APP_PATH_MUSIC_COMMENT_FORMAT, source_id);
                break;
            case ApiConfig.APP_COMPONENT_SOURCE_TABLE_MUSIC_SPECIALS:
                path = String.format(ApiConfig.APP_PATH_MUSIC_ABLUM_COMMENT_FORMAT, source_id);
                break;
            case ApiConfig.APP_LIKE_NEWS:
                path = String.format(ApiConfig.APP_PATH_INFO_COMMENT_FORMAT, source_id);
                break;
            default:
                break;
        }
        return path;
    }

    @Override
    public Observable<PurChasesBean> checkNote(int note) {
        return mCommonClient.checkNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<BaseJsonV2<String>> paykNote(int note) {
        return mCommonClient.payNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
