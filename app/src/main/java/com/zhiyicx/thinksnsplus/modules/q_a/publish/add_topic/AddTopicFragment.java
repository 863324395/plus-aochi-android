package com.zhiyicx.thinksnsplus.modules.q_a.publish.add_topic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.utils.recycleviewdecoration.CustomLinearDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.qa.QATopicBean;
import com.zhiyicx.thinksnsplus.modules.q_a.publish.detail.PublishContentActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;


/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/7/25
 * @Contact master.jungle68@gmail.com
 */

public class AddTopicFragment extends TSListFragment<AddTopicContract.Presenter, QATopicBean> implements AddTopicContract.View, MultiItemTypeAdapter.OnItemClickListener, TagFlowLayout.OnTagClickListener {

    @BindView(R.id.et_qustion)
    EditText mEtQustion;

    @BindView(R.id.line)
    View mLine;

    @BindView(R.id.fl_topics)
    TagFlowLayout mFLTopics;

    private String mQuestionStr = "";
    private List<QATopicBean> mQATopicBeanList = new ArrayList<>();
    private TopicsAdapter mTopicsAdapter;
    private int mMaxTagNums;

    public static AddTopicFragment newInstance() {

        Bundle args = new Bundle();
        AddTopicFragment fragment = new AddTopicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_publish_qustion_add_topic;
    }

    @Override
    protected int setEmptView() {
        return 0;
    }

    @Override
    protected String setRightTitle() {
        return getString(R.string.qa_publish_next);
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.qa_publish_select_topic_hint);
    }

    @Override
    protected boolean isRefreshEnable() {
        return false;
    }

    @Override
    protected boolean isLoadingMoreEnable() {
        return false;
    }

    @Override
    protected void setRightClick() {
        super.setRightClick();
        startActivity(new Intent(getActivity(), PublishContentActivity.class));
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new CustomLinearDecoration(0, getResources().getDimensionPixelSize(R.dimen
                .divider_line), 0, 0, ContextCompat.getDrawable(getContext(), R.drawable
                .shape_recyclerview_grey_divider));
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        initTopicsView();
        RxTextView.afterTextChangeEvents(mEtQustion)
                .compose(this.bindToLifecycle())
                .subscribe(new Subscriber<TextViewAfterTextChangeEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mToolbarRight.setEnabled(false);
                    }

                    @Override
                    public void onNext(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                        mQuestionStr = textViewAfterTextChangeEvent.editable().toString().trim();
                        if (!TextUtils.isEmpty(mQuestionStr)) {
                            mToolbarRight.setEnabled(true);
                            // TODO: 20177/25  搜索相同的問題
                        } else {
                            mToolbarRight.setEnabled(false);
                        }

                    }
                });
    }

    private void initTopicsView() {
        mTopicsAdapter = new TopicsAdapter(mQATopicBeanList, getContext());
        mFLTopics.setAdapter(mTopicsAdapter);
        mFLTopics.setOnTagClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mMaxTagNums = getResources().getInteger(R.integer.tag_max_nums);
        for (int i = 0; i < 10; i++) {
            QATopicBean qa_lIstInfoBean = new QATopicBean();
            mListDatas.add(qa_lIstInfoBean);
        }
        refreshData();
        if (mListDatas.isEmpty()) {
            mLine.setVisibility(View.INVISIBLE);
        } else {
            mLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        AddTopicAdapter adapter = new AddTopicAdapter(getContext(), R.layout.item_publish_question_add_topic, mListDatas);
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        if (mQATopicBeanList.size() < mMaxTagNums) {
            mQATopicBeanList.add(mListDatas.get(position));
            mTopicsAdapter.notifyDataChanged();
        } else {
            showSnackErrorMessage(getString(R.string.qa_publish_select_topic_count_hint, mMaxTagNums));
        }
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    @Override
    public boolean onTagClick(View view, int position, FlowLayout parent) {
        mQATopicBeanList.remove(position);
        mTopicsAdapter.notifyDataChanged();
        return true;
    }
}