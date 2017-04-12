package com.zhiyicx.thinksnsplus.modules.home;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.imsdk.entity.ChatRoomContainer;
import com.zhiyicx.imsdk.entity.Conversation;
import com.zhiyicx.imsdk.entity.Message;
import com.zhiyicx.imsdk.manage.ChatClient;
import com.zhiyicx.imsdk.manage.listener.ImMsgReceveListener;
import com.zhiyicx.imsdk.manage.listener.ImStatusListener;
import com.zhiyicx.imsdk.manage.listener.ImTimeoutListener;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.config.JpushMessageTypeConfig;
import com.zhiyicx.thinksnsplus.data.beans.JpushMessageBean;
import com.zhiyicx.thinksnsplus.data.source.repository.AuthRepository;
import com.zhiyicx.thinksnsplus.utils.NotificationUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import javax.inject.Inject;


/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/13
 * @Contact master.jungle68@gmail.com
 */
@FragmentScoped
class HomePresenter extends BasePresenter<HomeContract.Repository, HomeContract.View> implements HomeContract.Presenter, ImMsgReceveListener, ImStatusListener, ImTimeoutListener {
    @Inject
    AuthRepository mAuthRepository;

    @Inject

    public HomePresenter(HomeContract.Repository repository, HomeContract.View rootView) {
        super(repository, rootView);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    public void initIM() {
        mAuthRepository.loginIM();
        ChatClient.getInstance(mContext).setImMsgReceveListener(this);
        ChatClient.getInstance(mContext).setImStatusListener(this);
        ChatClient.getInstance(mContext).setImTimeoutListener(this);
    }

    /*******************************************
     * 聊天相关回调
     *********************************************/

    @Override
    public void onMessageReceived(Message message) {
        setMessageTipVisable(true);
        EventBus.getDefault().post(message, EventBusTagConfig.EVENT_IM_ONMESSAGERECEIVED);
        JpushMessageBean jpushMessageBean = new JpushMessageBean();
        jpushMessageBean.setType(JpushMessageTypeConfig.JPUSH_MESSAGE_TYPE_IM);
        jpushMessageBean.setMessage(message.getTxt());
        jpushMessageBean.setNofity(false);
        NotificationUtil.showNotifyMessage(mContext, jpushMessageBean);


    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONMESSAGERECEIVED)
    public void setMessageTipVisable(boolean isShow) {
        mRootView.setMessageTipVisable(isShow);
    }

    @Override
    public void onMessageACKReceived(Message message) {
        EventBus.getDefault().post(message, EventBusTagConfig.EVENT_IM_ONMESSAGEACKRECEIVED);
    }

    @Override
    public void onConversationJoinACKReceived(ChatRoomContainer chatRoomContainer) {

    }

    @Override
    public void onConversationLeaveACKReceived(ChatRoomContainer chatRoomContainer) {

    }

    @Override
    public void onConversationMCACKReceived(List<Conversation> conversations) {

    }

    @Override
    public void synchronousInitiaMessage(int limit) {

    }

    @Override
    public void onConnected() {
        EventBus.getDefault().post("", EventBusTagConfig.EVENT_IM_ONCONNECTED);
    }

    @Override
    public void onDisconnect(int code, String reason) {
        EventBus.getDefault().post(code, EventBusTagConfig.EVENT_IM_ONDISCONNECT);
    }

    @Override
    public void onError(Exception error) {
        EventBus.getDefault().post(error, EventBusTagConfig.EVENT_IM_ONERROR);
    }

    @Override
    public void onMessageTimeout(Message message) {
        EventBus.getDefault().post(message, EventBusTagConfig.EVENT_IM_ONMESSAGETIMEOUT);
    }

    @Override
    public void onConversationJoinTimeout(int roomId) {

    }

    @Override
    public void onConversationLeaveTimeout(int roomId) {

    }

    @Override
    public void onConversationMcTimeout(List<Integer> roomIds) {

    }
}