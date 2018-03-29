package com.zhiyicx.thinksnsplus.modules.shortvideo.detail;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tym.shortvideo.utils.TrimVideoUtil;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.modules.shortvideo.helper.CustomMediaPlayerAssertFolder;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.LinkedHashMap;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/3/10
 * @Contact master.jungle68@gmail.com
 */

public class VideoDetailCommentItem implements ItemViewDelegate<DynamicCommentBean> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.view_video_header;
    }

    @Override
    public boolean isForViewType(DynamicCommentBean item, int position) {
        return position == 0 || !TextUtils.isEmpty(item.getComment_content());
    }

    @Override
    public void convert(ViewHolder holder, DynamicCommentBean dynamicCommentBean, DynamicCommentBean lastT, final int position, int itemCounts) {
        JZVideoPlayerStandard mVideoPlayerStandard = holder.getView(R.id.videoplayer);

        mVideoPlayerStandard.getLayoutParams().height = ConvertUtils.dp2px(mVideoPlayerStandard.getContext(), 400);
        LinkedHashMap map = new LinkedHashMap();
        map.put(JZVideoPlayer.URL_KEY_DEFAULT, "file:///storage/emulated/0/Codec/tym/tym/tym_1522302572466.mp4");
        Object[] dataSourceObjects = new Object[2];
        dataSourceObjects[0] = map;
        dataSourceObjects[1] = this;
        mVideoPlayerStandard.setUp(dataSourceObjects, 0, JZVideoPlayerStandard.SCREEN_WINDOW_LIST, "饺子快长大");
        JZVideoPlayer.setMediaInterface(new CustomMediaPlayerAssertFolder());//进入此页面修改MediaInterface，让此页面的jzvd正常工作
        Glide.with(mVideoPlayerStandard.getContext()).load("file:///storage/emulated/0/Codec/tym/tym/tym_1522302572466.mp4").into(mVideoPlayerStandard.thumbImageView);
    }
}
