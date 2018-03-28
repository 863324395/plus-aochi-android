package com.zhiyicx.thinksnsplus.modules.shortvideo.preview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;

/**
 * @Author Jliuer
 * @Date 2018/03/28/15:04
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class PreviewActivity extends TSActivity<AppBasePresenter,PreviewFragment> {

    @Override
    protected PreviewFragment getFragment() {
        return PreviewFragment.getInstance(getIntent().getExtras());
    }

    @Override
    public void onBackPressed() {
        mContanierFragment.onBackPressed();
    }

    @Override
    protected void componentInject() {

    }

    public static void startPreviewActivity(Context context, String path) {
        Intent intent = new Intent(context, PreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PreviewFragment.PATH, path);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
