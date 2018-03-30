package com.zhiyicx.thinksnsplus.modules.shortvideo.videostore;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.tym.shortvideo.media.VideoInfo;
import com.tym.shortvideo.utils.TrimVideoUtil;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.utils.recycleviewdecoration.TGridDecoration;
import com.zhiyicx.common.utils.recycleviewdecoration.VideoSelectItemDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.shortvideo.adapter.VideoGridViewAdapter;
import com.zhiyicx.thinksnsplus.modules.shortvideo.clipe.TrimmerActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.record.RecordActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

/**
 * @Author Jliuer
 * @Date 2018/03/28/14:14
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class VideoSelectFragment extends TSListFragment {

    @Override
    protected boolean isLoadingMoreEnable() {
        return false;
    }

    @Override
    protected boolean isRefreshEnable() {
        return false;
    }

    @Override
    protected void onEmptyViewClick() {

    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.video_select);
    }

    @Override
    protected void initData() {
        super.initData();
        TrimVideoUtil.getAllVideoFiles(mActivity, (videoInfos, integer) -> {
            if (videoInfos.isEmpty()) {
                setEmptyViewVisiable(true);
            }
            mListDatas.clear();
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setPath(null);
            mListDatas.add(videoInfo);
            mListDatas.addAll(videoInfos);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        VideoGridViewAdapter adapter = new VideoGridViewAdapter(mActivity, R.layout.item_select_video, mListDatas);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                VideoInfo videoInfo = (VideoInfo) mListDatas.get(position);
                if (TextUtils.isEmpty(videoInfo.getPath())) {
                    startActivity(new Intent(mActivity, RecordActivity.class));
                }else{
                    TrimmerActivity.startTrimmerActivity(mActivity,videoInfo.getPath());
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        return adapter;
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new VideoSelectItemDecoration(20, 20);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(mActivity, 4);
    }
}
