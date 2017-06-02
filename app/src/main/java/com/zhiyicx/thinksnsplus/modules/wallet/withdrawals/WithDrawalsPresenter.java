package com.zhiyicx.thinksnsplus.modules.wallet.withdrawals;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.WithdrawResultBean;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action0;

/**
 * @Author Jliuer
 * @Date 2017/05/23/14:06
 * @Email Jliuer@aliyun.com
 * @Description
 */
@SuppressWarnings("unchecked")
public class WithDrawalsPresenter extends AppBasePresenter<WithDrawalsConstract.Repository, WithDrawalsConstract.View>
        implements WithDrawalsConstract.Presenter {

    @Inject
    public WithDrawalsPresenter(WithDrawalsConstract.Repository repository, WithDrawalsConstract.View rootView) {
        super(repository, rootView);
    }


    @Override
    public void withdraw(int value, String type, String account) {
        Subscription subscribe = mRepository.withdraw(value, type, account)
                .compose(mSchedulersTransformer)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mRootView.showSnackLoadingMessage(mContext.getString(R.string.withdraw_doing));
                    }
                })
                .subscribe(new BaseSubscribeForV2<WithdrawResultBean>() {
                    @Override
                    protected void onSuccess(WithdrawResultBean data) {
                        mRootView.showSnackSuccessMessage(mContext.getString(R.string.withdraw_succes));
                    }
                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackSuccessMessage(message);
                    }

                    @Override
                    protected void onException(Throwable throwable) {
                        super.onException(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
