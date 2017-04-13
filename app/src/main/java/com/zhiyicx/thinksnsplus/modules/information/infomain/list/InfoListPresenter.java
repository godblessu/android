package com.zhiyicx.thinksnsplus.modules.information.infomain.list;

import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.common.utils.ToastUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.base.BaseSubscribe;
import com.zhiyicx.thinksnsplus.data.beans.InfoListBean;
import com.zhiyicx.thinksnsplus.data.source.local.InfoListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.InfoMainRepository;
import com.zhiyicx.thinksnsplus.modules.information.infomain.InfoMainContract;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @Author Jliuer
 * @Date 2017/03/14
 * @Email Jliuer@aliyun.com
 * @Description
 */
@FragmentScoped
public class InfoListPresenter extends BasePresenter<InfoMainContract.Reppsitory
        , InfoMainContract.InfoListView> implements InfoMainContract.InfoListPresenter {

    @Inject
    InfoMainRepository mInfoMainRepository;

    @Inject
    InfoListBeanGreenDaoImpl mInfoListBeanGreenDao;

    @Inject
    public InfoListPresenter(InfoMainContract.Reppsitory repository,
                             InfoMainContract.InfoListView rootInfoListView) {
        super(repository, rootInfoListView);
    }

    @Inject
    void setupListeners() {
        mRootView.setPresenter(this);
    }

    @Override
    public void requestNetData(Long maxId, final boolean isLoadMore) {
        Subscription subscription = mInfoMainRepository.getInfoList(mRootView.getInfoType()
                , maxId, mRootView.getPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribe<InfoListBean>() {
                    @Override
                    protected void onSuccess(InfoListBean data) {
                        List<BaseListBean> list = new ArrayList<>();
                        if (data.getRecommend() != null) {
                            list.addAll(data.getRecommend());
                        }
                        if (data.getList() != null) {
                            list.addAll(data.getList());
                        }
                        String typeString = mRootView.getInfoType();
                        int type = Integer.valueOf(typeString);
                        data.setInfo_type(type);
                        mInfoListBeanGreenDao.insertOrReplace(data);
                        mRootView.onNetResponseSuccess(list, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        mRootView.showMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        LogUtils.i("onException" + throwable.getMessage());
                        mRootView.onResponseError(throwable, isLoadMore);
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public List<BaseListBean> requestCacheData(Long max_Id, final boolean isLoadMore) {
        final List<BaseListBean> localData = new ArrayList<>();
        Observable.just(mInfoListBeanGreenDao)
                .map(new Func1<InfoListBeanGreenDaoImpl, InfoListBean>() {
                    @Override
                    public InfoListBean call(InfoListBeanGreenDaoImpl infoListBeanGreenDao) {
                        String typeString = mRootView.getInfoType();
                        int type = Integer.valueOf(typeString);
                        return infoListBeanGreenDao
                                .getInfoListByInfoType(type);
                    }
                })
                .filter(new Func1<InfoListBean, Boolean>() {
                    @Override
                    public Boolean call(InfoListBean infoListBean) {
                        return infoListBean != null;
                    }
                }).subscribe(new Action1<InfoListBean>() {
            @Override
            public void call(InfoListBean data) {
                if (data.getRecommend() != null) {
                    localData.addAll(data.getRecommend());
                }
                if (data.getList() != null) {
                    localData.addAll(data.getList());
                }
                data.setInfo_type(Integer.valueOf(mRootView.getInfoType()));
            }
        });
        return localData;
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<BaseListBean> data, boolean isLoadMore) {
        return false;
    }

    @Override
    public void getInfoList(String cate_id, long max_id, long limit, final long page) {

    }
}
