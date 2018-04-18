package com.zhiyicx.thinksnsplus.modules.shortvideo.videostore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.tym.shortvideo.interfaces.SingleCallback;
import com.tym.shortvideo.media.VideoInfo;
import com.tym.shortvideo.recodrender.ParamsManager;
import com.tym.shortvideo.recordcore.CountDownManager;
import com.tym.shortvideo.utils.TrimVideoUtil;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DrawableProvider;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.common.utils.recycleviewdecoration.TGridDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.adapter.VideoGridViewAdapter;
import com.zhiyicx.thinksnsplus.modules.shortvideo.clipe.TrimmerActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.preview.PreviewActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.record.RecordActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow.POPUPWINDOW_ALPHA;

/**
 * @Author Jliuer
 * @Date 2018/03/28/14:14
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class VideoSelectFragment extends TSListFragment {

    private ActionPopupWindow mPopWindow;

    // 分批次加载预留
    private List<VideoInfo> copyData;

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
    protected boolean setUseCenterLoading() {
        return true;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.video_select);
    }

    @Override
    protected void initData() {
        super.initData();
        copyData = new ArrayList<>();
        mRvList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRvList.setPadding(20, 20, 0, 0);
        mRvList.setBackgroundColor(0xffffffff);

        TrimVideoUtil.getAllVideoFiles(mActivity, (videoInfos, integer) -> mActivity.runOnUiThread(() -> {
            copyData.addAll(videoInfos);
            mListDatas.clear();
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setPath(null);
            mListDatas.add(videoInfo);
            mListDatas.addAll(copyData);
            refreshData();
            closeLoadingView();
        }));
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        VideoGridViewAdapter adapter = new VideoGridViewAdapter(mActivity, R.layout.item_select_video, mListDatas);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                VideoInfo videoInfo = (VideoInfo) mListDatas.get(position);

                if (TextUtils.isEmpty(videoInfo.getPath())) {
                    // 自己去拍
                    startActivity(new Intent(mActivity, RecordActivity.class));
                    mActivity.finish();
                } else {
                    // 选择列表
                    ImageView coverView = (ImageView) view.findViewById(R.id.iv_cover);
                    if (coverView != null) {
                        Bitmap cover = ConvertUtils.drawable2BitmapWithWhiteBg(getContext()
                                , coverView.getDrawable(), R.mipmap.icon);
                        initReSendDynamicPopupWindow(videoInfo, cover);
                    }

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
        return new TGridDecoration(20, 20, true);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(mActivity, 4);
    }

    /**
     * 初始化重发动态选择弹框
     */
    private void initReSendDynamicPopupWindow(VideoInfo videoInfo, Bitmap cover) {
        if (mPopWindow == null) {
            mPopWindow = ActionPopupWindow.builder()
                    .item1Str(videoInfo.getDuration() >= 300000 ? "" : getString(R.string.direct_upload))
                    .item2Str(getString(R.string.edite_upload))
                    .bottomStr(getString(R.string.cancel))
                    .isOutsideTouch(true)
                    .isFocus(true)
                    .backgroundAlpha(POPUPWINDOW_ALPHA)
                    .with(getActivity())
                    .item1ClickListener(() -> {
                        mPopWindow.hide();

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        videoInfo.setCover(FileUtils.saveBitmapToFile(mActivity, cover, ParamsManager.VideoCover));
                        SendDynamicDataBean sendDynamicDataBean = new SendDynamicDataBean();
                        sendDynamicDataBean.setDynamicBelong(SendDynamicDataBean.NORMAL_DYNAMIC);
                        videoInfo.setNeedCompressVideo(true);
                        List<ImageBean> pic = new ArrayList<>();
                        ImageBean imageBean = new ImageBean();
                        imageBean.setImgUrl(videoInfo.getCover());
                        pic.add(imageBean);
                        sendDynamicDataBean.setDynamicPrePhotos(pic);

                        sendDynamicDataBean.setDynamicType(SendDynamicDataBean.VIDEO_TEXT_DYNAMIC);
                        sendDynamicDataBean.setVideoInfo(videoInfo);
                        SendDynamicActivity.startToSendDynamicActivity(getContext(), sendDynamicDataBean);
                        mActivity.finish();
                    })
                    .item2ClickListener(() -> {
                        mPopWindow.hide();
                        if (videoInfo.getDuration() <= CountDownManager.getInstance().getMinMilliSeconds()) {
                            ArrayList<String> arrayList = new ArrayList<>();
                            arrayList.add(videoInfo.getPath());
                            PreviewActivity.startPreviewActivity(mActivity, arrayList);
                        } else {
                            TrimmerActivity.startTrimmerActivity(mActivity, videoInfo);
                        }
                        mActivity.finish();
                    })
                    .bottomClickListener(() -> mPopWindow.hide())
                    .build();
        }
        mPopWindow.show();

    }

    @Override
    public void onDestroyView() {
        releasePop(mPopWindow);
        super.onDestroyView();

    }

    public void releasePop(PopupWindow popupWindow) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}
