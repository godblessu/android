package com.zhiyicx.thinksnsplus.modules.q_a.detail.answer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.impl.share.UmengSharePolicyImpl;
import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.common.thridmanager.share.OnShareCallbackListener;
import com.zhiyicx.common.thridmanager.share.Share;
import com.zhiyicx.common.thridmanager.share.ShareContent;
import com.zhiyicx.common.thridmanager.share.SharePolicy;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.config.ErrorCodeConfig;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.AnswerCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.AnswerDigListBean;
import com.zhiyicx.thinksnsplus.data.beans.AnswerInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.InfoCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.RealAdvertListBean;
import com.zhiyicx.thinksnsplus.data.beans.RewardsCountBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.AnswerInfoListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.AllAdvertListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.AnswerCommentListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.UserInfoBeanGreenDaoImpl;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhiyicx.baseproject.config.ApiConfig.APP_DOMAIN;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_INFO_DETAILS_FORMAT;
import static com.zhiyicx.thinksnsplus.config.EventBusTagConfig.EVENT_SEND_INFO_LIST_COLLECT;
import static com.zhiyicx.thinksnsplus.config.EventBusTagConfig.EVENT_SEND_INFO_LIST_DELETE_UPDATE;
import static com.zhiyicx.thinksnsplus.data.beans.InfoCommentListBean.SEND_ING;

/**
 * @Author Jliuer
 * @Date 2017/03/15
 * @Email Jliuer@aliyun.com
 * @Description
 */
@FragmentScoped
public class AnswerDetailsPresenter extends AppBasePresenter<AnswerDetailsConstract.Repository,
        AnswerDetailsConstract.View> implements AnswerDetailsConstract.Presenter, OnShareCallbackListener {

    @Inject
    public SharePolicy mSharePolicy;

    @Inject
    UserInfoBeanGreenDaoImpl mUserInfoBeanGreenDao;

    @Inject
    AnswerCommentListBeanGreenDaoImpl mAnswerCommentListBeanGreenDao;

    @Inject
    AnswerInfoListBeanGreenDaoImpl mAnswerInfoListBeanGreenDao;

    @Inject
    AllAdvertListBeanGreenDaoImpl mAllAdvertListBeanGreenDao;

    @Inject
    public AnswerDetailsPresenter(AnswerDetailsConstract.Repository repository, AnswerDetailsConstract
            .View rootView) {
        super(repository, rootView);
    }


    @Override
    public void requestNetData(Long maxId, final boolean isLoadMore) {
        if (mRootView.getAnswerInfo().getCommentList() == null) {
            getAnswerDetail(mRootView.getAnswerInfo().getId());
        } else {
            mRepository.getAnswerCommentList(mRootView.getAnswerId(), maxId)
                    .subscribe(new BaseSubscribeForV2<List<AnswerCommentListBean>>() {
                        @Override
                        protected void onSuccess(List<AnswerCommentListBean> data) {
                            mRootView.onNetResponseSuccess(data, isLoadMore);
                        }

                        @Override
                        protected void onFailure(String message, int code) {
                            super.onFailure(message, code);
                            handleInfoHasBeDeleted(code);
                        }

                        @Override
                        protected void onException(Throwable throwable) {
                            super.onException(throwable);
                            mRootView.onResponseError(throwable, isLoadMore);
                        }
                    });
        }
    }

    private void handleInfoHasBeDeleted(int code) {
        if (code == ErrorCodeConfig.DATA_HAS_BE_DELETED) {
            mAnswerInfoListBeanGreenDao.deleteSingleCache(mRootView.getAnswerInfo());
            EventBus.getDefault().post(mRootView.getAnswerInfo(), EVENT_SEND_INFO_LIST_DELETE_UPDATE);
            mRootView.infoMationHasBeDeleted();
        } else {
            mRootView.loadAllError();
        }
    }

    @Override
    public void shareInfo(Bitmap bitmap) {
        ((UmengSharePolicyImpl) mSharePolicy).setOnShareCallbackListener(this);
        ShareContent shareContent = new ShareContent();
        shareContent.setTitle("ThinkSNS+\b\b资讯");
        shareContent.setUrl(String.format(Locale.getDefault(), APP_DOMAIN + APP_PATH_INFO_DETAILS_FORMAT,
                mRootView.getAnswerInfo().getId()));
        shareContent.setContent(mRootView.getAnswerInfo().getBody());

        if (bitmap == null) {
            shareContent.setBitmap(ConvertUtils.drawBg4Bitmap(Color.WHITE, BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_256)));
        } else {
            shareContent.setBitmap(bitmap);
        }

//        if (mRootView.getAnswerInfo().getImage() != null) {
//            shareContent.setImage(ImageUtils.imagePathConvertV2(mRootView.getAnswerInfo()
//                            .getImage().getId()
//                    , mContext.getResources().getDimensionPixelOffset(R.dimen.headpic_for_user_home)
//                    , mContext.getResources().getDimensionPixelOffset(R.dimen.headpic_for_user_home)
//                    , ImageZipConfig.IMAGE_70_ZIP));
//        }
        mSharePolicy.setShareContent(shareContent);
        mSharePolicy.showShare(((TSFragment) mRootView).getActivity());
    }

    @Override
    public void handleCollect(boolean isUnCollected, long answer_id) {
        if (AppApplication.getmCurrentLoginAuth() == null) {
            return;
        }
        mRootView.setCollect(isUnCollected);
        mRootView.getAnswerInfo().setCollected(isUnCollected);

        mAnswerInfoListBeanGreenDao.insertOrReplace(mRootView.getAnswerInfo());
        EventBus.getDefault().post(mRootView.getAnswerInfo(), EVENT_SEND_INFO_LIST_COLLECT);
        mRepository.handleCollect(isUnCollected, answer_id);
    }

    @Override
    public void handleLike(boolean isLiked, long news_id) {
        if (AppApplication.getmCurrentLoginAuth() == null) {
            return;
        }
        UserInfoBean userInfoBean = mUserInfoBeanGreenDao
                .getSingleDataFromCache(AppApplication.getmCurrentLoginAuth().getUser_id());
        AnswerDigListBean digListBean = new AnswerDigListBean();
        digListBean.setUser_id(userInfoBean.getUser_id());
        digListBean.setId(System.currentTimeMillis());
        digListBean.setDiggUserInfo(userInfoBean);
        if (mRootView.getAnswerInfo().getLikes() == null) {
            mRootView.getAnswerInfo().setLikes(new ArrayList<>());
        }
        if (isLiked) {
            mRootView.getAnswerInfo().getLikes().add(digListBean);
            mRootView.getAnswerInfo().setLikes_count(mRootView.getAnswerInfo().getLikes_count() + 1);
        } else {
            for (AnswerDigListBean answerDigListBean : mRootView.getAnswerInfo().getLikes()) {
                if (answerDigListBean.getUser_id().equals(userInfoBean.getUser_id())) {
                    mRootView.getAnswerInfo().getLikes().remove(answerDigListBean);
                    mRootView.getAnswerInfo().setLikes_count(mRootView.getAnswerInfo().getLikes_count() - 1);
                    break;
                }
            }
        }
        mRootView.getAnswerInfo().setLiked(isLiked);
        mRootView.setDigg(isLiked);

        mAnswerInfoListBeanGreenDao.insertOrReplace(mRootView.getAnswerInfo());
        mRepository.handleLike(isLiked, news_id);
    }

    @Override
    public void reqReWardsData(int id) {

    }

    @Override
    public void getAnswerDetail(long answer_id) {
        Subscription subscription = Observable.zip(mRepository.getAnswerDetail(answer_id),
                mRepository.getAnswerCommentList(answer_id, 0L), (answerInfoBean, answerCommentListBeen) -> {
                    answerInfoBean.setCommentList(answerCommentListBeen);
                    return answerInfoBean;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscribeForV2<AnswerInfoBean>() {
                    @Override
                    protected void onSuccess(AnswerInfoBean data) {
                        mRootView.updateReWardsView(new RewardsCountBean(data.getRewarder_count(), "" + data.getRewards_amount()),
                                data.getRewarders());
                        mRootView.updateAnswerHeader(data);
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void deleteInfo() {
//        mRootView.deleteInfo(true, false, "");
//        Subscription subscription = mRepository.deleteInfo(String.valueOf(mRootView.getAnswerInfo().getCategory().getId()),
//                String.valueOf(mRootView.getNewsId()))
//                .compose(mSchedulersTransformer)
//                .subscribe(new BaseSubscribeForV2<BaseJsonV2<Object>>() {
//
//                    @Override
//                    protected void onSuccess(BaseJsonV2<Object> data) {
//                        mRootView.deleteInfo(false, true, "");
//                    }
//
//                    @Override
//                    protected void onFailure(String message, int code) {
//                        super.onFailure(message, code);
//                        mRootView.deleteInfo(false, false, message);
//                    }
//                });
//        addSubscrebe(subscription);
    }

    @Override
    public void deleteComment(AnswerCommentListBean data) {
        mAnswerCommentListBeanGreenDao.deleteSingleCache(data);
        mRootView.getListDatas().remove(data);
        mRootView.getAnswerInfo().setComments_count(mRootView.getAnswerInfo().getComments_count() - 1);
        if (mRootView.getListDatas().size() == 1) {// 占位
            AnswerCommentListBean emptyData = new AnswerCommentListBean();
            mRootView.getListDatas().add(emptyData);
        }
        mRootView.refreshData();
       // mRepository.deleteComment(mRootView.getNewsId().intValue(), data.getId().intValue());
    }

    @Override
    public List<RealAdvertListBean> getAdvert() {
        if (!com.zhiyicx.common.BuildConfig.USE_ADVERT || mAllAdvertListBeanGreenDao.getInfoDetailAdvert() == null) {
            return new ArrayList<>();
        }
        return mAllAdvertListBeanGreenDao.getInfoDetailAdvert().getMRealAdvertListBeen();
    }


    /**
     * 处理发送动态数据
     */
    @Subscriber(tag = EventBusTagConfig.EVENT_SEND_COMMENT_TO_INFO_LIST)
    public void handleSendComment(InfoCommentListBean infoCommentListBean) {
        LogUtils.d(TAG, "dynamicCommentBean = " + infoCommentListBean.toString());
        Observable.just(infoCommentListBean)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(infoCommentListBean1 -> {
                    int size = mRootView.getListDatas().size();
                    int infoPosition = -1;
                    for (int i = 0; i < size; i++) {
//                        if (mRootView.getListDatas().get(i).getComment_mark()
//                                == infoCommentListBean1.getComment_mark()) {
//                            infoPosition = i;
//                            mRootView.getListDatas().get(i).setState(infoCommentListBean1
//                                    .getState());
//                            mRootView.getListDatas().get(i).setId(infoCommentListBean1.getId());
//                            mRootView.getListDatas().get(i).setComment_mark
//                                    (infoCommentListBean1.getComment_mark());
//                            break;
//                        }
                    }
                    return infoPosition;
                })
                .subscribe(integer -> {
                    if (integer > 0) {
                        mRootView.refreshData(); // 加上 header
                    }

                }, throwable -> throwable.printStackTrace());

    }

    @Override
    public void sendComment(int reply_id, String content) {
        InfoCommentListBean createComment = new InfoCommentListBean();

        createComment.setInfo_id(mRootView.getAnswerId().intValue());

        createComment.setState(SEND_ING);

        createComment.setComment_content(content);

        createComment.setReply_to_user_id(reply_id);

        createComment.setCreated_at(TimeUtils.getCurrenZeroTimeStr());

        createComment.setUser_id(AppApplication.getmCurrentLoginAuth().getUser_id());

        String comment_mark = AppApplication.getmCurrentLoginAuth().getUser_id()
                + "" + System.currentTimeMillis();
        createComment.setComment_mark(Long.parseLong(comment_mark));

        if (reply_id == 0) {// 回复资讯
            UserInfoBean userInfoBean = new UserInfoBean();
            userInfoBean.setUser_id(0L);
            createComment.setToUserInfoBean(userInfoBean);
        } else {
            createComment.setToUserInfoBean(mUserInfoBeanGreenDao.getSingleDataFromCache(
                    (long) reply_id));
        }
        createComment.setFromUserInfoBean(mUserInfoBeanGreenDao.getSingleDataFromCache(
                AppApplication.getmCurrentLoginAuth().getUser_id()));
//        mInfoCommentListBeanDao.insertOrReplace(createComment);
//        if (mRootView.getListDatas().get(0).getComment_content() == null) {
//            mRootView.getListDatas().remove(0);// 去掉占位图
//        }
//        mRootView.getListDatas().add(0, createComment);
//        mRootView.getAnswerInfo().setComment_count(mRootView.getAnswerInfo().getComment_count() + 1);
//        mRootView.refreshData();
//        mRepository.sendComment(content, mRootView.getNewsId(), reply_id,
//                createComment.getComment_mark());
    }

    @Override
    public void onStart(Share share) {
    }

    @Override
    public void onSuccess(Share share) {
        mRootView.showSnackSuccessMessage(mContext.getString(R.string.share_sccuess));
    }

    @Override
    public void onError(Share share, Throwable throwable) {
        mRootView.showSnackErrorMessage(mContext.getString(R.string.share_fail));
    }

    @Override
    public void onCancel(Share share) {
        mRootView.showSnackSuccessMessage(mContext.getString(R.string.share_cancel));
    }

    @Override
    public List<AnswerCommentListBean> requestCacheData(Long max_Id, boolean isLoadMore) {
        return null;
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<AnswerCommentListBean> data, boolean isLoadMore) {
        return false;
    }


}
