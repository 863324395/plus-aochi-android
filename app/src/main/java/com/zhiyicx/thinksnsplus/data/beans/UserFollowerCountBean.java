package com.zhiyicx.thinksnsplus.data.beans;

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

    public static class UserBean {
        /**
         * following : 1
         */

        private int following;

        public int getFollowing() {
            return following;
        }

        public void setFollowing(int following) {
            this.following = following;
        }
    }
}
