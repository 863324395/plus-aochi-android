package com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.widget.imageview.FilterImageView;
import com.zhiyicx.common.utils.DrawableProvider;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.concurrent.TimeUnit;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2.ImagesBean.FILE_MIME_TYPE_GIF;

/**
 * @author Jliuer
 * @Date 2017/11/30/14:32
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CirclePostListItemForOneImage extends CirclePostListBaseItem {

    private static final int IMAGE_COUNTS = 1;// 动态列表图片数量
    private static final int CURREN_CLOUMS = 1; // 当前列数

    public CirclePostListItemForOneImage(Context context) {
        super(context);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_dynamic_list_one_image;
    }

    @Override
    public int getImageCounts() {
        return IMAGE_COUNTS;
    }

    @Override
    public void convert(ViewHolder holder, CirclePostListBean circlePostListBean, CirclePostListBean lastT, int position, int itemCounts) {
        super.convert(holder, circlePostListBean, lastT, position, itemCounts);
        initImageView(holder, holder.getView(R.id.siv_0), circlePostListBean, 0, 1);
    }

    /**
     * 设置 imageview 点击事件，以及显示
     *
     * @param view               the target
     * @param circlePostListBean item data
     * @param positon            image item position
     * @param part               this part percent of imageContainer
     */
    @Override
    protected void initImageView(final ViewHolder holder, FilterImageView view,
                                 final CirclePostListBean circlePostListBean, final int positon, int part) {
        /**
         * 一张图时候，需要对宽高做限制
         */
        int with;
        int height;
        int proportion; // 压缩比例
        int currentWith = getCurrenItemWith(part);
        CirclePostListBean.ImagesBean imageBean = circlePostListBean.getImages().get(0);

        if (TextUtils.isEmpty(imageBean.getImgUrl())) {
            with = currentWith;
            height = (with * imageBean.getHeight() / imageBean.getWidth());
            height = height > mImageMaxHeight ? mImageMaxHeight : height;
            proportion = ((with / imageBean.getWidth()) * 100);
            if (with * height == 0) {// 就怕是 0
                with = height = DEFALT_IMAGE_HEIGHT;
            }
            if (height < DEFALT_IMAGE_HEIGHT) {
                height = DEFALT_IMAGE_HEIGHT;
            }
            view.setLayoutParams(new LinearLayout.LayoutParams(with, height));
            // 是否是 gif
            view.setIshowGifTag(ImageUtils.imageIsGif(imageBean.getImgMimeType()));
            view.showLongImageTag(isLongImage(imageBean.getHeight(), imageBean.getWidth())); // 是否是长图

            Glide.with(mContext)
                    .load(ImageUtils.imagePathConvertV2(false, imageBean.getFile_id(), 0, 0,
                            100, AppApplication.getTOKEN()))
                    .asBitmap()
                    .override(with, height)
                    .placeholder(R.drawable.shape_default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.shape_default_image)
                    .into(view);
        } else {
            BitmapFactory.Options option = DrawableProvider.getPicsWHByFile(imageBean.getImgUrl());
//            with = option.outWidth > currentWith ? currentWith : option.outWidth;
            with = currentWith;

            if (option.outWidth == 0) {
                height = with;
                proportion = 100;
            } else {
                height = with * option.outHeight / option.outWidth;
                height = height > mImageMaxHeight ? mImageMaxHeight : height;
                proportion = ((with / option.outWidth) * 100);
                // 是否是 gif
                view.setIshowGifTag(ImageUtils.imageIsGif(option.outMimeType));
                view.showLongImageTag(isLongImage(option.outHeight, option.outWidth)); // 是否是长图
            }
            if (height < DEFALT_IMAGE_HEIGHT) {
                height = DEFALT_IMAGE_HEIGHT;
            }
            view.setLayoutParams(new LinearLayout.LayoutParams(with, height));

            Glide.with(mContext)
                    .load(imageBean.getImgUrl())
                    .asBitmap()
                    .override(with, height)
                    .placeholder(R.drawable.shape_default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.shape_default_image)
                    .into(view);
        }

        if (circlePostListBean.getImages() != null) {
            circlePostListBean.getImages().get(positon).setPropPart(proportion);
        }
        RxView.clicks(view)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)  // 两秒钟之内只取一个点击事件，防抖操作
                .subscribe(aVoid -> {
                    if (mOnImageClickListener != null) {
                        mOnImageClickListener.onImageClick(holder, circlePostListBean, positon);
                    }
                });
    }


    @Override
    protected int getCurrenCloums() {
        return CURREN_CLOUMS;
    }
}
