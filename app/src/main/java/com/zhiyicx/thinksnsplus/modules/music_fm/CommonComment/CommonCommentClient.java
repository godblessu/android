package com.zhiyicx.thinksnsplus.modules.music_fm.CommonComment;

import com.zhiyicx.baseproject.cache.CacheBean;
import com.zhiyicx.common.base.BaseJson;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_HANDLE_BACKGROUND_TASK;

/**
 * @Author Jliuer
 * @Date 2017/04/27/17:54
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface CommonCommentClient {

    @Multipart
    @POST(APP_PATH_HANDLE_BACKGROUND_TASK)
    Observable<BaseJson<Object>> sendComment(@Path("path") String path, @Part List<MultipartBody.Part> partList);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @HTTP(method = "DELETE", path = APP_PATH_HANDLE_BACKGROUND_TASK)
    Observable<BaseJson<CacheBean>> deleteComment(@Path("path") String path);
}
