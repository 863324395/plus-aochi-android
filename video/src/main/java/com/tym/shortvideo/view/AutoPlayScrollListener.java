package com.tym.shortvideo.view;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.jzvd.JZVideoPlayerStandard;

/**
 * @author Jliuer
 * @Date 18/04/03 13:49
 * @Email Jliuer@aliyun.com
 * @Description 监听recycleView滑动状态，自动播放可见区域内的第一个视频
 */
public abstract class AutoPlayScrollListener extends RecyclerView.OnScrollListener {

    private int firstVisibleItem = 0;
    private int firstCompoleteVisibleItem = 0;
    private int lastCompoleteVisibleItem = 0;
    private int lastVisibleItem = 0;
    private int visibleCount = 0;

    /**
     * 被处理的视频状态标签
     */
    private enum VideoTagEnum {

        /**
         * 自动播放视频
         */
        TAG_AUTO_PLAY_VIDEO,

        /**
         * 暂停视频
         */
        TAG_PAUSE_VIDEO
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                if (canAutoPlay()) {
                    autoPlayVideo(recyclerView, VideoTagEnum.TAG_AUTO_PLAY_VIDEO);
                }
            default:
                // 滑动时暂停视频 autoPlayVideo(recyclerView, VideoTagEnum.TAG_PAUSE_VIDEO);
                break;
        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            firstVisibleItem = linearManager.findFirstVisibleItemPosition();
            firstCompoleteVisibleItem = linearManager.findFirstCompletelyVisibleItemPosition();
            lastVisibleItem = linearManager.findLastVisibleItemPosition();
            lastCompoleteVisibleItem = linearManager.findLastCompletelyVisibleItemPosition();
            visibleCount = lastVisibleItem - firstVisibleItem + 1;
        }

    }

    /**
     * 循环遍历 可见区域的播放器
     * 然后通过 getLocalVisibleRect(rect)方法计算出哪个播放器完全显示出来
     * <p>
     * getLocalVisibleRect相关链接：http://www.cnblogs.com/ai-developers/p/4413585.html
     *
     * @param recyclerView
     * @param handleVideoTag 视频需要进行状态
     */
    private void autoPlayVideo(RecyclerView recyclerView, VideoTagEnum handleVideoTag) {
        // 当前正在播放的 view
        JZVideoPlayerStandard currentPlayView = null;
        // 准备播放的 view
        JZVideoPlayerStandard nextplayView = null;
        for (int i = 0; i < visibleCount; i++) {
            if (recyclerView != null && recyclerView.getChildAt(i) != null && recyclerView.getChildAt(i).findViewById(getPlayerViewId()) != null) {
                JZVideoPlayerStandard playView = (JZVideoPlayerStandard) recyclerView.getChildAt(i).findViewById(getPlayerViewId());
                Rect playViewRect = new Rect();
                playView.getLocalVisibleRect(playViewRect);
                if (playView.currentState == JZVideoPlayerStandard.CURRENT_STATE_PLAYING) {
                    currentPlayView = playView;
                    int current = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i));
                    // 当前正在播放的 view 少于 1/3 的可见高度
                    if (playViewRect.bottom - playViewRect.top < (float) currentPlayView.getHeight() / 3f) {
                        currentPlayView.startButton.callOnClick();
                        JZVideoPlayerStandard view = null;
                        if (current + 1 == firstCompoleteVisibleItem) {
                            View itemView = recyclerView.getChildAt(i + 1);
                            if (itemView != null) {
                                view = (JZVideoPlayerStandard) itemView.findViewById(getPlayerViewId());
                            }
                        } else if (current - 1 == lastCompoleteVisibleItem) {
                            View itemView = recyclerView.getChildAt(i - 1);
                            if (itemView != null) {
                                view = (JZVideoPlayerStandard) itemView.findViewById(getPlayerViewId());
                            }
                        }
                        // 滑动方向上的第一个完全可见的 view
                        if (view != null) {
                            view.startVideo();
                        }

                    }
                }
                int videoheight = playView.getHeight();
                if (playViewRect.top == 0 && playViewRect.bottom == videoheight && nextplayView == null) {
                    nextplayView = playView;
                }

            }
        }
        if (currentPlayView == null && nextplayView != null) {
            if (nextplayView.currentState == JZVideoPlayerStandard.CURRENT_STATE_PAUSE) {
                nextplayView.startButton.callOnClick();
            } else {
                nextplayView.startVideo();
            }
        }

    }

    /**
     * 视频状态处理
     *
     * @param handleVideoTag     视频需要进行状态
     * @param homeGSYVideoPlayer JZVideoPlayer播放器
     */
    private void handleVideo(VideoTagEnum handleVideoTag, JZVideoPlayerStandard homeGSYVideoPlayer) {
        switch (handleVideoTag) {
            case TAG_AUTO_PLAY_VIDEO:
                if (homeGSYVideoPlayer.currentState != JZVideoPlayerStandard.CURRENT_STATE_PLAYING) {
                    // 进行播放
                    homeGSYVideoPlayer.startVideo();
                }
                break;
            case TAG_PAUSE_VIDEO:
                if (homeGSYVideoPlayer.currentState != JZVideoPlayerStandard.CURRENT_STATE_PAUSE) {
                    // 模拟点击,暂停视频
                    homeGSYVideoPlayer.startButton.callOnClick();
                }
                break;
            default:
                break;
        }
    }

    public abstract int getPlayerViewId();

    public abstract boolean canAutoPlay();
}