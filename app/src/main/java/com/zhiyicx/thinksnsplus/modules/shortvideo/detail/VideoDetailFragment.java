package com.zhiyicx.thinksnsplus.modules.shortvideo.detail;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tym.shortvideo.media.VideoInfo;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.widget.DynamicCommentEmptyItem;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;

/**
 * @Author Jliuer
 * @Date 2018/03/29/11:12
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class VideoDetailFragment extends TSListFragment<VideoDetailContract.Presenter, DynamicCommentBean>
        implements VideoDetailContract.View {

    public static final String SOURCEID = "sourceid";

    public static VideoDetailFragment newInstance(Bundle bundle) {
        VideoDetailFragment fragment = new VideoDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected boolean isLayzLoad() {
        return true;
    }

    @Override
    protected MultiItemTypeAdapter<DynamicCommentBean> getAdapter() {
        MultiItemTypeAdapter<DynamicCommentBean> adapter = new MultiItemTypeAdapter<>(getContext(), mListDatas);
//        DynamicDetailCommentItem dynamicDetailCommentItem = new DynamicDetailCommentItem();
//        dynamicDetailCommentItem.setOnUserInfoClickListener(this);
//        dynamicDetailCommentItem.setOnCommentTextClickListener(this);
//        dynamicDetailCommentItem.setOnCommentResendListener(this);
//        adapter.addItemViewDelegate(dynamicDetailCommentItem);
        VideoDetailCommentItem videoDetailCommentItem = new VideoDetailCommentItem();
        DynamicCommentEmptyItem dynamicCommentEmptyItem = new DynamicCommentEmptyItem();
        adapter.addItemViewDelegate(dynamicCommentEmptyItem);
//        adapter.addItemViewDelegate(videoDetailCommentItem);
//        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        initHeaderView();

//        mRvList.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
//            @Override
//            public void onChildViewAttachedToWindow(View view) {
//                JZVideoPlayer.onChildViewAttachedToWindow(view, R.id.videoplayer);
//            }
//
//            @Override
//            public void onChildViewDetachedFromWindow(View view) {
//                JZVideoPlayer.onChildViewDetachedFromWindow(view);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    private void initHeaderView() {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setPath("file:///storage/emulated/0/Codec/tym/tym/tym_1522302572466.mp4");
        videoInfo.setName("tym");
        VideoHeaderView videoHeaderView = new VideoHeaderView(mActivity, videoInfo);
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        mHeaderAndFooterWrapper.addHeaderView(videoHeaderView.getVideoHeaderView());
        mRvList.setAdapter(mHeaderAndFooterWrapper);
        mHeaderAndFooterWrapper.notifyDataSetChanged();

        mRvList.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                JZVideoPlayer jzvd = (JZVideoPlayer) view.findViewById(R.id.videoplayer);
                if (jzvd != null && JZUtils.dataSourceObjectsContainsUri(jzvd.dataSourceObjects, JZMediaManager.getCurrentDataSource())) {
                    JZVideoPlayer currentJzvd = JZVideoPlayerManager.getCurrentJzvd();
                    if (currentJzvd != null && currentJzvd.currentScreen != JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
                        JZVideoPlayer.releaseAllVideos();
                    }
                }
            }
        });
    }
}
