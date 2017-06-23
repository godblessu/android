package com.zhiyicx.baseproject.impl.photoselector;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Toll implements Parcelable, Serializable {

    private static final long serialVersionUID = 123L;

    public static final int LOOK_TOLL = 1000;// 查看收费
    public static final String LOOK_TOLL_TYPE = "read";// 查看收费
    public static final String LOOK_TOLL_TYPE_NONE = "none";// 查看收费
    public static final int DOWNLOAD_TOLL = 2000;// 下载收费
    public static final String DOWNLOAD_TOLL_TYPE = "download";// 下载收费

    int toll_type;
    String toll_type_string;
    float toll_money;
    float custom_money;

    public Toll() {
    }

    public Toll(int toll_type, float toll_money, float custom_money) {
        this.toll_type = toll_type;
        this.toll_money = toll_money;
        this.custom_money = custom_money;
    }

    public void setToll_type(int toll_type) {
        this.toll_type = toll_type;
    }

    public void setToll_money(float toll_money) {
        this.toll_money = toll_money;
    }

    public float getCustom_money() {
        return custom_money;
    }

    public void setCustom_money(float custom_money) {
        this.custom_money = custom_money;
    }

    public int getToll_type() {
        return toll_type;
    }

    public float getToll_money() {
        return toll_money;
    }

    private Toll(int toll_type, float toll_money) {
        this.toll_type = toll_type;
        this.toll_money = toll_money;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.toll_type);
        dest.writeFloat(this.toll_money);
        dest.writeFloat(this.custom_money);
    }

    protected Toll(Parcel in) {
        this.toll_type = in.readInt();
        this.toll_money = in.readFloat();
        this.custom_money = in.readFloat();
    }

    public static final Creator<Toll> CREATOR = new Creator<Toll>() {
        @Override
        public Toll createFromParcel(Parcel source) {
            return new Toll(source);
        }

        @Override
        public Toll[] newArray(int size) {
            return new Toll[size];
        }
    };

    public void reset() {
        custom_money = 0;
        toll_money = 0;
        toll_type = 0;
    }

    @Override
    public String toString() {
        return "toll_type=" + (toll_type == LOOK_TOLL ? LOOK_TOLL_TYPE : DOWNLOAD_TOLL_TYPE)
                + ",toll_money=" + toll_money
                + ",custom_money=" + custom_money;
    }
}