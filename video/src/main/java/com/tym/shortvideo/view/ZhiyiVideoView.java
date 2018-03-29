package com.tym.shortvideo.view;

import android.content.Context;
import android.util.AttributeSet;

import com.tym.video.R;

import cn.jzvd.JZVideoPlayerStandard;

/**
 * @Author Jliuer
 * @Date 2018/03/29/15:32
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ZhiyiVideoView extends JZVideoPlayerStandard {
    public ZhiyiVideoView(Context context) {
        super(context);
    }

    public ZhiyiVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.zhiyi_layout_standard;
    }
}
