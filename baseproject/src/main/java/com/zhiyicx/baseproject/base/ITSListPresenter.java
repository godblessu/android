package com.zhiyicx.baseproject.base;

import com.zhiyicx.common.mvp.i.IBasePresenter;

import java.util.List;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/8
 * @Contact master.jungle68@gmail.com
 */
public interface ITSListPresenter<T> extends IBasePresenter {
    /**
     * 请求列表数据
     *
     * @param maxId 当前获取到数据的最大 id
     * @param isLoadMore 加载状态
     */
    void requestNetData(int maxId, boolean isLoadMore);
    /**
     * 请求缓存列表数据
     *
     * @param minTime 当前获取到数据的最小时间
     * @param isLoadMore 加载状态
     * @return  返回数据按时间排序
     */
    List<T> requestCacheData(int minTime, boolean isLoadMore);


}
