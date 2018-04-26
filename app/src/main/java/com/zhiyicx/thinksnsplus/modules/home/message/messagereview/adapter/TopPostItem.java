package com.zhiyicx.thinksnsplus.modules.home.message.messagereview.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.klinker.android.link_builder.Link;
import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.config.MarkdownConfig;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;
import com.zhiyicx.thinksnsplus.data.beans.CommentedBean;
import com.zhiyicx.thinksnsplus.data.beans.TopNewsCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.TopPostCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.TopPostListBean;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.post.CirclePostDetailActivity;
import com.zhiyicx.thinksnsplus.modules.home.message.messagereview.MessageReviewContract;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2017/12/22/13:09
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class TopPostItem extends BaseTopItem implements BaseTopItem.TopReviewEvetnInterface {

    public TopPostItem(Context context, MessageReviewContract.Presenter presenter) {
        super(context, presenter);
        setTopReviewEvetnInterface(this);
    }

    @Override
    public boolean isForViewType(BaseListBean item, int position) {
        return item instanceof TopPostListBean;
    }

    @Override
    public void convert(ViewHolder holder, BaseListBean baseListBean, BaseListBean lastT, int position, int itemCounts) {
        TopPostListBean postListBean = (TopPostListBean) baseListBean;

        // 加载内容
        if (postListBean.getPost().getImages() != null && !postListBean.getPost().getImages().isEmpty()) {
            holder.setVisible(R.id.fl_image_container, View.VISIBLE);
            String url;
            holder.setVisible(R.id.iv_video_icon, View.GONE);
            url = ImageUtils.imagePathConvertV2(postListBean.getPost().getImages()
                            .get(0).getFile_id()
                    , mContext.getResources().getDimensionPixelOffset(R.dimen.headpic_for_user_center)
                    , mContext.getResources().getDimensionPixelOffset(R.dimen.headpic_for_user_center)
                    , ImageZipConfig.IMAGE_38_ZIP);
            Glide.with(holder.getConvertView().getContext())
                    .load(url)
                    .error(R.drawable.shape_default_error_image)
                    .into((ImageView) holder.getView(R.id.iv_detail_image));
        } else {
            holder.setVisible(R.id.fl_image_container, View.GONE);
        }
        holder.setText(R.id.tv_deatil, postListBean.getPost().getSummary());


        ImageUtils.loadCircleUserHeadPic(postListBean.getCommentUser(), holder.getView(R
                .id.iv_headpic));

        TextView review_flag = holder.getTextView(R.id.tv_review);
        if (postListBean.getStatus() == TopPostCommentListBean.TOP_REVIEW) {
            review_flag.setTextColor(holder.itemView.getResources().getColor(R.color
                    .dyanmic_top_flag));
            review_flag.setText(holder.itemView.getResources().getString(R.string.review));
            review_flag.setBackgroundResource(R.drawable.shape_bg_circle_box_radus_green);
        } else {
            review_flag.setBackground(null);
            if (postListBean.getStatus() == TopPostCommentListBean.TOP_REFUSE) {
                review_flag.setTextColor(holder.itemView.getResources().getColor(R.color.message_badge_bg));
                review_flag.setText(holder.itemView.getResources().getString(R.string.review_refuse));
            } else {
                review_flag.setTextColor(holder.itemView.getResources().getColor(R.color.general_for_hint));
                review_flag.setText(holder.itemView.getResources().getString(R.string.review_approved));
            }
        }

        if (postListBean.getPost() == null || postListBean.getPost() == null) {
            holder.setText(R.id.tv_deatil, holder.getConvertView().getResources().getString(R
                    .string.review_content_deleted));
            holder.setText(R.id.tv_content, String.format(Locale.getDefault(),
                    holder.itemView.getContext().getString(R.string
                            .stick_type_group_message), " "));
            review_flag.setTextColor(holder.itemView.getResources().getColor(R.color
                    .message_badge_bg));
            review_flag.setText(holder.itemView.getResources().getString(postListBean
                    .getPost() == null ?
                    R.string.review_dynamic_deleted : R.string.review_comment_deleted));
        } else {
            String commentBody = postListBean.getPost().getTitle();
            holder.setText(R.id.tv_content, String.format(Locale.getDefault(),
                    holder.itemView.getContext().getString(R.string
                            .stick_type_group_message), TextUtils.isEmpty(commentBody) ? "" +
                            " " : commentBody));
            List<Link> links = setLinks(holder.itemView.getContext());
            if (!links.isEmpty()) {
                ConvertUtils.stringLinkConvert(holder.getView(R.id.tv_content), links);
            }
            holder.setText(R.id.tv_deatil, postListBean.getPost().getSummary());
        }

        holder.setTextColorRes(R.id.tv_name, R.color.important_for_content);
        holder.setText(R.id.tv_name, postListBean.getCommentUser().getName());
        holder.setText(R.id.tv_time, TimeUtils.getTimeFriendlyNormal(postListBean.getUpdated_at()) + "    想用" + postListBean.getAmount
                () + mPresenter.getGoldName() + "置顶" + postListBean.getDay() + "天");


        // 响应事件
        RxView.clicks(holder.getView(R.id.tv_name))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> toUserCenter(holder.itemView.getContext(),
                        postListBean.getCommentUser()));
        RxView.clicks(holder.getView(R.id.iv_headpic))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> toUserCenter(holder.itemView.getContext(),
                        postListBean.getCommentUser()));
        RxView.clicks(holder.getView(R.id.tv_content))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> holder.itemView.performClick());


        RxView.clicks(holder.getView(R.id.fl_detial))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (postListBean.getPost() == null) {
                        initInstructionsPop(R.string.review_content_deleted);
                        return;
                    }
                    toDetail(postListBean.getPost(), false);
                });

        RxView.clicks(review_flag)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                .subscribe(aVoid -> {
                    handleReview(position, postListBean);
                });
        RxView.clicks(holder.itemView)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                .subscribe(aVoid -> {
                    handleReview(position, postListBean);
                });
    }

    private void handleReview(int position, TopPostListBean postListBean) {
        if (postListBean.getStatus() == TopPostCommentListBean.TOP_REVIEW
                && postListBean.getPost() != null) {
            initReviewPopWindow(postListBean, position);
        }
    }

    @Override
    public void onReviewApprovedClick(BaseListBean data, int position) {
        TopPostListBean postListBean = (TopPostListBean) data;
        postListBean.setExpires_at(TimeUtils.millis2String(System.currentTimeMillis() +
                1000000));
        postListBean.setStatus(TopNewsCommentListBean.TOP_SUCCESS);
        BaseListBean result = postListBean;
        mPresenter.approvedTopComment(postListBean.getPost().getId(),
                0, 0, result, position);
    }

    @Override
    public void onReviewRefuseClick(BaseListBean data, int position) {
        TopPostListBean postListBean = (TopPostListBean) data;
        postListBean.setStatus(TopNewsCommentListBean.TOP_REFUSE);
        mPresenter.refuseTopComment(postListBean.getPost().getId().intValue(), data, position);
    }

    @Override
    protected void toDetail(CommentedBean commentedBean) {
        super.toDetail(commentedBean);
    }

    protected void toDetail(CirclePostListBean postListBean, boolean isLookMoreComment) {
        CirclePostDetailActivity.startActivity(mContext, postListBean, isLookMoreComment, true);
    }
}
