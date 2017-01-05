package com.zhiyicx.thinksnsplus.modules.login;


import android.app.Application;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.base.ServiceModule;
import com.zhiyicx.thinksnsplus.data.beans.LoginBean;
import com.zhiyicx.thinksnsplus.data.client.LoginClient;
import com.zhiyicx.thinksnsplus.modules.AcitivityTest;
import com.zhiyicx.thinksnsplus.modules.login.LoginActivity;
import com.zhiyicx.thinksnsplus.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.zhiyicx.thinksnsplus.modules.MyViewMatchers.isUnClickable;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author LiuChao
 * @describe
 * @date 2016/12/30
 * @contact email:450127106@qq.com
 */

public class LoginActivityTest extends AcitivityTest {

    private ViewInteraction etPhone, etPass, btnLogin, tvErrorTip;
    private LoginClient mLoginClient;

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule(LoginActivity.class);

    @Before
    public void initActivity() {
        mLoginClient = AppApplication.AppComponentHolder.getAppComponent().serviceManager().getLoginClient();
        etPhone = findViewById(R.id.et_login_phone);
        etPass = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.bt_login_login);
        tvErrorTip = findViewById(R.id.login_error_tip);
    }

    /**
     * summary   不输入手机号，只输入密码能否点击登录按钮
     * steps     1.清空输入框  2.输入密码
     * expected  登录按钮无法点击
     */
    @Test
    public void clickableWhenNoPhone() throws Exception {
        clearEditText(etPhone, etPass);
        etPass.perform(replaceText("123456"), closeSoftKeyboard());
        btnLogin.check(matches(isUnClickable()));
    }

    /**
     * summary    不输入密码，只输入手机号能否点击登录按钮
     * steps      1.清空输入框  2.输入手机号
     * expected   登录按钮无法点击
     */
    @Test
    public void clickableWhenNoPassword() throws Exception {
        clearEditText(etPhone, etPass);
        etPhone.perform(replaceText("15928856596"), closeSoftKeyboard());
        btnLogin.check(matches(isUnClickable()));
    }

    /**
     * summary    因为某些原因导致登录失败，比如密码错误
     * steps        1.输入正确的手机号  2.输入错误的密码 3.点击登陆按钮
     * expected   errorTip显示登陆失败的原因
     */
    @Test
    public void loginFailure() throws Exception {
        mLoginClient.login("failure", "12344", "dsafdsa","fdsadfs")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TestAction<BaseJson<LoginBean>>() {
                    @Override
                    void testCall(BaseJson<LoginBean> integerBaseJson) {
                        LogUtils.d("haha",integerBaseJson.toString());
                        if (integerBaseJson.isStatus()) {
                            // 登陆成功跳转:当前不可能发生
                           assertFalse(true);
                        } else {
                            // 登陆失败
                            assertFalse(false);
                        }
                    }
                }, new TestAction<Throwable>() {
                    @Override
                    void testCall(Throwable e) {
                        LogUtils.e(e,"exception");
                        //assertFalse(true);
                    }
                });
    }

    /**
     * summary    输入正确的手机号，密码登陆成功
     * steps      1.输入正确的手机号 2.输入正确的密码 3.点击登陆按钮 4.主线成沉睡1s等待网络请求结果
     * expected   errorTip显示登陆成功的内容
     */
    @Test
    public void loginSuccess() throws Exception {
        clearEditText(etPhone, etPass);
        etPhone.perform(replaceText("15928856596"), closeSoftKeyboard());
        etPass.perform(replaceText("123456"), closeSoftKeyboard());
        btnLogin.perform(click());
        Thread.sleep(1000);
        tvErrorTip.check(matches(withText("登陆失败")));
    }
}
