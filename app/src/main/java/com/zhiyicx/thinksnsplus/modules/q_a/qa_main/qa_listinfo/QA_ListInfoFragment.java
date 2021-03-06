package com.zhiyicx.thinksnsplus.modules.q_a.qa_main.qa_listinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.widget.popwindow.PayPopWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.question.QuestionDetailActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.Subscriber;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow.POPUPWINDOW_ALPHA;
import static com.zhiyicx.thinksnsplus.modules.dynamic.list.DynamicFragment.ITEM_SPACING;
import static com.zhiyicx.thinksnsplus.modules.q_a.detail.question.QuestionDetailActivity.BUNDLE_QUESTION_BEAN;

/**
 * @Author Jliuer
 * @Date 2017/07/25/10:47
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class QA_ListInfoFragment extends TSListFragment<QA_ListInfoConstact.Presenter, QAListInfoBean>
        implements QA_ListInfoConstact.View, SpanTextClickable.SpanTextClickListener {

    public static final String BUNDLE_QA_TYPE = "qa_type";

    private String mQAInfoType;

    public String[] QA_TYPES;

    private PayPopWindow mPayWatchPopWindow; // 围观答案

    @Inject
    QA_ListInfoFragmentPresenter mQA_listInfoFragmentPresenter;
    private boolean mIsLoadedNetData;

    public static QA_ListInfoFragment newInstance(String params) {
        QA_ListInfoFragment fragment = new QA_ListInfoFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_QA_TYPE, params);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getQAInfoType() {
        return mQAInfoType;
    }

    @Override
    public void showDeleteSuccess() {
//        showSnackSuccessMessage(getString(R.string.qa_question_delete_success));
    }

    @Override
    protected void onEmptyViewClick() {
        mRefreshlayout.autoRefresh();
    }

    @Override
    protected boolean isNeedRefreshDataWhenComeIn() {
        return true;
    }

    @Override
    protected boolean isNeedRefreshAnimation() {
        return true;
    }

    @Override
    protected boolean setUseSatusbar() {
        return false;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QA_TYPES = getResources().getStringArray(R.array.qa_net_type);
        mQAInfoType = getArguments().getString(BUNDLE_QA_TYPE);
        Observable.create(subscriber -> {
            DaggerQA_ListInfoComponent
                    .builder().appComponent(AppApplication.AppComponentHolder.getAppComponent())
                    .qA_listInfoFragmentPresenterModule(new QA_listInfoFragmentPresenterModule(this))
                    .build().inject(this);
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        initData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });

    }

    @Override
    protected void initData() {
        super.initData();
        // 控制 + 号按钮的隐藏显示
//        mRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                QA_InfoContainerFragment infoContainerFragment = (QA_InfoContainerFragment) getParentFragment();
//                infoContainerFragment.addBtnAnimation(dy > 0);
//            }
//        });

    }

    @Override
    protected void requestNetData(Long maxId, boolean isLoadMore) {
        requestNetData(null, maxId, mQAInfoType, isLoadMore);
    }

    private void requestNetData(String subject, Long maxId, String type, boolean isLoadMore) {
        mPresenter.requestNetData(subject, maxId, type, isLoadMore);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mPresenter != null && !mIsLoadedNetData) {
            startRefrsh();
            mIsLoadedNetData = true;
        }
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<QAListInfoBean> data, boolean isLoadMore) {
        mIsLoadedNetData = true;
        super.onNetResponseSuccess(data, isLoadMore);
    }

    @Override
    protected float getItemDecorationSpacing() {
        return ITEM_SPACING;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        QAListInfoAdapter adapter = new QAListInfoAdapter(getActivity(), mListDatas) {
            @Override
            protected int getExcellentTag(boolean isExcellent) {
                boolean isNewOrExcellent = getQAInfoType().equals(QA_TYPES[1]) || getQAInfoType().equals(QA_TYPES[3]);
                return isNewOrExcellent ? 0 : (isExcellent ? R.mipmap.icon_choice : 0);
            }

            @Override
            protected boolean isTourist() {
                return mPresenter.handleTouristControl();
            }

            @Override
            protected int getRatio() {
                return mPresenter.getRatio();
            }
        };
        adapter.setSpanTextClickListener(this);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (mPresenter.handleTouristControl() || position < 0) {
                    return;
                }
                Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                Bundle bundle = new Bundle();
                QAListInfoBean listInfoBean = mListDatas.get(position);
                bundle.putSerializable(BUNDLE_QUESTION_BEAN, listInfoBean);
                intent.putExtra(BUNDLE_QUESTION_BEAN, bundle);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        return adapter;
    }

    @Override
    protected Long getMaxId(@NotNull List<QAListInfoBean> data) {
        return (long) mListDatas.size();
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    public void onSpanClick(long answer_id, int position) {
        initOnlookPopWindow(answer_id, position);
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_UPDATE_QUESTION_DELETE)
    public void updateList(Bundle bundle) {
        if (bundle != null) {
            QAListInfoBean qaListInfoBean = (QAListInfoBean) bundle.
                    getSerializable(EventBusTagConfig.EVENT_UPDATE_QUESTION_DELETE);
            if (qaListInfoBean != null) {
                for (int i = 0; i < mListDatas.size(); i++) {
                    if (qaListInfoBean.getId().equals(mListDatas.get(i).getId())) {
                        mListDatas.remove(i);
                        refreshData();
                        showDeleteSuccess();
                        break;
                    }
                }
            }
        }
    }

    private void initOnlookPopWindow(long answer_id, int pisotion) {
        mPayWatchPopWindow = PayPopWindow.builder()
                .with(getActivity())
                .isWrap(true)
                .isFocus(true)
                .isOutsideTouch(true)
                .buildLinksColor1(R.color.themeColor)
                .buildLinksColor2(R.color.important_for_content)
                .contentView(R.layout.ppw_for_center)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .buildDescrStr(String.format(getString(R.string.qa_pay_for_watch_answer_hint) + getString(R
                                .string.buy_pay_member),
                        mPresenter.getSystemConfig().getOnlookQuestion()
                        , mPresenter.getGoldName()))
                .buildLinksStr(getString(R.string.qa_pay_for_watch))
                .buildTitleStr(getString(R.string.qa_pay_for_watch))
                .buildItem1Str(getString(R.string.buy_pay_in_payment))
                .buildItem2Str(getString(R.string.buy_pay_out))
                .buildMoneyStr(getString(R.string.buy_pay_integration, mPresenter.getSystemConfig()
                        .getOnlookQuestion()))
                .buildCenterPopWindowItem1ClickListener(() -> {
                    mPresenter.payForOnlook(answer_id, pisotion);
                    mPayWatchPopWindow.hide();
                })
                .buildCenterPopWindowItem2ClickListener(() -> mPayWatchPopWindow.hide())
                .buildCenterPopWindowLinkClickListener(new PayPopWindow
                        .CenterPopWindowLinkClickListener() {
                    @Override
                    public void onLongClick() {

                    }

                    @Override
                    public void onClicked() {

                    }
                })
                .build();
        mPayWatchPopWindow.show();
    }

    /**
     * 默认值 new, all - 全部、new - 最新、hot - 热门、reward - 悬赏、excellent - 精选 follow - 关注
     */
    public enum QuestionType {
        ALL("all"),
        NEWS("new"),
        HOT("hot"),
        REWARD("reward"),
        EXCELLENT("excellent"),
        FOLLOW("follow");

        public String value;

        QuestionType(String value) {
            this.value = value;
        }
    }

}
