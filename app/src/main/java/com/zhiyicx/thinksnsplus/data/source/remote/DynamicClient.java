package com.zhiyicx.thinksnsplus.data.source.remote;

import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentToll;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDigListBean;
import com.zhiyicx.thinksnsplus.data.beans.RewardsListBean;
import com.zhiyicx.thinksnsplus.data.beans.TopDynamicCommentBean;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_COMMENT_PAID_V2;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_DYNAMIC_REWARDS;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_DYNAMIC_REWARDS_USER_LIST;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_INFO_REWARDS;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_INFO_REWARDS_USER_LIST;

/**
 * @author LiuChao
 * @describe 动态相关的接口
 * @date 2017/2/21
 * @contact email:450127106@qq.com
 */

public interface DynamicClient {
    /**
     * 发布动态
     *
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(ApiConfig.APP_PATH_SEND_DYNAMIC)
    Observable<BaseJson<Object>> sendDynamic(@Body RequestBody body);

    /**
     * 发布动态 v2 接口
     *
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(ApiConfig.APP_PATH_SEND_DYNAMIC_V2)
    Observable<BaseJsonV2<Object>> sendDynamicV2(@Body RequestBody body);

    /**
     * 发布动态到频道
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(ApiConfig.APP_PATH_SEND_DYNAMIC_TO_CHANNEL)
    Observable<BaseJson<Object>> sendDynamicToChannel(@Path("channel_id") long channel_id, @Body RequestBody body);

    /**
     * 获取动态列表
     *
     * @param type     "" 代表最新；follows 代表关注 ； hots 代表热门
     * @param max_id   用来翻页的记录id(对应数据体里的feed_id ,最新和关注选填)
     * @param limit    请求数据条数 默认10条
     * @param page     页码 热门选填
     * @param feed_ids 可以是以逗号隔开的id  可以是数组
     * @return dynamic list
     */
    @GET(ApiConfig.APP_PATH_GET_DYNAMIC_LIST)
    Observable<BaseJson<List<DynamicBean>>> getDynamicList(@Path("type") String type, @Query("max_id") Long max_id, @Query("limit") Long limit, @Query("page") int page, @Query("feed_ids") String feed_ids);

    @GET(ApiConfig.APP_PATH_GET_DYNAMIC_LIST_V2)
    Observable<DynamicBeanV2> getDynamicListV2(@Query("type") String type, @Query
            ("after") Long after, @Query("user") Long user_id, @Query("limit") Long limit);

    @GET(ApiConfig.APP_PATH_GET_COLLECT_DYNAMIC_LIST_V2)
    Observable<List<DynamicDetailBeanV2>> getCollectDynamicListV2(@Query
                                                                          ("after") Long after, @Query("user") Long user_id, @Query("limit") Long limit);

    /**
     * #点赞一条动态
     *
     * @param feed_id 动态的 id
     * @return
     */
    @POST(ApiConfig.APP_PATH_DYNAMIC_HANDLE_LIKE)
    Observable<BaseJson<String>> likeDynamic(@Path("feed_id") Long feed_id);

    @POST(ApiConfig.APP_PATH_DYNAMIC_CLICK_LIKE_V2)
    Observable<String> likeDynamicV2(@Path("feed_id") Long feed_id);

    /**
     * #取消点赞一条动态
     *
     * @param feed_id 动态的 id
     * @return
     */
    @DELETE(ApiConfig.APP_PATH_DYNAMIC_HANDLE_LIKE)
    Observable<BaseJson<String>> cancleLikeDynamic(@Path("feed_id") Long feed_id);

    @DELETE(ApiConfig.APP_PATH_DYNAMIC_CANCEL_CLICK_LIKE_V2)
    Observable<BaseJson<String>> cancleLikeDynamicV2(@Path("feed_id") Long feed_id);

    @GET(ApiConfig.APP_PATH_DYNAMIC_DIG_LIST_V2)
    Observable<List<DynamicDigListBean>> getDynamicDigListV2(@Path("feed_id") Long feed_id, @Query("after") Long max_id, @Query("limit") Integer limitCount);

    /**
     * 收藏动态
     */
    @POST(ApiConfig.APP_PATH_HANDLE_COLLECT)
    Observable<BaseJson<Object>> collectDynamic(@Path("feed_id") Long feed_id);

    /**
     * 取消动态收藏
     *
     * @param feed_id
     * @return
     */
    @DELETE(ApiConfig.APP_PATH_HANDLE_COLLECT)
    Observable<BaseJson<Object>> cancleCollectDynamic(@Path("feed_id") Long feed_id);

    /**
     * 一条动态的评论列表
     *
     * @param feed_id 动态的唯一 id
     * @param max_id  返回的 feed_digg_id 作为max_id,对象为null表示不传
     * @param limit
     * @return
     */
    @GET(ApiConfig.APP_PATH_DYNAMIC_COMMENT_LIST)
    Observable<BaseJson<List<DynamicCommentBean>>> getDynamicCommentList(@Path("feed_id") Long feed_id, @Query("max_id") Long max_id, @Query("limit") Long limit);

    @GET(ApiConfig.APP_PATH_DYNAMIC_COMMENT_LIST_V2)
    Observable<DynamicCommentBeanV2> getDynamicCommentListV2(@Path("feed_id") Long feed_id, @Query("after") Long after, @Query("limit") Long limit);

    /**
     * 根据id获取评论列表
     *
     * @param comment_ids 评论id 以逗号隔开或者数组形式传入
     * @return
     */
    @GET(ApiConfig.APP_PATH_DYNAMIC_COMMENT_LIST_BY_COMMENT_ID)
    Observable<BaseJson<List<DynamicCommentBean>>> getDynamicCommentListByCommentsId(@Query("comment_ids") String comment_ids);

    /**
     * 增加动态浏览量
     *
     * @param feed_id 动态的唯一 id
     * @return
     */
    @POST(ApiConfig.APP_PATH_HANDLE_DYNAMIC_VIEWCOUNT)
    Observable<BaseJson<Object>> handleDynamicViewCount(@Path("feed_id") Long feed_id);

    /**
     * 获取动态详情 V2
     *
     * @param feed_id 动态id
     * @return
     */
    @GET(ApiConfig.APP_PATH_GET_DYNAMIC_DETAIL)
    Observable<DynamicDetailBeanV2> getDynamicDetailBeanV2(@Path("feed_id") Long feed_id);

    /**
     * 设置动态评论收费 V2
     *
     * @param feed_id 动态id
     * @param amount  收费金额
     * @return
     */
    @FormUrlEncoded
    @PATCH(APP_PATH_COMMENT_PAID_V2)
    Observable<DynamicCommentToll> setDynamicCommentToll(@Path("feed_id") Long feed_id, @Field("amount") int amount);

    /**
     * 置顶动态 V2
     *
     * @param feed_id 动态的唯一 id
     * @return
     */
    @FormUrlEncoded
    @POST(ApiConfig.APP_PATH_TOP_DYNAMIC)
    Observable<BaseJsonV2<Integer>> stickTopDynamic(@Path("feed_id") Long feed_id, @Field("amount") int amount, @Field("day") int day);


    /**
     * 置顶动态评论  V2
     *
     * @param feed_id 动态的唯一 id
     * @return
     */
    @FormUrlEncoded
    @POST(ApiConfig.APP_PATH_TOP_DYNAMIC_COMMENT)
    Observable<BaseJsonV2<Integer>> stickTopDynamicComment(@Path("feed_id") Long feed_id, @Path("comment_id") Long comment_id, @Field("amount") int amount, @Field("day") int day);

    /**
     * 获取动态评论置顶审核列表 V2
     *
     * @return
     */
    @GET(ApiConfig.APP_PATH_REVIEW_DYNAMIC_COMMENT)
    Observable<List<TopDynamicCommentBean>> getReviewComment(@Query("after") int after, @Query("limit")
            int limit);

    /**
     * 动态评论置顶审核通过 V2
     *
     * @return
     */
    @PATCH(ApiConfig.APP_PATH_APPROVED_DYNAMIC_COMMENT)
    Observable<BaseJsonV2> approvedTopComment(@Path("feed_id") Long feed_id, @Path("comment_id")
            int comment_id, @Path("pinned_id") int pinned_id);

    /**
     * 动态评论置顶审核通过 V2
     *
     * @return
     */
    @DELETE(ApiConfig.APP_PATH_REFUSE_DYNAMIC_COMMENT)
    Observable<BaseJsonV2> refuseTopComment(@Path("pinned_id") int pinned_id);

    /**
     * 动态评论置顶审核通过 V2
     *
     * @return
     */
    @DELETE(ApiConfig.APP_PATH_APPROVED_DYNAMIC_COMMENT)
    Observable<BaseJsonV2> deleteTopComment(@Path("feed_id") Long feed_id, @Path("comment_id")
            int comment_id);


    /*******************************************  打赏  *********************************************/


    /**
     * 对一条动态打赏
     *
     * @param news_id 动态 id
     * @param amount  打赏金额
     * @return
     */
    @FormUrlEncoded
    @POST(APP_PATH_DYNAMIC_REWARDS)
    Observable<Object> rewardDynamic(@Path("feed_id") long news_id, @Field("amount") float amount);


    /**
     * 动态打赏列表
     *
     * @param news_id    动态 id
     * @param limit      列表返回数据条数
     * @param since      翻页标识 时间排序时为数据 id 金额排序时为打赏金额 amount
     * @param order      翻页标识 排序 正序-asc 倒序 desc
     * @param order_type 排序规则 date-按时间 amount-按金额
     * @return
     */
    @GET(APP_PATH_DYNAMIC_REWARDS_USER_LIST)
    Observable<List<RewardsListBean>> rewardDynamicList(@Path("feed_id") long news_id, @Query("limit") Integer limit, @Query("since") Integer since, @Query("order") String order, @Query("order_type") String order_type);



}
