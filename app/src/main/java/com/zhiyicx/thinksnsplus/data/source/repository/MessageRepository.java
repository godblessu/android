package com.zhiyicx.thinksnsplus.data.source.repository;

import android.app.Application;
import android.text.TextUtils;
import android.util.SparseArray;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.zhiyicx.baseproject.base.SystemConfigBean;
import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.config.ConstantConfig;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.imsdk.db.dao.ConversationDao;
import com.zhiyicx.imsdk.db.dao.MessageDao;
import com.zhiyicx.imsdk.entity.Conversation;
import com.zhiyicx.imsdk.entity.Message;
import com.zhiyicx.rxerrorhandler.functions.RetryWithDelay;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBean;
import com.zhiyicx.thinksnsplus.data.beans.MessageItemBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.TSPNotificationBean;
import com.zhiyicx.thinksnsplus.data.beans.UnReadNotificaitonBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.UserInfoBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.remote.ChatInfoClient;
import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.data.source.remote.UserInfoClient;
import com.zhiyicx.thinksnsplus.modules.home.message.MessageContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/8
 * @Contact master.jungle68@gmail.com
 */

public class MessageRepository implements MessageContract.Repository {
    public static final int MAX_RETRY_COUNTS = 3;//重试次数
    public static final int RETRY_DELAY_TIME = 5;// 重试间隔时间,单位 s
    private ChatInfoClient mChatInfoClient;
    private UserInfoClient mUserInfoClient;
    @Inject
    Application mContext;
    @Inject
    UserInfoRepository mUserInfoRepository;
    @Inject
    UserInfoBeanGreenDaoImpl mUserInfoBeanGreenDao;

    @Inject
    public MessageRepository(ServiceManager serviceManager) {
        mChatInfoClient = serviceManager.getChatInfoClient();
        mUserInfoClient = serviceManager.getUserInfoClient();
    }

    /**
     * 获取当前用户的对话信息
     *
     * @param user_id 用户 id
     * @return
     */
    @Override
    public Observable<List<MessageItemBean>> getConversationList(final int user_id) {

        return mChatInfoClient.getConversaitonList()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Func1<List<Conversation>, Observable<List<MessageItemBean>>>) listBaseJson -> {
                    LogUtils.d("listBaseJson = " + listBaseJson);
                    if (!listBaseJson.isEmpty()) {
                        List<MessageItemBean> datas = new ArrayList<>();
                        List<Object> integers = new ArrayList<>();
                        for (Conversation tmp : listBaseJson) {
                            MessageItemBean messageItemBean = new MessageItemBean();
                            Message message = MessageDao.getInstance(mContext).getLastMessageByCid(tmp.getCid());
                            if (message != null) {
                                tmp.setLast_message(message);
                                tmp.setLast_message_time(message.getCreate_time());
                            } else {
                                // 去除没有聊天消息的
                                continue;
                            }
                            tmp.setIm_uid((int) AppApplication.getmCurrentLoginAuth().getUser_id());
                            // 单聊
                            if (tmp.getType() == Conversation.CONVERSATION_TYPE_PRIVATE) {
                                try {
                                    String[] uidsPair = tmp.getUsids().split(",");
                                    int pair1 = Integer.parseInt(uidsPair[0]);
                                    int pair2 = Integer.parseInt(uidsPair[1]);
                                    tmp.setPair(pair1 > pair2 ? (pair2 + "&" + pair1) : (pair1 + "&" + pair2)); // "pair":null,   //
                                    // type=0时此项为两个uid：min_uid&max_uid
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            // 存储对话信息
                            ConversationDao.getInstance(mContext).insertOrUpdateConversation(tmp);
                            String[] uidsTmp = tmp.getUsids().split(",");
                            UserInfoBean userInfoBean = new UserInfoBean();
                            long toChatUserId = Long.valueOf((uidsTmp[0].equals(AppApplication.getmCurrentLoginAuth().getUser_id() + "") ?
                                    uidsTmp[1] : uidsTmp[0]));
                            userInfoBean.setUser_id(toChatUserId);
                            integers.add(toChatUserId);
                            messageItemBean.setUserInfo(userInfoBean);
                            // 获取未读消息数量
                            int unreadMessageCount = MessageDao.getInstance(mContext).getUnReadMessageCount(tmp.getCid());
                            messageItemBean.setConversation(tmp);
                            messageItemBean.setUnReadMessageNums(unreadMessageCount);
                            datas.add(messageItemBean);
                        }
                        return mUserInfoRepository.getUserInfo(integers).
                                map(userInfoBeanBaseJson -> {
                                    SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                    for (UserInfoBean userInfoBean : userInfoBeanBaseJson) {
                                        userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(), userInfoBean);
                                    }
                                    for (int i = 0; i < datas.size(); i++) {
                                        datas.get(i).setUserInfo(userInfoBeanSparseArray.get(datas.get(i).getUserInfo().getUser_id().intValue()));
                                    }
                                    // 存储用户信息
                                    mUserInfoBeanGreenDao.insertOrReplace(userInfoBeanBaseJson);
                                    return datas;
                                });

                    } else {
                        return Observable.just(new ArrayList<>());
                    }
                })
                //去除没有聊过天的数据
                .map(listBaseJson -> {
                    if (!listBaseJson.isEmpty()) {
                        for (int i = 0; i < listBaseJson.size(); i++) {
                            if (listBaseJson.get(i).getConversation().getLast_message() == null || TextUtils.isEmpty(listBaseJson.get(i)
                                    .getConversation().getLast_message().getTxt())) {
                                listBaseJson.remove(i);
                            }
                        }
                    }
                    return listBaseJson;
                })
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 获取环信的会话列表
     *
     * @param user_id 用户id
     * @return Observable<List<MessageItemBeanV2>>
     */
    @Override
    public Observable<List<MessageItemBeanV2>> getConversationListV2(final int user_id) {
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        Observable<List<MessageItemBeanV2>> observable = Observable.just(conversations)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(stringEMConversationMap -> {
                    // 先将环信返回的数据转换为model 然后根据type来处理是否需要获取用户信息，如果不需要则直接返回数据
                    List<MessageItemBeanV2> list = new ArrayList<>();
                    for (Map.Entry<String, EMConversation> entry : stringEMConversationMap.entrySet()) {
                        MessageItemBeanV2 itemBeanV2 = new MessageItemBeanV2();
                        itemBeanV2.setConversation(entry.getValue());
                        itemBeanV2.setEmKey(entry.getKey());
                        list.add(itemBeanV2);
                    }
                    return list;
                })
                .flatMap(messageItemBeanV2s -> {
                    List<Object> users = new ArrayList<>();
                    for (MessageItemBeanV2 itemBeanV2 : messageItemBeanV2s) {
                        if (itemBeanV2.getConversation().getType() == EMConversation.EMConversationType.Chat) {
                            // 单聊处理用户信息
                            if (itemBeanV2.getEmKey().equals("admin")) {
                                users.add(1L);
                            } else {
                                users.add(itemBeanV2.getEmKey());
                            }
                        }
                    }
                    if (users.isEmpty()) {
                        return Observable.just(messageItemBeanV2s);
                    } else {
                        return mUserInfoRepository.getUserInfo(users)
                                .map(userInfoBeans -> {
                                    SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                    for (UserInfoBean userInfoBean : userInfoBeans) {
                                        userInfoBeanSparseArray.put(userInfoBean.getUser_id().intValue(), userInfoBean);
                                    }
                                    for (int i = 0; i < messageItemBeanV2s.size(); i++) {
                                        // 只有单聊才给用户信息
                                        if (messageItemBeanV2s.get(i).getConversation().getType() == EMConversation.EMConversationType.Chat) {
                                            int key;
                                            if (messageItemBeanV2s.get(i).getEmKey().equals("admin")) {
                                                key = 1;
                                            } else {
                                                key = Integer.parseInt(messageItemBeanV2s.get(i).getEmKey());
                                            }
                                            messageItemBeanV2s.get(i).setUserInfo(userInfoBeanSparseArray.get(key));
                                        }
                                    }
                                    return messageItemBeanV2s;
                                });
                    }

                })
                .observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    /**
     * 通过 cid 获取当前对话信息 ，暂未适配群聊
     *
     * @param cid 对话 id
     * @return
     */
    @Override
    public Observable<MessageItemBean> getSingleConversation(int cid) {
        Message message = MessageDao.getInstance(mContext).getLastMessageByCid(cid);
        if (message == null) {
            return Observable.just(new MessageItemBean());
        }
        return mChatInfoClient.getSingleConversaiton(cid)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(MAX_RETRY_COUNTS, RETRY_DELAY_TIME))
                .flatMap(tmp -> {
                            List<Object> integers = new ArrayList<>();
                            MessageItemBean messageItemBean = new MessageItemBean();
                            if (message != null) {
                                tmp.setLast_message(message);
                                tmp.setLast_message_time(message.getCreate_time());
                            }
                            tmp.setIm_uid((int) AppApplication.getmCurrentLoginAuth().getUser_id());
                            if (tmp.getType() == Conversation.CONVERSATION_TYPE_PRIVATE) { // 单聊
                                try {
                                    String[] uidsPair = tmp.getUsids().split(",");
                                    int pair1 = Integer.parseInt(uidsPair[0]);
                                    int pair2 = Integer.parseInt(uidsPair[1]);
                                    tmp.setPair(pair1 > pair2 ? (pair2 + "&" + pair1) : (pair1 + "&" + pair2)); // "pair":null,   //
                                    // type=0时此项为两个uid：min_uid&max_uid
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            // 存储对话信息
                            ConversationDao.getInstance(mContext).insertOrUpdateConversation(tmp);
                            String[] uidsTmp = tmp.getUsids().split(",");
                            UserInfoBean userInfoBean = new UserInfoBean();
                            for (int i = 0; i < uidsTmp.length; i++) {
                                long toChatUser_id = Long.valueOf((uidsTmp[0].equals(AppApplication.getmCurrentLoginAuth().getUser_id() + "")
                                        ? uidsTmp[1] : uidsTmp[0]));
                                integers.add(toChatUser_id);
                            }
                            try {
                                userInfoBean.setUser_id((Long) integers.get(0) == AppApplication.getmCurrentLoginAuth().getUser_id() ? (Long)
                                        integers.get(1) : (Long) integers.get(0));//保存聊天对象的 user_id ，如果是群聊暂不处理

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            messageItemBean.setUserInfo(userInfoBean);
                            // 获取未读消息数量
                            int unreadMessageCount = MessageDao.getInstance(mContext).getUnReadMessageCount(tmp.getCid());
                            messageItemBean.setConversation(tmp);
                            messageItemBean.setUnReadMessageNums(unreadMessageCount);


                            return mUserInfoRepository.getUserInfo(integers).
                                    map(userInfoBeanBaseJson -> {
                                        SparseArray<UserInfoBean> userInfoBeanSparseArray = new SparseArray<>();
                                        for (UserInfoBean tmpdata : userInfoBeanBaseJson) {
                                            userInfoBeanSparseArray.put(tmpdata.getUser_id().intValue(), tmpdata);
                                        }
                                        messageItemBean.setUserInfo(userInfoBeanSparseArray.get(messageItemBean.getUserInfo().getUser_id()
                                                .intValue()));
                                        // 存储用户信息
                                        mUserInfoBeanGreenDao.insertOrReplace(userInfoBeanBaseJson);

                                        return messageItemBean;
                                    });


                        }

                )
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @return
     */
    @Override
    public Observable<Void> ckeckUnreadNotification() {
        return mUserInfoClient.ckeckUnreadNotification()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @return
     */
    @Override
    public Observable<UnReadNotificaitonBean> getUnreadNotificationData() {
        return mUserInfoClient.getUnreadNotificationData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param notification
     * @param type
     * @param limit
     * @param offset
     * @return
     */
    @Override
    public Observable<List<TSPNotificationBean>> getNotificationList(String notification, String type, Integer limit, Integer offset) {
        return mUserInfoClient.getNotificationList(notification, type, limit, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param notificationId
     * @return
     */
    @Override
    public Observable<TSPNotificationBean> getNotificationDetail(String notificationId) {
        return mUserInfoClient.getNotificationDetail(notificationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * @param notificationId
     * @return
     */
    @Override
    public Observable<Object> makeNotificationReaded(String notificationId) {
        return mUserInfoClient.makeNotificationReaded(notificationId)
                .subscribeOn(Schedulers.io());
    }

    /**
     * @return
     */
    @Override
    public Observable<Object> makeNotificationAllReaded() {
        return mUserInfoClient.makeNotificationAllReaded()
                .subscribeOn(Schedulers.io());
    }
}
