package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.zhiyicx.thinksnsplus.data.source.local.data_convert.BaseConvert;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;

/**
 * @Author Jliuer
 * @Date 2017/08/10/16:28
 * @Email Jliuer@aliyun.com
 * @Description
 */
@Entity
public class QAPublishBean extends BaseDraftBean implements Parcelable {

    private String subject;// 问题主题或者说标题
    @Convert(converter = TopicConvert.class, columnType = String.class)
    private List<Topic> topics;// 绑定的话题
    @Convert(converter = InvitationsConvert.class, columnType = String.class)
    private List<Invitations> invitations;// 问题邀请回答的人
    private String body;// 问题描述
    private int anonymity;// 是否匿名 1 匿名 ，0 不匿名
    private int automaticity;// 邀请悬赏自动入账，只邀请一个人的情况下，允许悬赏金额自动入账到被邀请回答者钱包中。1 自动入账 ，0 不自动入账
    private int look;// 是否开启围观，当问题有采纳或者邀请人已回答，则对外部观众自动开启围观。设置围观必须设置悬赏金额。1 开启围观 ，0 不开启围观
    private double amount;// 问题价值，悬赏金额
    private Long id;
    @Id
    private Long mark;
    private Long user_id;
    private String updated_at;
    private String created_at;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        if (subject.isEmpty()) {
            return;
        }
        if (subject.endsWith("?") || subject.endsWith("？")) {
            this.subject = subject;
            return;
        }
        this.subject = subject + "?";
    }

    public Long getMark() {
        return mark;
    }

    public void setMark(Long mark) {
        this.mark = mark;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Invitations> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<Invitations> invitations) {
        this.invitations = invitations;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getAnonymity() {
        return anonymity;
    }

    public void setAnonymity(int anonymity) {
        this.anonymity = anonymity;
    }

    public int getAutomaticity() {
        return automaticity;
    }

    public void setAutomaticity(int automaticity) {
        this.automaticity = automaticity;
    }

    public int getLook() {
        return look;
    }

    public void setLook(int look) {
        this.look = look;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public static class Topic implements Parcelable, Serializable {
        private static final long serialVersionUID = -8734687577864836617L;
        private int id;
        @Expose
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.name);
        }

        public Topic() {
        }

        protected Topic(Parcel in) {
            this.id = in.readInt();
            this.name = in.readString();
        }

        public static final Creator<Topic> CREATOR = new Creator<Topic>() {
            @Override
            public Topic createFromParcel(Parcel source) {
                return new Topic(source);
            }

            @Override
            public Topic[] newArray(int size) {
                return new Topic[size];
            }
        };
    }

    public static class Invitations implements Parcelable, Serializable {
        private static final long serialVersionUID = -8734687577864836617L;
        private int user;
        @Expose
        private String name;

        public int getUser() {
            return user;
        }

        public void setUser(int user) {
            this.user = user;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.user);
            dest.writeString(this.name);
        }

        public Invitations() {
        }

        protected Invitations(Parcel in) {
            this.user = in.readInt();
            this.name = in.readString();
        }

        public static final Creator<Invitations> CREATOR = new Creator<Invitations>() {
            @Override
            public Invitations createFromParcel(Parcel source) {
                return new Invitations(source);
            }

            @Override
            public Invitations[] newArray(int size) {
                return new Invitations[size];
            }
        };
    }

    

    public static class TopicConvert extends BaseConvert<List<Topic>> {
    }

    public static class InvitationsConvert extends BaseConvert<List<Invitations>> {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subject);
        dest.writeTypedList(this.topics);
        dest.writeTypedList(this.invitations);
        dest.writeString(this.body);
        dest.writeInt(this.anonymity);
        dest.writeInt(this.automaticity);
        dest.writeInt(this.look);
        dest.writeDouble(this.amount);
        dest.writeValue(this.id);
        dest.writeValue(this.mark);
        dest.writeValue(this.user_id);
        dest.writeString(this.updated_at);
        dest.writeString(this.created_at);
    }

    public QAPublishBean() {
    }

    protected QAPublishBean(Parcel in) {
        this.subject = in.readString();
        this.topics = in.createTypedArrayList(Topic.CREATOR);
        this.invitations = in.createTypedArrayList(Invitations.CREATOR);
        this.body = in.readString();
        this.anonymity = in.readInt();
        this.automaticity = in.readInt();
        this.look = in.readInt();
        this.amount = in.readDouble();
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.mark = (Long) in.readValue(Long.class.getClassLoader());
        this.user_id = (Long) in.readValue(Long.class.getClassLoader());
        this.updated_at = in.readString();
        this.created_at = in.readString();
    }

    @Generated(hash = 1977474578)
    public QAPublishBean(String subject, List<Topic> topics, List<Invitations> invitations,
            String body, int anonymity, int automaticity, int look, double amount, Long id,
            Long mark, Long user_id, String updated_at, String created_at) {
        this.subject = subject;
        this.topics = topics;
        this.invitations = invitations;
        this.body = body;
        this.anonymity = anonymity;
        this.automaticity = automaticity;
        this.look = look;
        this.amount = amount;
        this.id = id;
        this.mark = mark;
        this.user_id = user_id;
        this.updated_at = updated_at;
        this.created_at = created_at;
    }

    public static final Creator<QAPublishBean> CREATOR = new Creator<QAPublishBean>() {
        @Override
        public QAPublishBean createFromParcel(Parcel source) {
            return new QAPublishBean(source);
        }

        @Override
        public QAPublishBean[] newArray(int size) {
            return new QAPublishBean[size];
        }
    };
}
