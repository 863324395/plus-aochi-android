package com.zhiyicx.thinksnsplus.modules.personal_center.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.GlideImageConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.GlideCircleBoundTransform;
import com.zhiyicx.common.utils.ColorPhrase;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.common.utils.ZoomView;
import com.zhiyicx.common.utils.imageloader.core.ImageLoader;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

/**
 * @author LiuChao
 * @describe 个人中心头部
 * @date 2017/3/8
 * @contact email:450127106@qq.com
 */

public class PersonalCenterHeaderViewItem {
    /**********************************
     * headerView控件
     ********************************/
    private FrameLayout fl_cover_contaner;// 封面图的容器
    private ImageView iv_background_cover;// 封面
    private ImageView iv_head_icon;// 用户头像
    private TextView tv_user_name;// 用户名
    private TextView tv_user_intro;// 用户简介
    private TextView tv_user_follow;// 用户关注数量
    private TextView tv_user_fans;// 用户粉丝数量

    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private View headerView;

    public PersonalCenterHeaderViewItem(Activity activity, RecyclerView recyclerView, View headerView) {
        mActivity = activity;
        mRecyclerView = recyclerView;
        this.headerView = headerView;
    }

    public void initHeaderView() {
        initHeaderViewUI(headerView);
       // mHeaderAndFooterWrapper.addHeaderView(headerView);
       // mRecyclerView.setAdapter(mHeaderAndFooterWrapper);
       // mHeaderAndFooterWrapper.notifyDataSetChanged();
    }

    public void initHeaderViewData(UserInfoBean userInfoBean) {
        ImageLoader imageLoader = AppApplication.AppComponentHolder.getAppComponent().imageLoader();
        // 显示头像
        imageLoader.loadImage(mActivity, GlideImageConfig.builder()
                .url(String.format(ApiConfig.IMAGE_PATH, userInfoBean.getAvatar(), 50))
                .placeholder(R.drawable.shape_default_image_circle)
                .errorPic(R.drawable.shape_default_image_circle)
                .imagerView(iv_head_icon)
                .transformation(new GlideCircleBoundTransform(mActivity))
                .build());
        // 设置用户名
        tv_user_name.setText(userInfoBean.getName());
        // 设置简介
        tv_user_intro.setText(userInfoBean.getIntro());

        // 设置关注人数
        String followContent = "关注 " + "<" + 100 + ">";
        CharSequence followString = ColorPhrase.from(followContent).withSeparator("<>")
                .innerColor(ContextCompat.getColor(mActivity, R.color.themeColor))
                .outerColor(ContextCompat.getColor(mActivity, R.color.normal_for_assist_text))
                .format();
        tv_user_follow.setText(followString);

        // 设置粉丝人数
        String fansContent = "粉丝 " + "<" + 204 + ">";
        CharSequence fansString = ColorPhrase.from(fansContent).withSeparator("<>")
                .innerColor(ContextCompat.getColor(mActivity, R.color.themeColor))
                .outerColor(ContextCompat.getColor(mActivity, R.color.normal_for_assist_text))
                .format();
        tv_user_fans.setText(fansString);
        //mHeaderAndFooterWrapper.notifyDataSetChanged();
    }

    private void initHeaderViewUI(View headerView) {
        LinearLayout.LayoutParams headerLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerView.setLayoutParams(headerLayoutParams);
        fl_cover_contaner = (FrameLayout) headerView.findViewById(R.id.fl_cover_contaner);
        iv_background_cover = (ImageView) headerView.findViewById(R.id.iv_background_cover);
        iv_head_icon = (ImageView) headerView.findViewById(R.id.iv_head_icon);
        tv_user_name = (TextView) headerView.findViewById(R.id.tv_user_name);
        tv_user_intro = (TextView) headerView.findViewById(R.id.tv_user_intro);
        tv_user_follow = (TextView) headerView.findViewById(R.id.tv_user_follow);
        tv_user_fans = (TextView) headerView.findViewById(R.id.tv_user_fans);
        // 高度为屏幕宽度一半加上20dp
        int width = UIUtils.getWindowWidth(mActivity);
        int height = UIUtils.getWindowWidth(mActivity) / 2 + mActivity.getResources().getDimensionPixelSize(R.dimen.spacing_large);
        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        fl_cover_contaner.setLayoutParams(containerLayoutParams);
        // 添加头部放缩
        //new ZoomView(fl_cover_contaner, mActivity, mRecyclerView, width, height).initZoom();

    }
}
