package com.zhiyicx.thinksnsplus.modules.personal_center;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.data.source.repository.IUploadRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.PersonalCenterRepository;
import com.zhiyicx.thinksnsplus.data.source.repository.UpLoadRepository;

import dagger.Module;
import dagger.Provides;

/**
 * @author LiuChao
 * @describe
 * @date 2017/3/7
 * @contact email:450127106@qq.com
 */
@Module
public class PersonalCenterPresenterModule {
    private PersonalCenterContract.View mView;

    public PersonalCenterPresenterModule(PersonalCenterContract.View view) {
        mView = view;
    }

    @Provides
    public PersonalCenterContract.View providePersonalCenterContractView() {
        return mView;
    }

    @Provides
    public PersonalCenterContract.Repository providePersonalCenterContractRepository(ServiceManager serviceManager, Application application) {
        return new PersonalCenterRepository(serviceManager, application);
    }

    @Provides
    IUploadRepository provideIUploadRepository(ServiceManager serviceManager, Application application) {
        return new UpLoadRepository(serviceManager, application);
    }
}
