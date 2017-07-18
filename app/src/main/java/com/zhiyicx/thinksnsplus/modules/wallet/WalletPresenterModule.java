package com.zhiyicx.thinksnsplus.modules.wallet;

import com.zhiyicx.thinksnsplus.data.source.repository.WalletRepository;

import dagger.Module;
import dagger.Provides;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/05/22
 * @Contact master.jungle68@gmail.com
 */
@Module
public class WalletPresenterModule {
    private final WalletContract.View mView;

    public WalletPresenterModule(WalletContract.View view) {
        mView = view;
    }

    @Provides
    WalletContract.View provideWalletContractView() {
        return mView;
    }


    @Provides
    WalletContract.Repository provideWalletContractRepository(WalletRepository walletRepository) {
        return walletRepository;
    }
}
