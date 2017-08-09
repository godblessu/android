package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.data.source.local.data_convert.BaseConvert;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.io.Serializable;
import java.util.List;

@Entity
public class InfoListDataBean extends BaseListBean implements Serializable {
    @Transient
    private static final long serialVersionUID = 1L;
    /**
     * id : 1
     * title : 123123
     * updated_at : 2017-03-13 09:59:32
     * storage : {"id":1,"image_width":null,"image_height":null}
     */
    @Id(autoincrement = true)
    private Long _id;
    @Unique
    private int id;
    private Long info_type;
    private int is_collection_news;
    private int is_digg_news;
    private String title;
    private String content;
    private String from;
    private String created_at;
    private String updated_at;
    @Convert(converter = InfoStorageBeanConverter.class, columnType = String.class)
    private StorageBean image;
    // v2 新增的
    private String subject;
    private boolean has_collect;
    private boolean has_like;
    @Convert(converter = InfoCategoryConvert.class, columnType = String.class)
    private InfoCategory category;
    private boolean isTop; // 是否是置顶的资讯
    private String author; // 如果from是原创 则展示author
    private int hits;
    @Convert(converter = TagConvert.class, columnType = String.class)
    private List<UserTagBean> tags;
    private int digg_count;
    private int comment_count;
    private int is_recommend;
    private int audit_count;

    @Override
    public String toString() {
        return "InfoListDataBean{" +
                "id=" + id +
                ", info_type=" + info_type +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", from='" + from + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", image=" + image +
                ", subject='" + subject + '\'' +
                ", has_collect=" + has_collect +
                ", has_like=" + has_like +
                ", category=" + category +
                ", isTop=" + isTop +
                ", author='" + author + '\'' +
                ", hits=" + hits +
                ", tags=" + tags +
                ", digg_count=" + digg_count +
                ", comment_count=" + comment_count +
                ", is_recommend=" + is_recommend +
                ", audit_count=" + audit_count +
                '}';
    }

    public int getIs_collection_news() {
        return is_collection_news;
    }

    public void setIs_collection_news(int is_collection_news) {
        this.is_collection_news = is_collection_news;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public StorageBean getImage() {
        return image;
    }

    public void setImage(StorageBean image) {
        this.image = image;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isHas_collect() {
        return has_collect;
    }

    public void setHas_collect(boolean has_collect) {
        this.has_collect = has_collect;
    }

    public boolean isHas_like() {
        return has_like;
    }

    public void setHas_like(boolean has_like) {
        this.has_like = has_like;
    }

    public InfoCategory getCategory() {
        return category;
    }

    public void setCategory(InfoCategory category) {
        this.category = category;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public InfoListDataBean() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getDigg_count() {
        return digg_count;
    }

    public void setDigg_count(int digg_count) {
        this.digg_count = digg_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getIs_recommend() {
        return is_recommend;
    }

    public void setIs_recommend(int is_recommend) {
        this.is_recommend = is_recommend;
    }

    public int getAudit_count() {
        return audit_count;
    }

    public void setAudit_count(int audit_count) {
        this.audit_count = audit_count;
    }

    public static class InfoStorageBeanConverter implements PropertyConverter<StorageBean, String> {

        @Override
        public StorageBean convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            return ConvertUtils.base64Str2Object(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(StorageBean entityProperty) {
            if (entityProperty == null) {
                return null;
            }
            return ConvertUtils.object2Base64Str(entityProperty);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof InfoListDataBean) {
            InfoListDataBean infoListDataBean = (InfoListDataBean) obj;
            return infoListDataBean.getId() == id;
        }
        return super.equals(obj);
    }


    public Long getInfo_type() {
        return this.info_type;
    }


    public void setInfo_type(Long info_type) {
        this.info_type = info_type;
    }

    public int getIs_digg_news() {
        return this.is_digg_news;
    }

    public void setIs_digg_news(int is_digg_news) {
        this.is_digg_news = is_digg_news;
    }

    public boolean getHas_collect() {
        return this.has_collect;
    }

    public boolean getHas_like() {
        return this.has_like;
    }

    public static class InfoCategory implements Serializable, Parcelable{
        private static final long serialVersionUID = -5033116664345775676L;
        private Long id;
        private String name;
        private int rank;

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

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        @Override
        public String toString() {
            return "InfoCategory{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", rank=" + rank +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.id);
            dest.writeString(this.name);
            dest.writeInt(this.rank);
        }

        public InfoCategory() {
        }

        protected InfoCategory(Parcel in) {
            this.id = (Long) in.readValue(Long.class.getClassLoader());
            this.name = in.readString();
            this.rank = in.readInt();
        }

        public static final Creator<InfoCategory> CREATOR = new Creator<InfoCategory>() {
            @Override
            public InfoCategory createFromParcel(Parcel source) {
                return new InfoCategory(source);
            }

            @Override
            public InfoCategory[] newArray(int size) {
                return new InfoCategory[size];
            }
        };
    }

    public static class InfoCategoryConvert extends BaseConvert<InfoCategory>{}

    public boolean getIsTop() {
        return this.isTop;
    }

    public void setIsTop(boolean isTop) {
        this.isTop = isTop;
    }



    @Generated(hash = 1161353270)
    public InfoListDataBean(Long _id, int id, Long info_type, int is_collection_news, int is_digg_news,
            String title, String content, String from, String created_at, String updated_at,
            StorageBean image, String subject, boolean has_collect, boolean has_like,
            InfoCategory category, boolean isTop, String author, int hits, List<UserTagBean> tags,
            int digg_count, int comment_count, int is_recommend, int audit_count) {
        this._id = _id;
        this.id = id;
        this.info_type = info_type;
        this.is_collection_news = is_collection_news;
        this.is_digg_news = is_digg_news;
        this.title = title;
        this.content = content;
        this.from = from;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.image = image;
        this.subject = subject;
        this.has_collect = has_collect;
        this.has_like = has_like;
        this.category = category;
        this.isTop = isTop;
        this.author = author;
        this.hits = hits;
        this.tags = tags;
        this.digg_count = digg_count;
        this.comment_count = comment_count;
        this.is_recommend = is_recommend;
        this.audit_count = audit_count;
    }

    public static class TagConvert extends BaseConvert<List<UserTagBean>>{}

    public List<UserTagBean> getTags() {
        return this.tags;
    }

    public void setTags(List<UserTagBean> tags) {
        this.tags = tags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this._id);
        dest.writeInt(this.id);
        dest.writeValue(this.info_type);
        dest.writeInt(this.is_collection_news);
        dest.writeInt(this.is_digg_news);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.from);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeParcelable(this.image, flags);
        dest.writeString(this.subject);
        dest.writeByte(this.has_collect ? (byte) 1 : (byte) 0);
        dest.writeByte(this.has_like ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.category, flags);
        dest.writeByte(this.isTop ? (byte) 1 : (byte) 0);
        dest.writeString(this.author);
        dest.writeInt(this.hits);
        dest.writeTypedList(this.tags);
        dest.writeInt(this.digg_count);
        dest.writeInt(this.comment_count);
        dest.writeInt(this.is_recommend);
        dest.writeInt(this.audit_count);
    }

    protected InfoListDataBean(Parcel in) {
        super(in);
        this._id = (Long) in.readValue(Long.class.getClassLoader());
        this.id = in.readInt();
        this.info_type = (Long) in.readValue(Long.class.getClassLoader());
        this.is_collection_news = in.readInt();
        this.is_digg_news = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.from = in.readString();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.image = in.readParcelable(StorageBean.class.getClassLoader());
        this.subject = in.readString();
        this.has_collect = in.readByte() != 0;
        this.has_like = in.readByte() != 0;
        this.category = in.readParcelable(InfoCategory.class.getClassLoader());
        this.isTop = in.readByte() != 0;
        this.author = in.readString();
        this.hits = in.readInt();
        this.tags = in.createTypedArrayList(UserTagBean.CREATOR);
        this.digg_count = in.readInt();
        this.comment_count = in.readInt();
        this.is_recommend = in.readInt();
        this.audit_count = in.readInt();
    }

    public static final Creator<InfoListDataBean> CREATOR = new Creator<InfoListDataBean>() {
        @Override
        public InfoListDataBean createFromParcel(Parcel source) {
            return new InfoListDataBean(source);
        }

        @Override
        public InfoListDataBean[] newArray(int size) {
            return new InfoListDataBean[size];
        }
    };
}

