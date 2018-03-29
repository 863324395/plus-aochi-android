package com.zhiyicx.thinksnsplus.modules.shortvideo.detail;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;

/**
 * @Author Jliuer
 * @Date 2018/03/29/11:47
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface VideoDetailContract {
    interface View extends ITSListView<DynamicCommentBean,Presenter>{}
    interface Presenter extends ITSListPresenter<DynamicCommentBean>{}
}
