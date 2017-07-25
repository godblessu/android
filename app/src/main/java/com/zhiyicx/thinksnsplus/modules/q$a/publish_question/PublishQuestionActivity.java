package com.zhiyicx.thinksnsplus.modules.q$a.publish_question;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;


/**
 * @Describe the page for publish qustion
 * @Author Jungle68
 * @Date 2017/7/25
 * @Contact master.jungle68@gmail.com
 */

public class PublishQuestionActivity extends TSActivity<PublishQuestionPresenter, PublishQuestionFragment> {
    @Override
    protected PublishQuestionFragment getFragment() {
        return PublishQuestionFragment.newInstance();
    }

    @Override
    protected void componentInject() {
        DaggerPublishQuestionComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .publishQuestionPresenterModule(new PublishQuestionPresenterModule(mContanierFragment))
                .build()
                .inject(this);
    }
}
