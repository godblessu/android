package com.zhiyicx.thinksnsplus.modules.circle.all_circle;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.thinksnsplus.base.AppComponent;
import com.zhiyicx.thinksnsplus.base.InjectComponent;

import dagger.Component;

/**
 * @Author Jliuer
 * @Date 2017/11/21/15:48
 * @Email Jliuer@aliyun.com
 * @Description
 */
@FragmentScoped
@Component(dependencies = AppComponent.class, modules = AllCircleContainerPresenterModule.class)
public interface AllCircleContainerComponent extends InjectComponent<AllCircleContainerActivity> {
}
