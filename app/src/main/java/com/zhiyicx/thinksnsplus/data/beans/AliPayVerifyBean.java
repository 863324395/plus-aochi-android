package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @Author Jliuer
 * @Date 2018/05/21/10:49
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class AliPayVerifyBean implements Serializable,Parcelable{
    private String memo;
    private String result;
    private String resultStatus;

    public AliPayVerifyBean(String memo, String result, String resultStatus) {
        this.memo = memo;
        this.result = result;
        this.resultStatus = resultStatus;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.memo);
        dest.writeString(this.result);
        dest.writeString(this.resultStatus);
    }

    protected AliPayVerifyBean(Parcel in) {
        this.memo = in.readString();
        this.result = in.readString();
        this.resultStatus = in.readString();
    }

    public static final Creator<AliPayVerifyBean> CREATOR = new Creator<AliPayVerifyBean>() {
        @Override
        public AliPayVerifyBean createFromParcel(Parcel source) {
            return new AliPayVerifyBean(source);
        }

        @Override
        public AliPayVerifyBean[] newArray(int size) {
            return new AliPayVerifyBean[size];
        }
    };
}
