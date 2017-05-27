package com.zhiyicx.thinksnsplus.modules.information.infomain.container;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.config.TouristConfig;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.InfoTypeBean;
import com.zhiyicx.thinksnsplus.data.beans.InfoTypeMyCatesBean;
import com.zhiyicx.thinksnsplus.modules.information.adapter.ScaleTransitionPagerTitleView;
import com.zhiyicx.thinksnsplus.modules.information.infochannel.ChannelActivity;
import com.zhiyicx.thinksnsplus.modules.information.infomain.InfoMainContract;
import com.zhiyicx.thinksnsplus.modules.information.infomain.list.InfoListFragment;
import com.zhiyicx.thinksnsplus.modules.information.infosearch.SearchActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

import static com.zhiyicx.thinksnsplus.modules.information.infomain.list.InfoListFragment.BUNDLE_INFO_TYPE;

/**
 * @Author Jliuer
 * @Date 2017/03/03
 * @Email Jliuer@aliyun.com
 * @Description 资讯的分类
 */
public class InfoContainerFragment extends TSFragment<InfoMainContract.InfoContainerPresenter>
        implements InfoMainContract.InfoContainerView {

    @BindView(R.id.fragment_infocontainer_indoctor)
    MagicIndicator mFragmentInfocontainerIndoctor;
    @BindView(R.id.fragment_infocontainer_change)
    ImageView mFragmentInfocontainerChange;
    @BindView(R.id.fragment_infocontainer_content)
    ViewPager mFragmentInfocontainerContent;

    public static final String SUBSCRIBE_EXTRA = "mycates";
    protected static final int DEFAULT_OFFSET_PAGE = 3;
    public static final String RECOMMEND_INFO = "-1";
    public static final int REQUEST_CODE = 0;
    // 定义默认样式值
    private static final int DEFAULT_TAB_UNSELECTED_TEXTCOLOR = com.zhiyicx.baseproject.R.color
            .normal_for_assist_text;// 缺省的tab未选择文字

    // 缺省的tab被选择文字
    private static final int DEFAULT_TAB_SELECTED_TEXTCOLOR = com.zhiyicx.baseproject.R.color
            .important_for_content;

    // 缺省的tab文字大小
    private static final int DEFAULT_TAB_TEXTSIZE = com.zhiyicx.baseproject.R.integer
            .tab_text_size;

    // 缺省的tab之间的空白间距
    private static final int DEFAULT_TAB_MARGIN = com.zhiyicx.baseproject.R.integer.tab_margin;//

    // 缺省的tab的线和文字的边缘距离
    private static final int DEFAULT_TAB_PADDING = com.zhiyicx.baseproject.R.integer.tab_padding;

    // 缺省的tab的线的颜色
    private static final int DEFAULT_TAB_LINE_COLOR = com.zhiyicx.baseproject.R.color.themeColor;

    // 缺省的tab的线的高度
    private static final int DEFAULT_TAB_LINE_HEGIHT = com.zhiyicx.baseproject.R.integer
            .no_line_height;

    private List<String> mTitle;
    private List<Fragment> mFragments;
    private MyAdapter mMyAdapter;
    private InfoTypeBean mInfoTypeBean;
    private CommonNavigator mCommonNavigator;

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_infocontainer;
    }

    @Override
    protected void initView(View rootView) {
        mFragmentInfocontainerContent.setOffscreenPageLimit(DEFAULT_OFFSET_PAGE);
        mMyAdapter = new MyAdapter(getFragmentManager());
        initMagicIndicator(initTitles());
        initFragments();
        mFragmentInfocontainerContent.setAdapter(mMyAdapter);
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.information);
    }

    @Override
    protected int setRightImg() {
        return R.mipmap.ico_search;
    }

    @Override
    protected void setRightClick() {

        if (!TouristConfig.INFO_CAN_SEARCH && mPresenter.handleTouristControl()) {
            return;
        }
        startActivity(new Intent(getActivity(), SearchActivity.class));
    }

    @Override
    protected void initData() {
        mPresenter.getInfoType();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            mTitle.clear();
            mFragments.clear();
            mInfoTypeBean = data.getBundleExtra(SUBSCRIBE_EXTRA).getParcelable(SUBSCRIBE_EXTRA);

            Observable.from(mInfoTypeBean.getMy_cates())
                    .subscribe(new Action1<InfoTypeMyCatesBean>() {
                        @Override
                        public void call(InfoTypeMyCatesBean myCatesBean) {
                            mTitle.add(myCatesBean.getName());
                            mFragments.add(InfoListFragment.newInstance(myCatesBean.getId() + ""));
                        }
                    });
            mMyAdapter.notifyDataSetChanged();
            mCommonNavigator.notifyDataSetChanged();
        }

    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @OnClick(R.id.fragment_infocontainer_change)
    public void onClick() {
        Intent intent = new Intent(getActivity(), ChannelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_INFO_TYPE, mInfoTypeBean);
        intent.putExtra(BUNDLE_INFO_TYPE, bundle);
        startActivityForResult(intent, REQUEST_CODE);
        getActivity().overridePendingTransition(R.anim.slide_from_top_enter, R.anim
                .slide_from_top_quit);
    }

    @Override
    public void setInfoType(InfoTypeBean infoType) {
        mInfoTypeBean = infoType;
        mInfoTypeBean.getMy_cates().add(0, new InfoTypeMyCatesBean(-1L, getString(R.string
                .info_recommend)));
        for (InfoTypeMyCatesBean myCatesBean : infoType.getMy_cates()) {
            if (mInfoTypeBean.getMy_cates().indexOf(myCatesBean) != 0
                    && !mTitle.contains(myCatesBean.getName())) {
                LogUtils.d(myCatesBean.getName());
                mTitle.add(myCatesBean.getName());
                mFragments.add(InfoListFragment.newInstance(myCatesBean.getId() + ""));
            }
        }
        mMyAdapter.notifyDataSetChanged();
        initMagicIndicator(mTitle);
    }

    @Override
    public void setPresenter(InfoMainContract.InfoContainerPresenter infoContainerPresenter) {
        mPresenter = infoContainerPresenter;
    }

    protected List<String> initTitles() {
        if (mTitle == null) {
            mTitle = new ArrayList<>();
            mTitle.add(getString(R.string.info_recommend));
        }
        return mTitle;
    }

    protected List<Fragment> initFragments() {
        if (mFragments == null) {
            mFragments = new ArrayList<>();
            mFragments.add(InfoListFragment.newInstance(RECOMMEND_INFO));
        }
        return mFragments;
    }

    private void initMagicIndicator(final List<String> mStringList) {
        mFragmentInfocontainerIndoctor.setBackgroundColor(Color.WHITE);
        mCommonNavigator = new CommonNavigator(getActivity());
        mCommonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mStringList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {

                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView
                        (context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(context,
                        DEFAULT_TAB_UNSELECTED_TEXTCOLOR));

                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(context,
                        DEFAULT_TAB_SELECTED_TEXTCOLOR));

                simplePagerTitleView.setText(mStringList.get(index));

                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, context.getResources
                        ().getInteger(DEFAULT_TAB_TEXTSIZE));

                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFragmentInfocontainerContent.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_MATCH_EDGE);// 占满
                linePagerIndicator.setXOffset(UIUtil.dip2px(context, context.getResources()
                        .getInteger(DEFAULT_TAB_PADDING)));// 每个item边缘到指示器的边缘距离
                linePagerIndicator.setLineHeight(UIUtil.dip2px(context, context.getResources()
                        .getInteger(DEFAULT_TAB_LINE_HEGIHT)));
                linePagerIndicator.setColors(ContextCompat.getColor(context,
                        DEFAULT_TAB_LINE_COLOR));
                return linePagerIndicator;
            }
        });
        mFragmentInfocontainerIndoctor.setNavigator(mCommonNavigator);
        ViewPagerHelper.bind(mFragmentInfocontainerIndoctor, mFragmentInfocontainerContent);

    }

    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
