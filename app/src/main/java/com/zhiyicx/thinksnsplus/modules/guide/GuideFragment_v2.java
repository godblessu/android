package com.zhiyicx.thinksnsplus.modules.guide;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.RealAdvertListBean;
import com.zhiyicx.thinksnsplus.modules.settings.aboutus.CustomWEBActivity;
import com.zhiyicx.thinksnsplus.utils.BannerImageLoaderUtil;
import com.zhiyicx.thinksnsplus.widget.TCountTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.modules.guide.GuideFragment.DEFAULT_DELAY_TIME;

public class GuideFragment_v2 extends TSFragment<GuideContract.Presenter> implements
        GuideContract.View,
        OnBannerListener, ViewPager.OnPageChangeListener, TCountTimer.OnTimeListener {

    @BindView(R.id.guide_banner)
    Banner mGuideBanner;
    @BindView(R.id.guide_text)
    TextView mGuideText;

    TCountTimer mTimer;
    Subscription subscription;
    int mPosition;

    boolean isFinish;
    boolean isClick;
    boolean isFirst = true;

    public static final String ADVERT = "advert";

    private List<RealAdvertListBean> mBootAdverts;

    public void onNewIntent(Intent intent) {
        isClick = false;
        isFirst = false;
        if (isFinish || mPosition != 0) {
            mPresenter.checkLogin();
        }
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_guide_v2;
    }

    @Override
    protected void initView(View rootView) {
        RxView.clicks(mGuideText).throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> mPresenter.checkLogin());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirst) {
            return;
        }
        if (com.zhiyicx.common.BuildConfig.USE_ADVERT) {
            mPresenter.getLaunchAdverts();
        } else {
            mPresenter.checkLogin();
            return;
        }
        subscription = Observable.timer(DEFAULT_DELAY_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map(aLong -> mPresenter.getBootAdvert()
                        != null && mPresenter.getAdvert() != null
                        && mPresenter.getAdvert().getAdverts() != null)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        if (com.zhiyicx.common.BuildConfig.USE_ADVERT) {
                            initAdvert();
                        }
                    } else {
                        mPresenter.checkLogin();
                    }
                }, throwable -> {

                });
    }


    @Override
    protected boolean showToolbar() {
        return false;
    }


    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected void initData() {
    }

    @Override
    public void startActivity(Class aClass) {
        repleaseAdvert();
        startActivity(new Intent(getActivity(), aClass));
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    private void repleaseAdvert() {
        if (!com.zhiyicx.common.BuildConfig.USE_ADVERT || mTimer == null)
            return;
        mGuideBanner.setOnPageChangeListener(null);
        mGuideBanner.stopAutoPlay();
        mTimer.replease();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mGuideBanner == null)
            return;
        mPosition = mGuideBanner.getCurrentItem();
        if (mPosition == mGuideBanner.getItemCount() - 1) {
            mGuideBanner.stopAutoPlay();
        }

        if (mPosition > 0) {
            mTimer.replease();
            mGuideBanner.setDelayTime(position * 2000);
            mTimer = mTimer.newBuilder()
                    .buildTimeCount(position * 2000)
                    .buildCanUseOntick(true)
                    .buildDurText(getString(R.string.skip))
                    .buildCanUseListener(mPosition == mGuideBanner.getItemCount() - 1)
                    .buildOnTimeListener(this)
                    .build();
            mTimer.start();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTick() {
    }

    @Override
    public void onFinish() {
        isFinish = true;
        if (isClick) {
            return;
        }
        mPresenter.checkLogin();
    }

    @Override
    public void OnBannerClick(int position) {

        isClick = true;
        if (isFinish) {
            return;
        }
//        mGuideBanner.stopAutoPlay();
        CustomWEBActivity.startToWEBActivity(getActivity(), mBootAdverts.get(position)
                        .getAdvertFormat().getImage().getLink(),
                mBootAdverts.get(position).getTitle(), ADVERT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.checkLogin();
    }

    @Override
    public void initAdvert() {
        List<String> urls = new ArrayList<>();
        mBootAdverts = mPresenter.getBootAdvert();
        if (mBootAdverts != null) {
            for (RealAdvertListBean realAdvertListBean : mBootAdverts) {
                urls.add(realAdvertListBean.getAdvertFormat().getImage().getImage());
            }
        }

        mGuideText.setVisibility(View.VISIBLE);
        mTimer = TCountTimer.builder()
                .buildBtn(mGuideText)
                .buildCanUseListener(urls.size() <= 1)// 单张图片
                .buildOnTimeListener(this)
                .buildCanUseOntick(false)
                .build();

        mGuideBanner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        mGuideBanner.setImageLoader(new BannerImageLoaderUtil());
        mGuideBanner.setImages(urls);
        mGuideBanner.isDownStopAutoPlay(false);
        mGuideBanner.setViewPagerIsScroll(false);
        mGuideBanner.setDelayTime(5000);
        mGuideBanner.setOnBannerListener(this);
        mGuideBanner.setOnPageChangeListener(this);
        mGuideBanner.start();
        mTimer.start();
    }
}
