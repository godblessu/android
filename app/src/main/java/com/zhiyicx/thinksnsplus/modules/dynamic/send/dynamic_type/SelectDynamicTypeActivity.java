package com.zhiyicx.thinksnsplus.modules.dynamic.send.dynamic_type;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;

import static com.zhiyicx.thinksnsplus.modules.dynamic.send.dynamic_type.SelectDynamicTypeFragment.SEND_OPTION;

public class SelectDynamicTypeActivity extends TSActivity<SelectDynamicTypePresenter, SelectDynamicTypeFragment> {

    @Override
    protected SelectDynamicTypeFragment getFragment() {
        return SelectDynamicTypeFragment.getInstance(getIntent().getBundleExtra(SEND_OPTION));
    }

    @Override
    protected void componentInject() {
        DaggerSelectDynamicTypeComponent.builder()
                .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                .selectDynamicTypePresenterModule(new SelectDynamicTypePresenterModule(mContanierFragment))
                .build()
                .inject(this);
    }

    @Override
    public void onBackPressed() {
        mContanierFragment.onBackPressed();
    }
}
