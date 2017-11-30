package com.zhiyicx.thinksnsplus.data.beans;

/**
 * @author Jliuer
 * @Date 2017/11/30/11:02
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CircleInfoDetail {

    /**
     * id : 1
     * name : tymss
     * user_id : 18
     * category_id : 2
     * location : chengdu
     * longitude : 100.23
     * latitude : 180.22
     * geo_hash : 1122tym
     * allow_feed : 0
     * mode : public
     * money : 0
     * summary : 第一个圈子
     * notice : null
     * users_count : 0
     * posts_count : 0
     * audit : 0
     * created_at : 2017-11-28 02:30:38
     * updated_at : 2017-11-28 02:30:38
     * join_income_count : 0
     * pinned_income_count : 0
     * user : {"id":18,"name":"仨仨仨仨仨","location":null,"sex":1,"bio":"没有","created_at":"2017-04-10 02:10:22","updated_at":"2017-11-10 09:54:09","avatar":null,"bg":null,"verified":{"type":"user","icon":null,"description":"玄奘普法"},"extra":{"user_id":18,"likes_count":40,"comments_count":68,"followers_count":24,"followings_count":18,"updated_at":"2017-11-23 10:30:11","feeds_count":15,"questions_count":10,"answers_count":14,"checkin_count":2,"last_checkin_count":1,"live_zans_count":0,"live_zans_remain":0,"live_time":0}}
     */

    private Long id;
    private String name;
    private int user_id;
    private int category_id;
    private String location;
    private String longitude;
    private String latitude;
    private String geo_hash;
    private int allow_feed;
    private String mode;
    private int money;
    private String summary;
    private Object notice;
    private int users_count;
    private int posts_count;
    private int audit;
    private String created_at;
    private String updated_at;
    private int join_income_count;
    private int pinned_income_count;
    private UserInfoBean user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getGeo_hash() {
        return geo_hash;
    }

    public void setGeo_hash(String geo_hash) {
        this.geo_hash = geo_hash;
    }

    public int getAllow_feed() {
        return allow_feed;
    }

    public void setAllow_feed(int allow_feed) {
        this.allow_feed = allow_feed;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Object getNotice() {
        return notice;
    }

    public void setNotice(Object notice) {
        this.notice = notice;
    }

    public int getUsers_count() {
        return users_count;
    }

    public void setUsers_count(int users_count) {
        this.users_count = users_count;
    }

    public int getPosts_count() {
        return posts_count;
    }

    public void setPosts_count(int posts_count) {
        this.posts_count = posts_count;
    }

    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getJoin_income_count() {
        return join_income_count;
    }

    public void setJoin_income_count(int join_income_count) {
        this.join_income_count = join_income_count;
    }

    public int getPinned_income_count() {
        return pinned_income_count;
    }

    public void setPinned_income_count(int pinned_income_count) {
        this.pinned_income_count = pinned_income_count;
    }

    public UserInfoBean getUser() {
        return user;
    }

    public void setUser(UserInfoBean user) {
        this.user = user;
    }
}
