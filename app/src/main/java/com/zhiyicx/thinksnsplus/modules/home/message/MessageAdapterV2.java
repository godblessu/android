package com.zhiyicx.thinksnsplus.modules.home.message;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemMangerImpl;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.bean.ChatUserInfoBean;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.GlideCircleTransform;
import com.zhiyicx.baseproject.widget.BadgeView;
import com.zhiyicx.baseproject.widget.UserAvatarView;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.i.OnUserInfoClickListener;
import com.zhiyicx.thinksnsplus.modules.chat.ChatActivity;
import com.zhiyicx.thinksnsplus.modules.chat.call.TSEMHyphenate;
import com.zhiyicx.thinksnsplus.modules.home.message.messagelist.MessageConversationContract;
import com.zhiyicx.thinksnsplus.modules.home.message.messagelist.MessageConversationPresenter;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @author Catherine
 * @describe 基于环信的会话列表adapter
 * @date 2017/12/12
 * @contact email:648129313@qq.com
 */
public class MessageAdapterV2 extends CommonAdapter<MessageItemBeanV2> implements SwipeItemMangerInterface, SwipeAdapterInterface {

    private SwipeItemMangerImpl mItemManger;
    private OnSwipeItemClickListener mOnSwipeItemClickListener;
    private OnConversationItemLongClickListener mOnConversationItemLongClickListener;
    private OnUserInfoClickListener mOnUserInfoClickListener;
    private MessageConversationContract.Presenter mPresenter;

    public MessageAdapterV2(Context context, List<MessageItemBeanV2> datas, MessageConversationContract.Presenter presenter) {
        super(context, R.layout.item_message_list, datas);
        mItemManger = new SwipeItemRecyclerMangerImpl(this);
        mPresenter = presenter;
    }

    @Override
    protected void convert(ViewHolder holder, MessageItemBeanV2 messageItemBean, int position) {
        setItemData(holder, messageItemBean, position);
    }

    /**
     * 设置item 数据
     *
     * @param holder          控件管理器
     * @param messageItemBean 当前数据
     * @param position        当前数据位置
     */
    private void setItemData(ViewHolder holder, final MessageItemBeanV2 messageItemBean, final int position) {
        // 右边
        final SwipeLayout swipeLayout = holder.getView(R.id.swipe);
        swipeLayout.setSwipeEnabled(false);
        UserAvatarView userAvatarView = holder.getView(R.id.iv_headpic);
        holder.getTextView(R.id.tv_time).setCompoundDrawables(null, null, null, null);
        switch (messageItemBean.getConversation().getType()) {
            case Chat:
                // 私聊
                UserInfoBean singleChatUserinfo = messageItemBean.getUserInfo();
                if (singleChatUserinfo == null) {
                    TSEMHyphenate.getInstance().getChatUser(messageItemBean.getEmKey());
                }
                userAvatarView.getIvVerify().setVisibility(View.VISIBLE);
                ImageUtils.loadUserHead(singleChatUserinfo, userAvatarView, false);
                // 响应事件
                holder.setText(R.id.tv_name, singleChatUserinfo.getName());
                setUserInfoClick(holder.getView(R.id.tv_name), messageItemBean);
                setUserInfoClick(holder.getView(R.id.iv_headpic), messageItemBean);
//                swipeLayout.setSwipeEnabled(mPresenter == null || (singleChatUserinfo!=null&&!mPresenter.checkUserIsImHelper(singleChatUserinfo
// .getUser_id())));
                break;
            case GroupChat:
                // 群组
                ChatGroupBean chatGroupBean = messageItemBean.getChatGroupBean();
                EMGroup group = EMClient.getInstance().groupManager().getGroup(messageItemBean.getEmKey());
                if (group != null && group.isMsgBlocked()) {
                    holder.getTextView(R.id.tv_time).setCompoundDrawablePadding(mContext.getResources().getDimensionPixelOffset(com.zhiyicx
                            .baseproject.R.dimen.spacing_small));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        holder.getTextView(R.id.tv_time).setCompoundDrawablesRelative(UIUtils.getCompoundDrawables(mContext, R.mipmap
                                .ico_newslist_shield), null, null, null);
                    } else {
                        holder.getTextView(R.id.tv_time).setCompoundDrawables(UIUtils.getCompoundDrawables(mContext, R.mipmap.ico_newslist_shield),
                                null, null, null);
                    }
                }
                userAvatarView.getIvVerify().setVisibility(View.GONE);
                Glide.with(mContext)
                        .load(chatGroupBean == null || TextUtils.isEmpty(chatGroupBean.getGroup_face()) ? R.mipmap.ico_ts_assistant : chatGroupBean
                                .getGroup_face())
                        .error(R.mipmap.ico_ts_assistant)
                        .placeholder(R.mipmap.ico_ts_assistant)
                        .transform(new GlideCircleTransform(mContext))
                        .into(userAvatarView.getIvAvatar());
                // 群名称
                String groupName = mContext.getString(R.string.chat_group_name_default, chatGroupBean == null ? group.getGroupName
                        () : chatGroupBean.getName(), chatGroupBean == null ? group.getMemberCount() : chatGroupBean.getAffiliations_count());
                // + "(" + chatGroupBean.getAffiliations_count() + ")";
                holder.setText(R.id.tv_name, groupName);
//                swipeLayout.setSwipeEnabled(true);
                setUserInfoClick(holder.getView(R.id.tv_name), messageItemBean);
                setUserInfoClick(holder.getView(R.id.iv_headpic), messageItemBean);
                break;
            default:

                break;
        }
        if (messageItemBean.getConversation().getLastMessage() == null) {
            holder.setText(R.id.tv_content, mContext.getString(R.string
                    .ts_chat_no_message_default_tip));
        } else {
            // 最新的消息的发言人，只有群组才管这个
            String lastUserName = "";
            ChatUserInfoBean chatUserInfoBean = TSEMHyphenate.getInstance().getChatUser(messageItemBean.getConversation().getLastMessage().getFrom());
            if (!TextUtils.isEmpty(chatUserInfoBean.getName())) {
                lastUserName = chatUserInfoBean.getName() + ": ";
            }
            EMMessage message = messageItemBean.getConversation().getLastMessage();
            // 根据发送状态设置是否有失败icon
            if (messageItemBean.getConversation().getLastMessage().status() == EMMessage.Status.FAIL) {
                holder.getTextView(R.id.tv_content).setCompoundDrawablePadding(mContext.getResources().getDimensionPixelOffset(com.zhiyicx
                        .baseproject.R.dimen.spacing_small));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    holder.getTextView(R.id.tv_content).setCompoundDrawablesRelative(UIUtils.getCompoundDrawables(mContext, R.mipmap
                            .msg_box_remind), null, null, null);
                } else {
                    holder.getTextView(R.id.tv_content).setCompoundDrawables(UIUtils.getCompoundDrawables(mContext, R.mipmap.msg_box_remind), null,
                            null, null);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    holder.getTextView(R.id.tv_content).setCompoundDrawablesRelative(null, null, null, null);
                } else {
                    holder.getTextView(R.id.tv_content).setCompoundDrawables(null, null, null, null);
                }
            }
            String content = "";
            switch (message.getType()) {
                case TXT:
                    // 文字聊天展示聊天内容
                    content = messageItemBean.getConversation().isGroup() ? lastUserName
                            + ((EMTextMessageBody) message.getBody()).getMessage() : ((EMTextMessageBody) message.getBody()).getMessage();
                    break;
                case IMAGE:
                    // 图片聊天 展示[图片]
                    content = messageItemBean.getConversation().isGroup() ? lastUserName
                            + mContext.getString(R.string.chat_type_image) : mContext.getString(R.string.chat_type_image);
                    break;
                case VOICE:
                    // 语音聊天 展示[语音]
                    content = messageItemBean.getConversation().isGroup() ? lastUserName
                            + mContext.getString(R.string.chat_type_voice) : mContext.getString(R.string.chat_type_voice);
                    break;
                case VIDEO:
                    // 视频聊天 展示[视频]
                    content = messageItemBean.getConversation().isGroup() ? lastUserName
                            + mContext.getString(R.string.chat_type_video) : mContext.getString(R.string.chat_type_video);
                    break;
                case LOCATION:
                    // 位置消息 展示[位置]
                    content = messageItemBean.getConversation().isGroup() ? lastUserName
                            + mContext.getString(R.string.chat_type_location) : mContext.getString(R.string.chat_type_location);
                    break;
                case FILE:
                    // 文件消息 展示[文件]
                    content = messageItemBean.getConversation().isGroup() ? lastUserName
                            + mContext.getString(R.string.chat_type_file) : mContext.getString(R.string.chat_type_file);
                    break;
                default:

            }
            holder.setText(R.id.tv_content, content);
        }
        if (messageItemBean.getConversation().getLastMessage() == null || messageItemBean.getConversation().getLastMessage().getMsgTime() == 0) {
            holder.setText(R.id.tv_time, "");
        } else {
            holder.setText(R.id.tv_time, TimeUtils.getTimeFriendlyNormal(messageItemBean.getConversation().getLastMessage().getMsgTime()));
        }
        try {
            ((BadgeView) holder.getView(R.id.tv_tip)).setBadgeCount(Integer.parseInt(ConvertUtils.messageNumberConvert(messageItemBean
                    .getConversation().getUnreadMsgCount())));
        } catch (Exception e) {
            e.printStackTrace();
        }


        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
//                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        RxView.clicks(holder.getView(R.id.tv_right))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    mItemManger.closeAllItems();
                    if (mOnSwipeItemClickListener != null) {
                        mOnSwipeItemClickListener.onRightClick(position);
                    }
                });
        RxView.clicks(holder.getView(R.id.rl_left))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (hasItemOpend()) {
                        closeAllItems();
                        return;
                    }
                    if (mOnSwipeItemClickListener != null && !mItemManger.isOpen(position)) {
                        mOnSwipeItemClickListener.onLeftClick(position);
                    }
                    mItemManger.closeAllItems();
                });
        RxView.longClicks(holder.getView(R.id.rl_left))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (mPresenter == null || hasItemOpend()) {
                        closeAllItems();
                        return;
                    }
                    // 小助手不可以被删除
                    boolean isCantDelete = messageItemBean.getConversation().getType() == EMConversation.EMConversationType.Chat && messageItemBean.getUserInfo() != null && mPresenter.checkUserIsImHelper(messageItemBean.getUserInfo()
                            .getUser_id());
                    if (mOnConversationItemLongClickListener != null && !isCantDelete) {
                        mOnConversationItemLongClickListener.onConversationItemLongClick(position);
                    }
                    mItemManger.closeAllItems();
                });
        mItemManger.bindView(holder.getConvertView(), position);

    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public void openItem(int position) {
        mItemManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        mItemManger.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        mItemManger.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        mItemManger.closeAllItems();
        mItemManger.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return mItemManger.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return mItemManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        mItemManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return mItemManger.isOpen(position);
    }

    @Override
    public Attributes.Mode getMode() {
        return mItemManger.getMode();
    }

    @Override
    public void setMode(Attributes.Mode mode) {
        mItemManger.setMode(mode);
    }

    private void setUserInfoClick(View v, final MessageItemBeanV2 messageItemBean) {
        RxView.clicks(v)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (hasItemOpend()) {
                        closeAllItems();
                        return;
                    }
                    ChatActivity.startChatActivity(mContext, messageItemBean.getConversation().conversationId()
                            , messageItemBean.getConversation().getType() == EMConversation.EMConversationType.Chat ? EaseConstant.CHATTYPE_SINGLE
                                    : EaseConstant.CHATTYPE_GROUP);
                });
    }

    /**
     * 是否有 item 被划开了
     *
     * @return true 有被划开的
     */
    public boolean hasItemOpend() {
        List<Integer> data = mItemManger.getOpenItems();
        return mItemManger != null && !data.isEmpty() && data.get(0) > -1;
    }

    public void setOnSwipItemClickListener(OnSwipeItemClickListener onSwipeItemClickListener) {
        mOnSwipeItemClickListener = onSwipeItemClickListener;
    }

    public void setOnUserInfoClickListener(OnUserInfoClickListener onUserInfoClickListener) {
        mOnUserInfoClickListener = onUserInfoClickListener;
    }

    public void setOnConversationItemLongClickListener(OnConversationItemLongClickListener onConversationItemLongClickListener) {
        mOnConversationItemLongClickListener = onConversationItemLongClickListener;
    }

    public interface OnSwipeItemClickListener {
        void onLeftClick(int position);

        void onRightClick(int position);
    }

    public interface OnConversationItemLongClickListener {
        void onConversationItemLongClick(int position);
    }
}
