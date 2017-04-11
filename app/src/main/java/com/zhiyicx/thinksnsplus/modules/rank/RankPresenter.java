package com.zhiyicx.thinksnsplus.modules.rank;

import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribe;
import com.zhiyicx.thinksnsplus.data.beans.DigBean;
import com.zhiyicx.thinksnsplus.data.source.local.DigBeanGreenDaoImpl;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/10
 * @Contact master.jungle68@gmail.com
 */

public class RankPresenter extends BasePresenter<RankContract.Repository, RankContract.View> implements RankContract.Presenter {

    @Inject
    DigBeanGreenDaoImpl mDigBeanGreenDao;

    @Inject
    public RankPresenter(RankContract.Repository repository, RankContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void requestNetData(Long maxId, final boolean isLoadMore) {
        Subscription subscription = mRepository.getDidRankList(mRootView.getPage())
                .subscribe(new BaseSubscribe<List<DigBean>>() {
                    @Override
                    protected void onSuccess(List<DigBean> data) {
                        mRootView.onNetResponseSuccess(data, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        mRootView.showMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                       mRootView.onResponseError(throwable,isLoadMore);
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public List<DigBean> requestCacheData(Long max_Id, boolean isLoadMore) {
        List<DigBean> cacheData = mDigBeanGreenDao.getMultiDataFromCache();
        return cacheData;
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<DigBean> data, boolean isLoadMore) {
        if (!isLoadMore) { // 刷新的时候清除数据
            mDigBeanGreenDao.clearTable();
        }
        mDigBeanGreenDao.saveMultiData(data);
        return true;
    }
}

