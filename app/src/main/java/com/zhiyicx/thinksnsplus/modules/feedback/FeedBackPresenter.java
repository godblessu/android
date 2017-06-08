package com.zhiyicx.thinksnsplus.modules.feedback;

import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.source.repository.SystemRepository;

import javax.inject.Inject;

import rx.functions.Action0;

/**
 * @Author Jliuer
 * @Date 2017/06/02/17:23
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class FeedBackPresenter extends AppBasePresenter<FeedBackContract.Repository, FeedBackContract.View>
        implements FeedBackContract.Presenter {

    @Inject
    SystemRepository mSystemRepository;

    @Inject
    public FeedBackPresenter(FeedBackContract.Repository repository, FeedBackContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void submitFeedBack(String content, String contract) {
        if (!(RegexUtils.isMobileExact(contract) && !RegexUtils.isEmail(content))) {
            mRootView.showWithdrawalsInstructionsPop();
        } else {
            String comment_mark = AppApplication.getmCurrentLoginAuth().getUser_id() + "" + System.currentTimeMillis();
            mSystemRepository.systemFeedback(content, Long.parseLong(comment_mark))
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mRootView.showSnackSuccessMessage(mContext.getString(R.string.feed_back_ing));
                        }
                    })
                    .subscribe(new BaseSubscribeForV2<BaseJson<Object>>() {
                        @Override
                        protected void onSuccess(BaseJson<Object> data) {
                            mRootView.showSnackSuccessMessage(mContext.getString(R.string.feed_back_success));
                        }

                        @Override
                        protected void onFailure(String message, int code) {
                            super.onFailure(message, code);
                            mRootView.showSnackErrorMessage(message);
                        }

                        @Override
                        protected void onException(Throwable throwable) {
                            super.onException(throwable);
                            mRootView.showSnackErrorMessage(mContext.getString(R.string.feed_back_failed));
                        }
                    });
        }
    }
}