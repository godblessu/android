package com.zhiyicx.thinksnsplus.modules.chat.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.impl.photoselector.DaggerPhotoSelectorImplComponent;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSelectorImpl;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSeletorImplModule;
import com.zhiyicx.baseproject.widget.EmptyView;
import com.zhiyicx.baseproject.widget.UserAvatarView;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.recycleviewdecoration.GridDecoration;
import com.zhiyicx.common.utils.recycleviewdecoration.TGridDecoration;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.chat.ChatActivity;
import com.zhiyicx.thinksnsplus.modules.chat.adapter.ChatMemberAdapter;
import com.zhiyicx.thinksnsplus.modules.chat.edit.manager.GroupManagerActivity;
import com.zhiyicx.thinksnsplus.modules.chat.edit.name.EditGroupNameActivity;
import com.zhiyicx.thinksnsplus.modules.chat.edit.owner.EditGroupOwnerActivity;
import com.zhiyicx.thinksnsplus.modules.chat.item.ChatConfig;
import com.zhiyicx.thinksnsplus.modules.chat.member.GroupMemberListActivity;
import com.zhiyicx.thinksnsplus.modules.chat.select.SelectFriendsActivity;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterFragment;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.utils.TSImHelperUtils;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.hyphenate.easeui.EaseConstant.EXTRA_TO_USER_ID;
import static com.zhiyicx.thinksnsplus.modules.chat.edit.name.EditGroupNameFragment.GROUP_ORIGINAL_NAME;
import static com.zhiyicx.thinksnsplus.modules.chat.edit.owner.EditGroupOwnerFragment.BUNDLE_GROUP_DATA;
import static com.zhiyicx.thinksnsplus.modules.chat.member.GroupMemberListFragment.BUNDLE_GROUP_MEMBER;
import static com.zhiyicx.thinksnsplus.modules.chat.select.SelectFriendsFragment.BUNDLE_GROUP_EDIT_DATA;
import static com.zhiyicx.thinksnsplus.modules.chat.select.SelectFriendsFragment.BUNDLE_GROUP_IS_DELETE;

/**
 * @author Catherine
 * @describe
 * @date 2018/1/22
 * @contact email:648129313@qq.com
 */

public class ChatInfoFragment extends TSFragment<ChatInfoContract.Presenter> implements ChatInfoContract.View, PhotoSelectorImpl.IPhotoBackListener {

    @BindView(R.id.iv_user_portrait)
    UserAvatarView mIvUserPortrait;
    @BindView(R.id.tv_user_name)
    TextView mTvUserName;
    @BindView(R.id.tv_group_header)
    TextView mTvGroupHeader;
    @BindView(R.id.iv_add_user)
    ImageView mIvAddUser;
    @BindView(R.id.ll_single)
    LinearLayout mLlSingle;
    @BindView(R.id.rv_member_list)
    RecyclerView mRvMemberList;
    @BindView(R.id.tv_to_all_members)
    TextView mTvToAllMembers;
    @BindView(R.id.ll_group)
    LinearLayout mLlGroup;
    @BindView(R.id.ll_manager)
    LinearLayout mLlManager;
    @BindView(R.id.tv_clear_message)
    TextView mTvClearMessage;
    @BindView(R.id.tv_delete_group)
    TextView mTvDeleteGroup;
    @BindView(R.id.iv_group_portrait)
    ImageView mIvGroupPortrait;
    @BindView(R.id.tv_group_name)
    TextView mTvGroupName;
    @BindView(R.id.sc_block_message)
    SwitchCompat mScBlockMessage;
    @BindView(R.id.ll_container)
    View mLlContainer;
    @BindView(R.id.emptyView)
    EmptyView mEmptyView;
    @BindView(R.id.rl_block_message)
    RelativeLayout mRlBlockMessage;
    @BindView(R.id.v_line_find_member)
    View mVLineFindMember;

    @BindView(R.id.iv_grop_icon_arrow)
    View mIvGropIconArrow;
    @BindView(R.id.iv_grop_name_arrow)
    View mIvGropNameArrow;

    private int mChatType;
    private String mChatId;

    // 删除群聊
    private ActionPopupWindow mDeleteGroupPopupWindow;
    private ActionPopupWindow mPhotoPopupWindow;// 图片选择弹框

    /**
     * 清楚消息记录
     */
    private ActionPopupWindow mClearAllMessagePop;
    private PhotoSelectorImpl mPhotoSelector;
    private ChatGroupBean mChatGroupBean;

    private ChatMemberAdapter mChatMemberAdapter;
    private List<UserInfoBean> mChatMembers = new ArrayList<>();

    public ChatInfoFragment instance(Bundle bundle) {
        ChatInfoFragment fragment = new ChatInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        // 初始化图片选择器
        mPhotoSelector = DaggerPhotoSelectorImplComponent
                .builder()
                .photoSeletorImplModule(new PhotoSeletorImplModule(this, this, PhotoSelectorImpl
                        .SHAPE_SQUARE))
                .build().photoSelectorImpl();
        mChatType = getArguments().getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        mChatId = getArguments().getString(EXTRA_TO_USER_ID);
        if (mChatType == EaseConstant.CHATTYPE_SINGLE) {
            // 屏蔽群聊的布局
            mLlGroup.setVisibility(View.GONE);
            mLlManager.setVisibility(View.GONE);
            mTvDeleteGroup.setVisibility(View.GONE);
            isShowEmptyView(false, true);
            setGroupData();
            setCenterText(getString(R.string.chat_info_title_single));
            // 单聊没有屏蔽消息
            mRlBlockMessage.setVisibility(View.GONE);
        } else {
            mPresenter.getGroupChatInfo(mChatId);
            mEmptyView.setNeedTextTip(false);
            mEmptyView.setNeedClickLoadState(false);
            mEmptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoadingView();
                    mPresenter.getGroupChatInfo(mChatId);
                }
            });
            // 屏蔽单聊的布局
            mLlSingle.setVisibility(View.GONE);
        }
        initPhotoPopupWindow();
    }

    @Override
    protected void initData() {
        // 成员列表
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 5);
        mRvMemberList.setLayoutManager(manager);
        mRvMemberList.addItemDecoration(new TGridDecoration(0, getResources().getDimensionPixelOffset(R.dimen.spacing_large), true));
        dealAddOrDeleteButton();
        mChatMemberAdapter = new ChatMemberAdapter(getContext(), mChatMembers, -1,false);
        mRvMemberList.setAdapter(mChatMemberAdapter);
        mChatMemberAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (mChatMembers.get(position).getUser_id() == -1L) {
                    // 添加
                    SelectFriendsActivity.startSelectFriendActivity(mActivity, mChatGroupBean, false);
                } else if (mChatMembers.get(position).getUser_id() == -2L) {
                    // 移除
                    SelectFriendsActivity.startSelectFriendActivity(mActivity, mChatGroupBean, true);
                } else {
                    PersonalCenterFragment.startToPersonalCenter(getContext(), mChatMembers.get(position));
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.chat_info_title);
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_chat_info;
    }

    @Override
    public void closeCurrentActivity() {
        mActivity.finish();
    }

    @OnClick({R.id.iv_add_user, R.id.tv_to_all_members, R.id.ll_manager, R.id.tv_clear_message, R.id.tv_delete_group,
            R.id.ll_group_portrait, R.id.ll_group_name, R.id.iv_user_portrait})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_user:
                // 添加成员
                if (mChatGroupBean == null) {
                    mChatGroupBean = new ChatGroupBean();
                }
                List<UserInfoBean> userInfoBeanList = new ArrayList<>();
                userInfoBeanList.add(mPresenter.getUserInfoFromLocal(mChatId));
                mChatGroupBean.setAffiliations(userInfoBeanList);
                SelectFriendsActivity.startSelectFriendActivity(mActivity, mChatGroupBean, false);

                break;
            case R.id.tv_to_all_members:
                // 查看所有成员
                Intent intentAllMember = new Intent(getContext(), GroupMemberListActivity.class);
                Bundle bundleAllMember = new Bundle();
                bundleAllMember.putParcelable(BUNDLE_GROUP_MEMBER, mChatGroupBean);
                intentAllMember.putExtras(bundleAllMember);
                startActivity(intentAllMember);
                break;
            case R.id.ll_manager:
                // 跳转群管理
//                Intent intent = new Intent(getContext(), GroupManagerActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable(BUNDLE_GROUP_DATA, mChatGroupBean);
//                intent.putExtras(bundle);
//                startActivity(intent);
                // 转让群主
                Intent intent = new Intent(getContext(), EditGroupOwnerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_GROUP_DATA, mChatGroupBean);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.tv_clear_message:
                // 清空消息记录
                showClearAllMsgPopupWindow(getString(R.string.ts_message_history_deleted_tip));
                break;
                /*
                 群主：删除群聊
                 普通用户：退出群
                 */
            case R.id.tv_delete_group:
                initDeletePopupWindow(mPresenter.isGroupOwner() ? getString(R.string.chat_delete) : getString(R.string.chat_quit)
                        , mPresenter.isGroupOwner() ? getString(R.string.chat_delete_group_alert) : getString(R.string.chat_quit_group_alert));
                break;
            case R.id.ll_group_portrait:
                // 修改群头像
                if (mChatType == ChatConfig.CHATTYPE_GROUP && mPresenter.isGroupOwner()) {
                    mPhotoPopupWindow.show();
                }
                break;
            case R.id.ll_group_name:
                // 修改群名称
                if (mChatType == ChatConfig.CHATTYPE_GROUP && mPresenter.isGroupOwner()) {
                    Intent intentName = new Intent(getContext(), EditGroupNameActivity.class);
                    Bundle bundleName = new Bundle();
                    bundleName.putString(GROUP_ORIGINAL_NAME, mChatGroupBean.getName());
                    intentName.putExtras(bundleName);
                    startActivity(intentName);
                }
                break;
            case R.id.iv_user_portrait:
                try {
                    UserInfoBean userInfoBean = new UserInfoBean();
                    userInfoBean.setUser_id(Long.parseLong(mChatId));
                    PersonalCenterFragment.startToPersonalCenter(getContext(), userInfoBean);
                } catch (NumberFormatException ignore) {
                }
                break;
            default:
        }
    }

    private void initDeletePopupWindow(String item2, String dec) {
        mDeleteGroupPopupWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.prompt))
                .item2Str(item2)
                .desStr(dec)
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item2ClickListener(() -> {
                    mPresenter.destoryOrLeaveGroup(mChatId);
                    mDeleteGroupPopupWindow.hide();
                })
                .bottomClickListener(() -> mDeleteGroupPopupWindow.hide())
                .build();
        mDeleteGroupPopupWindow.show();
    }

    /**
     * 初始化图片选择弹框
     */
    private void initPhotoPopupWindow() {
        if (mPhotoPopupWindow != null) {
            return;
        }
        mPhotoPopupWindow = ActionPopupWindow.builder()
                .item1Str(mActivity.getString(R.string.choose_from_photo))
                .item2Str(mActivity.getString(R.string.choose_from_camera))
                .bottomStr(mActivity.getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(0.8f)
                .with(mActivity)
                .item1ClickListener(() -> {
                    // 选择相册，单张
                    mPhotoSelector.getPhotoListFromSelector(1, null);
                    mPhotoPopupWindow.hide();
                })
                .item2ClickListener(() -> {
                    // 选择相机，拍照
                    mPhotoSelector.getPhotoFromCamera(null);
                    mPhotoPopupWindow.hide();
                })
                .bottomClickListener(() -> mPhotoPopupWindow.hide()).build();
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected boolean usePermisson() {
        return true;
    }

    @Override
    public String getChatId() {
        return mChatId;
    }

    @Override
    public void updateGroup(ChatGroupBean chatGroupBean) {
        // emm 由于没有完全返回所有信息 再加上字段也不同 所以手动改一下
        mChatGroupBean.setGroup_face(chatGroupBean.getGroup_face());
        mChatGroupBean.setOwner(chatGroupBean.getOwner());
        mChatGroupBean.setPublic(chatGroupBean.isPublic());
        mChatGroupBean.setName(chatGroupBean.getName());
        mChatGroupBean.setDescription(chatGroupBean.getDescription());
        mChatGroupBean.setMembersonly(chatGroupBean.isMembersonly());
        mChatGroupBean.setAllowinvites(chatGroupBean.isAllowinvites());
        mPresenter.saveGroupInfo(mChatGroupBean);
        setGroupData();
    }

    @Override
    public void updateGroupOwner(ChatGroupBean chatGroupBean) {
        // emm 由于没有完全返回所有信息 再加上字段也不同 所以手动改一下
        Observable.just(chatGroupBean)
                .subscribeOn(Schedulers.io())
                .map(chatGroupBean1 -> {
                    System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
                    mChatGroupBean.setGroup_face(chatGroupBean.getGroup_face());
                    mChatGroupBean.setOwner(chatGroupBean.getOwner());
                    mChatGroupBean.setPublic(chatGroupBean.isPublic());
                    mChatGroupBean.setName(chatGroupBean.getName());
                    mChatGroupBean.setDescription(chatGroupBean.getDescription());
                    mChatGroupBean.setMembersonly(chatGroupBean.isMembersonly());
                    mChatGroupBean.setAllowinvites(chatGroupBean.isAllowinvites());
                    if (mChatGroupBean.getAffiliations() != null) {
                        for (UserInfoBean userInfoBean : mChatGroupBean.getAffiliations()) {
                            if (mChatGroupBean.getOwner() == userInfoBean.getUser_id()) {
                                mChatGroupBean.getAffiliations().remove(userInfoBean);
                                mChatGroupBean.getAffiliations().add(0, userInfoBean);
                                break;
                            }
                        }
                    }
                    return mChatGroupBean;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chatGroupBean12 -> {
                    if (getActivity() != null) {
                        setGroupData();
                    }
                }, Throwable::printStackTrace);
    }

    @Override
    public void getGroupInfoSuccess(ChatGroupBean chatGroupBean) {
        mChatGroupBean = chatGroupBean;
        mChatGroupBean.setId(mChatId);
        setGroupData();
        // 切换是否屏蔽消息
        mScBlockMessage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mChatType == EaseConstant.CHATTYPE_SINGLE) {
                if (isChecked) {

                }
            } else {
                mPresenter.openOrCloseGroupMessage(isChecked, mChatId);
            }
        });
    }

    @Override
    protected boolean setUseCenterLoading() {
        return true;
    }

    @Override
    protected boolean setUseCenterLoadingAnimation() {
        return super.setUseCenterLoadingAnimation();
    }

    @Override
    public ChatGroupBean getGroupBean() {
        return mChatGroupBean;
    }

    @Override
    public void isShowEmptyView(boolean isShow, boolean isSuccess) {

        mLlContainer.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        if (!isSuccess) {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setErrorType(EmptyView.STATE_NETWORK_ERROR);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        if (!isShow) {
            closeLoadingView();
        }

    }


    @Override
    public String getToUserId() {
        return mChatType == EaseConstant.CHATTYPE_SINGLE ? mChatId : "";
    }

    @Override
    public void createGroupSuccess(ChatGroupBean chatGroupBean) {
        String id = chatGroupBean.getId();
        if (EMClient.getInstance().groupManager().getGroup(id) == null) {
            // 不知道为啥 有时候获取不到群组对象
            showSnackErrorMessage(getString(R.string.create_fail));
        } else {
            // 点击跳转聊天
            ChatActivity.startChatActivity(mActivity, id, EaseConstant.CHATTYPE_GROUP);
            getActivity().finish();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPhotoSelector.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        dismissPop(mClearAllMessagePop);
        dismissPop(mPhotoPopupWindow);
        dismissPop(mDeleteGroupPopupWindow);
        super.onDestroyView();
    }

    @Override
    public void getPhotoSuccess(List<ImageBean> photoList) {
        mChatGroupBean.setGroup_face(photoList.get(0).getImgUrl());
        mPresenter.updateGroup(mChatGroupBean, true);
    }

    @Override
    public void getPhotoFailure(String errorMsg) {
        showSnackErrorMessage(errorMsg);
    }

    private void dealAddOrDeleteButton() {

        if (mChatGroupBean == null) {
            return;
        }
        mChatMembers.clear();
        mChatMembers.addAll(mChatGroupBean.getAffiliations());
        if (mPresenter.isGroupOwner()) {
            if (mChatMembers.size() > 18) {
                // 是群主 18 + 2
                mChatMembers = mChatMembers.subList(0, 18);
                mVLineFindMember.setVisibility(View.VISIBLE);
                mTvToAllMembers.setVisibility(View.VISIBLE);
            } else {
                mVLineFindMember.setVisibility(View.GONE);
                mTvToAllMembers.setVisibility(View.GONE);
            }
        } else {
            // 不是群主
            if (mChatMembers.size() > 19) {
                // 19 +1
                mChatMembers = mChatMembers.subList(0, 19);
                mVLineFindMember.setVisibility(View.VISIBLE);
                mTvToAllMembers.setVisibility(View.VISIBLE);
            } else {
                mVLineFindMember.setVisibility(View.GONE);
                mTvToAllMembers.setVisibility(View.GONE);
            }
        }
        // 添加按钮，都可以拉人
        UserInfoBean chatUserInfoBean = new UserInfoBean();
        chatUserInfoBean.setUser_id(-1L);
        mChatMembers.add(chatUserInfoBean);
        if (mPresenter.isGroupOwner()) {
            // 删除按钮，仅群主
            UserInfoBean chatUserInfoBean1 = new UserInfoBean();
            chatUserInfoBean1.setUser_id(-2L);
            mChatMembers.add(chatUserInfoBean1);
        }

    }

    private void showClearAllMsgPopupWindow(String tipStr) {
        if (mClearAllMessagePop == null) {
            mClearAllMessagePop = ActionPopupWindow.builder()
                    .item1Str(getString(R.string.info_publish_hint))
                    .desStr(tipStr)
                    .item2Str(getString(R.string.chat_info_clear_message))
                    .bottomStr(getString(R.string.cancel))
                    .isOutsideTouch(true)
                    .isFocus(true)
                    .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                    .with(getActivity())
                    .item2ClickListener(() -> {
                        EMClient.getInstance().chatManager().getConversation(mChatId).clearAllMessages();
                        if (mPresenter != null && mPresenter.checkImhelper(mChatId)) {
                            TSImHelperUtils.saveDeletedHistoryMessageHelper(
                                    getContext().getApplicationContext()
                                    , mChatId
                                    , String.valueOf(AppApplication.getMyUserIdWithdefault())
                            );
                        }
                        mClearAllMessagePop.hide();
                    })
                    .bottomClickListener(() -> mClearAllMessagePop.hide()).build();
        }
        mClearAllMessagePop.show();
    }

    private void setGroupData() {
        if (mChatType == EaseConstant.CHATTYPE_SINGLE) {
            // 单聊处理布局
            UserInfoBean user = mPresenter.getUserInfoFromLocal(mChatId);
            ImageUtils.loadUserHead(user, mIvUserPortrait, false);
            mTvUserName.setText(user.getName());
            mIvUserPortrait.setOnClickListener(v -> PersonalCenterFragment.startToPersonalCenter(getContext(), user));
        } else {
            // 非群主屏蔽群管理
            if (!mPresenter.isGroupOwner()) {
                mLlManager.setVisibility(View.GONE);
                mRlBlockMessage.setVisibility(View.VISIBLE);
                mTvGroupHeader.setText(R.string.chat_group_portrait);
                mTvDeleteGroup.setText(getString(R.string.chat_quit_group));
                mScBlockMessage.setEnabled(true);
                mIvGropIconArrow.setVisibility(View.GONE);
                mIvGropNameArrow.setVisibility(View.GONE);
            } else {
                // 群主无法屏蔽消息
                mTvGroupHeader.setText(R.string.chat_set_group_portrait);
                mTvDeleteGroup.setText(getString(R.string.chat_delete_group));
                mRlBlockMessage.setVisibility(View.GONE);
                mScBlockMessage.setEnabled(false);
                mIvGropIconArrow.setVisibility(View.VISIBLE);
                mIvGropNameArrow.setVisibility(View.VISIBLE);
            }
            // 群聊的信息展示
            EMGroup group = EMClient.getInstance().groupManager().getGroup(mChatId);
            // 屏蔽按钮
            mScBlockMessage.setChecked(group.isMsgBlocked());
            // 群名称
            String groupName = mChatGroupBean.getName();
            // + "(" + mChatGroupBean.getAffiliations_count() + ")";
            mTvGroupName.setText(groupName);
            // 群头像
            ImageUtils.loadCircleImageDefault(mIvGroupPortrait, mChatGroupBean.getGroup_face(), R.mipmap.ico_ts_assistant, R.mipmap.ico_ts_assistant);

            if (mChatMemberAdapter != null && mChatGroupBean != null) {
                mChatMemberAdapter.setOwnerId(mChatGroupBean.getOwner());
                dealAddOrDeleteButton();
                mChatMemberAdapter.refreshData(mChatMembers);
            }
        }
    }
}
