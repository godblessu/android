package com.zhiyicx.thinksnsplus.data.source.repository;

import com.google.gson.Gson;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.data.beans.QAPublishBean;
import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.modules.q_a.reward.QARewardContract;

import javax.inject.Inject;

import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Catherine
 * @describe
 * @date 2017/7/25
 * @contact email:648129313@qq.com
 */

public class QA$RewardRepositoryPublish extends BasePublishQuestionRepository implements QARewardContract.RepositoryPublish {

    @Inject
    public QA$RewardRepositoryPublish(ServiceManager manager) {
        super(manager);
    }

    @Override
    public Observable<BaseJsonV2<QAPublishBean>> publishQuestion(QAPublishBean qaPublishBean) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(qaPublishBean));
        return mQAClient.publishQuestion(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
