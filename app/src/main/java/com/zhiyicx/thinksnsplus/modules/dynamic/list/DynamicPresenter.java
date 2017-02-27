package com.zhiyicx.thinksnsplus.modules.dynamic.list;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.BaseSubscribe;
import com.zhiyicx.thinksnsplus.config.BackgroundTaskRequestMethodConfig;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.BackgroundRequestTaskBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicToolBean;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicCommentBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicDetailBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicToolBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.AuthRepository;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskManager;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.functions.Action0;
import rx.functions.Func1;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/13
 * @Contact master.jungle68@gmail.com
 */
@FragmentScoped
public class DynamicPresenter extends BasePresenter<DynamicContract.Repository, DynamicContract.View> implements DynamicContract.Presenter {

    @Inject
    DynamicBeanGreenDaoImpl mDynamicBeanGreenDao;
    @Inject
    DynamicDetailBeanGreenDaoImpl mDynamicDetailBeanGreenDao;
    @Inject
    DynamicCommentBeanGreenDaoImpl mDynamicCommentBeanGreenDao;
    @Inject
    DynamicToolBeanGreenDaoImpl mDynamicToolBeanGreenDao;
    @Inject
    AuthRepository mAuthRepository;
    SparseArray<Long> msendingStatus = new SparseArray<>();

    @Inject
    public DynamicPresenter(DynamicContract.Repository repository, DynamicContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    /**
     * @param maxId      当前获取到数据的最大 id
     * @param isLoadMore 加载状态
     */
    @Override
    public void requestNetData(Long maxId, final boolean isLoadMore) {
        mRepository.getDynamicList(mRootView.getDynamicType(), maxId, mRootView.getPage())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        mRootView.hideLoading();
                    }
                })
                .map(new Func1<BaseJson<List<DynamicBean>>, BaseJson<List<DynamicBean>>>() {
                    @Override
                    public BaseJson<List<DynamicBean>> call(BaseJson<List<DynamicBean>> listBaseJson) {
                        if (!isLoadMore && listBaseJson.isStatus()) { // 如果是刷新，并且获取到了数据，更新发布的动态 ,把发布的动态信息放到请求数据的前面
                            insertOrUpdateDynamicDB(listBaseJson.getData());
                            if (mRootView.getDynamicType().equals(ApiConfig.DYNAMIC_TYPE_NEW)) {
                                List<DynamicBean> data = getDynamicBeenFromDB();
                                data.addAll(listBaseJson.getData());
                                listBaseJson.setData(data);
                            }
                        }
                        return listBaseJson;
                    }
                })
                .subscribe(new BaseSubscribe<List<DynamicBean>>() {
                    @Override
                    protected void onSuccess(List<DynamicBean> data) {
                        mRootView.onNetResponseSuccess(data, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message) {
                        mRootView.showMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        mRootView.onResponseError(throwable, isLoadMore);
                    }
                });
    }

    @Override
    public List<DynamicBean> requestCacheData(Long maxId, boolean isLoadMore) {
        List<DynamicBean> datas = null;
        switch (mRootView.getDynamicType()) {
            case ApiConfig.DYNAMIC_TYPE_FOLLOWS:
                datas = mDynamicBeanGreenDao.getFollowedDynamicList(maxId);
                break;
            case ApiConfig.DYNAMIC_TYPE_HOTS:
                datas = mDynamicBeanGreenDao.getHotDynamicList(maxId);
                break;
            case ApiConfig.DYNAMIC_TYPE_NEW:
                if (!isLoadMore) {// 刷新
                    datas = getDynamicBeenFromDB();
                    datas.addAll(mDynamicBeanGreenDao.getNewestDynamicList(maxId));
                } else {
                    datas = mDynamicBeanGreenDao.getNewestDynamicList(maxId);
                }

                break;
            default:
        }
        return datas;
    }

    /**
     * 此处需要先存入数据库，方便处理动态的状态，故此处不需要再次更新数据库
     *
     * @param data
     * @return
     */
    @Override
    public boolean insertOrUpdateData(@NotNull List<DynamicBean> data) {
        return true;
    }

    /**
     * 动态数据库更新
     *
     * @param data
     */
    private void insertOrUpdateDynamicDB(@NotNull List<DynamicBean> data) {
        List<DynamicDetailBean> dynamicDetailBeen = new ArrayList<>();
        List<DynamicCommentBean> dynamicCommentBeen = new ArrayList<>();
        List<DynamicToolBean> dynamicToolBeen = new ArrayList<>();
        for (DynamicBean dynamicBean : data) {
            dynamicBean.setFeed_id(dynamicBean.getFeed().getFeed_id());
            dynamicBean.getFeed().setFeed_mark(dynamicBean.getFeed_mark());
            dynamicBean.getTool().setFeed_mark(dynamicBean.getFeed_mark());
            for (DynamicCommentBean dynamicCommentBean : dynamicBean.getComments()) {
                dynamicCommentBean.setFeed_mark(dynamicBean.getFeed_mark());
            }
            dynamicDetailBeen.add(dynamicBean.getFeed());
            dynamicCommentBeen.addAll(dynamicBean.getComments());
            dynamicToolBeen.add(dynamicBean.getTool());

        }
        System.out.println("data = " + data.toString());
        mDynamicBeanGreenDao.insertOrReplace(data);
        mDynamicDetailBeanGreenDao.insertOrReplace(dynamicDetailBeen);
        mDynamicCommentBeanGreenDao.insertOrReplace(dynamicCommentBeen);
        mDynamicToolBeanGreenDao.insertOrReplace(dynamicToolBeen);
    }

    /**
     * 处理发送动态数据
     *
     * @param dynamicBean
     */
    @Subscriber(tag = EventBusTagConfig.EVENT_SEND_DYNAMIC_TO_LIST)
    public void handleSendDynamic(DynamicBean dynamicBean) {
        if (mRootView.getDynamicType().equals(ApiConfig.DYNAMIC_TYPE_NEW)) {
            int position = hasContanied(dynamicBean);
            if (position != -1) {
                mRootView.refresh(position);
            } else {
                mRootView.getDatas().add(dynamicBean); // TODO: 2017/2/27 加到头部
                mRootView.refresh();
            }

        }
    }

    private int hasContanied(DynamicBean dynamicBean) {
        int size = mRootView.getDatas().size();
        for (int i = 0; i < size; i++) {
            if (mRootView.getDatas().get(i).getFeed_mark() == dynamicBean.getFeed_mark()) {
                mRootView.getDatas().get(i).setState(dynamicBean.getState());
                return i;
            }
        }
        return -1;
    }

    @NonNull
    private List<DynamicBean> getDynamicBeenFromDB() {
        List<DynamicBean> datas = mDynamicBeanGreenDao.getMySendingDynamic((long) AppApplication.getmCurrentLoginAuth().getUser_id());
        msendingStatus.clear();
        for (int i = 0; i < datas.size(); i++) {
            msendingStatus.put(i, datas.get(i).getFeed_mark());
        }
        return datas;
    }

    /**
     * handle like or cancle like in background
     *
     * @param isLiked true,do like ,or  cancle like
     * @param feed_id the dynamic id
     */
    @Override
    public void handleLike(boolean isLiked, Long feed_id) {
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
}