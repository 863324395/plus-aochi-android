package com.zhiyicx.thinksnsplus.modules.channel.group_dynamic.dig_list;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.GlideImageConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.GlideCircleTransform;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.baseproject.widget.imageview.FilterImageView;
import com.zhiyicx.common.utils.imageloader.core.ImageLoader;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.AuthBean;
import com.zhiyicx.thinksnsplus.data.beans.FollowFansBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterFragment;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @author Catherine
 * @describe
 * @date 2017/7/20
 * @contact email:648129313@qq.com
 */

public class GroupDigListAdapter extends CommonAdapter<FollowFansBean> {

    private GroupDigListContract.Presenter mPresenter;

    public GroupDigListAdapter(Context context, List<FollowFansBean> datas, GroupDigListContract.Presenter presenter) {
        super(context, R.layout.item_dig_list, datas);
        this.mPresenter = presenter;
    }

    @Override
    protected void convert(ViewHolder holder, FollowFansBean followFansBean, int position) {
        final FilterImageView filterImageView = holder.getView(R.id.iv_headpic);
        TextView tv_name = holder.getView(R.id.tv_name);
        TextView tv_content = holder.getView(R.id.tv_content);
        ImageView iv_follow = holder.getView(R.id.iv_follow);

        final UserInfoBean userInfoBean = followFansBean.getTargetUserInfo();
        if (userInfoBean != null) {
            tv_name.setText(userInfoBean.getName());
            tv_content.setText(userInfoBean.getIntro());
            // 显示用户头像
            ImageLoader imageLoader = AppApplication.AppComponentHolder.getAppComponent().imageLoader();
            int storegeId;
            String userIconUrl;
            try {
                storegeId = Integer.parseInt(userInfoBean.getAvatar());
                userIconUrl = ImageUtils.imagePathConvertV2(storegeId
                        , getContext().getResources().getDimensionPixelOffset(R.dimen.headpic_for_list)
                        , getContext().getResources().getDimensionPixelOffset(R.dimen.headpic_for_list)
                        , ImageZipConfig.IMAGE_38_ZIP);
            } catch (Exception e) {
                userIconUrl = userInfoBean.getAvatar();
            }
            imageLoader.loadImage(filterImageView.getContext(), GlideImageConfig.builder()
                    .imagerView(filterImageView)
                    .transformation(new GlideCircleTransform(filterImageView.getContext()))
                    .url(userIconUrl)
                    .placeholder(R.mipmap.pic_default_portrait1)
                    .errorPic(R.mipmap.pic_default_portrait1)
                    .build());
            RxView.clicks(holder.getConvertView())
                    .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                    .subscribe(aVoid -> PersonalCenterFragment.startToPersonalCenter(filterImageView.getContext(), userInfoBean));
        }
        // 如果当前列表包含了自己，就隐藏该关注按钮
        AuthBean authBean = AppApplication.getmCurrentLoginAuth();
        if (userInfoBean != null && userInfoBean.getUser_id() == authBean.getUser_id()) {
            iv_follow.setVisibility(View.GONE);
        } else {
            iv_follow.setVisibility(View.VISIBLE);
            // 设置关注状态
            int state = followFansBean.getFollowState();
            switch (state) {
                case FollowFansBean.UNFOLLOWED_STATE:
                    iv_follow.setImageResource(R.mipmap.detail_ico_follow);
                    break;
                case FollowFansBean.IFOLLOWED_STATE:
                    iv_follow.setImageResource(R.mipmap.detail_ico_followed);
                    break;
                case FollowFansBean.FOLLOWED_EACHOTHER_STATE:
                    iv_follow.setImageResource(R.mipmap.detail_ico_followed_eachother);
                    break;
                default:
            }

            // 设置关注状态点击事件
            RxView.clicks(iv_follow)
                    .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                    //.compose(.<Void>bindToLifecycle())
                    .subscribe(aVoid -> mPresenter.handleFollowUser(position, followFansBean));
        }
    }
}
