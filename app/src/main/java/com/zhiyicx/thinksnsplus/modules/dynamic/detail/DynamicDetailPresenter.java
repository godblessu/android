package com.zhiyicx.thinksnsplus.modules.dynamic.detail;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.common.thridmanager.share.ShareContent;
import com.zhiyicx.common.thridmanager.share.SharePolicy;
import com.zhiyicx.rxerrorhandler.functions.RetryWithDelay;
import com.zhiyicx.thinksnsplus.base.BaseSubscribe;
import com.zhiyicx.thinksnsplus.config.BackgroundTaskRequestMethodConfig;
import com.zhiyicx.thinksnsplus.data.beans.BackgroundRequestTaskBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDigListBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicToolBean;
import com.zhiyicx.thinksnsplus.data.beans.FollowFansBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicToolBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.FollowFansBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskManager;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author LiuChao
 * @describe
 * @date 2017/2/27
 * @contact email:450127106@qq.com
 */
@FragmentScoped
public class DynamicDetailPresenter extends BasePresenter<DynamicDetailContract.Repository,
        DynamicDetailContract.View> implements DynamicDetailContract.Presenter {

    @Inject
    DynamicToolBeanGreenDaoImpl mDynamicToolBeanGreenDao;
    @Inject
    FollowFansBeanGreenDaoImpl mFollowFansBeanGreenDao;
    @Inject
    public SharePolicy mSharePolicy;

    @Inject
    public DynamicDetailPresenter(DynamicDetailContract.Repository repository, DynamicDetailContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        // 更新点赞列表
        // 更新评论列表
    }

    @Override
    public List<DynamicBean> requestCacheData(Long max_Id, boolean isLoadMore) {
        // 从数据库获取评论列表
        // 从数据库获取点赞列表
        // 从数据库获取关注状态，如果没有从服务器获取
        return null;
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<DynamicBean> data) {
        return false;
    }

    @Override
    public void getDynamicDigList(Long feed_id, Long max_id) {
        Subscription subscription = mRepository.getDynamicDigList(feed_id, max_id)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(ApiConfig.DEFAULT_MAX_RETRY_COUNT, 0))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<List<FollowFansBean>>() {
                    @Override
                    protected void onSuccess(List<FollowFansBean> data) {
                        mRootView.setDigHeadIcon(data);
                    }

                    @Override
                    protected void onFailure(String message) {

                    }

                    @Override
                    protected void onException(Throwable throwable) {

                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void handleLike(boolean isLiked, final Long feed_id, final DynamicToolBean dynamicToolBean) {
        // 更新UI
        mRootView.setLike(isLiked);
        // 更新数据库
        mDynamicToolBeanGreenDao.insertOrReplace(dynamicToolBean);
        // 通知服务器
        BackgroundRequestTaskBean backgroundRequestTaskBean;
        HashMap<String, Object> params = new HashMap<>();
        params.put("feed_id", feed_id);
        // 后台处理
        if (isLiked) {
            backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.POST, params);
        } else {
            backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.DELETE, params);
        }
        backgroundRequestTaskBean.setPath(String.format(ApiConfig.APP_PATH_DYNAMIC_HANDLE_LIKE_FORMAT, feed_id));
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
    }

    @Override
    public void handleCollect(boolean isCollected, Long feed_id, DynamicToolBean dynamicToolBean) {
        // 更新UI
        mRootView.setCollect(isCollected);
        // 更新数据库
        mDynamicToolBeanGreenDao.insertOrReplace(dynamicToolBean);
        // 通知服务器
        BackgroundRequestTaskBean backgroundRequestTaskBean;
        HashMap<String, Object> params = new HashMap<>();
        params.put("feed_id", feed_id);
        // 后台处理
        if (isCollected) {
            backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.POST, params);
        } else {
            backgroundRequestTaskBean = new BackgroundRequestTaskBean(BackgroundTaskRequestMethodConfig.DELETE, params);
        }
        backgroundRequestTaskBean.setPath(String.format(ApiConfig.APP_PATH_HANDLE_COLLECT_FORMAT, feed_id));
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
    }

    @Override
    public void shareDynamic() {
        ShareContent shareContent = new ShareContent();
        mSharePolicy.setShareContent(shareContent);
        mSharePolicy.showShare(((TSFragment) mRootView).getActivity());
    }

    @Override
    public void handleFollowUser(FollowFansBean followFansBean) {
        BackgroundRequestTaskBean backgroundRequestTaskBean = null;
        if (followFansBean.getOrigin_follow_status() == FollowFansBean.UNFOLLOWED_STATE) {
            // 当前未关注，进行关注
            followFansBean.setOrigin_follow_status(FollowFansBean.IFOLLOWED_STATE);
            // 进行后台任务请求
            backgroundRequestTaskBean = new BackgroundRequestTaskBean();
            backgroundRequestTaskBean.setMethodType(BackgroundTaskRequestMethodConfig.POST);
            backgroundRequestTaskBean.setPath(ApiConfig.APP_PATH_FOLLOW_USER);
        } else {
            // 已关注，取消关注
            followFansBean.setOrigin_follow_status(FollowFansBean.UNFOLLOWED_STATE);
            // 进行后台任务请求
            backgroundRequestTaskBean = new BackgroundRequestTaskBean();
            backgroundRequestTaskBean.setMethodType(BackgroundTaskRequestMethodConfig.DELETE);
            backgroundRequestTaskBean.setPath(ApiConfig.APP_PATH_CANCEL_FOLLOW_USER);
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user_id", followFansBean.getTargetUserId() + "");
        backgroundRequestTaskBean.setParams(hashMap);
        BackgroundTaskManager.getInstance(mContext).addBackgroundRequestTask(backgroundRequestTaskBean);
        // 本地数据库和ui进行刷新
        mFollowFansBeanGreenDao.insertOrReplace(followFansBean);
        mRootView.upDateFollowFansState(followFansBean.getFollowState());
    }

    @Override
    public void getUserFollowState(String user_ids) {
        Subscription subscription = mRepository.getUserFollowState(user_ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<List<FollowFansBean>>() {
                    @Override
                    protected void onSuccess(List<FollowFansBean> data) {
                        mRootView.initFollowState(data.get(0));
                    }

                    @Override
                    protected void onFailure(String message) {

                    }

                    @Override
                    protected void onException(Throwable throwable) {

                    }
                });
        addSubscrebe(subscription);
    }

}
