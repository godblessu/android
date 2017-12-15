package com.zhiyicx.thinksnsplus.modules.circle.create.types;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.trycatch.mysnackbar.Prompt;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.CircleTypeBean;
import com.zhiyicx.thinksnsplus.data.beans.UserCertificationInfo;
import com.zhiyicx.thinksnsplus.modules.circle.all_circle.container.AllCircleContainerContract;
import com.zhiyicx.thinksnsplus.modules.circle.all_circle.container.AllCircleContainerPresenter;
import com.zhiyicx.thinksnsplus.modules.circle.all_circle.container.AllCircleContainerPresenterModule;
import com.zhiyicx.thinksnsplus.modules.circle.all_circle.container.DaggerAllCircleContainerComponent;
import com.zhiyicx.thinksnsplus.modules.information.publish.addinfo.AddInfoCategoryFragment;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

/**
 * @author Jliuer
 * @Date 2017/11/28/14:28
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CircleTypesFragment extends TSFragment<AllCircleContainerContract.Presenter> implements AllCircleContainerContract.View {

    public static final String BUNDLE_CIRCLE_CATEGORY = "circle_category";
    private static final int DEFAULT_COLUMN = 4;

    @BindView(R.id.fragment_channel_content_unsubscribe)
    RecyclerView mFragmentChannelContentUnsubscribe;

    private List<CircleTypeBean> mCircleTypeBeans;
    private CommonAdapter mAdapter;

    @Inject
    AllCircleContainerPresenter mAllCircleContainerPresenter;

    public static CircleTypesFragment newInstance(Bundle bundle) {
        CircleTypesFragment fragment = new CircleTypesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_info_publish_add_category;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.info_choose_categorys);
    }

    @Override
    protected void setRightClick() {
        super.setRightClick();
    }

    @Override
    protected int setToolBarBackgroud() {
        return R.color.white;
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        mFragmentChannelContentUnsubscribe.setLayoutManager(new GridLayoutManager(getActivity(),
                DEFAULT_COLUMN));
    }

    private CommonAdapter initUnsubscribeAdapter() {
        mAdapter = new CommonAdapter<CircleTypeBean>(getActivity(),
                R.layout.item_info_channel, mCircleTypeBeans) {
            @Override
            protected void convert(ViewHolder holder, CircleTypeBean data, int position) {
                holder.setText(R.id.item_info_channel, data.getName());
            }
        };

        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                CircleTypeBean bean = mCircleTypeBeans.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_CIRCLE_CATEGORY, bean);
                intent.putExtras(bundle);
                getActivity().setResult(RESULT_OK, intent);
                getActivity().finish();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int
                    position) {
                return false;
            }
        });

        return mAdapter;
    }

    @Override
    public void setCategroiesList(List<CircleTypeBean> circleTypeList) {
        mCircleTypeBeans = circleTypeList;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setUserCertificationInfo(UserCertificationInfo userCertificationInfo) {

    }

    @Override
    protected void initData() {
        DaggerAllCircleContainerComponent
                .builder()
                .allCircleContainerPresenterModule(new AllCircleContainerPresenterModule(this))
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .build().inject(this);
        mCircleTypeBeans = mPresenter.getCircleTypesFormLocal();
        if (mCircleTypeBeans.isEmpty()) {
            mPresenter.getCategroiesList(0, 0);
        }
        mFragmentChannelContentUnsubscribe.setAdapter(initUnsubscribeAdapter());
    }
}
