package com.zhiyicx.thinksnsplus.modules.register.rule;

import android.os.Bundle;
import android.view.View;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.thinksnsplus.R;

import br.tiagohm.markdownview.MarkdownView;
import butterknife.BindView;

/**
 * @Author Jliuer
 * @Date 2017/10/27/13:48
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class UserRuleFragment extends TSFragment {

    public static final String RULE = "markdown_rule";

    @BindView(R.id.md_user_rule)
    MarkdownView mMarkdownView;

    String mMarkDownRule;

    @Override
    protected String setCenterTitle() {
        return getString(R.string.user_rule_register);
    }

    public static UserRuleFragment newInstance(Bundle bundle) {
        UserRuleFragment fragment = new UserRuleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View rootView) {
        mMarkDownRule = getArguments().getString(RULE, RULE);
        mMarkdownView.loadMarkdown(mMarkDownRule);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_user_rule;
    }
}
