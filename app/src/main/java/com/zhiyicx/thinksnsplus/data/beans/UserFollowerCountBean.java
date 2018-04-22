package com.zhiyicx.thinksnsplus.data.beans;

import com.google.gson.annotations.SerializedName;

/**
 * @author Jungle68
 * @describe
 * @date 2018/4/16
 * @contact master.jungle68@gmail.com
 */
public class UserFollowerCountBean {

    /**
     * user : {"following":1}
     */

    private UserBean user;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    /**
     * 消息通知，统计最新未读数
     */
    public static class UserBean {
        public static final String MESSAGE_TYPE_FOLLOWING = "following";
        public static final String MESSAGE_TYPE_LIKED = "liked";
        public static final String MESSAGE_TYPE_COMMENTED = "commented";
        public static final String MESSAGE_TYPE_SYSTEM = "system";
        public static final String MESSAGE_TYPE_NEWS_COMMENT_PINNED = "news-comment-pinned";
        public static final String MESSAGE_TYPE_FEED_COMMENT_PINNED = "feed-comment-pinned";

        /**
         * following : 1 最新粉丝数
         * liked : 1   最新点赞数
         * commented : 1 最新评论数
         * system : 1  最新系统消息数
         * news-comment-pinned : 1  最新资讯评论置顶数
         * feed-comment-pinned：1   最新动态评论置顶数
         */

        private int following;
        private int liked;
        private int commented;
        private int system;
        @SerializedName("news-comment-pinned")
        private int newsCommentPinned;
        @SerializedName("feed-comment-pinned")
        private int feedCommentPinned;

        public int getFollowing() {
            return following;
        }

        public int getLiked() {
            return liked;
        }

        public void setLiked(int liked) {
            this.liked = liked;
        }

        public int getCommented() {
            return commented;
        }

        public void setCommented(int commented) {
            this.commented = commented;
        }

        public int getSystem() {
            return system;
        }

        public void setSystem(int system) {
            this.system = system;
        }

        public int getNewsCommentPinned() {
            return newsCommentPinned;
        }

        public void setNewsCommentPinned(int newsCommentPinned) {
            this.newsCommentPinned = newsCommentPinned;
        }

        public int getFeedCommentPinned() {
            return feedCommentPinned;
        }

        public void setFeedCommentPinned(int feedCommentPinned) {
            this.feedCommentPinned = feedCommentPinned;
        }

        public void setFollowing(int following) {
            this.following = following;
        }
    }
}
