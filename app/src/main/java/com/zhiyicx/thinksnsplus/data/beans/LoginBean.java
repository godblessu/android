package com.zhiyicx.thinksnsplus.data.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhiyicx.baseproject.cache.CacheBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author LiuChao
 * @describe
 * @date 2017/1/3
 * @contact email:450127106@qq.com
 */
@Entity
public class LoginBean extends CacheBean implements Parcelable {
    @Id
    private Long created_at;
    private int expires;
    private String token;
    private String refresh_token;

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.created_at);
        dest.writeInt(this.expires);
        dest.writeString(this.token);
        dest.writeString(this.refresh_token);
    }

    public LoginBean() {
    }

    protected LoginBean(Parcel in) {
        this.created_at = (Long) in.readValue(Long.class.getClassLoader());
        this.expires = in.readInt();
        this.token = in.readString();
        this.refresh_token = in.readString();
    }

    @Generated(hash = 2036631705)
    public LoginBean(Long created_at, int expires, String token,
            String refresh_token) {
        this.created_at = created_at;
        this.expires = expires;
        this.token = token;
        this.refresh_token = refresh_token;
    }

    public static final Creator<LoginBean> CREATOR = new Creator<LoginBean>() {
        @Override
        public LoginBean createFromParcel(Parcel source) {
            return new LoginBean(source);
        }

        @Override
        public LoginBean[] newArray(int size) {
            return new LoginBean[size];
        }
    };
}
