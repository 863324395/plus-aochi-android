package com.zhiyicx.thinksnsplus.modules.q_a.qa_main.qa_listinfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkMetadata;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.config.MarkdownConfig;
import com.zhiyicx.baseproject.config.PayConfig;
import com.zhiyicx.baseproject.widget.UserAvatarView;
import com.zhiyicx.common.utils.ColorPhrase;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.answer.AnswerDetailsActivity;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.modules.q_a.detail.answer.AnswerDetailsFragment.BUNDLE_ANSWER;
import static com.zhiyicx.thinksnsplus.modules.q_a.detail.answer.AnswerDetailsFragment.BUNDLE_SOURCE_ID;

/**
 * @Author Jliuer
 * @Date 2017/08/15/15:44
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class QAListInfoAdapter extends CommonAdapter<QAListInfoBean> {

    private SpanTextClickable.SpanTextClickListener mSpanTextClickListener;


    public QAListInfoAdapter(Context context, int layoutId, List<QAListInfoBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, QAListInfoBean infoBean, int position) {
        ImageView imageView = holder.getImageViwe(R.id.item_info_imag);
        TextView titleView = holder.getTextView(R.id.item_info_title);
        titleView.setText(infoBean.getSubject());
        holder.setText(R.id.item_info_time, TimeUtils.getTimeFriendlyNormal(infoBean.getCreated_at()));
        holder.setText(R.id.item_info_count, String.format(Locale.getDefault(), mContext.getString(R.string.qa_show_topic_followed_content)
                , infoBean.getWatchers_count(), infoBean.getAnswers_count()));
        double rewardMoney = PayConfig.realCurrency2GameCurrency(infoBean.getAmount(), getRatio());
        if (rewardMoney > 9999) {

            String rewardstr = String.format(Locale.getDefault(), mContext.getString(R.string.qa_show_topic_followed_reward_str)
                    , "<" + ConvertUtils.numberConvert((int) rewardMoney) + ">");
            CharSequence chars = ColorPhrase.from(rewardstr).withSeparator("<>")
                    .innerColor(ContextCompat.getColor(mContext, R.color.withdrawals_item_enable))
                    .outerColor(ContextCompat.getColor(mContext, R.color.general_for_hint))
                    .format();
            ((TextView) holder.getView(R.id.item_info_reward)).setText(chars);
        } else {
            holder.setText(R.id.item_info_reward, String.format(Locale.getDefault(), mContext.getString(R.string.qa_show_topic_followed_reward)
                    , rewardMoney));
            ConvertUtils.stringLinkConvert(holder.getTextView(R.id.item_info_reward), setLinks());

        }
        holder.setVisible(R.id.item_info_reward, infoBean.getAmount() > 0 ? View.VISIBLE : View.GONE);
        ConvertUtils.stringLinkConvert(holder.getTextView(R.id.item_info_count), setLinks(infoBean), false);
        RelativeLayout contentView = holder.getView(R.id.rl_hotcomment_container);
        boolean isExcellent = infoBean.getExcellent() == 1;

        UserAvatarView userAvatarView = (UserAvatarView) contentView.findViewById(R.id.iv_head_icon);
        TextView contentTextView = (TextView) contentView.findViewById(R.id.item_info_hotcomment);

        titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, getExcellentTag(isExcellent), 0);

        int id = 0;

        try {
            id = RegexUtils.getImageIdFromMarkDown(MarkdownConfig.IMAGE_FORMAT, infoBean.getAnswer().getBody());
        } catch (Exception e) {

        }

        if (id > 0) {
            imageView.setVisibility(View.VISIBLE);
            RxView.clicks(imageView)
                    .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                    .subscribe(aVoid -> contentView.performClick());
            int w = DeviceUtils.getScreenWidth(mContext);
            int h = mContext.getResources().getDimensionPixelOffset(R.dimen.qa_info_iamge_height);
            imageView.getLayoutParams().width = w;
            imageView.getLayoutParams().height = h;
            String url = ImageUtils.imagePathConvertV2(id, w, h, ImageZipConfig.IMAGE_80_ZIP);
            try {
                Glide.with(mContext).load(url)
                        .override(w, h)
                        .placeholder(R.drawable.shape_default_image)
                        .error(R.drawable.shape_default_image)
                        .into(imageView);
            } catch (Exception e) {
                // 加载图片 context 被销毁了
            }
        } else {
            imageView.setVisibility(View.GONE);
        }
        contentView.setVisibility(infoBean.getAnswer() != null ? View.VISIBLE : View.GONE);
        if (infoBean.getAnswer() != null) {
            boolean canLook = infoBean.getAnswer().getCould();
            boolean isSelf = infoBean.getAnswer().getUser_id() == AppApplication.getMyUserIdWithdefault();
            boolean isAnonymity = infoBean.getAnswer().getAnonymity() == 1;
            String content = RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, infoBean.getAnswer().getBody());
            ImageUtils.loadCircleUserHeadPic(infoBean.getAnswer().getUser(), userAvatarView, isAnonymity && !isSelf);
            String prefix = (isAnonymity && !isSelf ? getContext().getString(R.string.qa_question_answer_anonymity_user)
                    : infoBean.getAnswer().getUser().getName()) + (isSelf && isAnonymity
                    ? mContext.getString(R.string.qa_question_answer_anonymity_current_user) : "") + "：";
            if (!canLook) {
                content = mContext.getString(R.string.words_holder);
            }
            content = prefix + content;
            RxView.clicks(contentView)
                    .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                    .subscribe(aVoid -> {
                        if (!infoBean.getAnswer().getCould() && mSpanTextClickListener != null) {
                            mSpanTextClickListener.onSpanClick(infoBean.getAnswer().getId(), position);
                            return;
                        }
                        Intent intent = new Intent(getContext(), AnswerDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(BUNDLE_ANSWER, infoBean.getAnswer());
                        bundle.putLong(BUNDLE_SOURCE_ID, infoBean.getAnswer().getId());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    });
            int w = getContext().getResources().getDimensionPixelOffset(R.dimen.headpic_for_question_list);
            LogUtils.d(content);
            contentTextView.setOnClickListener(v -> contentView.performClick());
            content = content.replaceAll(MarkdownConfig.NETSITE_FORMAT, MarkdownConfig.LINK_EMOJI + Link.DEFAULT_NET_SITE);
            makeSpan(contentTextView, w, w, content, infoBean.getAnswer().getId(), position, prefix.length(), canLook);
            ConvertUtils.stringLinkConvert(contentTextView, setLinks(infoBean.getAnswer().getBody(), content));
        }

    }

    private List<Link> setLinks(QAListInfoBean infoBean) {
        List<Link> links = new ArrayList<>();
        String reg = "\\b\\d+\\b";
        Link followCountLink = new Link(Pattern.compile(reg)).setTextColor(ContextCompat.getColor(getContext(), R.color
                .themeColor))
                .setTextColorOfHighlightedLink(ContextCompat.getColor(getContext(), R.color
                        .general_for_hint))
                .setHighlightAlpha(.8f)
                .setUnderlined(false);
        links.add(followCountLink);
        return links;
    }

    private List<Link> setLinks() {
        List<Link> links = new ArrayList<>();
        String reg = "\\d+\\.\\d+";
        Link rewardMoneyLink = new Link(Pattern.compile(reg)).setTextColor(ContextCompat.getColor(getContext(), R.color
                .withdrawals_item_enable))
                .setTextColorOfHighlightedLink(ContextCompat.getColor(getContext(), R.color
                        .general_for_hint))
                .setHighlightAlpha(.8f)
                .setUnderlined(false);
        links.add(rewardMoneyLink);
        return links;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public void setSpanTextClickListener(SpanTextClickable.SpanTextClickListener spanTextClickListener) {
        mSpanTextClickListener = spanTextClickListener;
    }

    protected int getExcellentTag(boolean isExcellent) {
        return 0;
    }

    protected int getRatio() {
        return 0;
    }

    private void makeSpan(TextView mTextView, int h, int w, String plainText,
                          long answer_id, int question_position, int start, boolean canLook) {

//        Spanned htmlText = Html.fromHtml(plainText);
        SpannableString mSpannableString = new SpannableString(plainText);

        int allTextStart = 0;
        int allTextEnd = plainText.length() - 1;

        int lines;
        float fontSpacing = mTextView.getPaint().getFontSpacing();
        lines = (int) (h / (fontSpacing));

        TextRoundSpan span = new TextRoundSpan(lines, w + 10);
        mSpannableString.setSpan(span, allTextStart, allTextEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextView.setSingleLine(!canLook);
        mTextView.setMaxLines(canLook ? 3 : 1);
        mTextView.setEllipsize(TextUtils.TruncateAt.END);
        if (!canLook) {
            SpanTextClickable clickable = new SpanTextClickable(answer_id, h / 3, question_position);
            clickable.setSpanTextClickListener(mSpanTextClickListener);
            SpanTextClickable.dealTextViewClickEvent(mTextView);
            mSpannableString.setSpan(clickable, start, mSpannableString.length(), Spannable
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        mTextView.setText(mSpannableString);

    }

    private List<Link> setLinks(String realContentText, String replacedContentText) {
        List<Link> links = new ArrayList<>();
        Link followCountLink = new Link(mContext.getString(R.string.qa_question_answer_anonymity_current_user)).setTextColor(ContextCompat.getColor
                (mContext, R.color.normal_for_assist_text))
                .setTextColorOfHighlightedLink(ContextCompat.getColor(mContext, R.color.general_for_hint))
                .setHighlightAlpha(.8f)
                .setUnderlined(false);
        links.add(followCountLink);

        if (replacedContentText.contains(Link.DEFAULT_NET_SITE)) {
            Link commentNameLink = new Link(MarkdownConfig.LINK_EMOJI + Link.DEFAULT_NET_SITE)
                    .setTextColor(ContextCompat.getColor(mContext, R.color
                            .themeColor))
                    .setLinkMetadata(LinkMetadata.builder()
                            .putString(LinkMetadata.METADATA_KEY_COTENT, realContentText)
                            .putSerializableObj(LinkMetadata.METADATA_KEY_TYPE, LinkMetadata.SpanType.NET_SITE)
                            .build())
                    .setTextColorOfHighlightedLink(ContextCompat.getColor(mContext, R.color
                            .general_for_hint))
                    .setHighlightAlpha(.8f)
                    .setOnClickListener((clickedText, linkMetadata) -> {
                        LogUtils.d(clickedText);
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(clickedText);
                        intent.setData(content_url);
                        mContext.startActivity(intent);
                    })
                    .setOnLongClickListener((clickedText, linkMetadata) -> {

                    })
                    .setUnderlined(false);
            links.add(commentNameLink);
        }

        return links;
    }

}
