package com.zhiyicx.thinksnsplus.modules.music_fm.CommonComment;

import com.zhiyicx.common.base.BaseApplication;
import com.zhiyicx.thinksnsplus.config.BackgroundTaskRequestMethodConfig;
import com.zhiyicx.thinksnsplus.data.beans.BackgroundRequestTaskBean;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskHandler;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskManager;

import java.util.HashMap;

/**
 * @Author Jliuer
 * @Date 2017/04/12/9:24
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SendComment implements ICommentEvent<ICommentBean> {

    private BackgroundTaskHandler.OnNetResponseCallBack mCallBack;

    @Override
    public void setListener(BackgroundTaskHandler.OnNetResponseCallBack callBack) {
        mCallBack = callBack;
    }

    @Override
    public void handleCommentInBackGroud(ICommentBean comment) {
        CommonMetadata commentBean = comment.get$$Comment();
        sendComment(commentBean);
        BackgroundRequestTaskBean backgroundRequestTaskBean;
        HashMap<String, Object> params = new HashMap<>();
        params.put("comment_content", commentBean.getString(CommonMetadata.METADATA_KEY_COMMENT_CONTENT));
        params.put("comment_mark", commentBean.getLong(CommonMetadata.METADATA_KEY_COMMENT_MARK));
        params.put("reply_to_user_id", commentBean.getInteger(CommonMetadata.METADATA_KEY_TARGET_ID));

        params.put(BackgroundTaskHandler.NET_CALLBACK,mCallBack);
        // 后台处理
        backgroundRequestTaskBean = new BackgroundRequestTaskBean
                (BackgroundTaskRequestMethodConfig.POST, params);
        backgroundRequestTaskBean.setPath(commentBean.getString(CommonMetadata.METADATA_KEY_COMMENT_URL));
        BackgroundTaskManager.getInstance(BaseApplication.getContext()).addBackgroundRequestTask
                (backgroundRequestTaskBean);
    }

    @Override
    public void handleComment(ICommentBean comment) {

    }

    protected void sendComment(CommonMetadata commentBean) {

    }
}
