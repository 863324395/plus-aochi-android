package com.zhiyicx.thinksnsplus.modules.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author LiuChao
 * @describe
 * @date 2016/12/23
 * @contact email:450127106@qq.com
 */

public class LoginFragment extends TSFragment {
    @BindView(R.id.et_login_phone)
    EditText mEtLoginPhone;
    @BindView(R.id.et_login_password)
    EditText mEtLoginPassword;
    @BindView(R.id.bt_login_login)
    Button mBtLoginLogin;

    @Override
    protected int getlayoutId() {
        return R.layout.fragment_login;
    }

    @OnClick(R.id.bt_login_login)
    public void onClick() {
        LogUtils.i("lalallalalalla");
    }
}
