package com.zhiyicx.thinksnsplus.data.source.remote;

import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.thinksnsplus.data.beans.IMBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;

import java.util.HashMap;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import rx.Observable;

import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_CHANGE_USER_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_IM_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_USER_INFO;

/**
 * @author LiuChao
 * @describe 用户信息相关的网络请求
 * @date 2017/1/9
 * @contact email:450127106@qq.com
 */

public interface UserInfoClient {
    // 上传头像功能，写在CommonClient中

    /**
     * 修改用户资料
     *
     * @param userFieldMap 用户需要修改哪那些信息不确定
     */
    @FormUrlEncoded
    @PATCH(APP_PATH_CHANGE_USER_INFO)
    Observable<BaseJson> changeUserInfo(@FieldMap HashMap<String, String> userFieldMap);

    /**
     * 获取用户信息
     *
     * @param user_id
     * @return
     */
    @GET(APP_PATH_GET_USER_INFO)
    Observable<BaseJson<UserInfoBean>> getUserInfo(@Path("user_id") int user_id);


    @GET(APP_PATH_GET_IM_INFO)
    Observable<BaseJson<IMBean>> getIMInfo();

}
