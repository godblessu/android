package com.zhiyicx.thinksnsplus.modules.music_fm.media_data;

import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;

/**
 * @Author Jliuer
 * @Date 2017/2/21/14:21
 * @Email Jliuer@aliyun.com
 * @Description 音乐数据封装类
 */
public class MutableMediaMetadata {

    public MediaMetadataCompat metadata;
    public final String trackId;

    public MutableMediaMetadata(String trackId, MediaMetadataCompat metadata) {
        this.metadata = metadata;
        this.trackId = trackId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != MutableMediaMetadata.class) {
            return false;
        }
        MutableMediaMetadata that = (MutableMediaMetadata) o;
        return TextUtils.equals(trackId, that.trackId);
    }

    @Override
    public int hashCode() {
        return trackId.hashCode();
    }
}
