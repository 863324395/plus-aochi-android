package com.zhiyicx.baseproject.impl.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zhiyicx.baseproject.R;
import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.config.UmengConfig;
import com.zhiyicx.baseproject.widget.popwindow.RecyclerViewPopupWindow;
import com.zhiyicx.common.thridmanager.share.OnShareCallbackListener;
import com.zhiyicx.common.thridmanager.share.Share;
import com.zhiyicx.common.thridmanager.share.ShareContent;
import com.zhiyicx.common.thridmanager.share.SharePolicy;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_SHARE_DEFAULT;
import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;


/**
 * @Describe 友盟分享方针实现
 * @Author Jungle68
 * @Date 2016/12/20
 * @Contact master.jungle68@gmail.com
 */
public class UmengSharePolicyImpl implements SharePolicy, OnShareCallbackListener {
    private static final int SHARE_COLUMS = 5;// item 列数

    /**
     * 友盟初始化
     */
    static {
        PlatformConfig.setQQZone(UmengConfig.QQ_APPID, UmengConfig.QQ_SECRETKEY);
        PlatformConfig.setWeixin(UmengConfig.WEIXIN_APPID, UmengConfig.WEIXIN_SECRETKEY);
        PlatformConfig.setSinaWeibo(UmengConfig.SINA_APPID, UmengConfig.SINA_SECRETKEY, UmengConfig.SINA_RESULT_RUL);
    }

    private Context mContext;
    private ShareContent mShareContent;
    private RecyclerViewPopupWindow mRecyclerViewPopupWindow;

    public void setOnShareCallbackListener(OnShareCallbackListener onShareCallbackListener) {
        mOnShareCallbackListener = onShareCallbackListener;
    }

    OnShareCallbackListener mOnShareCallbackListener;

    public UmengSharePolicyImpl(Context mContext) {
        mOnShareCallbackListener = this;
        this.mContext = mContext;
        init(mContext);
    }

    public UmengSharePolicyImpl(ShareContent mShareContent, Context mContext) {
        this.mContext = mContext;
        this.mShareContent = mShareContent;
        init(mContext);
    }

    private void init(Context mContext) {
        UMShareAPI.get(mContext);
        Config.DEBUG = true;
        initSharePopupWindow();
    }

    public ShareContent getShareContent() {
        return mShareContent;
    }

    /**
     * 如果使用的是 qq 或者新浪精简版 jar，需要在您使用分享或授权的 Activity（ fragment 不行）中添加如下回调代码
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data, Context context) {
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 在使用分享或者授权的Activity中，重写onDestory()方法：
     *
     * @param activity
     */
    public static void onDestroy(Activity activity) {
        UMShareAPI.get(activity).release();
    }

    /**
     * 设置分享内容
     *
     * @param shareContent
     */
    @Override
    public void setShareContent(ShareContent shareContent) {
        mShareContent = shareContent;
    }

    /**
     * 微信朋友分享
     */
    @Override
    public void shareMoment(Activity activity, final OnShareCallbackListener l) {
        if (checkShareContent()) {
            return;
        }
        shareActionConfig(activity, l, SHARE_MEDIA.WEIXIN_CIRCLE);

    }


    /**
     * QQ分享
     */
    @Override
    public void shareQQ(Activity activity, OnShareCallbackListener l) {
        if (checkShareContent()) {
            return;
        }
        shareActionConfig(activity, l, SHARE_MEDIA.QQ);
    }

    /**
     * QQ空间分享
     */
    @Override
    public void shareZone(Activity activity, OnShareCallbackListener l) {
        if (checkShareContent()) {
            return;
        }
        shareActionConfig(activity, l, SHARE_MEDIA.QZONE);

    }

    /**
     * 微博分享
     */
    @Override
    public void shareWeibo(Activity activity, OnShareCallbackListener l) {
        if (checkShareContent()) {
            return;
        }
        shareActionConfig(activity, l, SHARE_MEDIA.SINA);
    }

    /**
     * 微信分享
     */
    @Override
    public void shareWechat(Activity activity, OnShareCallbackListener l) {
        if (checkShareContent()) {
            return;
        }
        shareActionConfig(activity, l, SHARE_MEDIA.WEIXIN);
    }


    /**
     * 分享弹框
     */
    @Override
    public void showShare(Activity activity) {

        mRecyclerViewPopupWindow.show();
    }

    /**
     * 检测分享内容是否为空
     *
     * @return
     */
    private boolean checkShareContent() {
        if (mShareContent == null) {
            throw new NullPointerException(mContext.getResources().getString(R.string.err_share_no_content));
        }
        return false;
    }

    /**
     * 配置分享内容
     *
     * @param activity
     * @param l
     * @param share_media
     */
    private void shareActionConfig(Activity activity, final OnShareCallbackListener l, SHARE_MEDIA share_media) {
        ShareAction shareAction = new ShareAction(activity);
        shareAction.setPlatform(share_media);
        UMImage image = null;
        if (!TextUtils.isEmpty(mShareContent.getContent())) {
            shareAction.withText(mShareContent.getContent());
        }
        if (!TextUtils.isEmpty(mShareContent.getImage())) {
            image = new UMImage(activity, mShareContent.getImage());
        }
        if (0 != mShareContent.getResImage()) {
            image = new UMImage(activity, mShareContent.getResImage());
        }
        if (null != mShareContent.getBitmap()) {
            image = new UMImage(activity, mShareContent.getBitmap());
        }
        if (null != mShareContent.getFile()) {
            image = new UMImage(activity, mShareContent.getFile());
        }
        if (!TextUtils.isEmpty(mShareContent.getUrl())) {
            UMWeb web = new UMWeb(mShareContent.getUrl());
//            UMWeb web = new UMWeb(APP_PATH_SHARE_DEFAULT); // 由于后台还未开发完毕，暂时使用
            if (!TextUtils.isEmpty(mShareContent.getTitle())) {
                web.setTitle(mShareContent.getTitle());//标题
            }
            if (!TextUtils.isEmpty(mShareContent.getContent())) {
                web.setDescription(mShareContent.getContent());
            }
            if (image != null) {
                web.setThumb(image);
            }
            shareAction.withMedia(web);
        } else {
            shareAction.withMedia(image);
        }
        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                if (l != null) {
                    Share share = changeShare(share_media);
                    l.onStart(share);
                }
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                if (l != null) {
                    Share share = changeShare(share_media);
                    l.onSuccess(share);
                }
                mRecyclerViewPopupWindow.hide();
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                if (l != null) {
                    Share share = changeShare(share_media);
                    l.onError(share, throwable);
                }
                mRecyclerViewPopupWindow.hide();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                if (l != null) {
                    Share share = changeShare(share_media);
//                    l.onCancel(share);
                }
                mRecyclerViewPopupWindow.hide();
            }
        });
        shareAction.share();
    }

    /**
     * 转换分享平台标识，方便修改三方时回调标识错误
     *
     * @param share_media
     * @return
     */
    private Share changeShare(SHARE_MEDIA share_media) {
        Share share = null;
        switch (share_media) {
            case QQ:
                share = Share.QQ;
                break;
            case QZONE:
                share = Share.QZONE;
                break;
            case WEIXIN:
                share = Share.WEIXIN;
                break;
            case WEIXIN_CIRCLE:
                share = Share.WEIXIN_CIRCLE;
                break;
            case SINA:
                share = Share.SINA;
                break;
            default:
                break;

        }
        return share;
    }

    @Override
    public void onStart(Share share) {
        LogUtils.i(" share start");
    }

    @Override
    public void onSuccess(Share share) {
        LogUtils.i(" share success");

    }

    @Override
    public void onError(Share share, Throwable throwable) {
        LogUtils.i(" share onError");
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Share share) {
        LogUtils.i(" share cancle");
    }

    private static class ShareBean {
        int image;
        String name;

        public ShareBean(int image, String name) {
            this.image = image;
            this.name = name;
        }
    }

    private void initSharePopupWindow() {
        List<ShareBean> mDatas = new ArrayList<>();
        ShareBean qq = new ShareBean(R.mipmap.detail_share_qq, mContext.getString(R.string.qq_share));
        ShareBean qZone = new ShareBean(R.mipmap.detail_share_zone, mContext.getString(R.string.qZone_share));
        ShareBean weChat = new ShareBean(R.mipmap.detail_share_wechat, mContext.getString(R.string.weChat_share));
        ShareBean weCircle = new ShareBean(R.mipmap.detail_share_friends, mContext.getString(R.string.weCircle_share));
        ShareBean weibo = new ShareBean(R.mipmap.detail_share_weibo, mContext.getString(R.string.weibo_share));
        mDatas.add(qq);
        mDatas.add(qZone);
        mDatas.add(weChat);
        mDatas.add(weCircle);
        mDatas.add(weibo);

        mRecyclerViewPopupWindow = RecyclerViewPopupWindow.Builder()
                .isOutsideTouch(true)
                .asGrid(SHARE_COLUMS)// 列数
                .itemSpacing(mContext.getResources().getDimensionPixelSize(R.dimen.spacing_mid)) // 间距
                .with((Activity) mContext)
                .adapter(new CommonAdapter<ShareBean>(mContext, R.layout.item_share_popup_window, mDatas) {
                    @Override
                    protected void convert(ViewHolder holder, ShareBean shareBean, final int position) {
                        holder.setImageResource(R.id.iv_share_type_image, shareBean.image);
                        holder.setText(R.id.iv_share_type_name, shareBean.name);
                        RxView.clicks(holder.getConvertView())
                                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                                .subscribe(new Action1<Void>() {
                                    @Override
                                    public void call(Void aVoid) {
                                        switch (position) {
                                            case 0:
                                                // QQ 和微信 该版本不提供网页支持，故提示安装应用
                                                if (UMShareAPI.get(mContext).isInstall((Activity) mContext, SHARE_MEDIA.QQ)) {

                                                    shareQQ((Activity) mContext, mOnShareCallbackListener);
                                                } else {
                                                    installThirdAppTip();

                                                }
                                                break;
                                            case 1:
                                                if (UMShareAPI.get(mContext).isInstall((Activity) mContext, SHARE_MEDIA.QQ) || (UMShareAPI.get
                                                        (mContext).isInstall((Activity) mContext, SHARE_MEDIA.QZONE))) {

                                                    shareZone((Activity) mContext, mOnShareCallbackListener);
                                                } else {
                                                    installThirdAppTip();

                                                }
                                                break;
                                            case 2:
                                                if (UMShareAPI.get(mContext).isInstall((Activity) mContext, SHARE_MEDIA.WEIXIN)) {

                                                    shareWechat((Activity) mContext, mOnShareCallbackListener);
                                                } else {
                                                    installThirdAppTip();

                                                }
                                                break;
                                            case 3:
                                                if (UMShareAPI.get(mContext).isInstall((Activity) mContext, SHARE_MEDIA.WEIXIN) || UMShareAPI.get
                                                        (mContext).isInstall((Activity) mContext, SHARE_MEDIA.WEIXIN_CIRCLE)) {

                                                    shareMoment((Activity) mContext, mOnShareCallbackListener);
                                                } else {
                                                    installThirdAppTip();
                                                }
                                                break;
                                            case 4:
//                                                if (UMShareAPI.get(mContext).isInstall((Activity) mContext, SHARE_MEDIA.SINA)) {

                                                    shareWeibo((Activity) mContext, mOnShareCallbackListener);
//                                                } else {
//                                                    installThirdAppTip();
//                                                }
                                                break;
                                            default:
                                        }
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                });

                    }
                })
                .iFocus(true)
                .build();
    }

    private void installThirdAppTip() {
        try {
            ((TSFragment) ((TSActivity) mContext).getContanierFragment()).showSnackErrorMessage
                    (mContext.getString(R
                            .string.please_install_app));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
