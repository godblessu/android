package com.zhiyicx.thinksnsplus.data.source.repository;

import android.app.Application;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.common.net.UpLoadFile;
import com.zhiyicx.thinksnsplus.config.BackgroundTaskRequestMethodConfig;
import com.zhiyicx.thinksnsplus.data.beans.BackgroundRequestTaskBean;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;
import com.zhiyicx.thinksnsplus.data.beans.CircleTypeBean;
import com.zhiyicx.thinksnsplus.data.beans.PostDigListBean;
import com.zhiyicx.thinksnsplus.data.beans.PostPublishBean;
import com.zhiyicx.thinksnsplus.data.beans.RewardsListBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.UserInfoBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.remote.CircleClient;
import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.data.source.repository.i.IBaseCircleRepository;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CirclePostBean;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Jliuer
 * @Date 2017/11/21/15:36
 * @Email Jliuer@aliyun.com
 * @Description 圈子请求仓库管理
 */
public class BaseCircleRepository implements IBaseCircleRepository {

    protected CircleClient mCircleClient;

    @Inject
    protected Application mContext;
    @Inject
    protected UserInfoRepository mUserInfoRepository;
    @Inject
    UserInfoBeanGreenDaoImpl mUserInfoBeanGreenDao;

    @Inject
    public BaseCircleRepository(ServiceManager serviceManager) {
        mCircleClient = serviceManager.getCircleClient();
    }

    @Override
    public Observable<List<CircleTypeBean>> getCategroiesList(int limit, int offet) {
        return mCircleClient.getCategroiesList(TSListFragment.DEFAULT_ONE_PAGE_SIZE, offet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<BaseJsonV2<CircleInfo>> createCircle(long categoryId, Map<String, Object> params, Map<String, String> filePathList) {
        return mCircleClient.createCircle(categoryId, UpLoadFile.upLoadFileAndParams(filePathList, params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<BaseJsonV2<Object>> sendCirclePost(PostPublishBean publishBean) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson()
                .toJson(publishBean));
        return mCircleClient.publishPost(publishBean.getCircle_id(), body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<CircleInfo>> getMyJoinedCircle(int limit, int offet) {
        return mCircleClient.getMyJoinedCircle(limit, offet, CircleClient.MineCircleType.JOIN.value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<CircleInfo>> getAllCircle(int limit, int offet) {
        return mCircleClient.getAllCircle(limit, offet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<BaseJsonV2<Integer>> getCircleCount() {
        return mCircleClient.getCircleCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<RewardsListBean>> getPostRewardList(long postId, int limit, long offet) {
        return mCircleClient.getPostRewardList(postId, TSListFragment.DEFAULT_ONE_PAGE_SIZE, offet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<PostDigListBean>> getPostDigList(long postId, int limit, long offet) {
        return mCircleClient.getPostDigList(postId, TSListFragment.DEFAULT_ONE_PAGE_SIZE, offet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void dealLike(boolean isLiked, long postId) {
        Observable.just(isLiked)
                .observeOn(Schedulers.io())
                .subscribe(aBoolean -> {
                    BackgroundRequestTaskBean backgroundRequestTaskBean;
                    HashMap<String, Object> params = new HashMap<>();

                    // 后台处理
                    if (aBoolean) {
                        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.POST_V2, params);
                        backgroundRequestTaskBean.setPath(
                                String.format(ApiConfig.APP_PATH_LIKE_POST_FORMAT, String.valueOf(postId)));
                    } else {
                        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.DELETE_V2, params);
                        backgroundRequestTaskBean.setPath(
                                String.format(ApiConfig.APP_PATH_LIKE_POST_FORMAT, String.valueOf(postId)));
                    }
                    BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void dealCollect(boolean isCollected, long postId) {
        Observable.just(isCollected)
                .observeOn(Schedulers.io())
                .subscribe(aBoolean -> {
                    BackgroundRequestTaskBean backgroundRequestTaskBean;
                    HashMap<String, Object> params = new HashMap<>();
                    // 后台处理
                    if (!aBoolean) {
                        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.POST_V2, params);
                        backgroundRequestTaskBean.setPath(
                                String.format(ApiConfig.APP_PATH_COLLECT_POST_FORMAT, String.valueOf(postId)));
                    } else {
                        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.DELETE_V2, params);
                        backgroundRequestTaskBean.setPath(
                                String.format(ApiConfig.APP_PATH_UNCOLLECT_POST_FORMAT, String.valueOf(postId)));
                    }
                    BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void sendPostComment(String commentContent, Long postId, Long replyToUserId, Long commentMark) {
        BackgroundRequestTaskBean backgroundRequestTaskBean;
        HashMap<String, Object> params = new HashMap<>();
        params.put("body", commentContent);
        params.put("group_post_comment_mark", commentMark);
        if (replyToUserId != 0) {
            params.put("reply_user", replyToUserId);
        }

        // 后台处理
        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.SEND_CIRCLE_POST_COMMENT, params);
        backgroundRequestTaskBean.setPath(String.format(Locale.getDefault(), ApiConfig.APP_PATH_COMMENT_POST_FORMAT, postId));
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
    }

    @Override
    public void deletePostComment(long postId, long commentId) {
        BackgroundRequestTaskBean backgroundRequestTaskBean;
        HashMap<String, Object> params = new HashMap<>();
        // 后台处理
        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.DELETE, params);
        backgroundRequestTaskBean.setPath(String.format(ApiConfig.APP_PATH_DELETE_POST_COMMENT_FORMAT, postId, commentId));
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
    }

    @Override
    public void deletePost(long circleId, long postId) {
        BackgroundRequestTaskBean backgroundRequestTaskBean;
        HashMap<String, Object> params = new HashMap<>();
        backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.DELETE, params);
        backgroundRequestTaskBean.setPath(String.format(ApiConfig.APP_PATH_POST_FORMAT, circleId, postId));
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
    }

    @Override
    public Observable<BaseJsonV2<Object>> dealCircleJoinOrExit(CircleInfo circleInfo) {
        boolean isJoined = circleInfo.getJoined() != null;
        BackgroundRequestTaskBean backgroundRequestTaskBean;
        backgroundRequestTaskBean = new BackgroundRequestTaskBean();
        if (isJoined) {
            // 已经订阅，变为未订阅
            backgroundRequestTaskBean.setMethodType(BackgroundTaskRequestMethodConfig.DELETE_V2);
            backgroundRequestTaskBean.setPath(String.format(ApiConfig.APP_PATH_PUT_EXIT_CIRCLE_FROMAT, String.valueOf(circleInfo.getId())));
        } else {
            // 未订阅，变为已订阅
            backgroundRequestTaskBean.setMethodType(BackgroundTaskRequestMethodConfig.PUT);
            backgroundRequestTaskBean.setPath(String.format(ApiConfig.APP_PATH_PUT_JOIN_CIRCLE_FORMAT, String.valueOf(circleInfo.getId())));
        }
        // 启动后台任务
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
        return null;
    }

    @Override
    public Observable<List<CirclePostListBean>> getPostListFromCircle(long circleId, long maxId) {
        return dealWithPostList(mCircleClient.getPostListFromCircle(circleId, TSListFragment.DEFAULT_ONE_PAGE_SIZE, (int) maxId));
    }

    private Observable<List<CirclePostListBean>> dealWithPostList(Observable<CirclePostBean> observable) {

        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(circlePostBean -> {
                    List<CirclePostListBean> data = circlePostBean.getPinneds();
                    data.addAll(circlePostBean.getPosts());
                    return data;
                })
                .flatMap(postListBeans -> {
                    final List<Object> user_ids = new ArrayList<>();
                    for (CirclePostListBean circlePostListBean : postListBeans) {
                        user_ids.add(circlePostListBean.getUser_id());
                        if (circlePostListBean.getComments() == null || circlePostListBean.getComments().isEmpty()) {
                            continue;
                        }
                        for (CirclePostCommentBean commentListBean : circlePostListBean.getComments()) {
                            user_ids.add(commentListBean.getUser_id());
                            user_ids.add(commentListBean.getReply_to_user_id());
                        }
                    }
                    if (user_ids.isEmpty()) {
                        return Observable.just(postListBeans);
                    }
                    return mUserInfoRepository.getUserInfo(user_ids)
                            .map(userinfobeans -> {
                                SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                for (UserInfoBean userInfoBean : userinfobeans) {
                                    userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(), userInfoBean);
                                }
                                for (CirclePostListBean circlePostListBean : postListBeans) {
                                    circlePostListBean.setUserInfoBean(userInfoBeanSparseArray.get(circlePostListBean
                                            .getUser_id().intValue()));
                                    if (circlePostListBean.getComments() == null || circlePostListBean.getComments()
                                            .isEmpty()) {
                                        continue;
                                    }
                                    for (int i = 0; i < circlePostListBean.getComments().size(); i++) {
                                        UserInfoBean tmpUserinfo = userInfoBeanSparseArray.get((int) circlePostListBean.getComments()
                                                .get(i).getUser_id());
                                        if (tmpUserinfo != null) {
                                            circlePostListBean.getComments().get(i).setCommentUser(tmpUserinfo);
                                        }
                                        if (circlePostListBean.getComments().get(i).getReply_to_user_id() == 0) {
                                            // 如果 reply_user_id = 0 回复动态
                                            UserInfoBean userInfoBean = new UserInfoBean();
                                            userInfoBean.setUser_id(0L);
                                            circlePostListBean.getComments().get(i).setReplyUser(userInfoBean);
                                        } else {
                                            if (userInfoBeanSparseArray.get((int) circlePostListBean.getComments()
                                                    .get(i).getReply_to_user_id()) != null) {
                                                circlePostListBean.getComments().get(i).setReplyUser
                                                        (userInfoBeanSparseArray.get((int) circlePostListBean.getComments()
                                                                .get(i).getReply_to_user_id()));
                                            }
                                        }
                                    }

                                }
                                mUserInfoBeanGreenDao.insertOrReplace(userinfobeans);
                                return postListBeans;
                            });
                });
    }
}
