package com.zhiyicx.thinksnsplus.modules.dynamic.list.adapterv2;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.GlideImageConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.GlideCircleTransform;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.widget.DynamicListMenuView;
import com.zhiyicx.common.base.BaseApplication;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.TextViewUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.common.utils.imageloader.core.ImageLoader;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicToolBean;
import com.zhiyicx.thinksnsplus.i.OnUserInfoClickListener;
import com.zhiyicx.thinksnsplus.widget.comment.DynamicListCommentView;
import com.zhiyicx.thinksnsplus.widget.comment.DynamicNoPullRecycleView;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.data.beans.DynamicToolBean.STATUS_DIGG_FEED_CHECKED;

/**
 * @Describe 动态列表适配器基类
 * requirement document :{@see https://github.com/zhiyicx/thinksns-plus-document/blob/master/document/%E5%8A%A8%E6%80%81%E6%A8%A1%E5%9D%97.md}
 * design document  {@see https://github.com/zhiyicx/thinksns-plus-document/blob/master/document/TS%2B%E8%A7%86%E8%A7%89%E8%A7%84%E8%8C%83%202.0/TS%2B%E8%A7%86%E8%A7%89%E8%A7%84%E8%8C%83%202.0.pdf}
 * @Author Jungle68
 * @Date 2017/1/6
 * @Contact master.jungle68@gmail.com
 */

public class DynamicListBaseItem implements ItemViewDelegate<DynamicDetailBeanV2> {
    protected final String TAG = this.getClass().getSimpleName();
    private static final int CURREN_CLOUMS = 0;
    private final int mWidthPixels; // 屏幕宽度
    private final int mMargin; // 图片容器的边距
    protected final int mDiverwith; // 分割先的宽高
    protected final int mImageContainerWith; // 图片容器最大宽度
    protected final int mImageMaxHeight; // 单张图片最大高度
    protected ImageLoader mImageLoader;
    protected Context mContext;
    private boolean showToolMenu = true;// 是否显示工具栏:默认显示
    private boolean showCommentList = true;// 是否显示评论内容:默认显示
    private boolean showReSendBtn = true;// 是否显示重发按钮

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        mOnImageClickListener = onImageClickListener;
    }

    protected OnImageClickListener mOnImageClickListener; // 图片点击监听

    public void setOnUserInfoClickListener(OnUserInfoClickListener onUserInfoClickListener) {
        mOnUserInfoClickListener = onUserInfoClickListener;
    }

    protected OnUserInfoClickListener mOnUserInfoClickListener; // 用户信息点击监听

    public void setOnMenuItemClickLisitener(OnMenuItemClickLisitener onMenuItemClickLisitener) {
        mOnMenuItemClickLisitener = onMenuItemClickLisitener;
    }

    protected OnMenuItemClickLisitener mOnMenuItemClickLisitener; // 工具栏被点击


    public void setOnReSendClickListener(OnReSendClickListener onReSendClickListener) {
        mOnReSendClickListener = onReSendClickListener;
    }

    protected OnReSendClickListener mOnReSendClickListener;

    public void setOnCommentClickListener(DynamicListCommentView.OnCommentClickListener
                                                  onCommentClickListener) {
        mOnCommentClickListener = onCommentClickListener;
    }

    protected DynamicListCommentView.OnCommentClickListener mOnCommentClickListener;

    protected DynamicListCommentView.OnMoreCommentClickListener mOnMoreCommentClickListener;

    public void setOnCommentStateClickListener(DynamicNoPullRecycleView
                                                       .OnCommentStateClickListener
                                                       onCommentStateClickListener) {
        mOnCommentStateClickListener = onCommentStateClickListener;
    }

    protected DynamicNoPullRecycleView.OnCommentStateClickListener mOnCommentStateClickListener;

    public void setOnMoreCommentClickListener(DynamicListCommentView.OnMoreCommentClickListener
                                                      onMoreCommentClickListener) {
        mOnMoreCommentClickListener = onMoreCommentClickListener;
    }

    private int mTitleMaxShowNum;
    private int mContentMaxShowNum;


    public DynamicListBaseItem(Context context) {
        mContext = context;
        mImageLoader = AppApplication.AppComponentHolder.getAppComponent().imageLoader();
        mTitleMaxShowNum = mContext.getResources().getInteger(R.integer
                .dynamic_list_title_max_show_size);
        mContentMaxShowNum = mContext.getResources().getInteger(R.integer
                .dynamic_list_content_max_show_size);
        mWidthPixels = DeviceUtils.getScreenWidth(context);
        mMargin = 2 * context.getResources().getDimensionPixelSize(R.dimen
                .dynamic_list_image_marginright);
        mDiverwith = context.getResources().getDimensionPixelSize(R.dimen.spacing_small);
        mImageContainerWith = mWidthPixels - mMargin;
        mImageMaxHeight = mImageContainerWith * 4 / 3; // 最大高度是最大宽度的4/3 保持 宽高比 3：4
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_dynamic_list_zero_image;
    }

    @Override
    public boolean isForViewType(DynamicDetailBeanV2 item, int position) {
        // 当本地和服务器都没有图片的时候，使用
        return item.getFeed_mark() != null && (item.getImages() != null && item.getImages().size() == getImageCounts());
    }

    /**
     * 获取图片数量
     *
     * @return
     */
    protected int getImageCounts() {
        return CURREN_CLOUMS;
    }

    /**
     * @param holder
     * @param dynamicBean
     * @param lastT       android:descendantFocusability
     * @param position
     */
    @Override
    public void convert(ViewHolder holder, DynamicDetailBeanV2 dynamicBean, DynamicDetailBeanV2
            lastT,
                        final int position, int itemCounts) {
        try {
            String userIconUrl = String.format(ApiConfig.IMAGE_PATH, dynamicBean.getUserInfoBean
                    ().getAvatar(), ImageZipConfig.IMAGE_38_ZIP);
            mImageLoader.loadImage(mContext, GlideImageConfig.builder()
                    .url(userIconUrl)
                    .placeholder(R.mipmap.pic_default_portrait1)
                    .transformation(new GlideCircleTransform(mContext))
                    .errorPic(R.mipmap.pic_default_portrait1)
                    .imagerView((ImageView) holder.getView(R.id.iv_headpic))
                    .build());
            holder.setText(R.id.tv_name, dynamicBean.getUserInfoBean().getName());
            holder.setText(R.id.tv_time, TimeUtils.getTimeFriendlyNormal(dynamicBean
                    .getCreated_at()));
            holder.setVisible(R.id.tv_title, View.GONE);

            String content = dynamicBean.getFeed_content();
            TextView contentView = holder.getView(R.id.tv_content);
            try { // 置顶标识
                TextView topFlagView = holder.getView(R.id.tv_top_flag);
                topFlagView.setVisibility(View.GONE);
            } catch (Exception e) {
            }

            if (TextUtils.isEmpty(content)) {
                contentView.setVisibility(View.GONE);
            } else {
                if (content.length() > mContentMaxShowNum) {
                    content = content.substring(0, mContentMaxShowNum) + "...";
                }
                TextViewUtils.newInstance(contentView, content)
                        .setSpanTextColor(BaseApplication.getContext().getResources().getColor(R
                                .color.normal_for_assist_text))
                        .setPosition(0, content.length())
                        .disPlayText(true);
                contentView.setVisibility(View.VISIBLE);
            }
            setUserInfoClick(holder.getView(R.id.iv_headpic), dynamicBean);
            setUserInfoClick(holder.getView(R.id.tv_name), dynamicBean);

            holder.setVisible(R.id.dlmv_menu, showToolMenu ? View.VISIBLE : View.GONE);
            // 分割线跟随工具栏显示隐藏
            holder.setVisible(R.id.v_line, showToolMenu ? View.VISIBLE : View.GONE);
            if (showToolMenu) {
                // 显示工具栏
                DynamicListMenuView dynamicListMenuView = holder.getView(R.id.dlmv_menu);
                dynamicListMenuView.setItemTextAndStatus(ConvertUtils.numberConvert(dynamicBean
                        .getFeed_digg_count()), dynamicBean.isHas_digg(), 0);
                dynamicListMenuView.setItemTextAndStatus(ConvertUtils.numberConvert(dynamicBean
                        .getFeed_comment_count()), false, 1);
                dynamicListMenuView.setItemTextAndStatus(ConvertUtils.numberConvert(dynamicBean
                                .getFeed_view_count() == 0 ? 1 : dynamicBean.getFeed_view_count()),
                        false, 2);// 浏览量没有 0
                // 控制更多按钮的显示隐藏
                dynamicListMenuView.setItemPositionVisiable(3, View.VISIBLE);
                // 设置工具栏的点击事件
                dynamicListMenuView.setItemOnClick(new DynamicListMenuView.OnItemClickListener() {
                    @Override
                    public void onItemClick(ViewGroup parent, View v, int menuPostion) {
                        if (mOnMenuItemClickLisitener != null) {
                            mOnMenuItemClickLisitener.onMenuItemClick(v, position, menuPostion);
                        }
                    }
                });
            }

            holder.setVisible(R.id.fl_tip, showReSendBtn ? View.VISIBLE : View.GONE);
            if (showReSendBtn) {
                // 设置动态发送状态
                if (dynamicBean.getState() == DynamicBean.SEND_ERROR) {
                    holder.setVisible(R.id.fl_tip, View.VISIBLE);
                } else {
                    holder.setVisible(R.id.fl_tip, View.GONE);
                }
                RxView.clicks(holder.getView(R.id.fl_tip))
                        .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)  // 两秒钟之内只取一个点击事件，防抖操作
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                if (mOnReSendClickListener != null) {
                                    mOnReSendClickListener.onReSendClick(position);
                                }
                            }
                        });
            }

            holder.setVisible(R.id.dcv_comment, showCommentList ? View.VISIBLE : View.GONE);
            if (showCommentList) {
                // 设置评论内容
                DynamicListCommentView comment = holder.getView(R.id.dcv_comment);
                if (dynamicBean.getComments() == null || dynamicBean.getComments().size() == 0) {
                    comment.setVisibility(View.GONE);
                } else {
                    comment.setVisibility(View.VISIBLE);
                }
                comment.setData(dynamicBean);
                comment.setOnCommentClickListener(mOnCommentClickListener);
                comment.setOnMoreCommentClickListener(mOnMoreCommentClickListener);
                comment.setOnCommentStateClickListener(mOnCommentStateClickListener);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setUserInfoClick(View view, final DynamicDetailBeanV2 dynamicBean) {
        RxView.clicks(view)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)  // 两秒钟之内只取一个点击事件，防抖操作
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mOnUserInfoClickListener != null) {
                            mOnUserInfoClickListener.onUserInfoClick(dynamicBean.getUserInfoBean());
                        }
                    }
                });
    }


    /**
     * 设置 imageview 点击事件，以及显示
     *
     * @param view        the target
     * @param dynamicBean item data
     * @param positon     image item position
     * @param part        this part percent of imageContainer
     */
    protected void initImageView(final ViewHolder holder, ImageView view, final DynamicDetailBeanV2 dynamicBean, final int positon, int part) {
        int propPart = getProportion(view, dynamicBean, part);
        String url = "";

        if (dynamicBean.getImages() != null && dynamicBean.getImages().size() > 0) {
            DynamicDetailBeanV2.ImagesBean imageBean = dynamicBean.getImages().get(positon);
            if (TextUtils.isEmpty(imageBean.getImgUrl())) {
                url = String.format(ApiConfig.IMAGE_PATH_V2, imageBean.getFile(), 50, 50, propPart);
            } else {
                url = imageBean.getImgUrl();
            }

        }
        LogUtils.e("initImageView:url:" + url);
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.shape_default_image)
                .error(R.drawable.shape_default_image)
                .into(view);

        if (dynamicBean.getImages() != null) {
            dynamicBean.getImages().get(positon).setPropPart(propPart);
        }
        RxView.clicks(view)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)  // 两秒钟之内只取一个点击事件，防抖操作
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mOnImageClickListener != null) {
                            mOnImageClickListener.onImageClick(holder, dynamicBean, positon);
                        }
                    }
                });

    }


    /**
     * 计算压缩比例
     *
     * @param view
     * @param dynamicBean
     * @param part        比例，总大小的份数
     * @return
     */
    protected int getProportion(ImageView view, DynamicDetailBeanV2 dynamicBean, int part) {
        /**
         * 一张图时候，需要对宽高做限制
         */
        int with;
        int height;
        int proportion; // 压缩比例
        int currentWith = getCurrenItemWith(part);
        DynamicDetailBeanV2.ImagesBean imageBean = dynamicBean.getImages().get(0);
        if (imageBean.getSize() == null || imageBean.getSize().isEmpty()) {
            return 70;
        }
        with = imageBean.getWidth() > currentWith ? currentWith : imageBean.getWidth();
        proportion = ((with / imageBean.getWidth()) * 100);
        height = with;
        return proportion;
    }

    /**
     * 获取当前item 的宽
     *
     * @param part
     * @return
     */
    protected int getCurrenItemWith(int part) {
        try {
            return (mImageContainerWith - (getCurrenCloums() - 1) * mDiverwith) / getCurrenCloums() * part;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    protected int getCurrenCloums() {
        return CURREN_CLOUMS;
    }


    /**
     * image interface
     */
    public interface OnImageClickListener {

        void onImageClick(ViewHolder holder, DynamicDetailBeanV2 dynamicBean, int position);
    }

    /**
     * like interface
     */
    public interface OnMenuItemClickLisitener {
        void onMenuItemClick(View view, int dataPosition, int viewPosition);
    }

    /**
     * resend interface
     */
    public interface OnReSendClickListener {
        void onReSendClick(int position);
    }

    public DynamicListBaseItem setShowToolMenu(boolean showToolMenu) {
        this.showToolMenu = showToolMenu;
        return this;
    }

    public DynamicListBaseItem setShowCommentList(boolean showCommentList) {
        this.showCommentList = showCommentList;
        return this;
    }

    public DynamicListBaseItem setShowReSendBtn(boolean showReSendBtn) {
        this.showReSendBtn = showReSendBtn;
        return this;
    }

}
