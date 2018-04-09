package com.zhiyicx.thinksnsplus.modules.shortvideo.cover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSActivity;

import java.util.ArrayList;

import static com.zhiyicx.thinksnsplus.modules.shortvideo.cover.CoverFragment.REQUESTCODE;

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

    public static void startCoverActivity(Context context, ArrayList<String> path,boolean pre) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(CoverFragment.PATH, path);
        bundle.putBoolean(CoverFragment.PREVIEW,pre);
        Intent intent = new Intent(context, CoverActivity.class);
        intent.putExtras(bundle);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, REQUESTCODE);
            return;
        }
        context.startActivity(intent);

    }
}
