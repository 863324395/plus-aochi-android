package com.zhiyicx.thinksnsplus.modules.gallery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.config.PathConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.progress.ProgressListener;
import com.zhiyicx.baseproject.impl.imageloader.glide.progress.ProgressModelLoader;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.impl.photoselector.Toll;
import com.zhiyicx.baseproject.widget.photoview.PhotoViewAttacher;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.baseproject.widget.popwindow.PayPopWindow;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.DrawableProvider;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.AnimationRectBean;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.utils.TransferImageAnimationUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;


/**
 * @author LiuChao
 * @describe
 * @date 2017/3/20
 * @contact email:450127106@qq.com
 */
public class GalleryPictureFragment extends TSFragment<GalleryConstract.Presenter> implements View.OnLongClickListener, PhotoViewAttacher
        .OnPhotoTapListener, GalleryConstract.View {
    @BindView(R.id.fl_image_contaienr)
    FrameLayout mFlImageContaienr;
    @BindView(R.id.iv_orin_pager)
    ImageView mIvOriginPager;
    @BindView(R.id.iv_pager)
    ImageView mIvPager;
    @BindView(R.id.pb_progress)
    ImageView mPbProgressImage;
    @BindView(R.id.tv_origin_photo)
    TextView mTvOriginPhoto;
    @BindView(R.id.tv_to_pay)
    TextView mTvToPay;
    @BindView(R.id.tv_to_vip)
    TextView mTvToVip;
    @BindView(R.id.ll_toll)
    LinearLayout mLlToll;
    @BindView(R.id.iv_gif_control)
    ImageView mIvGifControl;
    @Inject
    GalleryPresenter mGalleryPresenter;

    private PhotoViewAttacher mPhotoViewAttacherOrigin;
    private PhotoViewAttacher mPhotoViewAttacherNormal;
    private ImageBean mImageBean;
    private ActionPopupWindow mActionPopupWindow;
    private Context context;
    private int screenW;
    private boolean hasAnim = false;
    private PayPopWindow mPayPopWindow;
    private boolean mIsLoaded = false;

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected boolean needCenterLoadingDialog() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasAnim = getArguments().getBoolean("animationIn");

    }

    @Override
    protected void initView(View rootView) {
        context = getContext();
        screenW = DeviceUtils.getScreenWidth(context);
        mPhotoViewAttacherNormal = new PhotoViewAttacher(mIvPager);
        mPhotoViewAttacherOrigin = new PhotoViewAttacher(mIvOriginPager);
        mPhotoViewAttacherNormal.setOnPhotoTapListener(this);
        mPhotoViewAttacherOrigin.setOnPhotoTapListener(this);
        // 图片长按，保存
        mPhotoViewAttacherOrigin.setOnLongClickListener(this);
        mPhotoViewAttacherNormal.setOnLongClickListener(this);

        RxView.clicks(mTvOriginPhoto)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> loadOriginImage(mImageBean));
    }

    @Override
    protected void initData() {
        boolean firstOpenPage = getArguments().getBoolean("firstOpenPage");
        if (firstOpenPage) {
            ((AnimationDrawable) mPbProgressImage.getDrawable()).start();
        }
        Observable.create(subscriber -> {
            DaggerGalleryComponent
                    .builder()
                    .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                    .galleryPresenterModule(new GalleryPresenterModule(GalleryPictureFragment.this))
                    .build()
                    .inject(GalleryPictureFragment.this);
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        if (firstOpenPage) {
                            preLoadData();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });
    }

    /**
     * 预加载
     */
    public void preLoadData() {
        if (!mIsLoaded) {
            checkAndLoadImage();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            GalleryFragment galleryFragment = (GalleryFragment) getParentFragment();
            if (galleryFragment != null) {
                boolean firstOpenPage = getArguments().getBoolean("firstOpenPage");
                if (firstOpenPage) {
                    if (hasAnim) {
                        ObjectAnimator animator = galleryFragment.showBackgroundAnimate();
                        animator.start();
                    } else {
                        galleryFragment.showBackgroundImmediately();
                    }
                    getArguments().putBoolean("firstOpenPage", false);
                }
            }
        }
    }

    private void checkAndLoadImage() {
        final AnimationRectBean rect = getArguments().getParcelable("rect");
        mImageBean = getArguments() != null ? (ImageBean) getArguments().getParcelable("url") : null;
        assert mImageBean != null;

        double scale = screenW / mImageBean.getWidth();

        int height = (int) (mImageBean.getHeight() * scale);
        if (mImageBean.getWidth() > 0 && mImageBean.getHeight() > 0) {
            ViewGroup.LayoutParams params = mIvOriginPager.getLayoutParams();
            params.width = screenW;
            params.height = height < DeviceUtils.getScreenHeight(getContext()) ? DeviceUtils.getScreenHeight(getContext()) : height;
            mIvOriginPager.setLayoutParams(params);

            ViewGroup.LayoutParams norParams = mIvPager.getLayoutParams();
            norParams.width = screenW;
            norParams.height = height < DeviceUtils.getScreenHeight(getContext()) ? DeviceUtils.getScreenHeight(getContext()) : height;
            mIvPager.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = mIvOriginPager.getLayoutParams();
            params.width = screenW;
            params.height = DeviceUtils.getScreenHeight(getContext());
            mIvOriginPager.setLayoutParams(params);

            ViewGroup.LayoutParams norParams = mIvPager.getLayoutParams();
            norParams.width = screenW;
            norParams.height = DeviceUtils.getScreenHeight(getContext());
            mIvPager.setLayoutParams(params);
        }


        if (mImageBean.getImgUrl() != null && FileUtils.isFileExists(mImageBean.getImgUrl())) {
            // 本地图片不需要查看原图
            mTvOriginPhoto.setVisibility(View.GONE);
            // 本地图片不需要保存
            mPhotoViewAttacherOrigin.setOnLongClickListener(null);
            mPhotoViewAttacherNormal.setOnLongClickListener(null);
        }
        // 显示图片
        loadImage(mImageBean, rect);
        mIsLoaded = true;
        // 兼容查看长图不完整
        mFlImageContaienr.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom != 0 && v.getBottom() == bottom) {
                v.setTop(0);
                v.setBottom(bottom - top);
                v.invalidate();
            }
        });
    }

    @Override
    public ImageBean getCurrentImageBean() {
        return mImageBean;
    }

    @Override
    public void reLoadImage() {
        checkAndLoadImage();
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.item_gallery_photo;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        if (mActionPopupWindow == null) {
            mActionPopupWindow = ActionPopupWindow.builder()
                    .backgroundAlpha(1.0f)
                    .bottomStr(context.getString(R.string.cancel))
                    .item1Str(context.getString(R.string.save_to_photo))
                    .isOutsideTouch(true)
                    .isFocus(true)
                    .with((Activity) context)
                    .item1ClickListener(() -> {
                        mActionPopupWindow.hide();
                        if (mImageBean.getToll() != null && mImageBean.getToll().getToll_type_string().equals(Toll.DOWNLOAD_TOLL_TYPE)
                                && !mImageBean.getToll().getPaid()) {
                            initCenterPopWindow(R.string.buy_pay_downlaod_desc);
                            return;
                        }
                        saveImage();
                    })
                    .bottomClickListener(() -> mActionPopupWindow.hide())
                    .build();
        }

        mActionPopupWindow.show();
        return false;
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        if (context instanceof Activity) {
            if (!((Activity) context).isFinishing()) {
                ((Activity) context).onBackPressed();
            }
        }
    }

    private static final Interpolator INTERPOLATOR =
            new FastOutSlowInInterpolator();

    /**
     * 显示或者隐藏查看原图按钮
     *
     * @param isIn 是否是进入页面
     */
    public void showOrHideOriginBtn(boolean isIn) {
        // 如果查看原图按钮不可见也就没有必要控制显示隐藏
        if (mTvOriginPhoto.getVisibility() == View.VISIBLE) {
            if (isIn) {
                ViewCompat.animate(mTvOriginPhoto).alpha(1.0f).scaleX(1.0f).scaleY(1.0f)
                        .setDuration(300)
                        .setInterpolator(INTERPOLATOR).withLayer()
                        .start();
            } else {
                ViewCompat.animate(mTvOriginPhoto).alpha(0.0f).scaleX(0.0f).scaleY(0.0f)
                        .setDuration(100)
                        .setInterpolator(INTERPOLATOR).withLayer()
                        .start();
            }
        }

    }

    /**
     * 保存图片
     */
    public void saveImage() {
        // 通过GLide获取bitmap,有缓存读缓存
        GlideUrl glideUrl = ImageUtils.imagePathConvertV2(mImageBean.getStorage_id(), (int) mImageBean.getWidth(), (int) mImageBean.getHeight()
                , ImageZipConfig.IMAGE_100_ZIP, AppApplication.getTOKEN());
        Glide.with(getActivity())
                .load(glideUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        getSaveBitmapResultObservable(resource, glideUrl.toStringUrl());
                    }
                });
    }

    /**
     * 构造函数
     *
     * @param imageBean
     * @param rect
     * @param animationIn
     * @return
     */
    public static GalleryPictureFragment newInstance(ImageBean imageBean, AnimationRectBean rect,
                                                     boolean animationIn, boolean firstOpenPage) {
        GalleryPictureFragment fragment = new GalleryPictureFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("url", imageBean);
        bundle.putParcelable("rect", rect);
        bundle.putBoolean("animationIn", animationIn);
        bundle.putBoolean("firstOpenPage", firstOpenPage);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 加载图片
     *
     * @param imageBean
     * @param rect
     */
    private void loadImage(final ImageBean imageBean, final AnimationRectBean rect) {
        final Toll toll = mImageBean.getToll();
        final boolean canLook;
        canLook = toll == null || !(toll.getPaid() != null && !toll.getPaid() && toll.getToll_type_string().equals(Toll.LOOK_TOLL_TYPE));
        mLlToll.setVisibility(!canLook ? View.VISIBLE : View.GONE);
        final int w, h;
        ///        w = imageBean.getWidth() > screenW ? screenW : (int) imageBean.getWidth();
        w = screenW;
        h = imageBean.getWidth() == 0 ? 0 : (int) (w * imageBean.getHeight() / imageBean.getWidth());
        // 本地图片
        if (imageBean.getImgUrl() != null) {
            DrawableRequestBuilder local = Glide.with(context)
                    .load(imageBean.getImgUrl())
                    .error(R.drawable.shape_default_image)
                    .thumbnail(0.1f);
            local.into(new GallaryGlideDrawableImageViewTarget(rect));
        } else {
            // 缩略图
            DrawableRequestBuilder thumbnailBuilder = Glide
                    .with(context)
                    .load(new CustomImageSizeModelImp(imageBean) {
                        @Override
                        public GlideUrl requestGlideUrl() {
                            return ImageUtils.imagePathConvertV2(canLook, mImageBean.getStorage_id(), w, h,
                                    ImageUtils.isLongImage((float) imageBean.getHeight(), (float) imageBean.getWidth())
                                            || ImageUtils.imageIsGif(imageBean.getImgMimeType())
                                            ? ImageZipConfig.IMAGE_100_ZIP : ImageZipConfig
                                            .IMAGE_ZIP_BIG, AppApplication.getTOKEN());
                        }
                    }
                            .requestGlideUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            // // 不从网络读取原图(CACHE_ONLY_STREAM_LOADER) 尝试从缓存获取原图
            DrawableRequestBuilder requestBuilder = Glide.with(context)
                    .using(CACHE_ONLY_STREAM_LOADER)
                    .load(ImageUtils.imagePathConvertV2(mImageBean.getStorage_id(), 0, 0,
                            ImageZipConfig.IMAGE_100_ZIP))
                    // 加载缩略图，上一个页面已经缓存好了，直接读取
                    .thumbnail(thumbnailBuilder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.shape_default_image)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        // 没有缓存到原图
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            LogUtils.i(TAG + "加载原图失败");
                            if (mTvOriginPhoto != null) {
                                mTvOriginPhoto.setVisibility(View.VISIBLE);
                            }
                            if (!canLook) {
                                if (mTvOriginPhoto != null) {
                                    mTvOriginPhoto.setVisibility(View.GONE);
                                }
                                stopCenterLoading();
                                mPhotoViewAttacherNormal.update();
                                mLlToll.setVisibility(View.VISIBLE);
                            }

                            // 原图没有缓存，从cacheOnlyStreamLoader抛出异常，在这儿加载高清图
                            DrawableRequestBuilder builder = Glide.with(context)
                                    .load(
                                            ImageUtils.imagePathConvertV2(canLook, mImageBean.getStorage_id(), w, h,
                                                    ImageUtils.isLongImage((float) imageBean.getHeight(), (float) imageBean.getWidth())
                                                            || ImageUtils.imageIsGif(imageBean.getImgMimeType()) ? ImageZipConfig.IMAGE_100_ZIP :
                                                            ImageZipConfig.IMAGE_ZIP_BIG, AppApplication.getTOKEN())
                                    )
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .listener(new RequestListener<GlideUrl, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean
                                                isFirstResource) {
                                            LogUtils.i(TAG + "加载高清图失败:" + e);
                                            stopCenterLoading();
                                            mTvOriginPhoto.setText(getString(R.string.see_origin_photos_failure));
                                            mPhotoViewAttacherNormal.update();
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target,
                                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                                            LogUtils.i(TAG + "加载高清图成功");
                                            stopCenterLoading();

                                            // mPhotoViewAttacherNormal.update() 必须在图片设置上后才有效果
                                            Observable.timer(40, TimeUnit.MILLISECONDS)
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribeOn(Schedulers.io())
                                                    .subscribe(aLong -> mPhotoViewAttacherNormal.update());

                                            return false;
                                        }


                                    });
                            if (imageBean.getWidth() * imageBean.getHeight() != 0) {
                                builder.override(w, h);
                            }
                            if (mIvPager != null) {
                                builder.into(mIvPager);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean
                                isFromMemoryCache, boolean isFirstResource) {
                            // 只有获取load的图片才会走这儿，缩略图不会
                            LogUtils.i(TAG + "加载原图成功");
                            if (mTvOriginPhoto != null) {
                                mTvOriginPhoto.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    });

            if (imageBean.getWidth() * imageBean.getHeight() != 0) {
                requestBuilder.override(w, h);
            }

            requestBuilder.into(
                    ImageUtils.imageIsGif(imageBean.getImgMimeType()) ? new GallaryGlideDrawableImageViewTarget(rect) : new GallarySimpleTarget(rect)
            );

        }
    }

    private void stopCenterLoading() {
        if (mPbProgressImage != null) {
            ((AnimationDrawable) mPbProgressImage.getDrawable()).stop();
            mPbProgressImage.setVisibility(View.GONE);
        }
    }

    // 加载原图:
    private void loadOriginImage(ImageBean imageBean) {
        final int w, h;
        w = screenW;
        h = (int) (w * imageBean.getHeight() / imageBean.getWidth());
        // 禁止点击查看原图按钮
        mTvOriginPhoto.setClickable(false);
        // 刚点击查看原图，可能会有一段时间，进行重定位请求，所以立即设置进度
        mTvOriginPhoto.setText("0%");
        Glide.with(context)
                .using(new ProgressModelLoader(new MyImageLoadHandler(this)
                        , AppApplication.getTOKEN()))
                .load(ImageUtils.imagePathConvertV2(imageBean.getStorage_id(), w, h, ImageZipConfig.IMAGE_100_ZIP))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.shape_default_image)
                .error(R.drawable.shape_default_image)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        LogUtils.i("loadOriginImage  onException");
                        // 如果通过okhttp查看原图，失败，在这儿接收异常
                        mTvOriginPhoto.setText(getString(R.string.see_origin_photos_failure));
                        // 查看失败可以再次点击
                        mTvOriginPhoto.setClickable(true);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        return false;
                    }
                })

                .into(new SimpleTarget<GlideDrawable>() {
                          @Override
                          public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                              mTvOriginPhoto.setText(R.string.completed);
                              mIvOriginPager.setImageDrawable(resource);
                              mPhotoViewAttacherOrigin.update();
                              mIvOriginPager.setVisibility(View.VISIBLE);
                              // 直接隐藏掉图片会有闪烁的效果，通过判断图片渲染成功后，隐藏，平滑过渡
                              Runnable runnable = () -> {
                                  while (mIvOriginPager.getDrawable() != null) {
                                      mIvPager.setVisibility(View.GONE);
                                      mTvOriginPhoto.setVisibility(View.GONE);
                                      break;
                                  }
                              };
                              runnable.run();

                          }
                      }
                );
    }

    /**
     * 退出动画，在返回操作中调用
     *
     * @param backgroundAnimator
     */
    public void animationExit(ObjectAnimator backgroundAnimator) {
        backgroundAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (getActivity() != null) {
                    getActivity().finish();
                    getActivity().overridePendingTransition(-1, -1);
                }
            }


            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (mIvOriginPager.getVisibility() == View.VISIBLE) {
                    mIvOriginPager.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
                } else {
                    mIvPager.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
                }

            }
        });
        // 高清图片可见，那就高清图片退出动画
        if (mIvPager.getVisibility() == View.VISIBLE) {
            // 图片处于放大状态，先让它复原
            if (Math.abs(mPhotoViewAttacherNormal.getScale() - 1.0f) > 0.1f) {
                mPhotoViewAttacherNormal.setScale(1, true);
                return;
            }
            // 退出隐藏查看原图按钮，防止显示在透明背景上
            mTvOriginPhoto.setVisibility(View.GONE);
            AnimationRectBean rect = getArguments().getParcelable("rect");
            TransferImageAnimationUtil.animateClose(backgroundAnimator, rect, mIvPager);

        }
        // 原图可见，退出就是用原图
        if (mIvOriginPager.getVisibility() == View.VISIBLE) {
            // 图片处于放大状态，先让它复原
            if (Math.abs(mPhotoViewAttacherOrigin.getScale() - 1.0f) > 0.1f) {
                mPhotoViewAttacherOrigin.setScale(1, true);
                return;
            }
            // 退出隐藏查看原图按钮，防止显示在透明背景上
            mTvOriginPhoto.setVisibility(View.GONE);
            AnimationRectBean rect = getArguments().getParcelable("rect");
            TransferImageAnimationUtil.animateClose(backgroundAnimator, rect, mIvOriginPager);
        }
    }

    /**
     * 进入动画，在加载图片后调用
     *
     * @param rect
     */
    private void startInAnim(final AnimationRectBean rect) {
        final Runnable endAction = new Runnable() {
            @Override
            public void run() {
                mIvPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("mFlImageContaienr.getTop() = " + mFlImageContaienr.getTop());
                        System.out.println("mFlImageContaienr.getBottom() = " + mFlImageContaienr.getBottom());
                    }
                }, 1000);
                LogUtils.i("startInAnim" + "endAction");
            }
        };
        TransferImageAnimationUtil.startInAnim(rect, mIvPager, endAction, mFlImageContaienr);
    }

    /**
     * 通过Rxjava在io线程中处理保存图片的逻辑，得到返回结果，否则会阻塞ui
     */
    private void getSaveBitmapResultObservable(final Bitmap bitmap, final String url) {

        Observable.just(1)// 不能empty否则map无法进行转换
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> {// .subscribeOn(Schedulers.io())  Animators may only be run on Looper threads
//                    mSavingTSnackbar = TSnackbar.make(mSnackRootView, , TSnackbar.LENGTH_INDEFINITE)
//                            .setPromptThemBackground(Prompt.SUCCESS)
//                            .addIconProgressLoading(0, true, false)
//                            .setMinHeight(0, getResources().getDimensionPixelSize(R.dimen.toolbar_and_statusbar_height));
//                    mSavingTSnackbar.show();
                    showSnackLoadingMessage(getString(R.string.save_pic_ing));
                })
                .map(integer -> {
                    String imgName = ConvertUtils.getStringMD5(url) + ".jpg";
                    String imgPath = PathConfig.PHOTO_SAVA_PATH;
                    return DrawableProvider.saveBitmap(bitmap, imgName, imgPath);
                })
                // subscribeOn & doOnSubscribe 的特殊性质
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    switch (result) {
                        case "-1":
                            result = getString(R.string.save_failure1);
                            break;
                        case "-2":
                            result = getString(R.string.save_failure2);
                            break;
                        default:
                            File file = new File(result);
                            if (file.exists()) {
                                result = getString(R.string.save_success) + result;
                                FileUtils.insertPhotoToAlbumAndRefresh(context, file);
                                result = getString(R.string.save_success);
                            }
                    }
                    showSnackSuccessMessage(result);
                });
    }

    @OnClick({R.id.tv_to_pay, R.id.tv_to_vip, R.id.iv_gif_control})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_to_pay:
                initCenterPopWindow(R.string.buy_pay_desc);
                break;
            case R.id.tv_to_vip:

                break;

            case R.id.iv_gif_control:

                break;
            default:
                break;
        }
    }

    /**
     * 支持 gif 的 加载
     */
    private class GallaryGlideDrawableImageViewTarget extends GlideDrawableImageViewTarget {
        private AnimationRectBean rect;

        GallaryGlideDrawableImageViewTarget(AnimationRectBean rect) {
            super(mIvPager);
            this.rect = rect;

        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            super.onResourceReady(resource, glideAnimation);
            stopCenterLoading();
            mPhotoViewAttacherNormal.update();
            // 获取到模糊图进行放大动画
            if (hasAnim) {
                hasAnim = false;
                startInAnim(rect);
            }
        }
    }

    /**
     * 不支持 gif
     */
    private class GallarySimpleTarget extends SimpleTarget<GlideDrawable> {
        private AnimationRectBean rect;

        GallarySimpleTarget(AnimationRectBean rect) {
            super();
            this.rect = rect;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            if (resource == null) {
                return;
            }
            stopCenterLoading();

            if (mIvPager != null) {
                mIvPager.setImageDrawable(resource);
            }
            mPhotoViewAttacherNormal.update();
            // 获取到模糊图进行放大动画
            if (hasAnim) {
                hasAnim = false;
                startInAnim(rect);
            }
        }
    }


    private static final StreamModelLoader<String> CACHE_ONLY_STREAM_LOADER
            = (model, i, i1) -> new DataFetcher<InputStream>() {
        @Override
        public InputStream loadData(Priority priority) throws Exception {
            // 如果是从网络获取图片肯定会走这儿，直接抛出异常，缓存从其他方法获取
            throw new IOException("intercupt net by own");
        }

        @Override
        public void cleanup() {

        }

        @Override
        public String getId() {
            return model;
        }

        @Override
        public void cancel() {

        }
    };

    @Override
    public void onDestroy() {
        DeviceUtils.gc();
        dismissPop(mPayPopWindow);
        dismissPop(mActionPopupWindow);
        super.onDestroy();
    }

    private void initCenterPopWindow(int resId) {
        if (mPresenter == null) {
            return;
        }
        if (mPayPopWindow != null) {
            mPayPopWindow.show();
            return;
        }
        mPayPopWindow = PayPopWindow.builder()
                .with(getActivity())
                .isWrap(true)
                .isFocus(true)
                .isOutsideTouch(true)
                .buildLinksColor1(R.color.themeColor)
                .buildLinksColor2(R.color.important_for_content)
                .contentView(R.layout.ppw_for_center)
                .backgroundAlpha(1.0f)
                .buildDescrStr(String.format(getString(resId) + getString(R
                        .string.buy_pay_member), mImageBean.getToll().getToll_money(), mPresenter.getGoldName()))
                .buildLinksStr(getString(R.string.buy_pay_member))
                .buildTitleStr(getString(R.string.buy_pay))
                .buildItem1Str(getString(R.string.buy_pay_in))
                .buildItem2Str(getString(R.string.buy_pay_out))
                .buildMoneyStr(String.format(getString(R.string.buy_pay_integration), mImageBean.getToll()
                        .getToll_money()))
                .buildCenterPopWindowItem1ClickListener(() -> {
                    mPresenter.payNote(mImageBean.getFeed_id(), mImageBean.getPosition(), mImageBean.getToll().getPaid_node());
                    mPayPopWindow.hide();
                })
                .buildCenterPopWindowItem2ClickListener(() -> mPayPopWindow.hide())
                .buildCenterPopWindowLinkClickListener(new PayPopWindow
                        .CenterPopWindowLinkClickListener() {
                    @Override
                    public void onLongClick() {

                    }

                    @Override
                    public void onClicked() {

                    }
                })
                .build();
        mPayPopWindow.show();

    }

    /**
     * Instances of static inner classes do not hold an implicit
     * reference to their outer class.
     */
    private static class MyImageLoadHandler extends Handler {

        private final WeakReference<GalleryPictureFragment> mFragment;

        public MyImageLoadHandler(GalleryPictureFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            GalleryPictureFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onMessageHandle(msg);
            }
        }


    }

    private void onMessageHandle(Message msg) {
        // 这部分的图片，都是通过 OKHttp 从网络获取的，如果改图片从 glide缓 存中读取，不会经过这儿
        if (msg.what == ProgressListener.SEND_LOAD_PROGRESS && mTvOriginPhoto != null) {
            int totalReadBytes = msg.arg1;
            int lengthBytes = msg.arg2;
            int progressResult = (int) (((float) totalReadBytes / (float) lengthBytes) * 100);
            mTvOriginPhoto.setText(String.format(Locale.getDefault(), "%d%%", progressResult));
            LogUtils.i("progress-result:-->" + progressResult + " msg.arg1-->" + msg.arg1 + "  msg.arg2-->" +
                    msg.arg2 + " 比例-->" + progressResult + "%/" + "100%");
            if (progressResult == 100) {
                mTvOriginPhoto.setText(R.string.completed);
            }
        }
    }

}
