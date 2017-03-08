package com.zhiyicx.thinksnsplus.modules.personal_center;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.GlideImageConfig;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.GlideCircleBoundTransform;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.common.utils.ZoomView;
import com.zhiyicx.common.utils.imageloader.core.ImageLoader;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.adapter.DynamicDetailItemForDig;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListBaseItem;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author LiuChao
 * @describe 用户个人中心页面
 * @date 2017/3/7
 * @contact email:450127106@qq.com
 */

public class PersonalCenterFragment extends TSListFragment<PersonalCenterContract.Presenter, DynamicBean> implements PersonalCenterContract.View, DynamicListBaseItem.OnReSendClickListener, DynamicListBaseItem.OnMenuItemClickLisitener, DynamicListBaseItem.OnImageClickListener, DynamicListBaseItem.OnUserInfoClickListener, MultiItemTypeAdapter.OnItemClickListener {

    public static final String PERSONAL_CENTER_DATA = "personal_center_data";

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.iv_more)
    ImageView mIvMore;
    @BindView(R.id.rl_toolbar_container)
    RelativeLayout mRlToolbarContainer;

    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private List<DynamicBean> mDynamicBeens = new ArrayList<>();
    // 当前需要显示的用户的id
    private long currentUserId = 5;

    /**********************************
     * headerView控件
     ********************************/
    private FrameLayout fl_cover_contaner;// 封面图的容器
    private ImageView iv_background_cover;// 封面
    private ImageView iv_head_icon;// 用户头像
    private TextView tv_user_name;// 用户名
    private TextView tv_user_intro;// 用户简介
    private TextView tv_user_follow;// 用户关注数量
    private TextView tv_user_fans;// 用户粉丝数量


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        initToolBar();
        initHeaderView();
    }

    @Override
    protected boolean getPullDownRefreshEnable() {
        return false;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_personal_center;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected MultiItemTypeAdapter<DynamicBean> getAdapter() {
        mDynamicBeens.add(new DynamicBean());
        mDynamicBeens.add(new DynamicBean());
        mDynamicBeens.add(new DynamicBean());
        mDynamicBeens.add(new DynamicBean());
        mDynamicBeens.add(new DynamicBean());

        MultiItemTypeAdapter adapter = new MultiItemTypeAdapter(getContext(), mDynamicBeens);
        adapter.addItemViewDelegate(new DynamicDetailItemForDig());
      /*  setAdapter(adapter, new DynamicListBaseItem(getContext()));
        setAdapter(adapter, new DynamicListItemForOneImage(getContext()));
        setAdapter(adapter, new DynamicListItemForTwoImage(getContext()));
        setAdapter(adapter, new DynamicListItemForThreeImage(getContext()));
        setAdapter(adapter, new DynamicListItemForFourImage(getContext()));
        setAdapter(adapter, new DynamicListItemForFiveImage(getContext()));
        setAdapter(adapter, new DynamicListItemForSixImage(getContext()));
        setAdapter(adapter, new DynamicListItemForSevenImage(getContext()));
        setAdapter(adapter, new DynamicListItemForEightImage(getContext()));
        setAdapter(adapter, new DynamicListItemForNineImage(getContext()));*/
        return adapter;
    }

    @Override
    protected void initData() {
        // 获取个人主页用户信息，显示在headerView中
        mPresenter.setCurrentUserInfo(currentUserId);
    }

    @Override
    public void setPresenter(PersonalCenterContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(String message) {

    }

    public static PersonalCenterFragment initFragment(Bundle bundle) {
        PersonalCenterFragment personalCenterFragment = new PersonalCenterFragment();
        personalCenterFragment.setArguments(bundle);
        return personalCenterFragment;
    }

    private void setAdapter(MultiItemTypeAdapter adapter, DynamicListBaseItem dynamicListBaseItem) {
        dynamicListBaseItem.setOnImageClickListener(this);
        dynamicListBaseItem.setOnUserInfoClickListener(this);
        dynamicListBaseItem.setOnMenuItemClickLisitener(this);
        dynamicListBaseItem.setOnReSendClickListener(this);
        adapter.addItemViewDelegate(dynamicListBaseItem);
    }

    @Override
    public void onImageClick(ViewHolder holder, DynamicBean dynamicBean, int position) {

    }

    @Override
    public void onMenuItemClick(View view, int dataPosition, int viewPosition) {

    }

    @Override
    public void onReSendClick(int position) {

    }

    @Override
    public void onUserInfoClick(UserInfoBean userInfoBean) {

    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    @OnClick({R.id.iv_back, R.id.iv_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().finish();
                break;
            case R.id.iv_more:
                break;
        }
    }

    @Override
    public void setHeaderInfo(UserInfoBean userInfoBean) {
        initHeaderViewData(userInfoBean);
    }

    private void initHeaderView() {
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_personal_center_header, null);
        initHeaderViewUI(headerView);
        mHeaderAndFooterWrapper.addHeaderView(headerView);
        mRvList.setAdapter(mHeaderAndFooterWrapper);
        mHeaderAndFooterWrapper.notifyDataSetChanged();

    }

    private void initHeaderViewUI(View headerView) {
        ViewGroup.LayoutParams headerLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerView.setLayoutParams(headerLayoutParams);
        fl_cover_contaner = (FrameLayout) headerView.findViewById(R.id.fl_cover_contaner);
        iv_background_cover = (ImageView) headerView.findViewById(R.id.iv_background_cover);
        iv_head_icon = (ImageView) headerView.findViewById(R.id.iv_head_icon);
        tv_user_name = (TextView) headerView.findViewById(R.id.tv_user_name);
        tv_user_intro = (TextView) headerView.findViewById(R.id.tv_user_intro);
        tv_user_follow = (TextView) headerView.findViewById(R.id.tv_user_follow);
        tv_user_fans = (TextView) headerView.findViewById(R.id.tv_user_fans);
        // 高度为屏幕宽度一半加上20dp
        int width = UIUtils.getWindowWidth(getContext());
        int height = UIUtils.getWindowWidth(getContext()) / 2 + getResources().getDimensionPixelSize(R.dimen.spacing_large);
        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        fl_cover_contaner.setLayoutParams(containerLayoutParams);
        // 添加头部放缩
        new ZoomView(fl_cover_contaner, getActivity(), mRvList, width, height).initZoom();

    }

    private void initHeaderViewData(UserInfoBean userInfoBean) {
        ImageLoader imageLoader = AppApplication.AppComponentHolder.getAppComponent().imageLoader();
        // 显示头像
        imageLoader.loadImage(getContext(), GlideImageConfig.builder()
                .url(String.format(ApiConfig.IMAGE_PATH, userInfoBean.getAvatar(), 50))
                .placeholder(R.drawable.shape_default_image_circle)
                .errorPic(R.drawable.shape_default_image_circle)
                .imagerView(iv_head_icon)
                .transformation(new GlideCircleBoundTransform(getContext()))
                .build());
        // 设置用户名
        tv_user_name.setText(userInfoBean.getName());
        // 设置简介
        tv_user_intro.setText(userInfoBean.getIntro());
        // 设置关注人数
        tv_user_follow.setText("关注 " + 100);
        // 设置粉丝人数
        tv_user_fans.setText("粉丝 " + 204);
        mHeaderAndFooterWrapper.notifyDataSetChanged();
    }

    private void initToolBar() {
        // toolBar设置状态栏高度的marginTop
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, DeviceUtils.getStatuBarHeight(getContext()), 0, 0);
        mRlToolbarContainer.setLayoutParams(layoutParams);
    }
}
