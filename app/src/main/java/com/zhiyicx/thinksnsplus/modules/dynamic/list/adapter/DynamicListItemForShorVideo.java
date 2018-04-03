package com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.widget.imageview.FilterImageView;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DrawableProvider;
import com.zhiyicx.common.utils.FastBlur;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.modules.shortvideo.helper.CustomMediaPlayerAssertFolder;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

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

    public DynamicListItemForShorVideo(Context context) {
        super(context);
        int maxWidth = context.getResources().getDimensionPixelOffset(R.dimen.dynamic_image_max_width);
        mImageContainerWith = mImageContainerWith > maxWidth ? maxWidth : mImageContainerWith;
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
        initVideoView(holder, holder.getView(R.id.videoplayer), dynamicBean, 0, 1);
    }

    /**
     * 设置 imageview 点击事件，以及显示
     *
     * @param view        the target
     * @param dynamicBean item data
     * @param positon     image item position
     * @param part        this part percent of imageContainer
     */
    protected void initVideoView(final ViewHolder holder, JZVideoPlayerStandard view, final DynamicDetailBeanV2 dynamicBean, final int positon, int part) {
        int with;
        int height;

        DynamicDetailBeanV2.ImagesBean imageBean = dynamicBean.getImages().get(0);
        if (TextUtils.isEmpty(imageBean.getImgUrl())) {
            with = imageBean.getImageViewWidth();
            height = imageBean.getImageViewHeight();
            // 是否是长图
            view.getLayoutParams().height = height;
            Glide.with(mContext)
                    .load(imageBean.getGlideUrl())
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
                            view.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
                            return false;
                        }
                    })
                    .into(view.thumbImageView);
        } else {
            // 本地
            BitmapFactory.Options option = DrawableProvider.getPicsWHByFile(imageBean.getImgUrl());
            with = imageBean.getCurrentWith();
            if (option.outWidth == 0) {
                height = with;
            } else {
                height = with * option.outHeight / option.outWidth;
                height = height > mImageMaxHeight ? mImageMaxHeight : height;
            }
            if (height < DEFALT_IMAGE_HEIGHT) {
                height = DEFALT_IMAGE_HEIGHT;
            }
            view.getLayoutParams().height = height;
            Glide.with(mContext)
                    .load(imageBean.getImgUrl())
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
                            view.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
                            return false;
                        }
                    })
                    .into(view.thumbImageView);

        }
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(JZVideoPlayer.URL_KEY_DEFAULT, "file:///storage/emulated/0/DCIM/Camera/VID_20180402_104256.mp4");
        Object[] dataSourceObjects = new Object[2];
        dataSourceObjects[0] = map;
        dataSourceObjects[1] = this;
        view.setUp(dataSourceObjects, 0, JZVideoPlayerStandard.SCREEN_WINDOW_LIST, "tym");
        JZVideoPlayer.setMediaInterface(new CustomMediaPlayerAssertFolder());//进入此页面修改MediaInterface，让此页面的jzvd正常工作
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

