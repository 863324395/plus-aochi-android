package com.zhiyicx.thinksnsplus.modules.wallet.recharge;

import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.widget.button.CombinationButton;
import com.zhiyicx.baseproject.widget.popwindow.CenterInfoPopWindow;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/05/22
 * @Contact master.jungle68@gmail.com
 */
public class RechargeFragment extends TSFragment<RechargeContract.Presenter> implements RechargeContract.View {


    private CenterInfoPopWindow mRulePop;// 充值提示规则选择弹框

    public static RechargeFragment newInstance() {
        return new RechargeFragment();
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_recharge;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.recharge);
    }

    @Override
    protected void initView(View rootView) {
        initListener();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setRightClick() {
        super.setRightClick();
        showSnackSuccessMessage("setRightClick");
    }

    private void initListener() {
//        // 充值
//        RxView.clicks(mBtReCharge)
//                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)   //两秒钟之内只取一个点击事件，防抖操作
//                .compose(this.<Void>bindToLifecycle())
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        showSnackSuccessMessage("mBtReCharge");
//                    }
//                });

    }

    /**
     * 初始化登录选择弹框
     */
    private void showRulePopupWindow() {
        if (mRulePop != null) {
            mRulePop.show();
            return;
        }
        mRulePop = CenterInfoPopWindow.builder()
                .titleStr(getString(R.string.recharge_and_withdraw_rule))
                .desStr(getString(R.string.recharge_and_withdraw_rule_detail))
                .item1Str(getString(R.string.get_it))
                .item1Color(R.color.themeColor)
                .isOutsideTouch(true)
                .isFocus(true)
                .animationStyle(R.style.style_actionPopupAnimation)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(getActivity())
                .buildCenterPopWindowItem1ClickListener(new CenterInfoPopWindow.CenterPopWindowItem1ClickListener() {
                    @Override
                    public void onClicked() {
                        mRulePop.hide();
                    }
                })
                .parentView(getView())
                .build();
        mRulePop.show();
    }

}