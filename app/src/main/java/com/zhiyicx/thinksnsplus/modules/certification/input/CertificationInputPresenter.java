package com.zhiyicx.thinksnsplus.modules.certification.input;

import android.text.TextUtils;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.common.mvp.BasePresenter;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.SendCertificationBean;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/2
 * @contact email:648129313@qq.com
 */
@FragmentScoped
public class CertificationInputPresenter extends BasePresenter<CertificationInputContract.Repository, CertificationInputContract.View>
        implements CertificationInputContract.Presenter{

    @Inject
    public CertificationInputPresenter(CertificationInputContract.Repository repository,
                                       CertificationInputContract.View rootView) {
        super(repository, rootView);
    }
}
