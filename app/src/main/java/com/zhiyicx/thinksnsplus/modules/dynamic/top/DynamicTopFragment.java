package com.zhiyicx.thinksnsplus.modules.dynamic.top;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.wallet.WalletActivity;
import com.zhiyicx.thinksnsplus.modules.wallet.recharge.RechargeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2017/05/22/11:13
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class DynamicTopFragment extends TSFragment<DynamicTopContract.Presenter> implements DynamicTopContract.View {

    @BindView(R.id.rb_one)
    RadioButton mRbOne;
    @BindView(R.id.rb_two)
    RadioButton mRbTwo;
    @BindView(R.id.rb_three)
    RadioButton mRbThree;
    @BindView(R.id.et_top_input)
    EditText mEtTopInput;
    @BindView(R.id.et_top_total)
    EditText mEtTopTotal;
    @BindView(R.id.bt_top)
    TextView mBtTop;
    @BindView(R.id.tv_dynamic_top_dec)
    TextView mTvDynamicTopDec;
    @BindView(R.id.rb_days_group)
    RadioGroup mRbDaysGroup;

    private List<Integer> mSelectDays;
    private int mCurrentDays;
    private float mInputMoney;
    private String mInputMoneyStr;

    private ActionPopupWindow mStickTopInstructionsPopupWindow;

    public static DynamicTopFragment newInstance() {
        return new DynamicTopFragment();
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.to_top);
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        mSelectDays = new ArrayList<>();
        mSelectDays.add(1);
        mSelectDays.add(5);
        mSelectDays.add(10);
        initSelectDays(mSelectDays);
        initListener();
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean insufficientBalance() {
        return mPresenter.getBalance() < mCurrentDays * mInputMoney;
    }

    @Override
    public void gotoRecharge() {
        startActivity(new Intent(getActivity(), WalletActivity.class));
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_dynamic_top;
    }

    @Override
    public String getInputMoneyStr() {
        return mInputMoneyStr;
    }

    private void initListener() {
        mRbDaysGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_one:
                        mCurrentDays = mSelectDays.get(0);
                        break;
                    case R.id.rb_two:
                        mCurrentDays = mSelectDays.get(1);
                        break;
                    case R.id.rb_three:
                        mCurrentDays = mSelectDays.get(2);
                        break;
                }
                setConfirmEnable();
            }
        });

        RxTextView.textChanges(mEtTopInput)
                .compose(this.<CharSequence>bindToLifecycle())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        mInputMoneyStr = charSequence.toString();
                        if (!TextUtils.isEmpty(charSequence)) {
                            mInputMoney = Float.parseFloat(mInputMoneyStr);
                        } else {
                            mInputMoney = 0f;
                        }
                        setConfirmEnable();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mInputMoney = 0f;
                    }
                });

        RxTextView.textChanges(mEtTopTotal)
                .compose(this.<CharSequence>bindToLifecycle())
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        mBtTop.setText(getString(mPresenter.getBalance() < mCurrentDays * mInputMoney
                                ? R.string.to_recharge : R.string.sure));
                    }
                });

        RxView.clicks(mBtTop)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mPresenter.stickTop(2);
                    }
                });
    }

    private void initSelectDays(List<Integer> mSelectDays) {
        mRbOne.setText(String.format(getString(R.string.select_day), mSelectDays.get(0)));
        mRbTwo.setText(String.format(getString(R.string.select_day), mSelectDays.get(1)));
        mRbThree.setText(String.format(getString(R.string.select_day), mSelectDays.get(2)));
    }

    private void setConfirmEnable() {
        boolean enable = mCurrentDays > 0 && mInputMoney > 0;
        mBtTop.setEnabled(enable);
        if (!enable)
            return;
        mEtTopTotal.setText(String.valueOf(mCurrentDays * mInputMoney));
        mTvDynamicTopDec.setText(String.format(getString(R.string.to_top_description), mInputMoney / mCurrentDays, mPresenter.getBalance()));
    }

    @Override
    public void initStickTopInstructionsPop() {
        if (mStickTopInstructionsPopupWindow != null) {
            mStickTopInstructionsPopupWindow.show();
            return;
        }
        mStickTopInstructionsPopupWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.sticktop_instructions))
                .desStr(getString(R.string.sticktop_instructions_detail))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(getActivity())
                .bottomClickListener(new ActionPopupWindow.ActionPopupWindowBottomClickListener() {
                    @Override
                    public void onItemClicked() {
                        mStickTopInstructionsPopupWindow.hide();
                    }
                })
                .build();
        mStickTopInstructionsPopupWindow.show();
    }

}
