package com.tym.shortvideo.media;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author Jliuer
 * @Date 18/04/08 14:03
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class VideoInfo implements Parcelable,Serializable{
    private static final long serialVersionUID = 1333866610918025407L;
    public String path;//路径
    public String cover;//封面路径
    public String name;//名字
    public String createTime;//时间
    public int duration;//时长
    public int rotation;//旋转角度
    public int width;//宽
    public int height;//高
    public int bitRate;//比特率
    public int frameRate;//帧率
    public int frameInterval;//关键帧间隔


    public int expWidth;//期望宽度
    public int expHeight;//期望高度
    public int cutPoint;//剪切的开始点
    public int cutDuration;//剪切的时长

    public int storeId;

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public VideoInfo() {
    }

    public VideoInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public int getFrameInterval() {
        return frameInterval;
    }

    public void setFrameInterval(int frameInterval) {
        this.frameInterval = frameInterval;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getExpWidth() {
        return expWidth;
    }

    public void setExpWidth(int expWidth) {
        this.expWidth = expWidth;
    }

    public int getExpHeight() {
        return expHeight;
    }

    public void setExpHeight(int expHeight) {
        this.expHeight = expHeight;
    }

    public int getCutPoint() {
        return cutPoint;
    }

    public void setCutPoint(int cutPoint) {
        this.cutPoint = cutPoint;
    }

    public int getCutDuration() {
        return cutDuration;
    }

    public void setCutDuration(int cutDuration) {
        this.cutDuration = cutDuration;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.cover);
        dest.writeString(this.name);
        dest.writeString(this.createTime);
        dest.writeInt(this.duration);
        dest.writeInt(this.rotation);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.bitRate);
        dest.writeInt(this.frameRate);
        dest.writeInt(this.frameInterval);
        dest.writeInt(this.expWidth);
        dest.writeInt(this.expHeight);
        dest.writeInt(this.cutPoint);
        dest.writeInt(this.cutDuration);
        dest.writeInt(this.storeId);
    }

    protected VideoInfo(Parcel in) {
        this.path = in.readString();
        this.cover = in.readString();
        this.name = in.readString();
        this.createTime = in.readString();
        this.duration = in.readInt();
        this.rotation = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
        this.bitRate = in.readInt();
        this.frameRate = in.readInt();
        this.frameInterval = in.readInt();
        this.expWidth = in.readInt();
        this.expHeight = in.readInt();
        this.cutPoint = in.readInt();
        this.cutDuration = in.readInt();
        this.storeId = in.readInt();
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel source) {
            return new VideoInfo(source);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };
}
