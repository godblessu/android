package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;

import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * @author Catherine
 * @describe 回答的详情
 * @date 2017/8/15
 * @contact email:648129313@qq.com
 */
@Entity
public class AnswerInfoBean extends BaseListBean implements Serializable{
    @Transient
    private static final long serialVersionUID = -6138662175756334333L;
    /*{
        "id":1,
            "question_id":1,
            "user_id":1,
            "body":"笑嘻嘻，我是回答。",
            "anonymity":0,
            "adoption":0,
            "invited":0,
            "comments_count":0,
            "rewards_amount":0,
            "rewarder_count":0,
            "likes_count":0,
            "created_at":"2017-08-01 03:40:54",
            "updated_at":"2017-08-01 03:40:54",
            "liked":false,
            "collected":false,
            "rewarded":false,
            "likes": [],
        "rewarders": [],
        "question":{
        "id":1,
                "user_id":1,
                "subject":"第一个提问?",
                "body":null,
                "anonymity":0,
                "amount":0,
                "automaticity":1,
                "look":1,
                "excellent":0,
                "status":0,
                "comments_count":0,
                "answers_count":3,
                "watchers_count":0,
                "likes_count":0,
                "view_count":0,
                "created_at":"2017-07-28 08:38:54",
                "updated_at":"2017-08-01 06:03:21",
                "user":{
            "id":1,
                    "name":"Seven",
                    "bio":"Seven 的个人传记",
                    "sex":2,
                    "location":"成都 中国",
                    "created_at":"2017-06-02 08:43:54",
                    "updated_at":"2017-07-25 03:59:39",
                    "avatar":"http://plus.io/api/v2/users/1/avatar",
                    "bg":"http://plus.io/storage/user-bg/000/000/000/01.png",
                    "verified":null,
                    "extra":{
                "user_id":1,
                        "likes_count":0,
                        "comments_count":8,
                        "followers_count":0,
                        "followings_count":1,
                        "updated_at":"2017-08-01 06:06:37",
                        "feeds_count":0,
                        "questions_count":5,
                        "answers_count":3
            }
        }
    },
        "user":{
        "id":1,
                "name":"Seven",
                "bio":"Seven 的个人传记",
                "sex":2,
                "location":"成都 中国",
                "created_at":"2017-06-02 08:43:54",
                "updated_at":"2017-07-25 03:59:39",
                "avatar":"http://plus.io/api/v2/users/1/avatar",
                "bg":"http://plus.io/storage/user-bg/000/000/000/01.png",
                "verified":null,
                "extra":{
            "user_id":1,
                    "likes_count":0,
                    "comments_count":8,
                    "followers_count":0,
                    "followings_count":1,
                    "updated_at":"2017-08-01 06:06:37",
                    "feeds_count":0,
                    "questions_count":5,
                    "answers_count":3
        }
    }
    }*/

    @Id
    private Long id;
    private Long question_id;
    private Long user_id;
    private String body;
    private int anonymity; // 是否匿名，1 代表匿名发布，匿名后不会返回任何用户信息。
    private int adoption; // 是否采纳的答案
    private int invited; // 是否该回答是被邀请的人的回答。
    private int comments_count; // 评论总数统计
    private double rewards_amount; // 回答打赏总额统计
    private int rewarder_count; // 打赏的人总数统计
    private int likes_count; // 回答喜欢总数统计
    private int views_count; // 回答浏览量统计
    private String created_at;
    private String updated_at;
    @ToOne(joinProperty = "user_id")
    private UserInfoBean user;
    private boolean liked;
    private boolean collected;
    private boolean rewarded;
    @ToOne(joinProperty = "question_id")
    private QAListInfoBean question;

    @Override
    public Long getMaxId() {
        return id;
    }

    @Override
    public String toString() {
        return "AnswerInfoBean{" +
                "id=" + id +
                ", question_id=" + question_id +
                ", user_id=" + user_id +
                ", body='" + body + '\'' +
                ", anonymity=" + anonymity +
                ", adoption=" + adoption +
                ", invited=" + invited +
                ", comments_count=" + comments_count +
                ", rewards_amount=" + rewards_amount +
                ", rewarder_count=" + rewarder_count +
                ", likes_count=" + likes_count +
                ", views_count=" + views_count +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", user=" + user +
                ", liked=" + liked +
                ", collected=" + collected +
                ", rewarded=" + rewarded +
                ", question=" + question +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.id);
        dest.writeValue(this.question_id);
        dest.writeValue(this.user_id);
        dest.writeString(this.body);
        dest.writeInt(this.anonymity);
        dest.writeInt(this.adoption);
        dest.writeInt(this.invited);
        dest.writeInt(this.comments_count);
        dest.writeDouble(this.rewards_amount);
        dest.writeInt(this.rewarder_count);
        dest.writeInt(this.likes_count);
        dest.writeInt(this.views_count);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeParcelable(this.user, flags);
        dest.writeByte(this.liked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.collected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.rewarded ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.question, flags);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestion_id() {
        return this.question_id;
    }

    public void setQuestion_id(Long question_id) {
        this.question_id = question_id;
    }

    public Long getUser_id() {
        return this.user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getAnonymity() {
        return this.anonymity;
    }

    public void setAnonymity(int anonymity) {
        this.anonymity = anonymity;
    }

    public int getAdoption() {
        return this.adoption;
    }

    public void setAdoption(int adoption) {
        this.adoption = adoption;
    }

    public int getInvited() {
        return this.invited;
    }

    public void setInvited(int invited) {
        this.invited = invited;
    }

    public int getComments_count() {
        return this.comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public double getRewards_amount() {
        return this.rewards_amount;
    }

    public void setRewards_amount(double rewards_amount) {
        this.rewards_amount = rewards_amount;
    }

    public int getRewarder_count() {
        return this.rewarder_count;
    }

    public void setRewarder_count(int rewarder_count) {
        this.rewarder_count = rewarder_count;
    }

    public int getLikes_count() {
        return this.likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public int getViews_count() {
        return this.views_count;
    }

    public void setViews_count(int views_count) {
        this.views_count = views_count;
    }

    public String getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public boolean getLiked() {
        return this.liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean getCollected() {
        return this.collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public boolean getRewarded() {
        return this.rewarded;
    }

    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1502532136)
    public UserInfoBean getUser() {
        Long __key = this.user_id;
        if (user__resolvedKey == null || !user__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserInfoBeanDao targetDao = daoSession.getUserInfoBeanDao();
            UserInfoBean userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
                user__resolvedKey = __key;
            }
        }
        return user;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1074717042)
    public void setUser(UserInfoBean user) {
        synchronized (this) {
            this.user = user;
            user_id = user == null ? null : user.getUser_id();
            user__resolvedKey = user_id;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1451724453)
    public QAListInfoBean getQuestion() {
        Long __key = this.question_id;
        if (question__resolvedKey == null || !question__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            QAListInfoBeanDao targetDao = daoSession.getQAListInfoBeanDao();
            QAListInfoBean questionNew = targetDao.load(__key);
            synchronized (this) {
                question = questionNew;
                question__resolvedKey = __key;
            }
        }
        return question;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 333227727)
    public void setQuestion(QAListInfoBean question) {
        synchronized (this) {
            this.question = question;
            question_id = question == null ? null : question.getId();
            question__resolvedKey = question_id;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 705869050)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAnswerInfoBeanDao() : null;
    }

    public AnswerInfoBean() {
    }

    protected AnswerInfoBean(Parcel in) {
        super(in);
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.question_id = (Long) in.readValue(Long.class.getClassLoader());
        this.user_id = (Long) in.readValue(Long.class.getClassLoader());
        this.body = in.readString();
        this.anonymity = in.readInt();
        this.adoption = in.readInt();
        this.invited = in.readInt();
        this.comments_count = in.readInt();
        this.rewards_amount = in.readDouble();
        this.rewarder_count = in.readInt();
        this.likes_count = in.readInt();
        this.views_count = in.readInt();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.user = in.readParcelable(UserInfoBean.class.getClassLoader());
        this.liked = in.readByte() != 0;
        this.collected = in.readByte() != 0;
        this.rewarded = in.readByte() != 0;
        this.question = in.readParcelable(QAListInfoBean.class.getClassLoader());
    }

    @Generated(hash = 398869185)
    public AnswerInfoBean(Long id, Long question_id, Long user_id, String body, int anonymity,
            int adoption, int invited, int comments_count, double rewards_amount,
            int rewarder_count, int likes_count, int views_count, String created_at,
            String updated_at, boolean liked, boolean collected, boolean rewarded) {
        this.id = id;
        this.question_id = question_id;
        this.user_id = user_id;
        this.body = body;
        this.anonymity = anonymity;
        this.adoption = adoption;
        this.invited = invited;
        this.comments_count = comments_count;
        this.rewards_amount = rewards_amount;
        this.rewarder_count = rewarder_count;
        this.likes_count = likes_count;
        this.views_count = views_count;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.liked = liked;
        this.collected = collected;
        this.rewarded = rewarded;
    }

    public static final Creator<AnswerInfoBean> CREATOR = new Creator<AnswerInfoBean>() {
        @Override
        public AnswerInfoBean createFromParcel(Parcel source) {
            return new AnswerInfoBean(source);
        }

        @Override
        public AnswerInfoBean[] newArray(int size) {
            return new AnswerInfoBean[size];
        }
    };

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1250738736)
    private transient AnswerInfoBeanDao myDao;

    @Generated(hash = 251390918)
    private transient Long user__resolvedKey;

    @Generated(hash = 527827701)
    private transient Long question__resolvedKey;
}