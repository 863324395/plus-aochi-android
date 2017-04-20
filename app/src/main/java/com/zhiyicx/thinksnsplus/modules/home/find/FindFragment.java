package com.zhiyicx.thinksnsplus.modules.home.find;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.tbruyelle.rxpermissions.Permission;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.widget.button.CombinationButton;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.baseproject.widget.popwindow.PermissionPopupWindow;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.channel.list.ChannelListActivity;
import com.zhiyicx.thinksnsplus.modules.information.infomain.InfoActivity;
import com.zhiyicx.thinksnsplus.modules.music_fm.music_album_list.MusicListActivity;
import com.zhiyicx.thinksnsplus.modules.settings.aboutus.CustomWEBActivity;
import com.zhiyicx.thinksnsplus.modules.settings.aboutus.CustomWEBFragment;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Describe 发现页面
 * @Author Jungle68
 * @Date 2017/1/5
 * @Contact master.jungle68@gmail.com
 */
public class FindFragment extends TSFragment {

    @BindView(R.id.find_info)
    CombinationButton mFindInfo;
    @BindView(R.id.find_chanel)
    CombinationButton mFindChanel;
    @BindView(R.id.find_active)
    CombinationButton mFindActive;
    @BindView(R.id.find_music)
    CombinationButton mFindMusic;
    @BindView(R.id.find_buy)
    CombinationButton mFindBuy;
    @BindView(R.id.find_person)
    CombinationButton mFindPerson;
    @BindView(R.id.find_nearby)
    CombinationButton mFindNearby;

    private ActionPopupWindow mActionPopupWindow;

    public FindFragment() {
    }

    public static FindFragment newInstance() {
        FindFragment fragment = new FindFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View rootView) {
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected boolean setUseStatusView() {
        return true;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_find;
    }

    @Override
    protected int setToolBarBackgroud() {
        return R.color.white;
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.find);
    }

    @Override
    protected int setLeftImg() {
        return 0;
    }

    @Override
    protected boolean usePermisson() {
        return true;
    }

    @OnClick({R.id.find_info, R.id.find_chanel, R.id.find_active, R.id.find_music, R.id.find_buy,
            R.id.find_person, R.id.find_nearby})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.find_info:
                startActivity(new Intent(getActivity(), InfoActivity.class));
                break;
            case R.id.find_chanel:
                startActivity(new Intent(getActivity(), ChannelListActivity.class));
                break;
            case R.id.find_active:
                break;
            case R.id.find_music:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(getContext())) {
                        startActivity(new Intent(getActivity(), MusicListActivity.class));
                    } else {
                        initPermissionPopUpWindow();
                        mActionPopupWindow.show();
                    }
                } else {
                    startActivity(new Intent(getActivity(), MusicListActivity.class));
                }
                break;
            case R.id.find_buy:
                Intent intent = new Intent(getActivity(), CustomWEBActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(CustomWEBFragment.BUNDLE_PARAMS_WEB_URL, ApiConfig.URL_JIPU_SHOP);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.find_person:
                break;
            case R.id.find_nearby:
                break;
            default:
                break;
        }
    }

    private void initPermissionPopUpWindow() {
        if (mActionPopupWindow != null) {
            return;
        }
        mActionPopupWindow = PermissionPopupWindow.builder()
                .permissionName(getString(com.zhiyicx.baseproject.R.string.windows_permission))
                .with(getActivity())
                .bottomStr(getString(com.zhiyicx.baseproject.R.string.cancel))
                .item1Str(getString(com.zhiyicx.baseproject.R.string.setting_windows_permission_hint))
                .item2Str(getString(com.zhiyicx.baseproject.R.string.setting_permission))
                .item2ClickListener(new ActionPopupWindow.ActionPopupWindowItem2ClickListener() {
                    @Override
                    public void onItem2Clicked() {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivity(intent);
                        mActionPopupWindow.hide();
                    }
                })
                .bottomClickListener(new ActionPopupWindow.ActionPopupWindowBottomClickListener() {
                    @Override
                    public void onBottomClicked() {
                        mActionPopupWindow.hide();
                    }
                })
                .isFocus(true)
                .isOutsideTouch(true)
                .backgroundAlpha(0.8f)
                .build();
    }
}
