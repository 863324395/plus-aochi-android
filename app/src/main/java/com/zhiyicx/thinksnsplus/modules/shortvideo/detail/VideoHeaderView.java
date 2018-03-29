package com.zhiyicx.thinksnsplus.modules.shortvideo.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.tym.shortvideo.media.VideoInfo;
import com.tym.shortvideo.utils.TrimVideoUtil;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.shortvideo.helper.CustomMediaPlayerAssertFolder;

import java.util.LinkedHashMap;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * @Author Jliuer
 * @Date 2018/03/29/11:13
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class VideoHeaderView {

    private View mVideoHeaderView;

    private JZVideoPlayerStandard mVideoPlayerStandard;
    private Context mContext;
    private VideoInfo mVideoInfo;

    public VideoHeaderView(Context context, VideoInfo videoInfo) {
        mContext = context;
        mVideoInfo = videoInfo;
        init();
    }

    private void init() {
        mVideoHeaderView = LayoutInflater.from(mContext).inflate(R.layout
                .view_video_header, null);

        mVideoPlayerStandard = (JZVideoPlayerStandard) mVideoHeaderView.findViewById(R.id.videoplayer);

        mVideoPlayerStandard.getLayoutParams().height = ConvertUtils.dp2px(mContext, 400);
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put(JZVideoPlayer.URL_KEY_DEFAULT, "file:///storage/emulated/0/Codec/tym/tym/tym_1522302572466.mp4");
        Object[] dataSourceObjects = new Object[2];
        dataSourceObjects[0] = map;
        dataSourceObjects[1] = this;
        mVideoPlayerStandard.setUp(dataSourceObjects, 0, JZVideoPlayerStandard.SCREEN_WINDOW_LIST, "饺子快长大");
        JZVideoPlayer.setMediaInterface(new CustomMediaPlayerAssertFolder());//进入此页面修改MediaInterface，让此页面的jzvd正常工作
        Glide.with(mContext).load(TrimVideoUtil.getVideoFilePath(mVideoInfo.getPath())).into(mVideoPlayerStandard.thumbImageView);
    }

    public View getVideoHeaderView() {
        return mVideoHeaderView;
    }

}
