package com.zhiyicx.thinksnsplus.modules.chat;

import android.text.TextUtils;

import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.imsdk.entity.Message;
import com.zhiyicx.imsdk.manage.ChatClient;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.ChatItemBean;

import org.simple.eventbus.Subscriber;

import java.util.List;

import javax.inject.Inject;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/1/6
 * @Contact master.jungle68@gmail.com
 */

public class ChatPresenter extends BasePresenter<ChatContract.Repository, ChatContract.View> implements ChatContract.Presenter {


    @Inject
    public ChatPresenter(ChatContract.Repository repository, ChatContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void getUserInfo(long user_id) {

    }

    @Override
    public List<ChatItemBean> getHistoryMessages(int cid, long mid) {
        return mRepository.getChatListData(cid, mid);
    }

    /*******************************************
     * IM 相关
     *********************************************/
    /**
     * 发送文本消息
     *
     * @param text 文本内容
     * @param cid  对话 id
     */
    @Override
    public void sendTextMessage(String text, int cid) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Message message = ChatClient.getInstance(mContext).sendTextMsg(text, cid, "", 0);
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONMESSAGERECEIVED)
    private void onMessageReceived(Message message) {
        LogUtils.d(TAG, "------onMessageReceived------->" + message);

    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONMESSAGEACKRECEIVED)
    private void onMessageACKReceived(Message message) {
        LogUtils.d(TAG, "------onMessageACKReceived------->" + message);
    }


    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONCONNECTED)
    private void onConnected() {
        mRootView.showMessage("IM 聊天加载成功");
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONDISCONNECT)
    private void onDisconnect(int code, String reason) {
        mRootView.showMessage("IM 聊天断开" + reason);
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONERROR)
    private void onError(Exception error) {
        mRootView.showMessage("IM 聊天错误" + error.toString());
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_IM_ONMESSAGETIMEOUT)
    private void onMessageTimeout(Message message) {
        mRootView.showMessage("IM 聊天超时" + message);
    }
}
