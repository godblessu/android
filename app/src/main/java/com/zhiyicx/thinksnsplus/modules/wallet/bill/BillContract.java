package com.zhiyicx.thinksnsplus.modules.wallet.bill;

import com.zhiyicx.baseproject.base.ITSListPresenter;
import com.zhiyicx.baseproject.base.ITSListView;
import com.zhiyicx.thinksnsplus.data.beans.WithdrawalsListBean;

/**
 * @Author Jliuer
 * @Date 2017/06/02/15:46
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface BillContract {
    interface View extends ITSListView<WithdrawalsListBean,Presenter>{

    }

    interface Presenter extends ITSListPresenter<WithdrawalsListBean>{

    }

    interface Repository{

    }
}
