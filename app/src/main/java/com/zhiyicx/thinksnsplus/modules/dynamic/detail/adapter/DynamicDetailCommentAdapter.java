package com.zhiyicx.thinksnsplus.modules.dynamic.detail.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.GlideImageConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.GlideCircleTransform;
import com.zhiyicx.baseproject.utils.ImageUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.i.OnUserInfoClickListener;
import com.zhiyicx.thinksnsplus.i.OnUserInfoLongClickListener;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/3/10
 * @Contact master.jungle68@gmail.com
 */

public class DynamicDetailCommentAdapter extends CommonAdapter<DynamicCommentBean> {
    private OnUserInfoClickListener mOnUserInfoClickListener;
    private OnUserInfoLongClickListener mOnUserInfoLongClickListener;

    public void setOnUserInfoClickListener(OnUserInfoClickListener onUserInfoClickListener) {
        mOnUserInfoClickListener = onUserInfoClickListener;
    }

    public void setOnUserInfoLongClickListener(OnUserInfoLongClickListener onUserInfoLongClickListener) {
        mOnUserInfoLongClickListener = onUserInfoLongClickListener;
    }

    public DynamicDetailCommentAdapter(Context context, int layoutId, List<DynamicCommentBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, DynamicCommentBean dynamicCommentBean, final int position) {
        holder.setText(R.id.tv_name, dynamicCommentBean.getCommentUser().getName());
        holder.setText(R.id.tv_time, TimeUtils.getTimeFriendlyNormal(dynamicCommentBean.getCommentUser().getCreated_at()));
        holder.setText(R.id.tv_content, setShowText(dynamicCommentBean, position));
        LinkBuilder.on((TextView) holder.getView(R.id.tv_content))
                .addLinks(setLiknks(dynamicCommentBean, position))
                .build();
        AppApplication.AppComponentHolder.getAppComponent()
                .imageLoader()
                .loadImage(holder.getConvertView().getContext(), GlideImageConfig.builder()
                        .url(ImageUtils.imagePathConvert(dynamicCommentBean.getCommentUser().getAvatar(), ImageZipConfig.IMAGE_26_ZIP))
                        .placeholder(R.drawable.shape_default_image_circle)
                        .transformation(new GlideCircleTransform(mContext))
                        .errorPic(R.drawable.shape_default_image_circle)
                        .imagerView((ImageView) holder.getView(R.id.iv_headpic))
                        .build()
                );
        setUserInfoClick(holder.getView(R.id.tv_name), dynamicCommentBean.getCommentUser());
        setUserInfoClick(holder.getView(R.id.iv_headpic), dynamicCommentBean.getCommentUser());

    }

    private void setUserInfoClick(View v, final UserInfoBean userInfoBean) {
        RxView.clicks(v).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (mOnUserInfoClickListener != null) {
                    mOnUserInfoClickListener.onUserInfoClick(userInfoBean);
                }
            }
        });
    }

    protected String setShowText(DynamicCommentBean dynamicCommentBean, int position) {
        return handleName(dynamicCommentBean);
    }

    protected List<Link> setLiknks(final DynamicCommentBean dynamicCommentBean, int position) {
        List<Link> links = new ArrayList<>();
        if (dynamicCommentBean.getReplyUser() != null && dynamicCommentBean.getReplyUser().getName() != null) {
            Link replyNameLink = new Link(dynamicCommentBean.getReplyUser().getName())
                    .setTextColor(ContextCompat.getColor(getContext(), R.color.important_for_content))                  // optional, defaults to holo blue
                    .setTextColorOfHighlightedLink(ContextCompat.getColor(getContext(), R.color.general_for_hint)) // optional, defaults to holo blue
                    .setHighlightAlpha(.5f)                                     // optional, defaults to .15f
                    .setUnderlined(false)                                       // optional, defaults to true
                    .setOnLongClickListener(new Link.OnLongClickListener() {
                        @Override
                        public void onLongClick(String clickedText) {
                            if (mOnUserInfoLongClickListener != null) {
                                mOnUserInfoLongClickListener.onUserInfoLongClick(dynamicCommentBean.getReplyUser());
                            }
                        }
                    })
                    .setOnClickListener(new Link.OnClickListener() {
                        @Override
                        public void onClick(String clickedText) {
                            // single clicked
                            if (mOnUserInfoClickListener != null) {
                                mOnUserInfoClickListener.onUserInfoClick(dynamicCommentBean.getReplyUser());
                            }
                        }
                    });
            links.add(replyNameLink);
        }


        return links;
    }

    /**
     * 处理名字的颜色与点击
     *
     * @param dynamicCommentBean
     * @return
     */
    private String handleName(DynamicCommentBean dynamicCommentBean) {
        String content = "";
        if (dynamicCommentBean.getReply_to_user_id() != 0) { // 当没有回复者时，就是回复评论
            content += " 回复 " + dynamicCommentBean.getReplyUser().getName() + " " + dynamicCommentBean.getComment_content();
        }
        return content;
    }


}
