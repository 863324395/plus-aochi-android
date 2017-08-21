package com.zhiyicx.thinksnsplus.modules.q_a.reward;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxRadioGroup;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trycatch.mysnackbar.Prompt;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.config.PayConfig;
import com.zhiyicx.baseproject.widget.button.CombinationButton;
import com.zhiyicx.baseproject.widget.popwindow.CenterInfoPopWindow;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.ExpertBean;
import com.zhiyicx.thinksnsplus.data.beans.QAPublishBean;
import com.zhiyicx.thinksnsplus.data.beans.qa.QATopicBean;
import com.zhiyicx.thinksnsplus.modules.information.infomain.InfoActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.QA_Activity;
import com.zhiyicx.thinksnsplus.modules.q_a.reward.expert_search.ExpertSearchActivity;
import com.zhiyicx.thinksnsplus.modules.usertag.TagFrom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.modules.q_a.publish.question.PublishQuestionFragment.BUNDLE_PUBLISHQA_BEAN;
import static com.zhiyicx.thinksnsplus.modules.usertag.TagFrom.QA_PUBLISH;

/**
 * @author Catherine
 * @describe 悬赏页面
 * @date 2017/7/25
 * @contact email:648129313@qq.com
 */

public class QARewardFragment extends TSFragment<QARewardContract.Presenter> implements QARewardContract.View,
        CenterInfoPopWindow.CenterPopWindowItem1ClickListener {

    public static final String BUNDLE_RESULT = "bundle_result";

    @BindView(R.id.tv_choose_tip)
    TextView mTvChooseTip;
    @BindView(R.id.rb_one)
    RadioButton mRbOne;
    @BindView(R.id.rb_two)
    RadioButton mRbTwo;
    @BindView(R.id.rb_three)
    RadioButton mRbThree;
    @BindView(R.id.rb_days_group)
    RadioGroup mRbDaysGroup;
    @BindView(R.id.tv_invite_hint)
    TextView mTvInviteHint;
    @BindView(R.id.wc_invite)
    SwitchCompat mWcInvite;
    @BindView(R.id.ll_qa_set_money)
    LinearLayout mLlQaSetMoney;
    @BindView(R.id.et_input)
    EditText mEtInput;
    @BindView(R.id.bt_qa_select_expert)
    CombinationButton mBtQaSelectExpert;
    @BindView(R.id.wc_onlooker)
    SwitchCompat mWcOnlooker;
    @BindView(R.id.rl_onlooker)
    RelativeLayout mRlOnlooker;
    @BindView(R.id.rb_onlookers_one)
    RadioButton mRbOnlookersOne;
    @BindView(R.id.rb_onlookers_two)
    RadioButton mRbOnlookersTwo;
    @BindView(R.id.rb_onlookers_three)
    RadioButton mRbOnlookersThree;
    @BindView(R.id.rb_onlookers_days_group)
    RadioGroup mRbOnlookersDaysGroup;
    @BindView(R.id.ll_qa_set_onlookers_money)
    LinearLayout mLlQaSetOnlookersMoney;
    @BindView(R.id.bt_publish)
    TextView mBtPublish;
    @BindView(R.id.tv_reward_rule)
    TextView mTvRewardRule;
    @BindView(R.id.et_onlooker_input)
    EditText mEtOnlookerInput;
    @BindView(R.id.ll_onlooker_set_custom_money)
    LinearLayout mLlOnlookerSetCustomMoney;

    // 悬赏相关
    private List<Float> mRewardLabels; // reward labels
    private double mRewardMoney; // money choosed for reward
    // 围观相关
    private List<Float> mOnLookerLabels; // reward labels
    private double mOnLookerMoney; // money pay for watch

    private CenterInfoPopWindow mRulePop; // 悬赏规则

    private QAPublishBean mQAPublishBean;

    public static QARewardFragment instance(Bundle bundle) {
        QARewardFragment fragment = new QARewardFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_post_reward;
    }

    @Override
    protected void initView(View rootView) {
        mTvChooseTip.setText(R.string.qa_publish_reward_set_money);
        mTvInviteHint.setText(getString(R.string.qa_publish_reward));
        mTvInviteHint.append(getString(R.string.qa_publish_reward_invite));
        initListener();
    }

    @Override
    protected void initData() {
        initDefaultMoney();
        initAlertPopupWindow();
        mQAPublishBean = getArguments().getParcelable(BUNDLE_PUBLISHQA_BEAN);
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.qa_publish_reward_enable_skip);
    }

    @Override
    protected String setRightTitle() {
        return getString(R.string.qa_publish_reset_money);
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected void setRightClick() {
        // 重置
        resetValue();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TagFrom.QA_PUBLISH.id && resultCode == RESULT_OK) {// 选择专家
            ExpertBean expertBean = data.getExtras().getParcelable(BUNDLE_RESULT);
            if (expertBean!=null){
                List<QAPublishBean.Invitations> typeIdsList = new ArrayList<>();
                QAPublishBean.Invitations typeIds = new QAPublishBean.Invitations();
                typeIds.setUser(expertBean.getId());
                typeIdsList.add(typeIds);

                mBtQaSelectExpert.setRightText(expertBean.getName());
                mQAPublishBean.setInvitations(typeIdsList);
            }
            configSureButton();
        }
    }

    private void initDefaultMoney() {
        mRewardLabels = new ArrayList<>();
        mOnLookerLabels = new ArrayList<>();
        mRewardLabels.add(1.00f);
        mRewardLabels.add(5.00f);
        mRewardLabels.add(10.00f);

        mOnLookerLabels.add(1.00f);
        mOnLookerLabels.add(5.00f);
        mOnLookerLabels.add(10.00f);

        setRbValue(mRbOne, mRewardLabels.get(0));
        setRbValue(mRbTwo, mRewardLabels.get(1));
        setRbValue(mRbThree, mRewardLabels.get(2));

        setRbValue(mRbOnlookersOne, mOnLookerLabels.get(0));
        setRbValue(mRbOnlookersTwo, mRewardLabels.get(1));
        setRbValue(mRbOnlookersThree, mRewardLabels.get(2));

    }

    @Override
    protected void snackViewDismissWhenTimeOut(Prompt prompt) {
        if (prompt == Prompt.SUCCESS) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), QA_Activity.class));
        }
    }

    private void initAlertPopupWindow() {
        if (mRulePop != null) {
            return;
        }
        mRulePop = CenterInfoPopWindow.builder()
                .titleStr(getString(R.string.qa_publish_reward_rule))
                .desStr("xxxxxxxxxxxxxxxxxxx")
                .item1Str(getString(R.string.get_it))
                .item1Color(R.color.themeColor)
                .isOutsideTouch(true)
                .isFocus(true)
                .animationStyle(R.style.style_actionPopupAnimation)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(getActivity())
                .buildCenterPopWindowItem1ClickListener(() -> mRulePop.hide())
                .parentView(getView())
                .build();
    }

    private void setRbValue(RadioButton radioButton, float f) {
        radioButton.setText(String.format(getString(R.string.dynamic_send_toll_select_money), f));
    }

    private void initListener() {
        // 悬赏金额修改
        RxRadioGroup.checkedChanges(mRbDaysGroup)
                .compose(this.bindToLifecycle())
                .subscribe(checkedId -> {
                    if (checkedId != -1) {
                        resetRewardInput();
                    }
                    switch (checkedId) {
                        case R.id.rb_one:
                            mRewardMoney = mRewardLabels.get(0);
                            break;
                        case R.id.rb_two:
                            mRewardMoney = mRewardLabels.get(1);
                            break;
                        case R.id.rb_three:
                            mRewardMoney = mRewardLabels.get(2);
                            break;
                    }
                    if (checkedId != -1) {
                        configSureButton();
                    }
                });
        // 围观金额修改
        RxRadioGroup.checkedChanges(mRbOnlookersDaysGroup)
                .compose(this.bindToLifecycle())
                .subscribe(checkedId -> {
                    if (checkedId != -1) {
                        resetOnlookerInput();
                    }
                    switch (checkedId) {
                        case R.id.rb_onlookers_one:
                            mOnLookerMoney = mOnLookerLabels.get(0);
                            break;
                        case R.id.rb_onlookers_two:
                            mOnLookerMoney = mOnLookerLabels.get(1);
                            break;
                        case R.id.rb_onlookers_three:
                            mOnLookerMoney = mOnLookerLabels.get(2);
                            break;
                    }
                    if (checkedId != -1) {
                        configSureButton();
                    }
                });
        // 监听悬赏金额的输入变化
        RxTextView.textChanges(mEtInput)
                .subscribe(charSequence -> {
                    String mRechargeMoneyStr = charSequence.toString();
                    if (mRechargeMoneyStr.replaceAll(" ", "").length() > 0) {
                        mRewardMoney = Double.parseDouble(mRechargeMoneyStr);
                        if (mRbDaysGroup.getCheckedRadioButtonId() != -1) {
                            mRbDaysGroup.clearCheck();
                        }
                    } else {
                        mRewardMoney = 0;
                    }
                    configSureButton();
                }, throwable -> {
                    mRewardMoney = 0;
                    configSureButton();
                });
        RxTextView.textChanges(mEtOnlookerInput)
                .subscribe(charSequence -> {
                    String mRechargeMoneyStr = charSequence.toString();
                    if (mRechargeMoneyStr.replaceAll(" ", "").length() > 0) {
                        mOnLookerMoney = Double.parseDouble(mRechargeMoneyStr);
                        if (mRbOnlookersDaysGroup.getCheckedRadioButtonId() != -1) {
                            mRbOnlookersDaysGroup.clearCheck();
                        }
                    } else {
                        mOnLookerMoney = 0;
                    }
                    configSureButton();
                }, throwable -> {
                    mOnLookerMoney = 0;
                    configSureButton();
                });
        // 邀请开关
        mWcInvite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mBtQaSelectExpert.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            mRlOnlooker.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            resetExpert();
            configSureButton();
        });
        // 围观开关
        mWcOnlooker.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            mLlOnlookerSetCustomMoney.setVisibility(isChecked ? View.VISIBLE : View.GONE);
//            mLlQaSetOnlookersMoney.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            // 关闭之后 重置围观的数据
            if (!isChecked) {
                resetOnLookerMoney();
            }
            configSureButton();
        });
        RxView.clicks(mBtQaSelectExpert)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> {
                    // 跳转搜索选择专家列表
                    Intent intent = new Intent(getActivity(), ExpertSearchActivity.class);
                    startActivityForResult(intent, QA_PUBLISH.id);
                });
        RxView.clicks(mBtPublish)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> {
                    // 发布
                    try {
                        mQAPublishBean.setAmount(PayConfig.realCurrencyYuan2Fen(mRewardMoney));
                        mQAPublishBean.setAutomaticity(mWcInvite.isChecked() ? 1 : 0);
                        mQAPublishBean.setLook(mWcOnlooker.isChecked() ? 1 : 0);
                        mPresenter.publishQuestion(mQAPublishBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
        RxView.clicks(mTvRewardRule)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> mRulePop.show());
    }

    private void resetValue() {
        mWcInvite.setChecked(false);
        resetExpert();
        resetRewardMoney();
        resetOnLookerMoney();
        resetRewardInput();
    }

    /**
     * 悬赏的数据重置
     */
    private void resetRewardMoney() {
        mRewardMoney = 0;
        mRbDaysGroup.clearCheck();
    }

    /**
     * 围观无论是关闭还是重置 都是直接清除整体
     * 同样 一旦打开就必须设置所有相关数据
     */
    private void resetOnLookerMoney() {
        mOnLookerMoney = 0;
        mEtOnlookerInput.setText("");
        mRbOnlookersDaysGroup.clearCheck();
        mWcOnlooker.setChecked(false);
    }

    /**
     * 悬赏的清空数据，在关闭邀请的时候调用
     * 此时 悬赏的钱不置为0 关闭按钮后，清除邀请人姓名
     */
    private void resetExpert() {
        mBtQaSelectExpert.setRightText("");
    }

    /**
     * 重置悬赏金额输入框，在选择默认三个时调用
     */
    private void resetRewardInput() {
        mEtInput.setText("");
    }

    /**
     * 重置围观金额，选择了默认三个时调用
     */
    private void resetOnlookerInput() {
        mEtOnlookerInput.setText("");
    }

    /**
     * 悬赏逻辑
     * 1、一旦开了邀请某人 那么必须设置金额和邀请人 否则不能发布
     * 2、一旦开启了围观 那么必须设置围观金额 否则不能发布
     * 3、什么都不设置 可以发布
     */
    private void configSureButton() {
        boolean isEnable = true;
        // 开了邀请但是居然不设置金额 想空手套白狼吗？？？ 不过这个邀请专家 就只能专家得钱了 万一专家并不想搭理你呢
        if (mWcInvite.isChecked() && (mRewardMoney <= 0 || TextUtils.isEmpty(mBtQaSelectExpert.getRightText()))) {
            isEnable = false;
        }
        // 围观都要收钱,但是收多少已经不是我能管的了QwQ.
        if (mWcOnlooker.isChecked() && mOnLookerMoney <= 0) {
            //isEnable = false;
        }
        mBtPublish.setEnabled(isEnable);
    }

    private void checkData() {

    }

    @Override
    public void onClicked() {
        mRulePop.dismiss();
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

}
