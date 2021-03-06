package com.zhiyicx.thinksnsplus.modules.chat.edit.owner;

import android.text.TextUtils;
import android.widget.TextView;

import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.UserInfoBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.EditGroupOwnerRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Catherine on 2018/1/22.
 */

public class EditGroupOwnerPresenter extends AppBasePresenter<EditGroupOwnerContract.View>
        implements EditGroupOwnerContract.Presenter {
    public static final int DEFAULT_MAX_GRUOP_NUMBER = 200;

    @Inject
    EditGroupOwnerRepository mRepository;

    @Inject
    public EditGroupOwnerPresenter(EditGroupOwnerContract.View rootView) {
        super(rootView);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        // 搜索时的刷新，只处理本地数据
        if (!TextUtils.isEmpty(mRootView.getsearchKeyWord())) {
            mRootView.hideRefreshState(isLoadMore);
            return;
        }
        getResult("");
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {

    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<UserInfoBean> data, boolean isLoadMore) {
        return false;
    }

    @Override
    public boolean checkNewOwner(UserInfoBean userInfoBean) {
        return userInfoBean != null && !userInfoBean.getUser_id().equals(AppApplication.getMyUserIdWithdefault());
    }

    @Override
    public List<UserInfoBean> getSearchResult(String key) {
        getResult(key);
        return null;
    }

    @Override
    public void updateGroup(ChatGroupBean chatGroupBean) {
        Subscription subscription = mRepository.updateGroup(chatGroupBean.getId(), chatGroupBean.getName(), chatGroupBean.getDescription(), 0,
                DEFAULT_MAX_GRUOP_NUMBER, chatGroupBean.isMembersonly(),
                0, chatGroupBean.getGroup_face(), false, chatGroupBean.getOwner() + "")
                .doOnSubscribe(() -> mRootView.showSnackLoadingMessage(mContext.getString(R.string.modifing)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<ChatGroupBean>() {
                    @Override
                    protected void onSuccess(ChatGroupBean data) {
                        // 成功后重置页面
                        LogUtils.d("updateGroup", data);
                        mRootView.updateGroup(data);
                        mRootView.dismissSnackBar();
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                        mRootView.showSnackErrorMessage(throwable.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mRootView.showSnackErrorMessage(e.getMessage());
                    }
                });
        addSubscrebe(subscription);
    }

    private void getResult(String key) {
        ChatGroupBean groupBean = mRootView.getGroupData();
        if (groupBean == null) {
            return;
        }
        List<UserInfoBean> list = groupBean.getAffiliations();
        // 移除自己
        Observable.just(list)
                .map(list1 -> {
                    int position = -1;
                    for (int i = 0; i < list1.size(); i++) {
                        if (list1.get(i).getUser_id().equals(AppApplication.getMyUserIdWithdefault())) {
                            position = i;
                            break;
                        }
                    }
                    if (position != -1) {
                        list1.remove(position);
                    }
                    return list1;
                })
                .subscribe(list12 -> {
                    // 有key表示是搜素，没有就是全部 直接获取就好了
                    if (TextUtils.isEmpty(key)) {
                        mRootView.onNetResponseSuccess(list12, false);
                    } else {
                        List<UserInfoBean> searchResult = new ArrayList<>();
                        for (UserInfoBean userInfoBean : mRootView.getGroupData().getAffiliations()) {
                            if (!TextUtils.isEmpty(userInfoBean.getName()) && userInfoBean.getName().toLowerCase().contains(key.toLowerCase())) {
                                searchResult.add(userInfoBean);
                            }
                        }
                        mRootView.onNetResponseSuccess(searchResult, false);
                    }
                });

    }
}
