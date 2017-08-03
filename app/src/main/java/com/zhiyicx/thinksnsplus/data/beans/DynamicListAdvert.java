package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @Author Jliuer
 * @Date 2017/07/31/17:14
 * @Email Jliuer@aliyun.com
 * @Description 动态列表模拟数据广告
 */
public class DynamicListAdvert implements Serializable, Parcelable {
    private static final long serialVersionUID = 124L;

    /**
     * avatar : 头像图|string
     * name : 用户名|string
     * content : 内容|string
     * image : 图片|string
     * time : 时间|date
     */

    private String avatar;
    private String name;
    private String content;
    private String image;
    private String time;
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.name);
        dest.writeString(this.link);
        dest.writeString(this.content);
        dest.writeString(this.image);
        dest.writeString(this.time);
    }

    public DynamicListAdvert() {
    }

    protected DynamicListAdvert(Parcel in) {
        this.avatar = in.readString();
        this.name = in.readString();
        this.link = in.readString();
        this.content = in.readString();
        this.image = in.readString();
        this.time = in.readString();
    }

    public static final Creator<DynamicListAdvert> CREATOR = new Creator<DynamicListAdvert>() {
        @Override
        public DynamicListAdvert createFromParcel(Parcel source) {
            return new DynamicListAdvert(source);
        }

        @Override
        public DynamicListAdvert[] newArray(int size) {
            return new DynamicListAdvert[size];
        }
    };

    public static DynamicDetailBeanV2 advert2Dynamic(DynamicListAdvert advert, long max_id) {
        DynamicDetailBeanV2 dynamicDetailBeanV2 = new DynamicDetailBeanV2();
        dynamicDetailBeanV2.setFeed_from(-1);// 广告位标识
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setUser_id(-1L);
        dynamicDetailBeanV2.setUser_id(-1L);
        userInfoBean.setName(advert.getName());// 广告名字
        userInfoBean.setAvatar(advert.getAvatar());// 广告头像
        dynamicDetailBeanV2.setMaxId(max_id);// max_id 转移
        dynamicDetailBeanV2.setDeleted_at(advert.getLink());// 广告外链
        dynamicDetailBeanV2.setUserInfoBean(userInfoBean);
        dynamicDetailBeanV2.setFeed_content(advert.getContent());// 广告内容
        dynamicDetailBeanV2.setCreated_at(advert.getTime());// 广告时间
        dynamicDetailBeanV2.setUpdated_at(advert.getTime());// 广告时间
        DynamicDetailBeanV2.ImagesBean imageBean = new DynamicDetailBeanV2.ImagesBean();
        imageBean.setImgUrl(advert.getImage());// 广告图片
        dynamicDetailBeanV2.setImages(Arrays.asList(imageBean));
        return dynamicDetailBeanV2;
    }
}