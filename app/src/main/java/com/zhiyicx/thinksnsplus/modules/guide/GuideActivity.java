package com.zhiyicx.thinksnsplus.modules.guide;

import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSActivity;

public class GuideActivity extends TSActivity {
    @Override
    protected Fragment getFragment() {
        return new GuideFragment();
    }
}
