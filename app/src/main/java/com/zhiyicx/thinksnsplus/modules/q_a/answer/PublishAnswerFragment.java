package com.zhiyicx.thinksnsplus.modules.q_a.answer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.zhiyicx.baseproject.impl.photoselector.DaggerPhotoSelectorImplComponent;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSelectorImpl;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSeletorImplModule;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.AnswerDraftBean;
import com.zhiyicx.thinksnsplus.data.beans.AnswerInfoBean;
import com.zhiyicx.thinksnsplus.modules.q_a.publish.detail.PublishContentFragment;

/**
 * @Author Jliuer
 * @Date 2017/08/15/16:11
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class PublishAnswerFragment extends PublishContentFragment {

    public static final String BUNDLE_SOURCE_ID = "source_id";
    public static final String BUNDLE_SOURCE_BODY = "source_body";
    public static final String BUNDLE_SOURCE_TYPE = "source_type";
    public static final String BUNDLE_SOURCE_TITLE = "source_title"; // 发布回答的提示语是问题的标题

    private PublishType mType;
    private String mBody;
    private String mTitle;

    private ActionPopupWindow mEditWarningPopupWindow;// 退出编辑警告弹框

    public static PublishAnswerFragment newInstance(Bundle bundle) {
        if (bundle == null || bundle.getLong(BUNDLE_SOURCE_ID) <= 0) {
            throw new IllegalArgumentException("questin_id can not be null");
        }
        PublishAnswerFragment publishContentFragment = new PublishAnswerFragment();
        publishContentFragment.setArguments(bundle);
        return publishContentFragment;
    }

    @Override
    protected void initData() {
        mPhotoSelector = DaggerPhotoSelectorImplComponent
                .builder()
                .photoSeletorImplModule(new PhotoSeletorImplModule(this, this, PhotoSelectorImpl
                        .NO_CRAFT))
                .build().photoSelectorImpl();

        mBody = getArguments().getString(BUNDLE_SOURCE_BODY, "");
        mType = (PublishType) getArguments().getSerializable(BUNDLE_SOURCE_TYPE);
        mTitle = getArguments().getString(BUNDLE_SOURCE_TITLE, "");

        if (mType == PublishType.PUBLISH_ANSWER) {
            mToolbarCenter.setText(getString(R.string.qa_publish_answer));
        } else if (mType == PublishType.UPDATE_ANSWER) {
            mToolbarCenter.setText(getString(R.string.qa_update_answer));
        } else if (mType == PublishType.UPDATE_QUESTION) {
            mToolbarCenter.setText(getString(R.string.qa_update_publish));
        }

        if (!mBody.isEmpty()) {
            mRicheTest.clearAllLayout();
            mPresenter.pareseBody(mBody);
        }
        if (!TextUtils.isEmpty(mTitle)){
            mRicheTest.setHint(mTitle);
        }
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.qa_publish_answer);
    }

    @Override
    protected String setRightTitle() {
        return getString(R.string.qa_publish_btn);
    }

    @Override
    protected void setRightClick() {
        mRicheTest.hideKeyBoard();
        if (mType == PublishType.PUBLISH_ANSWER) {
            mPresenter.publishAnswer(getArguments().getLong(BUNDLE_SOURCE_ID), getContentString()
                    , mAnonymity);
        } else if (mType == PublishType.UPDATE_ANSWER) {
            mPresenter.updateAnswer(getArguments().getLong(BUNDLE_SOURCE_ID), getContentString(),
                    mAnonymity);
        } else if (mType == PublishType.UPDATE_QUESTION) {
            mPresenter.updateQuestion(getArguments().getLong(BUNDLE_SOURCE_ID), getContentString(),
                    mAnonymity);
        }

    }

    @Override
    public void publishSuccess(AnswerInfoBean answerBean) {
        super.publishSuccess(answerBean);
        getActivity().finish();
    }

    @Override
    public void updateSuccess() {
        super.updateSuccess();
        getActivity().finish();
    }

    @Override
    protected void setLeftClick() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (!mToolbarRight.isEnabled() || mType != PublishType.UPDATE_ANSWER) {
            super.onBackPressed();
        } else {
            initEditWarningPop();
        }
    }

    /**
     * @param context
     * @param type
     * @param sourceId
     * @param body
     */
    public static void startQActivity(Context context, PublishType type, long sourceId,
                                      String body, String title) {

        Intent intent = new Intent(context, PublishAnswerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_SOURCE_TYPE, type);
        bundle.putLong(BUNDLE_SOURCE_ID, sourceId);
        bundle.putString(BUNDLE_SOURCE_BODY, body);
        bundle.putString(BUNDLE_SOURCE_TITLE, title);
        intent.putExtras(bundle);
        context.startActivity(intent);

    }

    /**
     * 初始化图片选择弹框
     */
    private void initEditWarningPop() {
        mRicheTest.hideKeyBoard();
        if (mEditWarningPopupWindow != null) {
            mEditWarningPopupWindow.show();
            return;
        }
        mEditWarningPopupWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.edit_quit))
                .item2Str(getString(R.string.save_to_draft_box))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(0.8f)
                .with(getActivity())
                .item1ClickListener(() -> {
                    mEditWarningPopupWindow.hide();
                    getActivity().finish();
                })
                .item2ClickListener(() -> {
                    AnswerDraftBean answerDraftBean = new AnswerDraftBean();
                    String mark = AppApplication.getmCurrentLoginAuth().getUser_id() + "" + System
                            .currentTimeMillis();
                    answerDraftBean.setMark(Long.parseLong(mark));
                    answerDraftBean.setId(getArguments().getLong(BUNDLE_SOURCE_ID));
                    answerDraftBean.setBody(getContentString());
                    answerDraftBean.setAnonymity(mAnonymity);
                    answerDraftBean.setCreated_at(TimeUtils.getCurrenZeroTimeStr());
                    mPresenter.saveAnswer(answerDraftBean);
                    mEditWarningPopupWindow.hide();
                    getActivity().finish();
                })
                .bottomClickListener(() -> mEditWarningPopupWindow.hide()).build();
        mEditWarningPopupWindow.show();
    }
}
