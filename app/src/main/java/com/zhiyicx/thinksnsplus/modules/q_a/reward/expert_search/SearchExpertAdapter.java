package com.zhiyicx.thinksnsplus.modules.q_a.reward.expert_search;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.klinker.android.link_builder.Link;
import com.zhiyicx.baseproject.config.TouristConfig;
import com.zhiyicx.baseproject.widget.UserAvatarView;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.ExpertBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.UserTagBean;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.widget.flowtag.FlowTagLayout;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import rx.Observable;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @author Catherine
 * @describe
 * @date 2017/7/26
 * @contact email:648129313@qq.com
 */

public class SearchExpertAdapter extends CommonAdapter<ExpertBean> {

    ExpertSearchContract.Presenter mPresenter;

    public SearchExpertAdapter(Context context, List<ExpertBean> datas, ExpertSearchContract.Presenter presenter) {
        super(context, R.layout.item_search_expert, datas);
        mPresenter = presenter;
    }

    @Override
    protected void convert(ViewHolder holder, ExpertBean expertBean, int position) {
        UserAvatarView ivHeadpic = holder.getView(R.id.iv_headpic);
        TextView tvName = holder.getView(R.id.tv_name);
        TextView tvDigCount = holder.getView(R.id.tv_dig_count);
        CheckBox subscrib = holder.getView(R.id.tv_expert_subscrib);
        FlowTagLayout ftlTags = holder.getView(R.id.ftl_tags);
        tvName.setText(expertBean.getName());
        int answerCount, digCount;
        answerCount = expertBean.getExtra() == null ? 0 : expertBean.getExtra().getAnswers_count();
        digCount = expertBean.getExtra() == null ? 0 : expertBean.getExtra().getLikes_count();
        tvDigCount.setText(String.format(Locale.getDefault(), mContext.getString(R.string.qa_publish_show_expert), answerCount, digCount));
        ConvertUtils.stringLinkConvert(tvDigCount, setLinks());
        ftlTags.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_NONE);
        List<UserTagBean> tagBeenList = expertBean.getTags();


        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setUser_id((long) expertBean.getId());
        userInfoBean.setFollower(expertBean.isFollower());
        userInfoBean.setName(expertBean.getName());
        userInfoBean.setVerified(expertBean.getVerified());
        boolean isJoined = expertBean.isFollower();
        userInfoBean.setFollower(isJoined);
        subscrib.setChecked(isJoined);
        subscrib.setText(isJoined ? getContext().getString(R.string.qa_topic_followed) : getContext().getString(R.string.qa_topic_follow));
        subscrib.setPadding(isJoined ? getContext().getResources().getDimensionPixelSize(R.dimen.spacing_small) : getContext().getResources().getDimensionPixelSize(R.dimen.spacing_normal), 0, 0, 0);
        subscrib.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPresenter.handleFollowUser(userInfoBean);
            subscrib.setText(userInfoBean.getFollower() ? getContext().getString(R.string.qa_topic_followed) :
                    getContext().getString(R.string.qa_topic_follow));
        });

        UserTagAdapter adapter = new UserTagAdapter(mContext);
        ftlTags.setAdapter(adapter);
        adapter.clearAndAddAll(tagBeenList);
        ftlTags.setOnTagSelectListener((parent, selectedList) -> {

        });
        ImageUtils.loadCircleUserHeadPic(userInfoBean, ivHeadpic);
    }

    private List<Link> setLinks() {
        List<Link> links = new ArrayList<>();
        Link numberCountLink = new Link(Pattern.compile("[0-9]+")).setTextColor(ContextCompat.getColor(getContext(), R.color
                .themeColor))
                .setTextColorOfHighlightedLink(ContextCompat.getColor(getContext(), R.color
                        .normal_for_assist_text))
                .setHighlightAlpha(.8f)
                .setUnderlined(false);
        links.add(numberCountLink);
        Link digCountLink = new Link(Pattern.compile("[0-9]+")).setTextColor(ContextCompat.getColor(getContext(), R.color
                .themeColor))
                .setTextColorOfHighlightedLink(ContextCompat.getColor(getContext(), R.color
                        .normal_for_assist_text))
                .setHighlightAlpha(.8f)
                .setUnderlined(false);
        links.add(digCountLink);
        return links;
    }

}
