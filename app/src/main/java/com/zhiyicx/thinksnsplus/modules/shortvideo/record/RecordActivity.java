package com.zhiyicx.thinksnsplus.modules.shortvideo.record;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;

/**
 * @Author Jliuer
 * @Date 2018/03/28/10:52
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class RecordActivity extends TSActivity<AppBasePresenter,RecordFragment> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onBackPressed() {
        mContanierFragment.onBackPressed();
    }

    @Override
    protected RecordFragment getFragment() {
        return new RecordFragment();
    }

    @Override
    protected void componentInject() {

    }
}
