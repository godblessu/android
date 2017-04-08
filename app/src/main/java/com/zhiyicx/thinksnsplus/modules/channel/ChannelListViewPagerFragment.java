package com.zhiyicx.thinksnsplus.modules.channel;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSViewPagerFragment;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.follow_fans.FollowFansViewPagerFragment;

import java.util.Arrays;
import java.util.List;

/**
 * @author LiuChao
 * @describe
 * @date 2017/4/8
 * @contact email:450127106@qq.com
 */

public class ChannelListViewPagerFragment extends TSViewPagerFragment<ChannelListContract.Presenter> {
    @Override
    protected List<String> initTitles() {
        return Arrays.asList(getString(R.string.subscrip_channel), getString(R.string.all_channel));
    }

    @Override
    protected List<Fragment> initFragments() {
        Fragment subscripChannelFragment = ChannelListFragment.newInstance(getArguments());
        Fragment allChannelFragment = ChannelListFragment.newInstance(getArguments());
        return Arrays.asList(subscripChannelFragment, allChannelFragment);
    }

    @Override
    protected void initData() {

    }

    public static ChannelListViewPagerFragment newInstance(Bundle bundle) {
        ChannelListViewPagerFragment channelListViewPagerFragment = new ChannelListViewPagerFragment();
        channelListViewPagerFragment.setArguments(bundle);
        return channelListViewPagerFragment;
    }
}
