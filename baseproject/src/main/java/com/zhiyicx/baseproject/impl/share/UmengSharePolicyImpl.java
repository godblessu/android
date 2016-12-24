package com.zhiyicx.baseproject.impl.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zhiyicx.baseproject.R;
import com.zhiyicx.baseproject.config.UmengConfig;
import com.zhiyicx.common.thridmanager.share.OnShareCallbackListener;
import com.zhiyicx.common.thridmanager.share.Share;
import com.zhiyicx.common.thridmanager.share.ShareContent;
import com.zhiyicx.common.thridmanager.share.SharePolicy;


/**
 * @Describe 友盟分享方针实现
 * @Author Jungle68
 * @Date 2016/12/20
 * @Contact master.jungle68@gmail.com
 */
public class UmengSharePolicyImpl implements SharePolicy {

    /**
     * 友盟初始化
     */
    static {
        PlatformConfig.setQQZone(UmengConfig.QQ_APPID, UmengConfig.QQ_SECRETKEY);
        PlatformConfig.setWeixin(UmengConfig.WEIXIN_APPID, UmengConfig.WEIXIN_SECRETKEY);
        PlatformConfig.setSinaWeibo(UmengConfig.SINA_APPID, UmengConfig.SINA_SECRETKEY);
        Config.REDIRECT_URL = UmengConfig.SINA_SECRETKEY;
    }

    private Context mContext;
    private ShareContent mShareContent;

    public UmengSharePolicyImpl(Context mContext) {
        this.mContext = mContext;
        init(mContext);
    }

    public UmengSharePolicyImpl(ShareContent mShareContent, Context mContext) {
        this.mContext = mContext;
        this.mShareContent = mShareContent;
        init(mContext);
    }

    private void init(Context mContext) {

        UMShareAPI.get(mContext);
    }

    public ShareContent getShareContent() {
        return mShareContent;
    }

    /**
     * 如果使用的是 qq 或者新浪精简版 jar，需要在您使用分享或授权的 Activity（ fragment 不行）中添加如下回调代码
     *
     * @param requestCode
     * @param resultCode
   @param data
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data,Context context) {
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置分享内容
     *
     * @param shareContent
     */
    @Override
    public void setShareContent(ShareContent shareContent) {
        mShareContent = shareContent;
    }

    /**
     * 微信朋友分享
     */
    @Override
    public void shareMoment(Activity activity, final OnShareCallbackListener l) {
        if (checkShareContent()) return;
        shareActionConfig(activity, l, SHARE_MEDIA.WEIXIN_CIRCLE);

    }


    /**
     * QQ分享
     */
    @Override
    public void shareQQ(Activity activity, OnShareCallbackListener l) {
        if (checkShareContent()) return;
        shareActionConfig(activity, l, SHARE_MEDIA.QQ);
    }

    /**
     * QQ空间分享
     */
    @Override
    public void shareZone(Activity activity, OnShareCallbackListener l) {
        if (checkShareContent()) return;
        shareActionConfig(activity, l, SHARE_MEDIA.QZONE);

    }

    /**
     * 微博分享
     */
    @Override
    public void shareWeibo(Activity activity, OnShareCallbackListener l) {
        if (checkShareContent()) return;
        shareActionConfig(activity, l, SHARE_MEDIA.SINA);
    }

    /**
     * 微信分享
     */
    @Override
    public void shareWechat(Activity activity, OnShareCallbackListener l) {
        if (checkShareContent()) return;
        shareActionConfig(activity, l, SHARE_MEDIA.WEIXIN);
    }


    /**
     * 分享弹框
     */
    @Override
    public void showShare(Activity activity) {


    }

    /**
     * 检测分享内容是否为空
     *
     * @return
     */
    private boolean checkShareContent() {
        if (mShareContent == null) {
            throw new NullPointerException(mContext.getResources().getString(R.string.err_share_no_content));
        }
        return false;
    }

    /**
     * 配置分享内容
     * @param activity
     * @param l
     * @param share_media
     */
    private void shareActionConfig(Activity activity, final OnShareCallbackListener l, SHARE_MEDIA share_media) {
        ShareAction shareAction = new ShareAction(activity);
        shareAction.setPlatform(share_media);
        if (!TextUtils.isEmpty(mShareContent.getTitle())) {
            shareAction.withTitle(mShareContent.getTitle());
        }
        if (!TextUtils.isEmpty(mShareContent.getContent())) {
            shareAction.withText(mShareContent.getContent());
        }
        if (!TextUtils.isEmpty(mShareContent.getImage())) {
            UMImage image = new UMImage(activity, mShareContent.getImage());
            shareAction.withMedia(image);
        }
        if (0 != mShareContent.getResImage()) {
            UMImage image = new UMImage(activity, mShareContent.getResImage());
            shareAction.withMedia(image);
        }
        if (null != mShareContent.getBitmap()) {
            UMImage image = new UMImage(activity, mShareContent.getBitmap());
            shareAction.withMedia(image);
        }
        if (null != mShareContent.getFile()) {
            UMImage image = new UMImage(activity, mShareContent.getFile());
            shareAction.withMedia(image);
        }
        if (!TextUtils.isEmpty(mShareContent.getUrl())) {
            shareAction.withTargetUrl(mShareContent.getUrl());
        }
        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onResult(SHARE_MEDIA share_media) {
                if (l == null) {
                    Share share = changeShare(share_media);
                    l.onSuccess(share);
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                if (l == null) {
                    Share share = changeShare(share_media);
                    l.onError(share, throwable);
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                if (l == null) {
                    Share share = changeShare(share_media);
                    l.onCancel(share);
                }
            }
        });
        shareAction.share();
    }

    /**
     * 转换分享平台标识，方便修改三方时回调标识错误
     * @param share_media
     * @return
     */
    private Share changeShare(SHARE_MEDIA share_media) {
        Share share = null;
        switch (share_media) {
            case QQ:
                share = Share.QQ;
                break;
            case QZONE:
                share = Share.QZONE;
                break;
            case WEIXIN:
                share = Share.WEIXIN;
                break;
            case WEIXIN_CIRCLE:
                share = Share.WEIXIN_CIRCLE;
                break;
            case SINA:
                share = Share.SINA;
                break;
            default:
                break;

        }
        return share;
    }
}
