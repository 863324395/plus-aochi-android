package com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.FastBlur;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.modules.shortvideo.helper.ZhiyiVideoView;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * @Describe 动态列表 五张图的时候的 item
 * @Author Jungle68
 * @Date 2017/2/22
 * @Contact master.jungle68@gmail.com
 */

public class DynamicListItemForShorVideo extends DynamicListBaseItem {

    /**
     * 动态列表图片数量
     */
    private static final int IMAGE_COUNTS = 1;
    /**
     * 当前列数
     */
    private static final int CURREN_CLOUMS = 1;

    private ZhiyiVideoView.ShareInterface mShareInterface;

    public DynamicListItemForShorVideo(Context context) {
        super(context);
        int maxWidth = context.getResources().getDimensionPixelOffset(R.dimen.dynamic_image_max_width);
        mImageContainerWith = mImageContainerWith > maxWidth ? maxWidth : mImageContainerWith;
    }

    public DynamicListItemForShorVideo(Context context, ZhiyiVideoView.ShareInterface shareInterface) {
        super(context);
        int maxWidth = context.getResources().getDimensionPixelOffset(R.dimen.dynamic_image_max_width);
        mImageContainerWith = mImageContainerWith > maxWidth ? maxWidth : mImageContainerWith;
        mShareInterface = shareInterface;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_dynamic_list_one_video;
    }

    @Override
    public int getImageCounts() {
        return IMAGE_COUNTS;
    }


    @Override
    public void convert(ViewHolder holder, final DynamicDetailBeanV2 dynamicBean, DynamicDetailBeanV2 lastT, int position, int itemCounts) {
        super.convert(holder, dynamicBean, lastT, position, itemCounts);
        initVideoView(holder, holder.getView(R.id.videoplayer), dynamicBean, position);
    }

    /**
     * 设置 imageview 点击事件，以及显示
     *
     * @param view        the target
     * @param dynamicBean item data
     * @param positon     image item position
     */
    private void initVideoView(final ViewHolder holder, JZVideoPlayerStandard view, final DynamicDetailBeanV2 dynamicBean, final int positon) {
        int with;
        int height;

        String videoUrl;
        DynamicDetailBeanV2.Video video = dynamicBean.getVideo();
        if (TextUtils.isEmpty(video.getUrl())) {

            videoUrl = String.format(ApiConfig.APP_DOMAIN + ApiConfig.FILE_PATH,
                    dynamicBean.getVideo().getVideo_id());
            if (view instanceof ZhiyiVideoView) {
                ZhiyiVideoView zhiyiVideoView = (ZhiyiVideoView) view;
                zhiyiVideoView.setShareInterface(mShareInterface);
            }
            with = video.getWidth();
            height = video.getHeight();
            view.getLayoutParams().height = height;
            Glide.with(mContext)
                    .load(video.getGlideUrl())
                    .override(with, height)
                    .placeholder(R.drawable.shape_default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.shape_default_image)
                    .listener(new RequestListener<GlideUrl, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Bitmap bitmap = FastBlur.blurBitmap(ConvertUtils.drawable2Bitmap(resource), resource.getIntrinsicWidth(), resource
                                    .getIntrinsicHeight());
                            view.setBackground(new BitmapDrawable(mContext.getResources(), bitmap));
                            return false;
                        }
                    })
                    .into(view.thumbImageView);
        } else {
            // 本地
            videoUrl = video.getUrl();
            with = video.getWidth();
            height = video.getHeight();

            view.getLayoutParams().height = height;
            Glide.with(mContext)
                    .load(video.getUrl())
                    .override(with, height)
                    .placeholder(R.drawable.shape_default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.shape_default_image)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                            Bitmap bitmap = FastBlur.blurBitmap(ConvertUtils.drawable2Bitmap(resource), resource.getIntrinsicWidth(), resource
                                    .getIntrinsicHeight());
                            view.setBackground(new BitmapDrawable(mContext.getResources(), bitmap));
                            return false;
                        }
                    })
                    .into(view.thumbImageView);

        }
        view.setUp(videoUrl, JZVideoPlayerStandard.SCREEN_WINDOW_LIST);
        if (JZVideoPlayerManager.getFirstFloor() != null
                && JZVideoPlayerManager.getFirstFloor().positionInList == positon
                && JZUtils.dataSourceObjectsContainsUri(JZVideoPlayerManager.getFirstFloor().dataSourceObjects, JZMediaManager.getCurrentDataSource())
                && !JZVideoPlayerManager.getCurrentJzvd().equals(view)) {

            JZVideoPlayer first = JZVideoPlayerManager.getFirstFloor();
            first.textureViewContainer.removeView(JZMediaManager.textureView);
            view.setState(first.currentState);
            view.addTextureView();
            JZVideoPlayerManager.setFirstFloor(view);
            view.startProgressTimer();
        }

        view.positionInList = positon;

    }

    @Override
    public boolean isForViewType(DynamicDetailBeanV2 item, int position) {
        return item.getVideo() != null;
    }

    @Override
    protected int getCurrenCloums() {
        return CURREN_CLOUMS;
    }
}

