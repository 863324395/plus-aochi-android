package com.zhiyicx.thinksnsplus.modules.shortvideo.videostore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        return VideoSelectFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {

    }

    public static void startVideoSelectActivity(Context context, boolean isRelaod) {
        Intent intent = new Intent(context, VideoSelectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(VideoSelectFragment.IS_RELOAD, isRelaod);
        intent.putExtras(bundle);
        if (isRelaod && context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, VideoSelectFragment.RECHOSE_VIDEO);
            return;
        }
        context.startActivity(intent);
    }
}
