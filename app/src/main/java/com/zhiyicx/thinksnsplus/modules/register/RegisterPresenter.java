package com.zhiyicx.thinksnsplus.modules.register;

import android.os.CountDownTimer;
import android.support.annotation.Nullable;

import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.thinksnsplus.R;

import javax.inject.Inject;

import butterknife.BindString;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2016/12/28
 * @Contact master.jungle68@gmail.com
 */
@FragmentScoped
public class RegisterPresenter extends BasePresenter<RegisterContract.Repository, RegisterContract.View> implements RegisterContract.Presenter {

    public static int S_TO_MS_SPACING = 1000; // s 和 ms 的比例
    public static int SNS_TIME = 60 * S_TO_MS_SPACING; // 发送短信间隔时间，单位 ms
    @Nullable
    @BindString(R.string.seconds)
    String mScondsStr;
    @Nullable
    @BindString(R.string.send_vertify_code)
    String mSendVertifyCodeStr;
    @Nullable
    @BindString(R.string.phone_number_toast_hint)
    String mPhoneNumberErrorStr;
    @Nullable
    @BindString(R.string.err_net_not_work)
    String mNetErrorStr;


    private int mTimeOut = SNS_TIME;

    CountDownTimer timer = new CountDownTimer(mTimeOut, S_TO_MS_SPACING) {

        @Override
        public void onTick(long millisUntilFinished) {
            mRootView.setVertifyCodeBtText(millisUntilFinished / S_TO_MS_SPACING + mScondsStr);//显示倒数的秒速
        }

        @Override
        public void onFinish() {
            mRootView.setVertifyCodeBtEnabled(true);//恢复初始化状态
            mRootView.setVertifyCodeBtText(mSendVertifyCodeStr);
        }
    };


    @Inject
    public RegisterPresenter(RegisterContract.Repository repository, RegisterContract.View rootView) {
        super(repository, rootView);
    }

    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        mRootView.setPresenter(this);
    }

    @Override
    public void getVertifyCode(String phone) {
        if (!RegexUtils.isMobileExact(phone)) {
            mRootView.showMessage(mPhoneNumberErrorStr);
            return;
        }
        mRepository.getVertifyCode(phone)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseJson<String>>() {
                    @Override
                    public void call(BaseJson<String> json) {
//                        if (json.code.equals(ZBLApi.REQUEST_SUCESS)) {
                            mRootView.hideLoading();//隐藏loading
                            timer.start();//开始倒计时
                            mRootView.setVertifyCodeBtEnabled(false);
                            mRootView.showMessage(json.getData());
//                        } else {
//                            mRootView.showMessage(json.getMessage());
//                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        mRootView.showMessage(mNetErrorStr);
                    }
                });
    }

    @Override
    public void register(String nickName, String phone, String vertifyCode, String password) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        timer.cancel();
    }

}
