package com.zhiyicx.thinksnsplus.jpush;

import android.content.Context;

import com.zhiyicx.common.utils.log.LogUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import rx.Observable;
import rx.functions.Action1;

import static com.zhiyicx.baseproject.crashhandler.CrashHandler.TAG;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/3/28
 * @Contact master.jungle68@gmail.com
 */

public class JpushAlias {
    private static final int TIME_INTERCEPT = 60;
    private Context mContext;
    private String mAlias;

    public JpushAlias(Context context, String alias) {
        mAlias = alias;
        mContext = context;
        setAlias();
    }

    // 这是来自 JPush Example 的设置别名的 Activity 里的代码。一般 App 的设置的调用入口，在任何方便的地方调用都可以。
    private void setAlias() {
        // 调用 JPush 接口来设置别名。
        JPushInterface.setAliasAndTags(mContext,
                String.valueOf(mAlias),
                null,
                mAliasCallback);
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    LogUtils.i(TAG, logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    LogUtils.i(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    Observable.timer(TIME_INTERCEPT, TimeUnit.SECONDS)
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    setAlias();
                                }
                            });
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    LogUtils.e(TAG, logs);
            }
        }
    };

}
