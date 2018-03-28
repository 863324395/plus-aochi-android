package com.zhiyicx.thinksnsplus.modules.shortvideo.videostore;

import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSActivity;


/**
 * @author Jliuer
 * @Date 18/03/28 11:25
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class VideoSelectActivity extends TSActivity {

    @Override
    protected Fragment getFragment() {
        return new VideoSelectFragment();
    }

    @Override
    protected void componentInject() {

    }
}
