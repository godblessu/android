package com.zhiyicx.thinksnsplus.base;

import android.app.Application;

import com.zhiyicx.common.dagger.module.AppModule;
import com.zhiyicx.common.dagger.module.HttpClientModule;
import com.zhiyicx.common.dagger.module.ImageModule;
import com.zhiyicx.common.utils.imageloader.core.ImageLoader;
import com.zhiyicx.rxerrorhandler.RxErrorHandler;
import com.zhiyicx.thinksnsplus.comment.DeleteComment;
import com.zhiyicx.thinksnsplus.comment.SendComment;
import com.zhiyicx.thinksnsplus.dagger.GreenDaoModule;
import com.zhiyicx.thinksnsplus.data.source.local.CacheManager;
import com.zhiyicx.thinksnsplus.data.source.local.ChannelInfoBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.ChannelSubscripBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.CommentedBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DigedBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicCommentBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicDetailBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicToolBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.FlushMessageBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.FollowFansBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.InfoListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.InfoTypeBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.MusicAlbumListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.SystemConversationBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.UserInfoBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.data.source.repository.AuthRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.SendDynamicRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.UpLoadRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.UserInfoRepository;
import com.zhiyicx.thinksnsplus.service.backgroundtask.BackgroundTaskHandler;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2016/12/16
 * @Contact 335891510@qq.com
 */

@Singleton
@Component(modules = {AppModule.class, HttpClientModule.class, ServiceModule.class, CacheModule.class, ImageModule.class, GreenDaoModule.class})
public interface AppComponent extends InjectComponent<AppApplication> {
    void inject(BackgroundTaskHandler backgroundTaskHandler);

    void inject(DeleteComment deleteComment);

    void inject(SendComment sendComment);

    Application Application();

    //服务管理器,retrofitApi
    ServiceManager serviceManager();

    //缓存管理器
    CacheManager cacheManager();

    //Rxjava错误处理管理类
    RxErrorHandler rxErrorHandler();

    OkHttpClient okHttpClient();

    //图片管理器,用于加载图片的管理类,默认使用glide,使用策略模式,可替换框架
    ImageLoader imageLoader();

    AuthRepository authRepository();

    UserInfoBeanGreenDaoImpl userInfoBeanGreenDao();

    FollowFansBeanGreenDaoImpl followFansBeanGreenDao();

    DynamicBeanGreenDaoImpl dynamicBeanGreenDao();

    DynamicCommentBeanGreenDaoImpl dynamicCommentBeanGreenDao();

    DigedBeanGreenDaoImpl digedBeanGreenDao();

    CommentedBeanGreenDaoImpl commentedBeanGreenDao();

    FlushMessageBeanGreenDaoImpl flushMessageBeanGreenDao();

    DynamicDetailBeanGreenDaoImpl dynamicDetailBeanGreenDao();

    DynamicToolBeanGreenDaoImpl dynamicToolBeanGreenDao();

    InfoTypeBeanGreenDaoImpl infoTypeBeanGreenDaoImpl();

    InfoListBeanGreenDaoImpl infoListBeanGreenDaoImpl();

    ChannelInfoBeanGreenDaoImpl channelInfoBeanGreenDaoImpl();

    ChannelSubscripBeanGreenDaoImpl channelSubscripBeanGreenDaoImpl();

    MusicAlbumListBeanGreenDaoImpl musicAlbumListBeanGreenDaoImpl();

    SystemConversationBeanGreenDaoImpl systemConversationBeanGreenDaoImpl();

    UserInfoRepository userInfoRepository();

    SendDynamicRepository dynamicPresenterRepository();

    UpLoadRepository uploadRepository();
}
