package com.zhiyicx.thinksnsplus.modules.rank;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.thinksnsplus.data.beans.FollowFansBean;

import java.util.List;

import rx.Observable;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/10
 * @Contact master.jungle68@gmail.com
 */

public interface RankContract {
    interface View extends ITSListView<FollowFansBean, Presenter> {
        /**
         * 更新列表的关注状态
         *
         * @param index 需要更新的item位置
         */
        void upDateFollowFansState(int index);

        void upDateFollowFansState();

        /**
         * 获取页面类型
         */
        int getPageType();
    }

    interface Presenter extends ITSListPresenter<FollowFansBean> {
        /**
         * 重写获取网络数据的方法，添加方法参数
         *
         * @param maxId
         * @param isLoadMore
         * @param userId     用户id
         * @param pageType   详见FollowFansListFragment.class定义的页面类型
         */
        void requestNetData(Long maxId, boolean isLoadMore, long userId, int pageType);

        List<FollowFansBean> requestCacheData(Long maxId, boolean isLoadMore, long userId, int pageType);

        /**
         * 关注用户
         *
         * @param index          item所在的列表位置
         * @param followFansBean 被关注的用户id
         */
        void followUser(int index, FollowFansBean followFansBean);

        void cancleFollowUser(int index, FollowFansBean followFansBean);
    }

    interface Repository {

        Observable<BaseJson<List<FollowFansBean>>> getFollowListFromNet(long userId, int maxId);

        Observable<BaseJson<List<FollowFansBean>>> getFansListFromNet(long userId, int maxId);

        Observable<BaseJson> followUser(long followedId);

        Observable<BaseJson> cancleFollowUser(long followedId);

    }
}
