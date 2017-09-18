package com.zhiyicx.thinksnsplus.modules.settings.init_password;

import com.zhiyicx.thinksnsplus.data.source.repository.InitPasswordRepository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Catherine
 * @describe
 * @date 2017/9/18
 * @contact email:648129313@qq.com
 */
@Module
public class InitPasswordPresenterModule {

    private InitPasswordContract.View mView;

    public InitPasswordPresenterModule(InitPasswordContract.View mView) {
        this.mView = mView;
    }

    @Provides
    public InitPasswordContract.View provideInitPasswordContractView(){
        return mView;
    }

    @Provides
    public InitPasswordContract.Repository provideInitPasswordContractRepository(InitPasswordRepository repository){
        return repository;
    }
}
