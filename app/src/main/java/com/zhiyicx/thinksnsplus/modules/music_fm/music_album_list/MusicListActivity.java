package com.zhiyicx.thinksnsplus.modules.music_fm.music_album_list;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.base.AppApplication;

/**
 * @Author Jliuer
 * @Date 2017/2/13/17:04
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MusicListActivity extends TSActivity<MusicPresenter, MusicListFragment> {

    @Override
    protected MusicListFragment getFragment() {
        return new MusicListFragment();
    }

    @Override
    protected void componentInject() {
        DaggerMusicComponent.builder().appComponent(AppApplication.AppComponentHolder
                .getAppComponent())
                .musicPresenterModule(new MusicPresenterModule(mContanierFragment))
                .build().inject(this);
    }
}
