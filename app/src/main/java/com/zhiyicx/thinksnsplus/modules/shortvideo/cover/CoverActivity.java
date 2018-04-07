package com.zhiyicx.thinksnsplus.modules.shortvideo.cover;

import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSActivity;

/**
 * @Author Jliuer
 * @Date 2018/04/07
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CoverActivity extends TSActivity {
    @Override
    protected Fragment getFragment() {
        return CoverFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {

    }
}
