package com.zhiyicx.thinksnsplus.modules.dynamic.list;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.config.TouristConfig;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.impl.share.ShareModule;
import com.zhiyicx.baseproject.widget.InputLimitView;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.baseproject.widget.popwindow.PayPopWindow;
import com.zhiyicx.common.utils.AndroidBug5497Workaround;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.AnimationRectBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicToolBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.i.OnUserInfoClickListener;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicBannerHeader;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListBaseItem;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForEightImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForFiveImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForFourImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForNineImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForOneImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForSevenImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForSixImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForThreeImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForTwoImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter.DynamicListItemForZeroImage;
import com.zhiyicx.thinksnsplus.modules.dynamic.top.DynamicTopActivity;
import com.zhiyicx.thinksnsplus.modules.gallery.GalleryActivity;
import com.zhiyicx.thinksnsplus.modules.home.HomeFragment;
import com.zhiyicx.thinksnsplus.modules.home.main.MainFragment;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterFragment;
import com.zhiyicx.thinksnsplus.widget.comment.DynamicListCommentView;
import com.zhiyicx.thinksnsplus.widget.comment.DynamicNoPullRecycleView;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow.POPUPWINDOW_ALPHA;
import static com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailFragment.DYNAMIC_DETAIL_DATA;
import static com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailFragment.DYNAMIC_DETAIL_DATA_POSITION;
import static com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailFragment.DYNAMIC_DETAIL_DATA_TYPE;
import static com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailFragment.LOOK_COMMENT_MORE;

/**
 * @Describe 动态列表
 * @Author Jungle68
 * @Date 2017/1/17
 * @Contact master.jungle68@gmail.com
 */
public class DynamicFragment extends TSListFragment<DynamicContract.Presenter, DynamicBean>
        implements DynamicNoPullRecycleView.OnCommentStateClickListener,
        InputLimitView.OnSendClickListener, DynamicContract.View, DynamicListCommentView
                .OnCommentClickListener, DynamicListCommentView.OnMoreCommentClickListener,
        DynamicListBaseItem.OnReSendClickListener, DynamicListBaseItem.OnMenuItemClickLisitener,
        DynamicListBaseItem.OnImageClickListener, OnUserInfoClickListener,
        MultiItemTypeAdapter.OnItemClickListener {
    protected static final String BUNDLE_DYNAMIC_TYPE = "dynamic_type";
    public static final long ITEM_SPACING = 5L; // 单位dp
    @BindView(R.id.fl_container)
    FrameLayout mFlContainer;
    @BindView(R.id.ilv_comment)
    InputLimitView mIlvComment;
    @BindView(R.id.v_shadow)
    View mVShadow;


    @Inject
    DynamicPresenter mDynamicPresenter;  // 仅用于构造
    private String mDynamicType = ApiConfig.DYNAMIC_TYPE_NEW;

    private ActionPopupWindow mDeletCommentPopWindow;
    private ActionPopupWindow mOtherDynamicPopWindow;
    // 每条动态都有三个点点了
    private ActionPopupWindow mMyDynamicPopWindow;
    private ActionPopupWindow mReSendCommentPopWindow;
    private ActionPopupWindow mReSendDynamicPopWindow;
    private PayPopWindow mPayPopWindow;
    private int mCurrentPostion;// 当前评论的动态位置
    private long mReplyToUserId;// 被评论者的 id

    private DynamicBannerHeader mDynamicBannerHeader;


    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        mOnCommentClickListener = onCommentClickListener;
    }

    OnCommentClickListener mOnCommentClickListener;

    public static DynamicFragment newInstance(String dynamicType, OnCommentClickListener l) {
        DynamicFragment fragment = new DynamicFragment();
        fragment.setOnCommentClickListener(l);
        Bundle args = new Bundle();
        args.putString(BUNDLE_DYNAMIC_TYPE, dynamicType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDynamicBannerHeader != null)
            mDynamicBannerHeader.startBanner();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDynamicBannerHeader != null)
            mDynamicBannerHeader.stopBanner();
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_list_with_input;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        initTestAdvert();
        initInputView();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 针对部分手机进入首页状态栏颜色修改无效
//            getActivity().getWindow().getDecorView().setSystemUiVisibility(View
//                    .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
        AndroidBug5497Workaround.assistActivity(getActivity());
    }

    private void initTestAdvert() {
        if (!com.zhiyicx.common.BuildConfig.USE_ADVERT)
            return;
        List<String> test = new ArrayList<>();
        test.add("oneoneoneoneoneoneoneoneoneoneoneoneoneoneoneoneoneoneoneoneoneoneoneone");
        test.add("twotwotwotwotwotwotwotwotwotwotwotwotwotwotwotwotwotwotwotwotwotwotwotwo");
        test.add("threethreethreethreethreethreethreethreethreethreethreethreethreethreethreethree");
        test.add("fourfourfourfourfourfourfourfourfourfourfourfourfourfourfourfourfourfour");
        mDynamicBannerHeader = new DynamicBannerHeader(getActivity());
        DynamicBannerHeader.DynamicBannerHeaderInfo headerInfo = mDynamicBannerHeader.new
                DynamicBannerHeaderInfo();
        headerInfo.setTitles(test);
        headerInfo.setUrls(test);
        headerInfo.setDelay(4000);
        mDynamicBannerHeader.setHeadInfo(headerInfo);
        mHeaderAndFooterWrapper.addHeaderView(mDynamicBannerHeader.getDynamicBannerHeader());
    }

    private void initInputView() {
        mVShadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeInputView();
            }
        });
        mIlvComment.setOnSendClickListener(this);
    }

    @Override
    protected float getItemDecorationSpacing() {
        return ITEM_SPACING;
    }


    @Override
    protected MultiItemTypeAdapter getAdapter() {
        MultiItemTypeAdapter adapter = new MultiItemTypeAdapter(getContext(), mListDatas);
        setAdapter(adapter, new DynamicListItemForZeroImage(getContext()));
        setAdapter(adapter, new DynamicListItemForOneImage(getContext()));
        setAdapter(adapter, new DynamicListItemForTwoImage(getContext()));
        setAdapter(adapter, new DynamicListItemForThreeImage(getContext()));
        setAdapter(adapter, new DynamicListItemForFourImage(getContext()));
        setAdapter(adapter, new DynamicListItemForFiveImage(getContext()));
        setAdapter(adapter, new DynamicListItemForSixImage(getContext()));
        setAdapter(adapter, new DynamicListItemForSevenImage(getContext()));
        setAdapter(adapter, new DynamicListItemForEightImage(getContext()));
        setAdapter(adapter, new DynamicListItemForNineImage(getContext()));

        adapter.setOnItemClickListener(this);
        return adapter;
    }

    protected void setAdapter(MultiItemTypeAdapter adapter, DynamicListBaseItem
            dynamicListBaseItem) {
        dynamicListBaseItem.setOnImageClickListener(this);
        dynamicListBaseItem.setOnUserInfoClickListener(this);
        dynamicListBaseItem.setOnMenuItemClickLisitener(this);
        dynamicListBaseItem.setOnReSendClickListener(this);
        dynamicListBaseItem.setOnMoreCommentClickListener(this);
        dynamicListBaseItem.setOnCommentClickListener(this);
        dynamicListBaseItem.setOnCommentStateClickListener(this);
        adapter.addItemViewDelegate(dynamicListBaseItem);
    }


    @Override
    protected void initData() {
        DaggerDynamicComponent // 在 super.initData();之前，因为initdata 会使用到 presenter
                .builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .shareModule(new ShareModule(getActivity()))
                .dynamicPresenterModule(new DynamicPresenterModule(this))
                .build().inject(this);
        mDynamicType = getArguments().getString(BUNDLE_DYNAMIC_TYPE);

        super.initData();
    }

    /**
     * 由于热门和关注和最新的 max_id 不同，所以特殊处理
     *
     * @param data
     * @return
     */
    @Override
    protected Long getMaxId(@NotNull List<DynamicBean> data) {
        if (mListDatas.size() > 0) {
            if (getDynamicType().equals(ApiConfig.DYNAMIC_TYPE_HOTS)) {
                return mListDatas.get(mListDatas.size() - 1).getHot_creat_time();
            } else {
                return mListDatas.get(mListDatas.size() - 1).getFeed_id();
            }
        } else {
            return DEFAULT_PAGE_MAX_ID;
        }
    }

    /**
     * scan imags
     *
     * @param dynamicBean
     * @param position
     */
    @Override
    public void onImageClick(ViewHolder holder, DynamicBean dynamicBean, int position) {

        if (!TouristConfig.DYNAMIC_BIG_PHOTO_CAN_LOOK && mPresenter.handleTouristControl()) {
            return;
        }

        List<ImageBean> imageBeanList = dynamicBean.getFeed().getStorages();
        ArrayList<AnimationRectBean> animationRectBeanArrayList
                = new ArrayList<>();
        for (int i = 0; i < imageBeanList.size(); i++) {
            int id = UIUtils.getResourceByName("siv_" + i, "id", getContext());
            ImageView imageView = holder.getView(id);
            AnimationRectBean rect = AnimationRectBean.buildFromImageView(imageView);
            animationRectBeanArrayList.add(rect);
        }

        GalleryActivity.startToGallery(getContext(), position, imageBeanList,
                animationRectBeanArrayList);
    }

    /**
     * scan user Info
     *
     * @param userInfoBean
     */
    @Override
    public void onUserInfoClick(UserInfoBean userInfoBean) {
        if (!TouristConfig.USER_INFO_CAN_LOOK && mPresenter.handleTouristControl()) { // 游客处理
            return;
        }
        PersonalCenterFragment.startToPersonalCenter(getContext(), userInfoBean);

    }

    @Override
    public String getDynamicType() {
        return mDynamicType;
    }

    @Override
    public void closeInputView() {
        if (mIlvComment.getVisibility() == View.VISIBLE) {
            mIlvComment.setVisibility(View.GONE);
            DeviceUtils.hideSoftKeyboard(getActivity(), mIlvComment.getEtContent());
        }
        mVShadow.setVisibility(View.GONE);
        if (mOnCommentClickListener != null) {
            mOnCommentClickListener.onButtonMenuShow(true);
        }
    }

    @Override
    public void showNewDynamic(int position) {
        if (position == -1) {
            // 添加一条新动态
            refreshData();
            // 回到顶部
            mRvList.smoothScrollToPosition(0);
            // viewpager切换到关注列表
            Fragment parentFragmentMain = getParentFragment();
            if (parentFragmentMain != null && parentFragmentMain instanceof MainFragment) {
                MainFragment mainFragment = (MainFragment) parentFragmentMain;
                mainFragment.setPagerSelection(MainFragment.PAGER_FOLLOW_DYNAMIC_LIST_POSITION);
                // 主页切换到首页
                Fragment parentFragmentHome = mainFragment.getParentFragment();
                if (parentFragmentHome != null && parentFragmentHome instanceof HomeFragment) {
                    HomeFragment homeFragment = (HomeFragment) parentFragmentHome;
                    homeFragment.setPagerSelection(HomeFragment.PAGE_HOME);
                }
            }
        } else {
            refreshData(position);
        }
    }

    /**
     * dynamic resend click
     *
     * @param position
     */
    @Override
    public void onReSendClick(int position) {
        initReSendDynamicPopupWindow(position);
        mReSendDynamicPopWindow.show();
    }


    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        position -= mHeaderAndFooterWrapper.getHeadersCount();
        if (!TouristConfig.DYNAMIC_DETAIL_CAN_LOOK && mPresenter.handleTouristControl()) { // 游客处理
            return;
        }
        goDynamicDetail(position, false);

    }


    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    @Override
    public void onMenuItemClick(View view, int dataPosition, int viewPosition) {
        dataPosition -= mHeaderAndFooterWrapper.getHeadersCount();
        switch (viewPosition) { // 0 1 2 3 代表 view item 位置
            case 0: // 喜欢
                // 还未发送成功的动态列表不查看详情
                if ((!TouristConfig.DYNAMIC_CAN_DIGG && mPresenter.handleTouristControl()) ||
                        mListDatas.get(dataPosition).getFeed_id() == null || mListDatas.get
                        (dataPosition).getFeed_id() == 0) {
                    return;
                }
                handleLike(dataPosition);
                break;

            case 1: // 评论

                // 还未发送成功的动态列表不查看详情
                if ((!TouristConfig.DYNAMIC_CAN_COMMENT && mPresenter.handleTouristControl()) ||
                        mListDatas.get(dataPosition).getFeed_id() == null || mListDatas.get
                        (dataPosition).getFeed_id() == 0) {
                    return;
                }
                showCommentView();
                mIlvComment.setEtContentHint(getString(R.string.default_input_hint));
                mCurrentPostion = dataPosition;
                mReplyToUserId = 0;// 0 代表评论动态
                break;

            case 2: // 浏览
                onItemClick(null, null, dataPosition);
                break;

            case 3: // 更多
                Bitmap shareBitMap = null;
                try {
                    ImageView imageView = (ImageView) layoutManager.findViewByPosition
                            (dataPosition).findViewById(R.id.siv_0);
                    shareBitMap = ConvertUtils.drawable2BitmapWithWhiteBg(getContext(), imageView
                            .getDrawable(), R.mipmap.icon_256);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (AppApplication.getmCurrentLoginAuth() != null && mListDatas.get(dataPosition)
                        .getUser_id() == AppApplication.getmCurrentLoginAuth().getUser_id()) {
                    initMyDynamicPopupWindow(mListDatas.get(dataPosition), dataPosition,
                            mListDatas.get(dataPosition)
                                    .getTool().getIs_collection_feed() == DynamicToolBean
                                    .STATUS_COLLECT_FEED_CHECKED, shareBitMap);
                    mMyDynamicPopWindow.show();
                } else {
                    initCenterPopWindow();
                    mPayPopWindow.show();

//                    initOtherDynamicPopupWindow(mListDatas.get(dataPosition), dataPosition,
//                            mListDatas.get(dataPosition)
//                                    .getTool().getIs_collection_feed() == DynamicToolBean
//                                    .STATUS_COLLECT_FEED_CHECKED, shareBitMap);
//                    mOtherDynamicPopWindow.show();
                }

                break;
            default:
                onItemClick(null, null, dataPosition);
        }
    }

    /**
     * 喜欢
     *
     * @param dataPosition
     */
    private void handleLike(int dataPosition) {
        // 先更新界面，再后台处理
        mListDatas.get(dataPosition).getTool().setIs_digg_feed(mListDatas.get(dataPosition)
                .getTool().getIs_digg_feed() == DynamicToolBean.STATUS_DIGG_FEED_UNCHECKED ?
                DynamicToolBean.STATUS_DIGG_FEED_CHECKED : DynamicToolBean
                .STATUS_DIGG_FEED_UNCHECKED);
        mListDatas.get(dataPosition).getTool().setFeed_digg_count(mListDatas.get(dataPosition)
                .getTool().getIs_digg_feed() == DynamicToolBean.STATUS_DIGG_FEED_UNCHECKED ?
                mListDatas.get(dataPosition).getTool().getFeed_digg_count() - 1 : mListDatas.get
                (dataPosition).getTool().getFeed_digg_count() + 1);
        refreshData();
        mPresenter.handleLike(mListDatas.get(dataPosition).getTool().getIs_digg_feed() ==
                        DynamicToolBean.STATUS_DIGG_FEED_CHECKED,
                mListDatas.get(dataPosition).getFeed().getFeed_id(), dataPosition);
    }

    @Override
    public void onCommentUserInfoClick(UserInfoBean userInfoBean) {
        onUserInfoClick(userInfoBean);
    }

    /**
     * comment has been clicked
     *
     * @param dynamicBean current dynamic
     * @param position    this position of comment
     */
    @Override
    public void onCommentContentClick(DynamicBean dynamicBean, int position) {
        if (!TouristConfig.DYNAMIC_CAN_COMMENT && mPresenter.handleTouristControl()) {
            return;
        }
        mCurrentPostion = mPresenter.getCurrenPosiotnInDataList(dynamicBean.getFeed_mark());
        if (dynamicBean.getComments().get(position).getUser_id() == AppApplication
                .getmCurrentLoginAuth().getUser_id()) {
            if (dynamicBean.getComments().get(position).getComment_id() != null) {
                initDeletCommentPopupWindow(dynamicBean, mCurrentPostion, position);
                mDeletCommentPopWindow.show();
            }
        } else {
            showCommentView();
            mReplyToUserId = dynamicBean.getComments().get(position).getUser_id();
            String contentHint = getString(R.string.default_input_hint);
            if (dynamicBean.getComments().get(position).getUser_id() != AppApplication
                    .getmCurrentLoginAuth().getUser_id()) {
                contentHint = getString(R.string.reply, dynamicBean.getComments().get(position)
                        .getCommentUser().getName());
            }
            mIlvComment.setEtContentHint(contentHint);
        }

    }

    private void showCommentView() {
        showBottomView(false);

    }

    /**
     * comment send
     *
     * @param text
     */
    @Override
    public void onSendClick(View v, final String text) {
        com.zhiyicx.imsdk.utils.common.DeviceUtils.hideSoftKeyboard(getContext(), v);
        mIlvComment.setVisibility(View.GONE);
        mVShadow.setVisibility(View.GONE);
        mPresenter.sendComment(mCurrentPostion, mReplyToUserId, text);
        showBottomView(true);
    }

    /**
     * 重发评论
     *
     * @param dynamicCommentBean
     * @param position
     */
    @Override
    public void onCommentStateClick(DynamicCommentBean dynamicCommentBean, int position) {
        initReSendCommentPopupWindow(dynamicCommentBean, mListDatas.get(mPresenter
                .getCurrenPosiotnInDataList(dynamicCommentBean.getFeed_mark())).getFeed_id());
        mReSendCommentPopWindow.show();
    }

    @Override
    public void onMoreCommentClick(View view, DynamicBean dynamicBean) {
        if (!TouristConfig.MORE_COMMENT_CAN_LOOK && mPresenter.handleTouristControl()) {
            return;
        }
        int position = mPresenter.getCurrenPosiotnInDataList(dynamicBean.getFeed_mark());
        goDynamicDetail(position, true);
    }

    /**
     * 初始化评论删除选择弹框
     *
     * @param dynamicBean     curent dynamic
     * @param dynamicPositon  dynamic comment position
     * @param commentPosition current comment position
     */
    private void initDeletCommentPopupWindow(final DynamicBean dynamicBean, final int
            dynamicPositon, final int commentPosition) {
        mDeletCommentPopWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.dynamic_list_delete_comment))
                .item1Color(ContextCompat.getColor(getContext(), R.color.themeColor))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(new ActionPopupWindow.ActionPopupWindowItem1ClickListener() {
                    @Override
                    public void onItemClicked() {
                        mDeletCommentPopWindow.hide();
                        mPresenter.deleteComment(dynamicBean, dynamicPositon, dynamicBean
                                        .getComments().get(commentPosition).getComment_id(),
                                commentPosition);
                        showBottomView(true);
                    }
                })
                .bottomClickListener(new ActionPopupWindow.ActionPopupWindowBottomClickListener() {
                    @Override
                    public void onItemClicked() {
                        mDeletCommentPopWindow.hide();
                        showBottomView(true);
                    }
                })
                .build();
    }

    /**
     * 初始化他人动态操作选择弹框
     *
     * @param dynamicBean curent dynamic
     * @param position    curent dynamic postion
     */
    private void initOtherDynamicPopupWindow(final DynamicBean dynamicBean, final int position,
                                             boolean isCollected, final Bitmap shareBitmap) {
        mOtherDynamicPopWindow = ActionPopupWindow.builder()
                .item1Str(getString(isCollected ? R.string.dynamic_list_uncollect_dynamic : R
                        .string.dynamic_list_collect_dynamic))
                .item2Str(getString(R.string.dynamic_list_share_dynamic))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(new ActionPopupWindow.ActionPopupWindowItem1ClickListener() {
                    @Override
                    public void onItemClicked() {// 收藏
                        if (!TouristConfig.DYNAMIC_CAN_COLLECT && mPresenter.handleTouristControl
                                ()) {
                            return;
                        }
                        mPresenter.handleCollect(dynamicBean);
                        mOtherDynamicPopWindow.hide();
                        showBottomView(true);
                    }
                })
                .item2ClickListener(new ActionPopupWindow.ActionPopupWindowItem2ClickListener() {
                    @Override
                    public void onItemClicked() {// 分享
                        mPresenter.shareDynamic(dynamicBean, shareBitmap);
                        mOtherDynamicPopWindow.hide();
                        showBottomView(true);
                    }
                })
                .bottomClickListener(new ActionPopupWindow.ActionPopupWindowBottomClickListener() {
                    @Override
                    public void onItemClicked() {
                        mOtherDynamicPopWindow.hide();
                        showBottomView(true);
                    }
                })
                .build();
    }

    /**
     * 初始化我的动态操作弹窗
     *
     * @param dynamicBean curent dynamic
     * @param position    curent dynamic postion
     */
    private void initMyDynamicPopupWindow(final DynamicBean dynamicBean, final int position,
                                          boolean isCollected, final Bitmap shareBitMap) {

        Long feed_id = dynamicBean.getFeed_id();
        boolean feedIdIsNull = feed_id == null || feed_id == 0;

        mMyDynamicPopWindow = ActionPopupWindow.builder()
                .item1Str(getString(feedIdIsNull ? R.string.empty : R.string
                        .dynamic_list_share_dynamic))
                .item2Str(getString(feedIdIsNull ? R.string.empty :
                        (isCollected ? R.string.dynamic_list_uncollect_dynamic : R.string
                                .dynamic_list_collect_dynamic)))
                .item3Str(getString(R.string.dynamic_list_top_dynamic))
                .item4Str(getString(R.string.dynamic_list_delete_dynamic))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(new ActionPopupWindow.ActionPopupWindowItem1ClickListener() {
                    @Override
                    public void onItemClicked() {// 分享
                        mPresenter.shareDynamic(dynamicBean, shareBitMap);
                        mMyDynamicPopWindow.hide();


                    }
                })
                .item2ClickListener(new ActionPopupWindow.ActionPopupWindowItem2ClickListener() {
                    @Override
                    public void onItemClicked() {// 收藏
                        mPresenter.handleCollect(dynamicBean);
                        mMyDynamicPopWindow.hide();
                        showBottomView(true);
                    }
                })
                .item3ClickListener(new ActionPopupWindow.ActionPopupWindowItem3ClickListener() {
                    @Override
                    public void onItemClicked() {// 申请置顶
                        startActivity(new Intent(getActivity(), DynamicTopActivity.class));
                        mMyDynamicPopWindow.hide();
                    }
                })
                .item4ClickListener(new ActionPopupWindow.ActionPopupWindowItem4ClickListener() {
                    @Override
                    public void onItemClicked() {// 删除
                        mMyDynamicPopWindow.hide();
                        mPresenter.deleteDynamic(dynamicBean, position);
                        showBottomView(true);
                    }
                })
                .bottomClickListener(new ActionPopupWindow.ActionPopupWindowBottomClickListener() {
                    @Override
                    public void onItemClicked() {//取消
                        mMyDynamicPopWindow.hide();
                        showBottomView(true);
                    }
                })
                .build();
    }

    /**
     * 初始化重发动态选择弹框
     */
    private void initReSendDynamicPopupWindow(final int position) {
        mReSendDynamicPopWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.resend))
                .item1Color(ContextCompat.getColor(getContext(), R.color.themeColor))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(new ActionPopupWindow.ActionPopupWindowItem1ClickListener() {
                    @Override
                    public void onItemClicked() {
                        mReSendDynamicPopWindow.hide();
                        mListDatas.get(position).setState(DynamicBean.SEND_ING);
                        refreshData();
                        mPresenter.reSendDynamic(position);
                    }
                })
                .bottomClickListener(new ActionPopupWindow.ActionPopupWindowBottomClickListener() {
                    @Override
                    public void onItemClicked() {
                        mReSendDynamicPopWindow.hide();
                    }
                })
                .build();
    }

    private void initCenterPopWindow() {
        mPayPopWindow = PayPopWindow.builder()
                .with(getActivity())
                .isWrap(true)
                .isFocus(true)
                .isOutsideTouch(true)
                .buildLinksColor1(R.color.themeColor)
                .buildLinksColor2(R.color.important_for_content)
                .contentView(R.layout.ppw_for_center)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .buildDescrStr(String.format(getString(R.string.buy_pay_desc) + getString(R
                        .string.buy_pay_member), 4.4))
                .buildLinksStr(getString(R.string.buy_pay_member))
                .buildTitleStr(getString(R.string.buy_pay))
                .buildItem1Str(getString(R.string.buy_pay_in))
                .buildItem2Str(getString(R.string.buy_pay_out))
                .buildMoneyStr(String.format(getString(R.string.buy_pay_money), 4.4))
                .buildCenterPopWindowItem1ClickListener(new PayPopWindow
                        .CenterPopWindowItem1ClickListener() {
                    @Override
                    public void onClicked() {
                        mPayPopWindow.hide();
                    }
                })
                .buildCenterPopWindowItem2ClickListener(new PayPopWindow
                        .CenterPopWindowItem2ClickListener() {
                    @Override
                    public void onClicked() {
                        mPayPopWindow.hide();
                    }
                })
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

    }

    /**
     * 初始化重发评论选择弹框
     */
    private void initReSendCommentPopupWindow(final DynamicCommentBean commentBean, final long
            feed_id) {
        mReSendCommentPopWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.dynamic_list_resend_comment))
                .item1Color(ContextCompat.getColor(getContext(), R.color.themeColor))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(new ActionPopupWindow.ActionPopupWindowItem1ClickListener() {
                    @Override
                    public void onItemClicked() {
                        mReSendCommentPopWindow.hide();
                        mPresenter.reSendComment(commentBean, feed_id);
                        showBottomView(true);
                    }
                })
                .bottomClickListener(new ActionPopupWindow.ActionPopupWindowBottomClickListener() {
                    @Override
                    public void onItemClicked() {
                        mReSendCommentPopWindow.hide();
                        showBottomView(true);
                    }
                })
                .build();
    }

    private void showBottomView(boolean isShow) {
        if (isShow) {
            mVShadow.setVisibility(View.GONE);
            mIlvComment.setVisibility(View.GONE);
            mIlvComment.clearFocus();
            mIlvComment.setSendButtonVisiable(false);
            DeviceUtils.hideSoftKeyboard(getActivity(), mIlvComment.getEtContent());
        } else {
            mVShadow.setVisibility(View.VISIBLE);
            mIlvComment.setVisibility(View.VISIBLE);
            mIlvComment.getFocus();
            mIlvComment.setSendButtonVisiable(true);
            DeviceUtils.showSoftKeyboard(getActivity(), mIlvComment.getEtContent());
        }
        if (mOnCommentClickListener != null) {
            mOnCommentClickListener.onButtonMenuShow(isShow);
        }
    }

    private void goDynamicDetail(int position, boolean isLookMoreComment) {
        // 还未发送成功的动态列表不查看详情
        if (mListDatas.get(position).getFeed_id() == null || mListDatas.get(position).getFeed_id
                () == 0) {
            return;
        }
        mPresenter.handleViewCount(mListDatas.get(position).getFeed_id(), position);
        Intent intent = new Intent(getActivity(), DynamicDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(DYNAMIC_DETAIL_DATA, mListDatas.get(position));
        bundle.putString(DYNAMIC_DETAIL_DATA_TYPE, getDynamicType());
        bundle.putInt(DYNAMIC_DETAIL_DATA_POSITION, position);
        bundle.putBoolean(LOOK_COMMENT_MORE, isLookMoreComment);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    public interface OnCommentClickListener {
        void onButtonMenuShow(boolean isShow);
    }
}
