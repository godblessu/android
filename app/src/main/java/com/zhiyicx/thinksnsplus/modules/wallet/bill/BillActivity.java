package com.zhiyicx.thinksnsplus.modules.wallet.bill;

import com.zhiyicx.baseproject.base.TSActivity;

public class BillActivity extends TSActivity<BillPresenter,BillListFragment> {

    @Override
    protected BillListFragment getFragment() {
        return BillListFragment.newInstance();
    }

    @Override
    protected void componentInject() {

    }
}
