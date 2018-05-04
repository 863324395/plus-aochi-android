package com.zhiyicx.thinksnsplus.modules.chat.item;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.DrawableProvider;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.AnimationRectBean;
import com.zhiyicx.thinksnsplus.modules.gallery.GalleryActivity;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @author Catherine
 * @describe 图片消息 ,修改这 Jungle68  email:master.jungle68@gmail.com
 * @date 2018/1/8
 * @contact email:648129313@qq.com
 */

public class ChatRowPicture extends ChatBaseRow {
    private static final int DEFAULT_IMAGE_MINE_WITH = 200;
    private static final int DEFAULT_IMAGE_SIZE = 200;

    /**
     * 显示图片最大为屏幕 1/3
     */
    private int mMaxLocalImageWith;
    /**
     * 显示图片最大的高度，为最大宽度的20：9
     */
    private int mMaxImageHeight;


    private AppCompatImageView mIvChatContent;

    public ChatRowPicture(Context context, EMMessage message, int position, BaseAdapter adapter, ChatUserInfoBean chatUserInfoBean) {
        super(context, message, position, adapter, chatUserInfoBean);
        mMaxLocalImageWith = DeviceUtils.getScreenWidth(context) * 4 / 15;
        mMaxImageHeight = mMaxLocalImageWith * 16 / 9;
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.SEND ?
                R.layout.item_chat_list_send_picture : R.layout.item_chat_list_receive_picture, this);
    }

    @Override
    protected void onFindViewById() {
        super.onFindViewById();
        mIvChatContent = (AppCompatImageView) findViewById(R.id.iv_chat_content);
    }

    @Override
    protected void onSetUpView() {
        super.onSetUpView();
        try {
            EMImageMessageBody imageMessageBody = (EMImageMessageBody) message.getBody();
            int width;
            int height;
            // 图片地址
            String url;
            // 自己发送的,并且文件还存在
            boolean isUseLocalImage = message.direct() == EMMessage.Direct.SEND && !TextUtils.isEmpty(imageMessageBody.getLocalUrl()) && FileUtils
                    .isFileExists
                            (imageMessageBody.getLocalUrl());
            url = isUseLocalImage ? imageMessageBody.getLocalUrl() : imageMessageBody.getRemoteUrl();

            if (isUseLocalImage) {
                // 本地
                BitmapFactory.Options option = DrawableProvider.getPicsWHByFile(imageMessageBody.getLocalUrl());
                if (option.outWidth == 0) {
                    width = DEFAULT_IMAGE_SIZE;
                    height = DEFAULT_IMAGE_SIZE;
                } else {
                    width = option.outWidth;
                    height = option.outHeight;
                }
            } else {
                width = imageMessageBody.getWidth();
                height = imageMessageBody.getHeight();
            }
            // 给一个最小值
            if (width < DEFAULT_IMAGE_MINE_WITH) {
                height = DEFAULT_IMAGE_MINE_WITH * height / width;
                width = DEFAULT_IMAGE_MINE_WITH;
            }

            // 是否是宽高超出了限制
            boolean isBigImage = width > mMaxLocalImageWith || height > mMaxImageHeight;
            if (isBigImage) {

                boolean isLongImage = ImageUtils.isLongImage(height, width);
                if (isLongImage) {
                    // 长图
                    if (width > height) {
                        // 宽的长图
                        width = mMaxLocalImageWith;
                        int tmpHeight = width * 3 / 4;
                        height = height > tmpHeight ? tmpHeight : height;
                    } else {
                        // 高的长图
                        width = width > mMaxLocalImageWith ? mMaxLocalImageWith : width;
                        height = width * 4 / 3;
                    }
                } else {
                    if (width > height) {
                        height = height * mMaxLocalImageWith / width;
                        width = mMaxLocalImageWith;
                    } else {
                        width = width * mMaxImageHeight / height;
                        height = mMaxImageHeight;
                    }
                }
            }

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvChatContent.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            mIvChatContent.setLayoutParams(layoutParams);

            ImageUtils.loadImageDefault(mIvChatContent, url);

            int finalWidth = width;
            int finalHeight = height;
            RxView.clicks(mIvChatContent)
                    .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                    .subscribe(aVoid -> {
                        // 跳转查看大图
                        List<ImageBean> imageBeanList = new ArrayList<>();
                        ArrayList<AnimationRectBean> animationRectBeanArrayList
                                = new ArrayList<>();
                        ImageBean imageBean = new ImageBean();
                        imageBean.setImgUrl(url);
                        imageBean.setStorage_id(0);
                        imageBean.setWidth(finalWidth);
                        imageBean.setHeight(finalHeight);
                        imageBeanList.add(imageBean);
                        AnimationRectBean rect = AnimationRectBean.buildFromImageView(mIvChatContent);
                        animationRectBeanArrayList.add(rect);
                        GalleryActivity.startToGallery(getContext(), 0, imageBeanList,
                                animationRectBeanArrayList);
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onViewUpdate(EMMessage msg) {
        super.onViewUpdate(msg);
        switch (msg.status()) {
            case CREATE:
                break;
            case SUCCESS:
                onSetUpView();
                break;
            case FAIL:
                break;
            case INPROGRESS:
                break;
            default:
        }
    }

}
